package bbl

import bbl.fp._

class IOSuite extends AbstractSuite {

  test("IO should delay effect") {
    val io = for {
      _ <- IO.printLine("What is your name? ")
      name <- IO.readLine
      _ <- IO.printLine(s"Your name is ${name}! ")
    } yield ()
  }

}