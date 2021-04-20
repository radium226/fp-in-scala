package bbl.fp
package instances

import bbl.fp.data.IO

trait IOInstances {

  implicit def monadForIO[T]: Monad[IO] = new Monad[IO] {

    override def pure[A](a: A): IO[A] = IO({ () =>
      a
    })

    override def ap[A, B](fa: IO[A])(fab: IO[A => B]): IO[B] = IO({ () =>
      fab.unsafeRun()(fa.unsafeRun())
    })

    override def flatMap[A, B](fa: IO[A])(afb: A => IO[B]): IO[B] = IO({ () =>
      afb(fa.unsafeRun()).unsafeRun()
    })

  }

}
