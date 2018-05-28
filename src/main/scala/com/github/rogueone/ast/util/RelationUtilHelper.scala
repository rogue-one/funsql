package com.github.rogueone.ast.util

import com.github.rogueone.ast.Nodes
import com.github.rogueone.ast.Nodes._
import com.github.rogueone.ast.util.ASTUtil.RelationUtil
import com.github.rogueone.data.Database

trait RelationUtilHelper {

  protected val relation: Relation

  /**
    * get projection from relation
    * @return
    */
  def projection: Seq[Nodes.Projection] = {
    relation match {
      case query: Sql.SubQuery => query.select.columns
        .map({case x: Nodes.ColumnNode => x.copy(alias=query.getAliasName) case _ => ???})
      case table: TableNode => Database.getTable(table.name).fields
        .map(x => ColumnNode(Identifier(x.name, prefix = table.getAliasName)))
      case joinedRelation: JoinedRelation => new RelationUtil(joinedRelation.relation).projection ++
        new RelationUtil(joinedRelation.join.relation).projection
    }
  }

  /**
    * get all tables in a [[Relation]]
    * @return
    */
  def tables: List[Nodes.TableNode] = {
    relation match {
      case x: Nodes.TableNode => x :: Nil
      case x: JoinedRelation => x.relation.tables ++ x.join.relation.tables
      case x: Sql.SubQuery => x.select.relation.tables
    }
  }

}
