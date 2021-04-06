package bbl

import cats.syntax.option._

// https://www.james-willett.com/scala-case-classes/
class CaseClassesSuite extends AbstractSuite {

  case class Dog(name: String, mood: String)

  class Cat(name: String, evilness: Int)

  val snoopy = Dog("Snoopy", "Happy :)")

  test("Case classes should have vals and auto-generated accessors whereas standard classes do not") {
    // Case classes
    assert(
      List("name", "mood")
        .forall({ dogPropertyName =>
          valsOf[Dog]
            .map(_.name.encodedName)
            .find({ dogValEncodedName =>
              info("dogValEncodedName={}", dogValEncodedName)
              s"${dogValEncodedName}".startsWith(dogPropertyName)
            })
            .isDefined
        })
    )

    assert(
      List("name", "mood")
        .forall({ dogPropertyName =>
          caseAccessorsOf[Dog]
            .map({ dogCaseAccessor =>
              info("dogCaseAccessor={}", dogCaseAccessor)
              s"${dogCaseAccessor.name.encodedName}"
            })
            .contains(dogPropertyName)
        })
    )


    // Standard classes
    assert(
      List("name", "evilness")
        .forall({ catPropertyName =>
          valsOf[Cat]
            .map({ catVal =>
              info("catVal={}", catVal)
              s"${catVal.name.encodedName}"
            })
            .contains(catPropertyName)
        })
    )

    assert(caseAccessorsOf[Cat].isEmpty)
  }

  test("Case classes should have a copy() method whereas standard classes do not") {
    // Case classes
    val dogCopyMethods = methodsOf[Dog]
      .filter({ dogMethodName =>
        s"${dogMethodName.name.encodedName}".startsWith("copy")
      })
    info("dogCopyMethods={}", dogCopyMethods)
    assert(dogCopyMethods.size >= 1)

    assert(snoopy.copy(mood = "Sad :(").mood == "Sad :(")


    // Standard Classes
    assert(
      methodsOf[Cat]
        .filter({ catsMethodName =>
          s"${catsMethodName.name.encodedName}".startsWith("copy")
        })
        .isEmpty
    )
  }

  test("Case classes should extends Serializable and Product whereas standard classes do not") {
    // Case classes
    val dogTraits = traitsOf[Dog]
    info("dogTraits={}", dogTraits)

    assert(
      List("Serializable", "Product")
        .forall({ dogTraitName =>
          dogTraits
            .find({ dogTrait =>
              s"${dogTrait.name.encodedName}" == dogTraitName
            })
            .isDefined
        })
    )

    assert(
      List("mood", "name")
        .forall({ dogElementName =>
          snoopy.productElementNames.contains(dogElementName)
        })
    )


    // Standard classes
    assert(traitsOf[Cat].isEmpty)
  }

  test("Case classes should have a companion object whereas standard classes do not") {
    // Case classes
    assert(companionObjectOf[Dog].isDefined)
    val companionObject = companionObjectOf[Dog].get
    info("companionObject={}", companionObject)

    assert(
      methodsOf(companionObject)
        .find({ dogCompanionObjectMethod =>
          s"${dogCompanionObjectMethod.name.encodedName}" == "apply"
        })
        .isDefined
    )

    assert(
      methodsOf(companionObject)
        .find({ dogCompanionObjectMethod =>
          s"${dogCompanionObjectMethod.name.encodedName}" == "unapply"
        })
        .isDefined
    )

    assert(Dog("Snoopy", "Happy :)") == snoopy)


    // Standard classes
    assert(companionObjectOf[Cat].isEmpty)
  }

  test("Case classes support pattern-matching") {
    val Dog(name, _) = snoopy
    assert(name == "Snoopy")
  }

  test("It should be possible to mimic a case class manually") {
    class Mouse(val name: String, val scary: Boolean) extends Product with Equals with Serializable {

      // Product
      override def productPrefix: String = "Mouse"

      override def productElementName(n: Int): String = {
        n match {
          case 0 =>
            "name"

          case 1 =>
            "scary"

          case _ =>
            throw new IndexOutOfBoundsException(s"${n}")
        }
      }

      override def productArity: Int = 2

      override def productElement(n: Int): Any = {
        n match {
          case 0 =>
            name

          case 1 =>
            scary

          case _ =>
            throw new IndexOutOfBoundsException(s"${n}")
        }
      }

      // copy
      def copy(name: String = this.name, scary: Boolean = this.scary): Mouse = {
        Mouse.apply(name, scary)
      }

      // equals, hashCode, etc.
      override def canEqual(that: Any): Boolean = {
        that.isInstanceOf[Mouse]
      }

      override def equals(that: Any): Boolean = {
        if (canEqual(that)) this.productIterator.sameElements(that.asInstanceOf[Mouse].productIterator)
        else false
      }

      override def hashCode(): Int = {
        // Through ScalaRunTime._hashcode
        scala.util.hashing.MurmurHash3.productHash(this)
      }

      override def toString(): String = {
        // Through ScalaRunTime._toString
        productIterator.mkString(productPrefix + "(", ",", ")")
      }

    }

    // Companion object
    object Mouse {

      def apply(name: String, scary: Boolean): Mouse = {
        new Mouse(name, scary)
      }

      def unapply(any: Any): Option[(String, Boolean)] = {
        any match {
          case mouse: Mouse =>
            (mouse.name, mouse.scary).some

          case _ =>
            none[(String, Boolean)]
        }
      }

    }

    val mouse @ Mouse(name, scary) = Mouse("Mickey", true).copy(scary = false)
    info("mouse={}", mouse)
    assert(name == "Mickey")
    assert(scary == false)
    assert(mouse == Mouse("Mickey", false))
  }

}
