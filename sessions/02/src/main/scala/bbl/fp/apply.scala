package bbl.fp

trait Apply[F[_]] extends Functor[F] {

  def ap[A, B](fa: F[A])(fab: F[A => B]): F[B]

  def product[A, B](fa: F[A], fb: F[B]): F[(A, B)] = {
    ap(fb)(map(fa)(a => (b: B) => (a, b)))
  }

}
