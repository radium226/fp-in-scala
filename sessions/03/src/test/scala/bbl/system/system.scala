package bbl.system

import bbl.AbstractSuite
import fs2.text.{utf8Decode, utf8Encode}
import cats.effect.IO
import fs2.Stream
import fs2.io.file.Files

class SystemSuite extends AbstractSuite {

  test("Process should be able to readStdout") {
    val seqProcess = Process("seq", "0", "10")

    val outcome = seqProcess
      .readStdout
      .through(utf8Decode[IO])
      .compile
      .foldMonoid
      .unsafeRunSync()

    val expectedOutcome = s"${(0 to 10).mkString("\n")}\n"

    assertEquals(outcome, expectedOutcome)
  }

  test("Process should be able to passThrough") {
    val words = List("Jurassic", "Park")
    val trProcess = Process("tr", "[a-z]", "[A-Z]")
    val outcome = Stream
      .emits[IO, String](words)
      .intersperse("\n")
      .through(utf8Encode[IO])
      .through(trProcess.passThrough)
      .through(utf8Decode[IO])
      .compile
      .foldMonoid
      .unsafeRunSync()

    val expectedOutcome = words.map(_.toUpperCase()).mkString("\n")

    assertEquals(outcome, expectedOutcome)
  }

  test("Process should be able to writeToStdin") {
    val words = List("Ian", "Malcolm")

    val outcome = Files[IO]
      .tempFile()
      .use({ tempFilePath =>
        val writeToTeeStdin = Stream
          .emits(words)
          .intersperse("\n")
          .through(utf8Encode[IO])
          .through(Process("tee", s"${tempFilePath}").writeToStdin)
          .compile
          .drain

        val readFromTempFile = Files[IO]
          .readAll(tempFilePath, 512)
          .through(utf8Decode[IO])
          .compile
          .foldMonoid

        writeToTeeStdin *> readFromTempFile
      })
      .unsafeRunSync()

    val expectedOutcome = words.mkString("\n")

    assertEquals(outcome, expectedOutcome)
  }

}
