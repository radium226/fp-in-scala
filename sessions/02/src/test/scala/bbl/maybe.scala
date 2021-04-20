package bbl

import bbl.fp._


class MaybeSuite extends AbstractSuite {

  object Credentials {
    type Login = String
    type Password = String
  }

  case class Credentials(
    login: Credentials.Login,
    password: Credentials.Password
  )

  test("This for-comprehension should return nothing[Credentials]") {
    val credentialsMaybe = for {
      login <- "Login".just
      password <- nothing[String]
    } yield Credentials(login, password)

    assertEquals(credentialsMaybe, nothing[Credentials])
  }

  test("This for-comprehension should return Credentials(...).just") {
    val credentialsMaybe = for {
      login <- "Login".just
      password <- "Password".just
    } yield Credentials(login, password)

    assertEquals(credentialsMaybe, Credentials("Login", "Password").just)
  }



}
