package bbl

trait Applicative[F[_]] extends Apply[F] {

  def pure[A](a: A): F[A]

  override def map[A, B](fa: F[A])(f: A => B): F[B] = {
    ap(fa)(pure(f))
  }

}
