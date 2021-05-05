package bbl

import scala.collection.mutable.Buffer

class CallByNameSuite extends AbstractSuite {

  test("The call-by-name syntax should make us invoke the produceSound() two times") {

    val steps: Buffer[String] = Buffer()

    def produceSound(): String = {
      steps.addOne("Producing")
      val sound = "BOOM! "
      steps.addOne("Produced")
      sound
    }


    steps.clear()

    def printSoundTwoTimesByValue(sound: String): Unit = {
      steps.addOne("Printing")
      println(sound)
      println(sound)
      steps.addOne("Printed")
    }

    printSoundTwoTimesByValue(produceSound())
    assert(steps == List("Producing", "Produced", "Printing", "Printed"))


    steps.clear()

    def printSoundTwoTimesByName(sound: => String): Unit = {
      steps.addOne("Printing")
      println(sound)
      println(sound)
      steps.addOne("Printed")
    }

    printSoundTwoTimesByName(produceSound())
    assert(steps == List("Printing", "Producing", "Produced", "Producing", "Produced", "Printed"))
  }

}
