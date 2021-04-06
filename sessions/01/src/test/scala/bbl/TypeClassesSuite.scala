package bbl

import cats.syntax.option._

class TypeClassesSuite extends AbstractSuite {

  test("Using Scala, we can define type-classes") {
    type Sound = String

    trait Animal[T] {

      def sound(t: T): Sound

    }

    trait AnimalLowPriorityInstances {

      implicit def animalForAnyRef[T <: AnyRef]: Animal[T] = {
        Animal.constant[T]("I make no sound :(")
      }

    }

    trait AnimalOptionInstances {

      implicit def animalForOption[T](implicit animalForT: Animal[T]): Animal[Option[T]] = {
        Animal.instance[Option[T]](_.fold("None can't make any sound! ")(animalForT.sound))
      }

    }

    trait AnimalListInstances {

      implicit def animalForList[T](implicit animalForT: Animal[T]): Animal[List[T]] = {
        Animal.instance[List[T]](_.map(animalForT.sound).mkString(", "))
      }

    }

    trait AnimalInstances extends AnimalLowPriorityInstances with AnimalOptionInstances with AnimalListInstances

    trait AnimalSyntax {

      implicit class AnimalOps[T](t: T)(implicit animalForT: Animal[T]) {

        def sound(): Sound = {
          animalForT.sound(t)
        }

      }

    }

    object instances {

      object animal extends AnimalInstances

    }

    object syntax {

      object animal extends AnimalSyntax

    }

    object implicits extends AnimalInstances with AnimalSyntax

    object Animal {

      // Factory methods
      def constant[T](sound: Sound): Animal[T] = Animal.instance({ _ => sound })

      def instance[T](f: T => Sound): Animal[T] = new Animal[T] {

        override def sound(t: T): Sound = f(t)

      }

      // Summon method
      def apply[T](implicit instance: Animal[T]): Animal[T] = {
        instance
      }

    }

    import implicits._

    case class Dog(name: String)

    implicit val animalForDog: Animal[Dog] = Animal.instance({ dog => s"Woof, woof! I'm ${dog.name}. "})

    case class Fish(color: String)

    val snoopy = Dog("Snoopy")
    val snoopySound = snoopy.sound()
    info("snoopySound={}", snoopySound)
    assert(snoopySound.contains("Snoopy"))

    val nemoSound = Fish("Nemo")
    assert(nemoSound.sound() contains ":(")

    val dingo = Dog("Dingo")
    val dogSounds = Animal[List[Option[Dog]]].sound(List(snoopy.some, dingo.some, none[Dog]))
    info(s"dogSounds={}", dogSounds)
    assert(dogSounds == List(snoopy.some, dingo.some, none[Dog]).sound())
  }

}
