package com.github.rogueone.ast

import java.util.Date

/**
  * Created by chlr on 3/26/17.
  */
object Nodes {

  sealed trait Node

  sealed trait Exp {
    def resolve: Exp
  }

  case class IntegerLiteral(value: Long) extends Exp {
    def resolve = this
  }

  case class DecimalLiteral(value: Double) extends Exp {
    def resolve = this
  }

  case class StringLiteral(value: String) extends Exp {
    def resolve = this
  }

  case class DateLiteral(value: Date) extends Exp {
    def resolve = this
  }

  case class Identifier(value: String) extends Exp {
    def resolve = this
  }

  trait Operator extends Exp {
    def lhs: Exp
    def rhs: Exp
  }

  case class Add(override val lhs: Exp, override val rhs: Exp) extends Operator {

    def resolve: Exp = lhs.resolve -> rhs.resolve match {
      case (IntegerLiteral(x), IntegerLiteral(y)) => IntegerLiteral(x + y)
      case (IntegerLiteral(x), DecimalLiteral(y)) => DecimalLiteral(x + y)
      case (DecimalLiteral(x), IntegerLiteral(y)) => DecimalLiteral(x + y)
      case (DecimalLiteral(x), DecimalLiteral(y)) => DecimalLiteral(x + y)
      case (Identifier(_), Identifier(_)) => this
      case (x: Operator, Identifier(_)) => this
    }

  }

  case class Sub(override val lhs: Exp, override val rhs: Exp) extends Operator {

    def resolve: Exp = lhs.resolve -> rhs.resolve match {
      case (IntegerLiteral(x), IntegerLiteral(y)) => IntegerLiteral(x - y)
      case (IntegerLiteral(x), DecimalLiteral(y)) => DecimalLiteral(x - y)
      case (DecimalLiteral(x), IntegerLiteral(y)) => DecimalLiteral(x - y)
      case (DecimalLiteral(x), DecimalLiteral(y)) => DecimalLiteral(x - y)
      case (Identifier(_), Identifier(_)) => this
      case (x: Operator, Identifier(_)) => this
    }

  }

  case class Mul(override val lhs: Exp, override val rhs: Exp) extends Operator {

    def resolve: Exp = lhs.resolve -> rhs.resolve match {
      case (IntegerLiteral(x), IntegerLiteral(y)) => IntegerLiteral(x - y)
      case (IntegerLiteral(x), DecimalLiteral(y)) => DecimalLiteral(x - y)
      case (DecimalLiteral(x), IntegerLiteral(y)) => DecimalLiteral(x - y)
      case (DecimalLiteral(x), DecimalLiteral(y)) => DecimalLiteral(x - y)
      case (Identifier(_), Identifier(_)) => this
      case (x: Operator, Identifier(_)) => this
    }

  }

  case class Div(override val lhs: Exp, override val rhs: Exp) extends Operator {
    def resolve: Exp = lhs.resolve -> rhs.resolve match {
      case (IntegerLiteral(x), IntegerLiteral(y)) => IntegerLiteral(x - y)
      case (IntegerLiteral(x), DecimalLiteral(y)) => DecimalLiteral(x - y)
      case (DecimalLiteral(x), IntegerLiteral(y)) => DecimalLiteral(x - y)
      case (DecimalLiteral(x), DecimalLiteral(y)) => DecimalLiteral(x - y)
      case (Identifier(_), Identifier(_)) => this
      case (x: Operator, Identifier(_)) => this
    }
  }


  trait Predicate

  trait PredicateOperator extends Predicate {
    def lhs: Exp
    def rhs: Exp
  }

  case class Eq(lhs: Exp, rhs: Exp) extends PredicateOperator

  case class Lt(lhs: Exp, rhs: Exp) extends PredicateOperator

  case class Gt(lhs: Exp, rhs: Exp) extends PredicateOperator

  case class GtEq(lhs: Exp, rhs: Exp) extends PredicateOperator

  case class LtEq(lhs: Exp, rhs: Exp) extends PredicateOperator

  sealed trait Condition extends Predicate {
    def left: Predicate
    def right: Predicate
  }

  case class OrCondition(override val left: Predicate, override val right: Predicate) extends Condition

  case class AndCondition(override val left: Predicate, override val right: Predicate) extends Condition

}
