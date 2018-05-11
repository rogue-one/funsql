package com.github.rogueone.work

import com.github.rogueone.ast.Nodes
import com.github.rogueone.data.DataValue

trait RowEvaluator {

  self: RelationSelector =>

  def evaluate(exp: Nodes.Exp, row: Array[DataValue]) = {
//    exp match {
//      case
//    }
  }



}
