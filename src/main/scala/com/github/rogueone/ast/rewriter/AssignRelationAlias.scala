package com.github.rogueone.ast.rewriter

import com.github.rogueone.ast.Nodes
import com.github.rogueone.ast.Nodes.Sql
import com.github.rogueone.data.DatabaseLike
import com.github.rogueone.ast.util.ASTUtil.QueryUtil

trait AssignRelationAlias extends QueryRewriteStrategy {

  private var numberStream: Stream[Int] = (0 until Int.MaxValue).toStream

  abstract override def rewrite(query: Sql.Query, database: DatabaseLike): Sql.Query = {
    super.rewrite(setAlias(query), database)
  }

  def getAlias: String = {
    val head #:: tail = numberStream
    numberStream = tail
    s"t$head"
  }

  protected def getAliasName(query: Sql.Query): String = {
    val queryAlias = query.outerRelations.map(_.getAliasName).collect({case Some(x) => x}).toSet
    val alias = getAlias
    if(queryAlias(alias)) getAliasName(query) else alias
  }

  /**
    * parse query to list all tables in a query
    * @return
    */
  protected def setAlias(query: Sql.Query): Sql.Query = {
    def parseQuery(relation: Nodes.Relation): Unit = {
      relation match {
        case x: Sql.SubQuery => x.setAliasName(getAliasName(query)); parseQuery(x.select.relation)
        case x: Nodes.TableNode => x.setAliasName(getAliasName(query))
        case x: Nodes.JoinedRelation =>
          x.setAliasName(getAliasName(query)); parseQuery(x.relation); x.join.relation.setAliasName(getAliasName(query))
          parseQuery(x.join.relation)
      }
    }
    parseQuery(query.select.relation)
    query
  }

}