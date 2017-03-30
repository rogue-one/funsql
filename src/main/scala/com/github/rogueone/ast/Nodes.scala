package com.github.rogueone.ast

import java.text.SimpleDateFormat
import java.util.Date

/**
  * Created by chlr on 3/26/17.
  */
object Nodes {

  sealed trait Node

  /**
    * Expression node
    */
  trait Expr extends Node {
    def value: String
  }

  /**
    * Identifier Node
    * @param value value of the Identifier
    */
  case class Identifier(override val value: String) extends Expr

  trait Literal[T] extends Expr {
    def literal: T
  }

  case class IntegerLiteral(override val value: String) extends Literal[Long] {
    def literal = value.toLong
  }

  case class DecimalLiteral(override val value: String) extends Literal[Double] {
    def literal = value.toDouble
  }

  case class StringLiteral(override val value: String) extends Literal[String] {
    def literal = value
  }

  case class DateLiteral(override val value: String) extends Literal[Date] {
    def literal = {
      val sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
      sdf.parse(value)
    }
  }

}
