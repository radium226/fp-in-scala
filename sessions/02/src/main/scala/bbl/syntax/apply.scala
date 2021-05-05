package bbl.syntax

trait ApplySyntax {

  implicit class ApplyOps[F[_], A](fa: F[A])(implicit F: Apply[F]) {

    def ap[B](fab: F[A => B]): F[B] = {
      F.ap(fa)(fab)
    }

    def product[B](fb: F[B]): F[(A, B)] = {
      F.product(fa, fb)
    }

  }

}
