package bbl

import cats.syntax.option._

class TypeClassesSuite extends AbstractSuite {

  test("Using vanilla Scala, we can define type-classes") {
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

    import instances.animal._
    import syntax.animal._

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

  /* test("Using Simulacrum's macros, we can reduce boilerplate") {
    import simulacrum._

    type Sound = String

    @typeclass trait Animal[T] {

      @op("sound") def sound(t: T): Sound

    }

    case class Dog(name: String)

    implicit val animalForDog: Animal[Dog] = { dog =>
      s"Woof, woof! I'm ${dog.name}. "
    }

    object syntax {

      object animal extends Animal.ToAnimalOps

    }

    import syntax.animal._

    val snoopy = Dog("Snoopy")

    assert(Animal[Dog].sound(snoopy) == snoopy.sound)

  } */

}
