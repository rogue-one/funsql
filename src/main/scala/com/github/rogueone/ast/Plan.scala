package com.github.rogueone.ast

import com.github.rogueone.ast.Nodes.Predicate
import com.github.rogueone.utils.Utils


sealed abstract class Plan(parent: Seq[Plan])

object Plan {

  case class Spool(name: String)

  case class LoadTable(tableName: String, alias: String, projection: Seq[Nodes.Column], parent: Seq[Plan])
    extends Plan(parent) {
    val relationId: Spool = Spool(alias)
  }

  case class Filter(relation: Spool, predicate: Predicate, parent: Seq[Plan]) extends Plan(parent)

  case class InnerJoinSpool(left: Spool, right: Spool, predicate: Option[Predicate], parent: Seq[Plan])
    extends Plan(parent) { val relationId: Spool = Spool(Utils.uuid) }

  case class LeftJoinSpool(left: Spool, right: Spool, predicate: Predicate, parent: Seq[Plan])
    extends Plan(parent) { val relationId: Spool = Spool(Utils.uuid) }

  case class RightJoinSpool(left: Spool, right: Spool, predicate: Predicate, parent: Seq[Plan])
    extends Plan(parent) { val relationId: Spool = Spool(Utils.uuid) }

  case class FullJoinSpool(left: Spool, right: Spool, predicate: Predicate, parent: Seq[Plan])
    extends Plan(parent) { val relationId: Spool = Spool(Utils.uuid) }

  case class CrossJoin(left: Spool, right: Spool, parent: Seq[Plan])
    extends Plan(parent) { val relationId: Spool = Spool(Utils.uuid) }

  case class GroupBy(relation: Spool, groupFields: Seq[Nodes.Exp], aggFields: Seq[Nodes.Exp],
                     parent: Seq[Plan]) extends Plan(parent)

  case class Fetch(relation: Spool, limit: Option[Long], parent: Seq[Plan]) extends Plan(parent)

}
