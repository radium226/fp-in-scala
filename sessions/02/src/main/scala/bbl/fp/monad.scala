package bbl.fp

trait Monad[F[_]] extends Applicative[F] with FlatMap[F] {

}
