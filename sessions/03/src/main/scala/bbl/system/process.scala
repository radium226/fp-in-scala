package bbl.system

import cats.effect.IO
import fs2.{Pipe, Stream}
import fs2.io.{readInputStream, writeOutputStream}

import java.lang.{
  Process => JavaProcess,
  ProcessBuilder => JavaProcessBuilder
}


case class Process(command: List[Argument]) {

  import Process._

  def writeToStdin: Pipe[IO, Byte, Unit] = { byteStream =>
    Stream
      .eval(
        IO.blocking(
          new JavaProcessBuilder()
            .command(command: _*)
            .inheritIO()
            .redirectInput(ProcessBuilder.Redirect.PIPE)
            .start()
        )
      )
      .flatMap({ javaProcess =>
        byteStream
          .through(writeOutputStream(IO.blocking(javaProcess.getOutputStream)))
          .concurrently(Stream.eval(IO.blocking(javaProcess.waitFor())))
      })
  }

  def readStdout: Stream[IO, Byte] = {
    Stream
      .eval[IO, JavaProcess](
        IO.blocking(new JavaProcessBuilder()
          .command(command: _*)
          .inheritIO()
          .redirectOutput(ProcessBuilder.Redirect.PIPE)
          .start())
      )
      .flatMap({ javaProcess =>
        readInputStream(IO.blocking(javaProcess.getInputStream), ChunkSize)
          .concurrently(Stream.eval(IO(javaProcess.waitFor()).void))
      })
  }

  def passThrough: Pipe[IO, Byte, Byte] = { byteStream =>
    Stream
      .eval(
        IO.blocking(
          new JavaProcessBuilder()
            .command(command: _*)
            .inheritIO()
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectInput(ProcessBuilder.Redirect.PIPE)
            .start()
        )
      )
      .flatMap({ javaProcess =>
        readInputStream(IO.blocking(javaProcess.getInputStream), ChunkSize)
          .concurrently(byteStream.through(writeOutputStream[IO](IO(javaProcess.getOutputStream))))
          .concurrently(Stream.eval(IO.blocking(javaProcess.waitFor())))
      })
  }

  def execute: Stream[IO, Unit] = {
    Stream.eval(
      IO.blocking(
        new JavaProcessBuilder()
          .command(command: _*)
          .inheritIO()
          .start()
          .waitFor()
      ).void
    )
  }

}

object Process {

  val ChunkSize = 512

  def apply(command: Command): Process = {
    new Process(command)
  }

  def apply(arguments: Argument*): Process = {
    new Process(arguments.toList)
  }

}
