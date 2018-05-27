package com.github.rogueone.ast

import com.github.rogueone.ast.Nodes._
import com.github.rogueone.data.Database

object ASTUtil {

  implicit class RelationUtil(relation: Relation) {

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

  }

}
