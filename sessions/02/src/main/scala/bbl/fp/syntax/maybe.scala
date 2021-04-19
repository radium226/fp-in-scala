package bbl.fp
package syntax

import bbl.fp.data.Maybe

trait MaybeSyntax {

  def nothing[T]: Maybe[T] = {
    Maybe.Nothing
  }

  implicit class MaybeAnyOps[T](value: T) {

    def just: Maybe[T] = {
      Maybe.Just(value)
    }

  }


}
