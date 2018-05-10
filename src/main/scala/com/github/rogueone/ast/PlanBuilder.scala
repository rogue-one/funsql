package com.github.rogueone.ast

import com.github.rogueone.ast.Nodes.Sql

class PlanBuilder(query: Sql.BasicSelect) {

  def getPlan = {
//    query match {
//      case x: Sql.Select => Plan.LoadTable(x.table.value, x.columns, None).copy(next = )
//    }
  }

}
