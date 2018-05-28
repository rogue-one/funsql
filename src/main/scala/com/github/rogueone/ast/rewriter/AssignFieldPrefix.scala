package com.github.rogueone.ast.rewriter

import com.github.rogueone.ast.Nodes.{ColumnNode, Relation, Sql, UnaryOperator}
import com.github.rogueone.ast._
import com.github.rogueone.ast.validator.QueryAnalyzer
import com.github.rogueone.data.DatabaseLike
import com.github.rogueone.utils.SemanticException

import scala.util.Try

class AssignFieldPrefix extends QueryRewriter {


  /**
    * get all fields/columns used
    * @param query
    * @return
    */
  def rewrite(query: Sql.Query, database: DatabaseLike): Sql.Query = {
    val tables = QueryAnalyzer.getTables(query)
    System.err.println(tables.mkString(","))
    new QueryTraverser(query, tables, database).traverse()
    query
  }


  private class QueryTraverser(query: Sql.Query, tables: List[Nodes.TableNode], database: DatabaseLike) {

    def traverse(): Unit = {
      query match {
        case x: Sql.Select => processQueryForFields(x.select)
        case x: Sql.SubQuery => processQueryForFields(x.select)
        case _ => ()
      }
    }

    private def processQueryForFields(query: Nodes.SelectExpression): Unit = {
      query.columns.foreach({ case ColumnNode(x, _) => resolveExp(x) case _ => ??? })
      query.where.foreach(resolveExp)
      query.groupBy.foreach(resolveExp)
      processRelationForFields(query.relation)
    }


    private def processRelationForFields(relation: Relation): Unit = {
      relation match {
        case x: Sql.SubQuery => processQueryForFields(x.select)
        case _: Nodes.TableNode => ()
        case x: Nodes.JoinedRelation =>
          processRelationForFields(x.relation); processRelationForFields(x.join.relation); processJoins(x.join)
      }
    }

    private def processJoins(join: Join): Unit = {
      join match {
        case x: InnerJoin => x.condition.foreach(x => resolveExp(x))
        case _: CrossJoin => ()
        case x: LeftJoin => resolveExp(x.condition)
        case x: RightJoin => resolveExp(x.condition)
        case x: FullJoin => resolveExp(x.condition)
      }
    }


    private def resolveExp(exp: Nodes.Exp): Unit = {
      exp match {
        case x: Nodes.Identifier => setFieldPrefix(x)
        case Nodes.Function(_, args) => args.foreach({case x: Nodes.Identifier => setFieldPrefix(x) case _ => ()})
        case x: Nodes.BinaryOperator => resolveExp(x.lhs); resolveExp(x.rhs)
        case x: UnaryOperator => resolveExp(x.arg)
        case Nodes.InClause(lhs, _) => resolveExp(lhs)
        case Nodes.SqlInClause(lhs, rhs) => resolveExp(lhs); processQueryForFields(rhs)
        case _ => ()
      }
    }

    private def setFieldPrefix(field: Nodes.Identifier): Unit = {
      if (field.prefix.isEmpty) {
        tables.find(x => Try(database.getTable(x.name).getColumnByName(field.value)).isSuccess) match {
          case Some(x) => field.prefix = x.getAliasName
          case None => throw new SemanticException(s"column ${field.value} is not found")
        }
      }
    }

  }

}
