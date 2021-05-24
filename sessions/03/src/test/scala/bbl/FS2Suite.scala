package bbl

import fs2._

import cats._
import cats.effect._

import cats.implicits._
import cats.effect.implicits._


class FS2Suite extends AbstractSuite {

  test("FS2 is great") {

    // Http4s
    type Content[F[_]] = Stream[F, Byte]

    // Doobie
    // Stream[F[_], Row]


    val stream: Stream[IO, String] = Stream
      .emits(Seq("Toto", "Tata"))
      .map({ word =>
        word.toUpperCase()
      })

    val outcome: IO[String] = stream
      .compile
      .foldMonoid

    println(outcome.unsafeRunSync())
  }

  test("FS2 provide through") {
    val transformToChar: Pipe[IO, String, Char] = { words =>
      words
        .map({ word =>
          word.toCharArray
        })
        .flatMap({ chars =>
          Stream.emits(chars)
        })
    }

    val words = Stream.emits(List("Hello", "Word"))
    val letters = words.through(transformToChar)
  }

}
