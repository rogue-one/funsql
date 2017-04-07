package com.github.rogueone.ast

import java.util.Date

/**
  * Created by chlr on 3/26/17.
  */
object Nodes {

  sealed trait Node

  sealed trait Exp

  sealed trait Literal extends Exp

  case class IntegerLiteral(value: Long) extends Literal

  case class DecimalLiteral(value: Double) extends Literal

  case class StringLiteral(value: String) extends Literal

  case class DateLiteral(value: Date) extends Literal

  case class Identifier(value: String) extends Exp

  case class Function(name: Identifier, exp: Seq[Exp]) extends Exp

  case class Add(lhs: Exp, rhs: Exp) extends Exp

  case class Sub(lhs: Exp, rhs: Exp) extends Exp

  case class Mul(lhs: Exp, rhs: Exp) extends Exp

  case class Div(lhs: Exp, rhs: Exp) extends Exp

  case class Not(exp: Exp) extends Exp

  case class Eq(lhs: Exp, rhs: Exp) extends Exp

  case class NtEq(lhs: Exp, rhs: Exp) extends Exp

  case class Lt(lhs: Exp, rhs: Exp) extends Exp

  case class Gt(lhs: Exp, rhs: Exp) extends Exp

  case class GtEq(lhs: Exp, rhs: Exp) extends Exp

  case class LtEq(lhs: Exp, rhs: Exp) extends Exp

  case class OrCond(lhs: Exp, rhs: Exp) extends Exp

  case class AndCond(lhs: Exp, rhs: Exp) extends Exp

}
