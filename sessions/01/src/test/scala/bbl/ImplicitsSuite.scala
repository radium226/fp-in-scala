package bbl

object ImplicitsSuite {

  trait Loud[T] {

    def sound(t: T): String

  }

  object Loud {

    /* implicit def loudForAny[T <: AnyRef]: Loud[T] = new Loud[T] {

      override def sound(t: T): String = {
        s"..."
      }

    } */

  }

}

class ImplicitsSuite extends AbstractSuite {

  case class Dog(name: String)

  test("Implicits can be used to handle automatic conversion") {
    // Deprecated!
    implicit def stringToDog(s: String): Dog = {
      Dog(s)
    }

    val snoopy: Dog = "Snoopy"
    assert(snoopy == Dog("Snoopy"))
  }

  test("Implicits can be used to add extension methods") {
    implicit class DogOps(dog: Dog) {

      def bark(): String = {
        s"Woof! I'm ${dog.name}."
      }

      override def toString: String = "TOTOTOTO"

    }

    val snoopy = Dog("Snoopy")
    //println(snoopy.toString)
    assert(snoopy.bark().contains("Snoopy"))
  }

  test("Implicits can be used to pass value automatically to functions") {
    case class Context(value: String)

    implicit val context: Context = Context("Bar")

    def dumpValueFromContext(prefix: String)(implicit context: Context): String = {
      s"[${prefix}] ${context.value}"
    }

    assert(dumpValueFromContext("Foo") == "[Foo] Bar")
  }

  test("Implicits can be used to define type classes") {

    import ImplicitsSuite._

    case class Cat(name: String)

    class LoudForCat extends Loud[Cat] {

      override def sound(cat: Cat): String = {
        s"Meow! I'm ${cat.name}."
      }

    }

    object Cat {

      implicit val loudForCat: Loud[Cat] = new Loud[Cat] {

        override def sound(cat: Cat): String = {
          s"Meow! I'm ${cat.name}."
        }

      }

    }

    case class Dog(name: String)

    implicit val loudForDog: Loud[Dog] = new Loud[Dog] {

      override def sound(dog: Dog): String = {
        s"Woof! I'm ${dog.name}"
      }

    }

    import animals._


    val snoopySound = implicitly[Loud[Dog]].sound(Dog("Snoopy"))
    info("snoopySound={}", snoopySound)
    assert(snoopySound.contains("Snoopy"))

    val felixSound = implicitly[Loud[Cat]].sound(Cat("Felix"))
    info("felixSound={}", felixSound)
    assert(felixSound.contains("Felix"))

    /* val nemoSound = implicitly[Loud[Fish]].sound(Fish("Nemo"))
    info("nemoSound={}", nemoSound)
    assert(nemoSound.contains("...")) */
  }

}
