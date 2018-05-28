package com.github.rogueone.ast.validator

import com.github.rogueone.ast.Nodes
import com.github.rogueone.ast.Nodes.Sql

trait TableAnalyzer {

  /**
    * parse query to list all tables in a query
    * @return
    */
  def getTables(query: Sql.Query): List[Nodes.TableNode] = {
    def parseQuery(relation: Nodes.Relation, tables: List[Nodes.TableNode]): List[Nodes.TableNode] = {
      relation match {
        case x: Sql.SubQuery => parseQuery(x.select.relation, tables) ++ tables
        case x: Nodes.TableNode => x :: tables
        case x: Nodes.JoinedRelation => parseQuery(x.relation,tables) ++ tables ++
          parseQuery(x.join.relation, tables)
      }
    }
    query match {
      case x: Sql.Select => parseQuery(x.select.relation, Nil)
      case x: Sql.SubQuery => parseQuery(x.select.relation, Nil)
      case _ => Nil
    }
  }

}
