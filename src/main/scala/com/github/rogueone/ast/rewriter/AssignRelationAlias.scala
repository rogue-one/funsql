package com.github.rogueone.ast.rewriter

import com.github.rogueone.ast.Nodes
import com.github.rogueone.ast.Nodes.Sql
import com.github.rogueone.data.DatabaseLike
import com.github.rogueone.utils.Utils

class AssignRelationAlias extends QueryRewriter {

  override def rewrite(query: Sql.Query, database: DatabaseLike): Sql.Query = { setAlias(query); query }

  protected def getAliasName: String = Utils.uuid

  /**
    * parse query to list all tables in a query
    * @return
    */
  protected def setAlias(query: Sql.Query): Unit = {
    def parseQuery(relation: Nodes.Relation): Unit = {
      relation match {
        case x: Sql.SubQuery => x.setAliasName(getAliasName); parseQuery(x.select.relation)
        case x: Nodes.TableNode => x.setAliasName(getAliasName)
        case x: Nodes.JoinedRelation =>
          x.setAliasName(getAliasName); parseQuery(x.relation); x.join.relation.setAliasName(getAliasName)
          parseQuery(x.join.relation)
      }
    }
    query match {
      case x: Sql.Select => parseQuery(x.select.relation)
      case x: Sql.SubQuery => parseQuery(x.select.relation)
      case _ => ()
    }
  }
}

object AssignRelationAlias extends AssignRelationAlias
