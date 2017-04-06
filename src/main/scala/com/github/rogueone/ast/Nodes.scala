package com.github.rogueone.ast

import java.util.Date

/**
  * Created by chlr on 3/26/17.
  */
object Nodes {

  sealed trait Node

  sealed trait Exp

  case class IntegerLiteral(value: Long) extends Exp

  case class DecimalLiteral(value: Double) extends Exp

  case class StringLiteral(value: String) extends Exp

  case class DateLiteral(value: Date) extends Exp

  case class Identifier(value: String) extends Exp

  case class Function(name: Identifier, exp: Seq[Exp]) extends Exp

  trait Operator extends Exp {
    def lhs: Exp
    def rhs: Exp
  }

  case class Add(override val lhs: Exp, override val rhs: Exp) extends Operator

  case class Sub(override val lhs: Exp, override val rhs: Exp) extends Operator

  case class Mul(override val lhs: Exp, override val rhs: Exp) extends Operator

  case class Div(override val lhs: Exp, override val rhs: Exp) extends Operator


  case class Eq(lhs: Exp, rhs: Exp) extends Operator

  case class NtEq(lhs: Exp, rhs: Exp) extends Operator

  case class Lt(lhs: Exp, rhs: Exp) extends Operator

  case class Gt(lhs: Exp, rhs: Exp) extends Operator

  case class GtEq(lhs: Exp, rhs: Exp) extends Operator

  case class LtEq(lhs: Exp, rhs: Exp) extends Operator


  case class OrCond(lhs: Exp, rhs: Exp) extends Operator

  case class AndCond(lhs: Exp, rhs: Exp) extends Operator

}
