package com.github.rogueone.ast

import com.github.rogueone.ast.Nodes.{Predicate, Relation}

/**
  * base abstract join type
  */
sealed trait Join { val relation: Relation }

/**
  * Inner Join
  * @param relation
  * @param condition
  */
case class InnerJoin(relation: Relation, condition: Option[Predicate]) extends Join

/**
  * Left Join
  * @param relation
  * @param condition
  */
case class LeftJoin(relation: Relation, condition: Predicate) extends Join

/**
  * Right Join
  * @param relation
  * @param condition
  */
case class RightJoin(relation: Relation, condition: Predicate) extends Join

/**
  * Full Join
  * @param relation
  * @param condition
  */
case class FullJoin(relation: Relation, condition: Predicate) extends Join

/**
  * Cross Join
  * @param relation
  */
case class CrossJoin(relation: Relation) extends Join
