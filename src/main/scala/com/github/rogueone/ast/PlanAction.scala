package com.github.rogueone.ast

import com.github.rogueone.ast.Nodes.Predicate
import com.github.rogueone.utils.Utils

/**
  * Base Plan Action
  * @param parents
  */
sealed abstract class PlanAction(parents: Seq[PlanAction])

object PlanAction {

  case class Spool(name: String)

  /**
    * Load Table into Spool
    * @param tableName table name
    * @param alias name of the spool
    * @param projection projection to be used in spool
    * @param parents
    */
  case class LoadTable(tableName: String, alias: String, projection: Seq[Nodes.ColumnNode], parents: Seq[PlanAction])
    extends PlanAction(parents) {
    val relationId: Spool = Spool(alias)
  }

  /**
    * Filter a spool
    * @param relation relation
    * @param predicate
    * @param parents
    */
  case class Filter(relation: Spool, predicate: Predicate, parents: Seq[PlanAction]) extends PlanAction(parents)

  /**
    * Inner join spool
    * @param left left spool
    * @param right right spool
    * @param predicate join condition
    * @param parents
    */
  case class InnerJoinSpool(left: Spool, right: Spool, predicate: Option[Predicate], parents: Seq[PlanAction])
    extends PlanAction(parents) { val relationId: Spool = Spool(Utils.uuid) }

  /**
    * Left join spool
    * @param left
    * @param right
    * @param predicate
    * @param parents
    */
  case class LeftJoinSpool(left: Spool, right: Spool, predicate: Predicate, parents: Seq[PlanAction])
    extends PlanAction(parents) { val relationId: Spool = Spool(Utils.uuid) }

  /**
    *
    * @param left
    * @param right
    * @param predicate
    * @param parents
    */
  case class RightJoinSpool(left: Spool, right: Spool, predicate: Predicate, parents: Seq[PlanAction])
    extends PlanAction(parents) { val relationId: Spool = Spool(Utils.uuid) }

  /**
    *
    * @param left
    * @param right
    * @param predicate
    * @param parents
    */
  case class FullJoinSpool(left: Spool, right: Spool, predicate: Predicate, parents: Seq[PlanAction])
    extends PlanAction(parents) { val relationId: Spool = Spool(Utils.uuid) }

  /**
    *
    * @param left
    * @param right
    * @param parents
    */
  case class CrossJoin(left: Spool, right: Spool, parents: Seq[PlanAction])
    extends PlanAction(parents) { val relationId: Spool = Spool(Utils.uuid) }

  /**
    *
    * @param relation
    * @param groupFields
    * @param aggFields
    * @param parent
    */
  case class GroupBy(relation: Spool, groupFields: Seq[Nodes.Exp], aggFields: Seq[Nodes.Exp],
                     parent: Seq[PlanAction]) extends PlanAction(parent)

  /**
    *
    * @param relation
    * @param limit
    * @param parents
    */
  case class Fetch(relation: Spool, limit: Option[Long], parents: Seq[PlanAction]) extends PlanAction(parents)

}
