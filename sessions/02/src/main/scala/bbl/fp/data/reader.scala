package bbl.fp.data

case class Reader[E, T](run: E => T)