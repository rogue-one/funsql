package com.github.rogueone.ast

import com.github.rogueone.ast.Nodes.Predicate

sealed trait Join

case class InnerJoin(condition: Option[Predicate]) extends Join

case class LeftJoin(condition: Predicate) extends Join

case class RightJoin(condition: Predicate) extends Join

case class FullJoin(condition: Predicate) extends Join

object CrossJoin extends Join
