package com.github.rogueone.parser

import com.github.rogueone.ast.Nodes
import fastparse.noApi._
import com.github.rogueone.parser.Parser.White._

object Queries {

  val select: P[(String, Nodes.Identifier)] = P(Keyword.Select.parser ~ Parser.expression.rep(min=1, sep = ",").! ~
    Keyword.From.parser ~ Primitives.identifier ~ Keyword.Where.parser)
}
