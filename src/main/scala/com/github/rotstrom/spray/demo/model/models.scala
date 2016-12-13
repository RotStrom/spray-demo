package com.github.rotstrom.spray.demo.model

import play.api.libs.json._

sealed trait Pet

case class Dog(id: Int, name: String, age: Option[Int] = None) extends Pet
object Dog {
  implicit val format: OFormat[Dog] = Json.format[Dog]
}

case class Cat(id: Int, name: String, age: Option[Int] = None) extends Pet
object Cat {
  implicit val format: OFormat[Cat] = Json.format[Cat]
}

case class Mouse(id: Int, name: String, age: Option[Int] = None) extends Pet
object Mouse {
  implicit val format: OFormat[Mouse] = Json.format[Mouse]
}

case class Parrot(id: Int, name: String, age: Option[Int] = None) extends Pet
object Parrot {
  implicit val format: OFormat[Parrot] = Json.format[Parrot]
}

case class Pets(cats: Map[String, Cat], mice: Map[String, Mouse])

object Pets {
  implicit val format: OFormat[Pets] = Json.format[Pets]
}

