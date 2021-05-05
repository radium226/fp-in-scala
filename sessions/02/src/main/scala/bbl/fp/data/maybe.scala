package bbl.fp
package data

sealed trait Maybe[+T]

object Maybe {
  case class Just[T](value: T) extends Maybe[T]
  case object Nothing extends Maybe[scala.Nothing]
}
