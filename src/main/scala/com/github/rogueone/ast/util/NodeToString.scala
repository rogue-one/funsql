package com.github.rogueone.ast.util

import com.github.rogueone.ast.Nodes.Sql.{Query, Select, SubQuery}
import com.github.rogueone.ast._
import com.github.rogueone.ast.Nodes._

object NodeToString {

  def stringify[T <: Nodes.Node : NodePrinter](node: T): String = {
    implicitly[NodePrinter[T]].string(node)
  }

  sealed trait NodePrinter[T <: Nodes.Node]  { def string(t: T): String }

  implicit lazy val expPrinter: NodePrinter[Exp] = new NodePrinter[Exp] {
    override def string(t: Exp): String = {
      t match {
        case x: BaseLiteral => stringify(x)
        case x: Identifier => stringify(x)
        case x: Function => stringify(x)
        case x: BinaryOperator => stringify(x)
        case x: UnaryOperator => stringify(x)
        case x: SelectExpression => stringify(x)
        case x: Predicate => stringify(x)
      }
    }

  }

  implicit lazy val predicatePrinter: NodePrinter[Predicate] = new NodePrinter[Predicate] {
    override def string(t: Predicate): String = {
      t match {
        case x: BinaryOperator => stringify[BinaryOperator](x)
        case x: UnaryOperator => stringify[UnaryOperator](x)
      }
    }
  }

  implicit lazy val functionPrinter: NodePrinter[Function] = new NodePrinter[Function] {
    override def string(t: Function): String = {
      s"${t.name}(${t.exp.map(x => stringify(x)).mkString(",")})"
    }
  }

  implicit lazy val baseLiteralPrinter: NodePrinter[BaseLiteral] = new NodePrinter[BaseLiteral] {
    override def string(t: BaseLiteral): String = {
      t match {
        case x: IntegerLiteral => x.value
        case x: DecimalLiteral => x.value
        case x: StringLiteral => x.value
        case x: DateLiteral => s"DATE '${x.value}'"
        case x: TimestampLiteral => s"TIMESTAMP '${x.value}'"
      }
    }
  }

  implicit lazy val identifierPrinter: NodePrinter[Identifier]  = new NodePrinter[Identifier] {
    override def string(t: Identifier): String = t match {
      case Nodes.Identifier(value, Some(prefix)) => s"`$prefix`.`$value`"
      case Nodes.Identifier(value, None) => s"`$value`"
    }
  }
  
  implicit lazy val binaryOperatorPrinter: NodePrinter[BinaryOperator] = new NodePrinter[BinaryOperator] {
    override def string(t: BinaryOperator): String = {
      t match {
        case x: Add => s"(${stringify(x.lhs)} + ${stringify(x.rhs)})"
        case x: Sub => s"(${stringify(x.lhs)} - ${stringify(x.rhs)})"
        case x: Mul => s"(${stringify(x.lhs)} * ${stringify(x.rhs)})"
        case x: Div => s"(${stringify(x.lhs)} / ${stringify(x.rhs)})"
        case x: Eq => s"(${stringify(x.lhs)} = ${stringify(x.rhs)})"
        case x: NtEq => s"(${stringify(x.lhs)} != ${stringify(x.rhs)})"
        case x: Lt => s"(${stringify(x.lhs)} < ${stringify(x.rhs)})"
        case x: Gt => s"(${stringify(x.lhs)} > ${stringify(x.rhs)})"
        case x: GtEq => s"(${stringify(x.lhs)} >= ${stringify(x.rhs)})"
        case x: LtEq => s"(${stringify(x.lhs)} <= ${stringify(x.rhs)})"
        case x: OrCond => s"(${stringify(x.lhs)} OR ${stringify(x.rhs)})"
        case x: AndCond => s"(${stringify(x.lhs)} AND ${stringify(x.rhs)})"
        case x: InClause => s"(${stringify[Exp](x.lhs)} IN (${x.rhs.map(y => stringify[Exp](y)).mkString(",")}))"
        case x: SqlInClause => s"(${stringify(x.lhs)} IN (${x.rhs}))"
      }
    }
  }

  implicit lazy val UnaryOperatorPrinter: NodePrinter[UnaryOperator]  = new NodePrinter[UnaryOperator] {
    override def string(t: UnaryOperator): String = {
      t match {
        case x: NotCond => s"NOT (${stringify(x.arg)})"
      }
    }
  }

  implicit lazy val selectExpression: NodePrinter[SelectExpression] = new NodePrinter[SelectExpression] {
    override def string(t: SelectExpression): String = {
      val result = s"""SELECT
                    |${t.columns.map(x => stringify(x)).mkString(",")}
                    |FROM ${stringify(t.relation)}
                    |${t.where.map(x => "WHERE "+stringify(x)).getOrElse("")}
                    |${groupBy(t.groupBy)}""".stripMargin
      result.split("\n").filterNot(_.trim == "").mkString("\n")
    }
    protected def groupBy(columns: Seq[Exp]): String = {
      columns.toList match {
        case Nil => ""
        case list => s"GROUP BY ${list.map(x => stringify(x)).mkString(",")}"
      }
    }
  }


  implicit lazy val projections: NodePrinter[Projection] = new NodePrinter[Projection] {
    override def string(t: Projection): String = {
      t match {
        case Star => "*"
        case ColumnNode(exp, Some(x)) => s"${stringify(exp)} AS $x"
        case ColumnNode(exp, None) => s"${stringify(exp)}"
      }
    }
  }

  implicit lazy val relation: NodePrinter[Relation] = new NodePrinter[Relation] {
    override def string(t: Relation): String = {
      t match {
        case TableNode(name, Some(x)) => s"$name AS $x"
        case TableNode(name, None) => name
        case x: JoinedRelation => joinedRelation(x)
        case SubQuery(select, Some(alias)) => s"( ${stringify(select)} ) $alias"
        case SubQuery(select, None) => s"( ${stringify(select)} )"
      }
    }
    protected def join(r1: Relation,
                       r2: Relation,
                       join: String,
                       onCondition: Option[Predicate]): String = {
      s"""|${stringify(r1)}
          |$join
          |${stringify(r2)}
          |${onCondition.map(x => s"ON ${stringify(x)}").getOrElse("")}""".stripMargin
    }
    protected def joinedRelation(relation: JoinedRelation): String = {
      val result = relation match {
        case JoinedRelation(r1, InnerJoin(r2, condition), _) => join(r1, r2, "INNER JOIN", condition)
        case JoinedRelation(r1, LeftJoin(r2, condition), _) => join(r1, r2, "LEFT OUTER JOIN", Some(condition))
        case JoinedRelation(r1, RightJoin(r2, condition), _) => join(r1, r2, "RIGHT OUTER JOIN", Some(condition))
        case JoinedRelation(r1, FullJoin(r2, condition), _) => join(r1, r2, "FULL OUTER JOIN", Some(condition))
        case JoinedRelation(r1, CrossJoin(r2), _) => join(r1, r2, "FULL OUTER JOIN", None)
      }
      result.split(System.lineSeparator).filterNot(_.trim == "").mkString(System.lineSeparator)
    }
  }

  implicit lazy val query: NodePrinter[Query] = new NodePrinter[Query] {
    override def string(t: Query): String = {
      t match {
        case x: SubQuery => stringify[Relation](x)
        case Select(select, Some(limit)) => s"${stringify(select)} LIMIT ${limit.value}"
        case Select(select, None) => stringify(select)
      }
    }
  }

}