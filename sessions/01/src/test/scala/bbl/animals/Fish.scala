package bbl.animals

import bbl.ImplicitsSuite._

case class Fish(name: String)

object Fish {

  implicit val loudForFish: Loud[Fish] = new Loud[Fish] {

    override def sound(fish: Fish): String = {
      s"Bloop! I'm ${fish.name}"
    }

  }

}
