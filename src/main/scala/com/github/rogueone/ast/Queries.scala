package com.github.rogueone.ast

import com.github.rogueone.ast.Nodes.Exp

/**
  * Created by chlr on 4/2/17.
  */
object Queries {

  case class Select(columns: Seq[Nodes.Exp], table: Nodes.Identifier, where: Exp)
}
