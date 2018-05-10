package com.github.rogueone.ast

import java.text.SimpleDateFormat
import java.util.Date

import scala.util.{Success, Try}

/**
  * Created by chlr on 3/26/17.
  */
object Nodes {

  sealed trait Node

  sealed trait Aliasable extends Node { def alias: Option[String] }

  sealed trait Relation extends Aliasable

  sealed trait Projection extends Aliasable

  sealed trait Exp extends Node

  sealed trait Literal extends Exp

  sealed abstract class BaseLiteral[T](value: String) extends Literal {
    val data: Try[T]
    def parsed: Boolean = data.isSuccess
  }

  case class IntegerLiteral(value: String) extends BaseLiteral[Long](value) {
    val data: Try[Long] = Try(value.toLong)
  }

  case class DecimalLiteral(value: String) extends BaseLiteral[Double](value) {
    val data: Try[Double] = Try(value.toDouble)
  }

  case class StringLiteral(value: String) extends BaseLiteral[String](value) {
    val data: Try[String] = Success(value)
  }

  case class DateLiteral(value: String) extends BaseLiteral[Date](value) {
    val sdf = new SimpleDateFormat("yyyy-MM-dd")
    sdf.setLenient(false)
    val data: Try[Date] = Try(sdf.parse(value))
  }

  case class TimestampLiteral(value: String) extends BaseLiteral[Date](value) {
    val sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    sdf.setLenient(false)
    val data: Try[Date] = Try(sdf.parse(value))
  }

  case class Identifier(value: String) extends Exp

  case class Function(name: Identifier, exp: Seq[Exp]) extends Exp

  case class Add(lhs: Exp, rhs: Exp) extends Exp

  case class Sub(lhs: Exp, rhs: Exp) extends Exp

  case class Mul(lhs: Exp, rhs: Exp) extends Exp

  case class Div(lhs: Exp, rhs: Exp) extends Exp

  trait Predicate extends Exp

  case class Eq(lhs: Exp, rhs: Exp) extends Predicate

  case class NtEq(lhs: Exp, rhs: Exp) extends Predicate

  case class Lt(lhs: Exp, rhs: Exp) extends Predicate

  case class Gt(lhs: Exp, rhs: Exp) extends Predicate

  case class GtEq(lhs: Exp, rhs: Exp) extends Predicate

  case class LtEq(lhs: Exp, rhs: Exp) extends Predicate

  case class OrCond(lhs: Predicate, rhs: Predicate) extends Predicate

  case class AndCond(lhs: Predicate, rhs: Predicate) extends Predicate

  case class NotCond(exp: Predicate) extends Predicate

  case class InClause(lhs: Exp, rhs: Seq[Exp]) extends Predicate

  case class SubQuery(lhs: Exp, rhs: Sql.BasicSelect) extends Predicate

  case class Table(name: String, alias: Option[String]=None) extends Relation with Aliasable

  case class Column(exp: Exp, alias: Option[String]=None) extends Projection

  case object Star extends Projection { val alias: Option[String] = None }

  case class JoinedRelation(relation: Relation, join: Join) extends Relation { val alias: Option[String]=None }

  object Sql {
    case class BasicSelect(columns: Seq[Nodes.Projection], table: Nodes.Relation,
                           where: Option[Predicate], groupBy: Seq[Nodes.Exp]) extends Exp

    case class SelectRelation(select: BasicSelect, alias: Option[String]) extends Relation

    case class Select(select: BasicSelect, limit: Option[IntegerLiteral]) extends Node
  }

}
