package bbl

import cats.syntax.option._

class SealedTraitSuite extends AbstractSuite {

  test("Sealed traits should work") {

    sealed trait TextOption[+A]

    object TextOption {

      case class TextSome[A](text: A) extends TextOption[A]

      case object TextNone extends TextOption[Nothing]

    }

    val l: Either[String, Int] = Left("toto")

    Option("toto")
      .fold(none[String])(_.some)

    val o = Some("tto")
    val n = None

    import TextOption._

    val textOption: TextOption[String] = TextSome("Coucou! ")

    textOption match {
      case TextSome(value) =>
        println(value)

      case TextNone =>
        println("Oh non :(")
    }

    // case class Dog(name: String, age: Int) <==> String ::: Int

    // type StringOrInt = String :+: Int

    // Option[T] <==> Some[T] :+: None
  }

}
