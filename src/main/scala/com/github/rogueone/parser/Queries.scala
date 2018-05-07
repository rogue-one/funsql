package com.github.rogueone.parser

import fastparse.noApi._
import com.github.rogueone.parser.Parser.White._

object Queries {

  val select = P(Keyword.Select.parser ~ MathParser.mathExp.rep(min=1, sep = ",").! ~ Keyword.From.parser )
}
