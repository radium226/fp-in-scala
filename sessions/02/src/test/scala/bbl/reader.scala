package bbl

import bbl.fp._

import bbl.fp.data.Reader

class ReaderSuite extends AbstractSuite {

  // Some models
  object Config {
    type FirstName = String
    type LastName = String

    def firstName: Program[FirstName] = Program(_.firstName)
    def lastName: Program[FirstName] = Program(_.lastName)

  }

  case class Config(
    firstName: Config.FirstName,
    lastName: Config.LastName,
  )

  // Some type aliases
  type Program[T] = Reader[Config, T]

  object Program {

    def apply[T](run: Config => T): Program[T] = {
      Reader(run)
    }

  }

  test("Reader should work") {
    val helloProgram = for {
      firstName <- Config.firstName
      lastName <- Config.lastName
    } yield s"Hello, ${firstName} ${lastName}! "

    val helpProgram = "Welcome to... Jurassic Park! ".pure[Program]

    val program = (helloProgram product helpProgram)
      .map({ case (hello, help) =>
        s"${hello}\n${help}"
      })

    val outcome = program.run(Config("Ian", "Malcom"))
    println(outcome)
    assertEquals(outcome, s"Hello, Ian Malcom! \nWelcome to... Jurassic Park! ")
  }

}
