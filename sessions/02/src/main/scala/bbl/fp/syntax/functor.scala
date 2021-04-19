package bbl.fp
package syntax


trait FunctorSyntax {

  implicit class FunctorOps[F[_], A](fa: F[A])(implicit F: Functor[F]) {

    def map[B](ab: A => B): F[B] = {
      F.map(fa)(ab)
    }

  }

}
