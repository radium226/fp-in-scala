package bbl

import cats.syntax.option._
import cats.data.NonEmptyList

import scala.reflect.runtime.universe

class ForComprehensionSuite extends AbstractSuite {

  object User {

    type ID = Int

    type Name = String

  }

  case class User(id: User.ID, firstName: User.Name, lastName: User.Name)

  object Book {

    type Title = String

  }

  case class Book(title: Book.Title)

  object Library {

    def lookUpUserByID(id: User.ID): Option[User] = {
      id match {
        case 0 =>
          User(0, "Maxime", "Chattam").some

        case 1 =>
          User(1, "Brian", "Greene").some

        case _ =>
          none[User]
      }
    }

    def lookUpBooksByUser(user: User): Option[NonEmptyList[Book]] = {
      user match {
        case User(0, _, _) =>
          NonEmptyList.of(Book("L'Âme du mal"), Book("In Tenebris")).some

        case User(1, _, _) =>
          NonEmptyList.of(Book("The Elegant Universe"), Book("The Fabric of the Cosmos"), Book("The Hidden Reality")).some

        case _ =>
          none[NonEmptyList[Book]]
      }
    }

  }


  test("The for-comprehension syntax should produce the same result as using flatMap() and map()") {
    val userID = 1
    val forComprehensionExpr = universe reify {
      for {
          user <- Library.lookUpUserByID(userID)
          books <- Library.lookUpBooksByUser(user)
      } yield books.length
    }
    info(s"forComprehensionExpr={}", forComprehensionExpr)

    val forComprehensionResult = for {
      user <- Library.lookUpUserByID(userID)
      books <- Library.lookUpBooksByUser(user)
    } yield books.length

    val standardResult = Library.lookUpUserByID(userID)
      .flatMap({ user =>
        Library.lookUpBooksByUser(user)
          .map({ books =>
            books.length
          })
      })

    assert(forComprehensionResult == standardResult)
  }

}
