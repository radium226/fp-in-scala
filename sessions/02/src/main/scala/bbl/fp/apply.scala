package bbl.fp

trait Apply[F[_]] extends Functor[F] {

  def ap[A, B](fa: F[A])(fab: F[A => B]): F[B]

}
