package com.github.rogueone.ast

import com.github.rogueone.ast.Nodes.Sql


class PlanBuilder(query: Sql.SelectExpression) {

  def getPlan = {
//    query.relation match {
//      case x: Nodes.Table => x
//    }
  }

}
