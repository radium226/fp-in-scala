package bbl

import cats.effect._
import cats.implicits._

class IOSuite extends AbstractSuite {

  def ask(question: String): IO[String] = {
    IO(println(question)) *> IO.pure("Toto")
  }

  /* test("IO should work") {
    val io = for {
      name <- ask("Name ?")
      _    <- IO(println(s"name=${name}"))
      _    <- IO.raiseError(new IllegalArgumentException())
    } yield ()

    io.unsafeRunSync()
  } */

}
