package com.github.rogueone.ast.validator

import com.github.rogueone.ast.Nodes
import com.github.rogueone.ast.Nodes.{ColumnNode, Identifier, Sql, TableNode}
import com.github.rogueone.ast.semantic.{QueryAnalyzer, SemanticAnalyzer}
import com.github.rogueone.data.Database
import com.github.rogueone.utils.SemanticException

import scala.util.{Failure, Success, Try}


class ProjectionFieldValidator extends SemanticAnalyzer {

  override def analyze(query: Sql.Query): Try[Unit] = {
    val tables = QueryAnalyzer.getTables(query)
    query match {
      case x: Sql.Select => validate(x.select.columns, tables)
      case _ => Success(())
    }
  }

  protected def validate(columns: Seq[Nodes.Projection], tables: Seq[TableNode]): Try[Unit] = {
    columns
      .collect({ case ColumnNode(x: Nodes.Identifier, _) => processColumnNode(x, tables)})
      .foldLeft[Try[Unit]](Success(()))({ case (_: Success[Unit], node: Failure[Unit]) => node case (acc, _) => acc})
  }

  protected def processColumnNode(identifier: Identifier, tables: Seq[TableNode]): Try[Unit] = {
    identifier match {
      case Nodes.Identifier(colName, Some(prefix)) =>
        tables.find(_.getAliasName.contains(prefix)) match {
          case Some(x: TableNode) => Try(Database.getTable(x.name).getColumnByName(colName))
          case None => throw new SemanticException(s"alias $prefix is not found")
        }
      case Nodes.Identifier(colName, None) =>
        tables.map(x => Database.getTable(x.name))
          .map(x => Try(x.getColumnByName(colName)))
          .collectFirst({ case Success(x) => x }) match {
          case Some(_) => Success(())
          case None => throw new SemanticException(s"column $colName not found")
        }
    }
  }

}
