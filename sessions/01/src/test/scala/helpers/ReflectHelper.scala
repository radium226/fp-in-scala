package helpers

import scala.reflect.runtime.universe._
import cats.syntax.option._


trait ReflectHelper {

  def valsOf[T: TypeTag]: List[TermSymbol] = {
    typeOf[T]
      .members
      .collect({
        case termSymbol: TermSymbol if termSymbol.isVal =>
          termSymbol
      })
      .toList
  }

  def varOf[T: TypeTag]: List[TermSymbol] = {
    typeOf[T]
      .members
      .collect({
        case termSymbol: TermSymbol if termSymbol.isVar =>
          termSymbol
      })
      .toList
  }

  def methodsOf[T: TypeTag]: List[MethodSymbol] = {
    methodsOf(typeOf[T])
  }

  def methodsOf(typeSymbol: TypeSymbol): List[MethodSymbol] = {
    methodsOf(typeSymbol.info)
  }

  def methodsOf(`type`: Type): List[MethodSymbol] = {
    `type`
    .members
    .collect({
      case methodSymbol: MethodSymbol =>
        methodSymbol
    })
    .toList
  }

  def caseAccessorsOf[T: TypeTag]: List[MethodSymbol] = {
    typeOf[T]
      .members
      .collect({
        case methodSymbol: MethodSymbol if methodSymbol.isCaseAccessor =>
          methodSymbol
      })
      .toList
  }

  def traitsOf[T: TypeTag]: List[ClassSymbol] = {
    typeOf[T]
      .typeSymbol
      .asClass
      .baseClasses
      .map(_.asClass)
      .filter(_.isTrait)
  }

  def companionObjectOf[T: TypeTag]: Option[TypeSymbol] = {
    typeOf[T].companion match {
      case NoType =>
        none[TypeSymbol]

      case otherType =>
        otherType
          .typeSymbol
          .asType
          .some
    }
  }

}
