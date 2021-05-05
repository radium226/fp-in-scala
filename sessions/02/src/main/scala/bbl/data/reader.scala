package bbl.data

case class Reader[E, T](run: E => T)