package bbl

package object fp extends syntax.AllSyntax with instances.AllInstances {

  val IO = data.IO
  type IO[T] = data.IO[T]

  val Reader = data.Reader
  type Reader[E, T] = data.Reader[E, T]

  val Maybe = data.Maybe
  type Maybe[T] = data.Maybe[T]

}