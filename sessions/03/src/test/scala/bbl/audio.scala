package bbl

import bbl.Audio.NoiseColor

import java.nio.file.Paths
import scala.concurrent.duration._


class AudioSuite extends AbstractSuite {

  test("Audio should be able to readFile") {
    val musicFilePath = Paths.get("sessions/03/src/test/resources/music.mp3")
    Audio
      .readFile(musicFilePath)
      .content
      .compile
      .drain
      .unsafeRunSync()
  }

  test("Audio should be able to play") {
    val musicFilePath = Paths.get("sessions/03/src/test/resources/music.mp3")
    Audio.readFile(musicFilePath).play.unsafeRunSync()
  }
  test("Audio should be able to generate brown noise") {
    Audio.noise(NoiseColor.Brown).take(1.second).play.unsafeRunSync()
  }

  test("Audio can be concatenated") {
    Audio.noise(NoiseColor.Brown).take(1.second).concat(Audio.noise(NoiseColor.Pink).take(1.second)).play.unsafeRunSync()
  }

  test("Audio can be mixed") {
    val music = Audio.readFile(Paths.get("sessions/03/src/test/resources/music.mp3"))
    val parisians = Audio.readFile(Paths.get("sessions/03/src/test/resources/parisians.mp3"))

    music.mix(parisians).take(10.seconds).play.unsafeRunSync()
  }

}
