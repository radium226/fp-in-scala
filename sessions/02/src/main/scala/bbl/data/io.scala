package bbl.data

import java.util.Scanner

case class IO[T](unsafeRun: () => T)

object IO {

  def delay[T](thunk: => T): IO[T] = IO({ () => thunk })

  def readLine: IO[String] = {
    IO.delay(new Scanner(System.in).nextLine())
  }

  def printLine(line: String): IO[Unit] = {
    IO.delay(println(line))
  }

}
