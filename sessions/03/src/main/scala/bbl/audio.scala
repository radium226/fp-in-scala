package bbl

import bbl.Audio.throughFFmpeg
import cats.effect.IO
import fs2.{Pipe, Stream}
import fs2.io.file.Files
import fs2.io.writeOutputStream

import java.nio.file.{Path, Files => JavaFiles}
import java.io.IOException

import scala.concurrent.duration.FiniteDuration
import system.{Argument, Process}

import cats.syntax.traverse._
import cats.syntax.option._


object Audio {

  val ChunkSize = 1024

  val Rate = 44100
  val Channels = 2
  val Format = "s16le"
  val Codec = "pcm_s16le"

  val FFmpegGlobalArguments = List(
    "-re",
    "-y",
    "-fflags",
    "+shortest",
    "-hide_banner",
    "-loglevel", "quiet"
  )

  val FFmpegPCMArguments = List(
    "-ar", s"${Rate}",
    "-ac", s"${Channels}",
    "-acodec", s"${Codec}",
    "-f", s"${Format}",
  )

  type Content = Stream[IO, Byte]

  sealed trait NoiseColor {
    val ffmpegArgument: Argument
  }
  object NoiseColor {
    case object White extends NoiseColor {
      val ffmpegArgument = "white"
    }
    case object Pink extends NoiseColor {
      val ffmpegArgument = "pink"
    }
    case object Brown extends NoiseColor {
      val ffmpegArgument = "brown"
    }
  }

  def decodeContent: Pipe[IO, Byte, Byte] = { byteStream =>
    val ffmpegInputArguments = List(
      "-hide_banner",
      "-loglevel", "error",
      "-i", "-"
    )

    byteStream.through(Process("ffmpeg" +: (FFmpegGlobalArguments ++ ffmpegInputArguments ++ FFmpegPCMArguments) :+ "-").passThrough)
  }

  def readFile(filePath: Path): Audio = {
    Audio(Files[IO].readAll(filePath, ChunkSize).through(decodeContent))
  }

  def noise(color: NoiseColor): Audio = {
    val ffmpegInputArguments = List(
      "-f", "lavfi",
      "-i", s"anoisesrc=color=${color.ffmpegArgument}"
    )

    val ffmpegCommand = "ffmpeg" +: (FFmpegGlobalArguments ++ ffmpegInputArguments ++ FFmpegPCMArguments) :+ "-"

    Audio(Process(ffmpegCommand).readStdout)
  }

  def durationToContentLength(duration: FiniteDuration): Long = {
    (duration.toMicros / 1_000_000d * Audio.Rate * Audio.Channels.toDouble * 16d / 8d).toLong
  }

  def throughFFmpeg(audios: List[Audio])(ffmpegArguments: List[Argument]): Audio = {
    val content = (for {
      directoryPath <- Stream.resource(Files[IO].tempDirectory())
      namedPipePaths <- audios
        .zipWithIndex
        .traverse({ case (_, index) =>
          val namedPipePath = directoryPath.resolve(s"input-${index}")
          Process("mkfifo", s"${namedPipePath}").execute.as(namedPipePath)
        })
      } yield namedPipePaths)
      .flatMap({ namedPipePaths =>
        val ffmpegCommand = "ffmpeg" +: (
          FFmpegGlobalArguments ++
          namedPipePaths
            .flatMap({ namedPipePath =>
              FFmpegPCMArguments ++
                List("-i", s"${namedPipePath}")
            }) ++
          ffmpegArguments ++
          FFmpegPCMArguments
        ) :+ "-"

        audios
          .zip(namedPipePaths)
          .foldLeft( Process(ffmpegCommand).readStdout)({ case (byteStream, (audio, namedPipePath)) =>
            byteStream
              .concurrently(
                audio
                  .content
                  .through(writeOutputStream(IO.blocking(JavaFiles.newOutputStream(namedPipePath))))
                  .handleErrorWith({
                    // FIXME: We should handle both fs2.CompositeFailure and java.io.IOException
                    case exception if exception.getMessage() contains "Broken pipe" =>
                      Stream.empty

                    case exception =>
                      Stream.raiseError[IO](exception)
                  })
              )
          })
      })

    Audio(content)
  }

}

case class Audio(content: Audio.Content) { self =>

  def concat(audio: Audio): Audio = {
    Audio(self.content ++ audio.content)
  }

  def take(duration: FiniteDuration): Audio = {
    contentThrough(_.take(Audio.durationToContentLength(duration)))
  }

  def contentThrough(pipe: Pipe[IO, Byte, Byte]): Audio = {
    self.copy(content = self.content.through(pipe))
  }

  def mix(audio: Audio, threshold: Double = 0.003, ratio: Int = 20, release: Int = 1000, knee: Int = 4): Audio = {
    throughFFmpeg(List(self, audio))(List(
      "-filter_complex",
      s"[1:a]asplit=2[sc][mix];[0:a][sc]sidechaincompress=threshold=${threshold}:ratio=${ratio}:release=${release}:knee=${knee}[bg]; [bg][mix]amerge[final]",
      "-map",
      "[final]"
    ))
  }

  def play: IO[Unit] = {
    val ffplayProcess = Process(
        "ffplay",
        "-hide_banner",
        "-loglevel", "error",
        "-autoexit",
        "-f", s"${Audio.Format}",
        "-acodec", s"${Audio.Codec}",
        "-ac", s"${Audio.Channels}",
        "-ar", s"${Audio.Rate}",
        "-"
      )

    self
      .content
      .through(ffplayProcess.writeToStdin)
      .compile
      .drain
  }

}