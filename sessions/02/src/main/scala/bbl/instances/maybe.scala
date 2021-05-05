package bbl.instances

import bbl.Monad
import bbl.fp.data.Maybe

trait MaybeInstances {

  implicit def monadForMaybe[F[_]]: Monad[Maybe] = new Monad[Maybe] {

    override def pure[A](a: A): Maybe[A] = {
      Maybe.Just(a)
    }

    override def ap[A, B](fa: Maybe[A])(fab: Maybe[A => B]): Maybe[B] = {
      fa match {
        case Maybe.Just(a) =>
          fab match {
            case Maybe.Just(ab) =>
              Maybe.Just(ab(a))

            case Maybe.Nothing =>
              Maybe.Nothing
          }

        case Maybe.Nothing =>
          Maybe.Nothing

      }
    }

    override def flatMap[A, B](fa: Maybe[A])(afb: A => Maybe[B]): Maybe[B] = {
      fa match {
        case Maybe.Just(a) =>
          afb(a)

        case Maybe.Nothing =>
          Maybe.Nothing
      }
    }

  }

}
