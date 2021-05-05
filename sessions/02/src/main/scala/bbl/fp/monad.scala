package bbl.fp

trait Monad[F[_]] extends Applicative[F] with FlatMap[F] {

  /* def ap[A, B](fa: F[A])(fab: F[A => B]): F[B] = {
    flatMap(fab)(pure[A => F[B]]({ a => pure(a) }))
  } */

}