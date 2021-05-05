package bbl.syntax

trait FlatMapSyntax {

  implicit class FlatMapOps[F[_], A](fa: F[A])(implicit F: FlatMap[F]) {

    def flatMap[B](afb: A => F[B]): F[B] = {
      F.flatMap(fa)(afb)
    }

  }

}
