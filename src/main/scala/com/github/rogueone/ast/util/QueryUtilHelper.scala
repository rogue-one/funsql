package com.github.rogueone.ast.util

import com.github.rogueone.ast.Nodes
import com.github.rogueone.ast.Nodes.{JoinedRelation, Relation, Sql}

trait QueryUtilHelper {

  protected val query: Sql.Query

  /**
    * get the outer relations in the query.
    * ie for eg if one relation is a sub-query then only the sub-query is returned
    * and the inner relations that make up the sub-query is not returned.
    * @return
    */
  def relations: List[Relation] = {
    def get(relation: Relation): List[Relation] = {
      relation match {
        case x: Nodes.TableNode => x :: Nil
        case x: JoinedRelation => get(x.relation) ++ get(x.join.relation)
        case x: Sql.SubQuery => x :: Nil
      }
    }
    get(query.select.relation)
  }

}
