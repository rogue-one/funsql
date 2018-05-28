package com.github.rogueone.ast

import java.text.SimpleDateFormat
import java.util.Date
import scala.util.{Success, Try}

/**
  * Created by chlr on 3/26/17.
  */
object Nodes {

  sealed trait Node

  sealed trait Aliasable extends Node {

    protected var alias: Option[String]

    /**
      * set alias if not already sey
      * @param name
      */
    def setAliasName(name: String): Unit = alias match { case Some(_) => () case None => alias = Some(name) }

    def getAliasName: Option[String] = alias

  }

  sealed trait Relation extends Aliasable

  sealed trait Projection extends Aliasable

  sealed trait  Exp extends Node

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

  case class Identifier(value: String, var prefix: Option[String]=None) extends Exp

  case class Function(name: Identifier, exp: Seq[Exp]) extends Exp

  sealed trait BinaryOperator extends Exp { val lhs: Exp; val rhs: Exp }

  sealed trait UnaryOperator extends Exp { val arg: Exp }

  case class Add(lhs: Exp, rhs: Exp) extends BinaryOperator

  case class Sub(lhs: Exp, rhs: Exp) extends BinaryOperator

  case class Mul(lhs: Exp, rhs: Exp) extends BinaryOperator

  case class Div(lhs: Exp, rhs: Exp) extends BinaryOperator

  trait Predicate extends Exp

  case class Eq(lhs: Exp, rhs: Exp) extends Predicate with BinaryOperator

  case class NtEq(lhs: Exp, rhs: Exp) extends Predicate with BinaryOperator

  case class Lt(lhs: Exp, rhs: Exp) extends Predicate with BinaryOperator

  case class Gt(lhs: Exp, rhs: Exp) extends Predicate with BinaryOperator

  case class GtEq(lhs: Exp, rhs: Exp) extends Predicate with BinaryOperator

  case class LtEq(lhs: Exp, rhs: Exp) extends Predicate with BinaryOperator

  case class OrCond(lhs: Predicate, rhs: Predicate) extends Predicate with BinaryOperator

  case class AndCond(lhs: Predicate, rhs: Predicate) extends Predicate with BinaryOperator

  case class NotCond(arg: Predicate) extends Predicate with UnaryOperator

  case class InClause(lhs: Exp, rhs: Seq[Exp]) extends Predicate

  case class SqlInClause(lhs: Exp, rhs: Nodes.SelectExpression) extends Predicate

  case class TableNode(name: String, protected var alias: Option[String]=None) extends Relation with Aliasable

  case class ColumnNode(exp: Exp, var alias: Option[String]=None) extends Projection

  case object Star extends Projection { var alias: Option[String] = None }

  case class JoinedRelation(relation: Relation, join: Join) extends Relation { var alias: Option[String]=None }

  /**
    * The Basic version is not a [[Relation]] << insert reason here >>
    * @param columns
    * @param relation
    * @param where
    * @param groupBy
    */
  case class SelectExpression(columns: Seq[Nodes.Projection], relation: Nodes.Relation,
                              where: Option[Predicate], groupBy: Seq[Nodes.Exp]) extends Exp

  object Sql {

    sealed trait Query

    /**
      * A paranthesesed query with alias
      * @param select
      * @param alias
      */
    case class SubQuery(select: SelectExpression, protected var alias: Option[String]) extends Relation with Query

    /**
      * Select Query
      * @param select
      * @param limit
      */
    case class Select(select: SelectExpression, limit: Option[IntegerLiteral]) extends Node with Query

  }

}
