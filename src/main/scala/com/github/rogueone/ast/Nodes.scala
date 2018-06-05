package com.github.rogueone.ast

import java.text.SimpleDateFormat
import java.util.Date
import com.github.rogueone.ast.util.AliasActions.{EditAlias, ReadAlias}
import scala.util.{Success, Try}

/**
  * Created by chlr on 3/26/17.
  */
object Nodes {

  sealed trait Node

  sealed trait Aliasable extends Node with ReadAlias { protected var alias: Option[String] }

  sealed trait AliasEditable extends Aliasable with EditAlias with ReadAlias

  sealed trait Relation extends AliasEditable

  sealed trait Projection extends Node

  sealed trait  Exp extends Node

  sealed trait Literal extends Exp

  sealed trait Prefixable { protected var prefix: Option[String] }

  sealed abstract class BaseLiteral(val value: String) extends Literal {
    type T
    val data: Try[T]
    def parsed: Boolean = data.isSuccess
  }

  case class IntegerLiteral(override val value: String) extends BaseLiteral(value) {
    type T = Long
    val data: Try[Long] = Try(value.toLong)
  }

  case class DecimalLiteral(override val value: String) extends BaseLiteral(value) {
    type T = Double
    val data: Try[Double] = Try(value.toDouble)
  }

  case class StringLiteral(override val value: String) extends BaseLiteral(value) {
    type T =  String
    val data: Try[String] = Success(value)
  }

  case class DateLiteral(override val value: String) extends BaseLiteral(value) {
    type T = Date
    val sdf = new SimpleDateFormat("yyyy-MM-dd")
    sdf.setLenient(false)
    val data: Try[Date] = Try(sdf.parse(value))
  }

  case class TimestampLiteral(override val value: String) extends BaseLiteral(value) {
    type T = Date
    val sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    sdf.setLenient(false)
    val data: Try[Date] = Try(sdf.parse(value))
  }

  case class Identifier(value: String, var prefix: Option[String]=None) extends Exp with Prefixable

  case class Function(name: Identifier, exp: Seq[Exp]) extends Exp

  sealed trait Operator

  sealed trait BinaryOperator extends Operator with Exp { val lhs: Exp; val rhs: Exp }

  sealed trait UnaryOperator extends Operator with Exp { val arg: Exp }

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

  case class SqlInClause(lhs: Exp, rhs: Nodes.SelectExpression) extends Predicate with BinaryOperator

  case class TableNode(name: String, protected var alias: Option[String]=None) extends Relation with Aliasable

  case class ColumnNode(exp: Exp, protected var alias: Option[String]=None) extends Projection with AliasEditable

  case class Star(prefix: Option[String]=None) extends Projection with Aliasable { protected var alias: Option[String] = None }

  case class JoinedRelation(relation: Relation, join: Join, protected var alias: Option[String]=None) extends Relation

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

    sealed trait Query extends Node { val select : SelectExpression }

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
