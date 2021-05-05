package bbl.syntax

trait ApplicativeSyntax {

  implicit class ApplicativeAnyOps[F[_], A](a: A) {

    def pure[F[_]](implicit F: Applicative[F]): F[A] = {
      F.pure(a)
    }

  }

}
