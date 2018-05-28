package com.github.rogueone.ast

import com.github.rogueone.ast.Nodes.Sql.{Query, Select, SubQuery}
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


  implicit class QueryUtil(query: Query) {

    def relations: List[Relation] = {
      def get(relation: Relation): List[Relation] = {
        relation match {
          case x: Nodes.TableNode => x :: Nil
          case x: JoinedRelation => get(x.relation) ++ get(x.join.relation)
          case x: Sql.SubQuery => x :: Nil
        }
      }
      query match {
        case x: Sql.SubQuery => get(x.select.relation)
        case x: Sql.Select => get(x.select.relation)
      }
    }

  }

}
