package com.github.rogueone.ast

import com.github.rogueone.ast.Nodes.{Predicate, Relation}

sealed trait Join

case class InnerJoin(relation: Relation, condition: Option[Predicate]) extends Join

case class LeftJoin(relation: Relation, condition: Predicate) extends Join

case class RightJoin(relation: Relation, condition: Predicate) extends Join

case class FullJoin(relation: Relation, condition: Predicate) extends Join

case class CrossJoin(relation: Relation) extends Join
