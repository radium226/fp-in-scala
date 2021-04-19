package bbl.fp
package instances

import bbl.fp.data.Reader

trait ReaderInstances {

  implicit def monadForReader[E]: Monad[Reader[E, *]] = new Monad[Reader[E, *]] {

    override def flatMap[A, B](fa: Reader[E, A])(afb: A => Reader[E, B]): Reader[E, B] = {
      Reader({ e =>
        afb(fa.run(e)).run(e)
      })
    }

    override def pure[A](a: A): Reader[E, A] = {
      Reader({ _ => a })
    }

    override def ap[A, B](fa: Reader[E, A])(fab: Reader[E, A => B]): Reader[E, B] = {
      Reader({ e =>
        fab.run(e)(fa.run(e))
      })
    }
  }

}
