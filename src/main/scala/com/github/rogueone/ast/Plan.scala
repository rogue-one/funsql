package com.github.rogueone.ast

import com.github.rogueone.ast.Nodes.Predicate
import com.github.rogueone.utils.Utils


sealed abstract class Plan(next: Option[Plan])

object Plan {

  case class Relation(id: String)

  case class LoadTable(tableName: String, projection: Seq[Nodes.Exp], next: Option[Plan]) extends Plan(next) {
    val relationId: Relation = Relation(Utils.uuid)
  }

  case class Filter(relation: Relation, predicate: Predicate, next: Option[Plan]) extends Plan(next)

  case class Join(left: Relation, right: Relation, conditions: Predicate, next: Option[Plan]) extends Plan(next) {
    val relationId: Relation = Relation(Utils.uuid)
  }

  case class GroupBy(relation: Relation, groupFields: Seq[Nodes.Exp], aggFields: Seq[Nodes.Exp],
                     next: Option[Plan]) extends Plan(next)

  case class Fetch(relation: Relation, limit: Option[Long], next: Option[Plan]) extends Plan(next)

}
