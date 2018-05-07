package com.github.rogueone.ast

import com.github.rogueone.ast.Nodes.{Exp, Identifier, Literal, Predicate}

/**
  * Created by chlr on 4/2/17.
  */
object Sql {

  case class Select(columns: Seq[Nodes.Exp], table: Nodes.Identifier, where: Option[Predicate], groupBy: Seq[Nodes.Exp],
                    limit: Option[Nodes.IntegerLiteral])
}
