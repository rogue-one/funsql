package com.github.rogueone.parser

import com.github.rogueone.ast
import com.github.rogueone.ast.Nodes
import fastparse.all._
import fastparse.core
import Primitives._
import com.github.rogueone.ast.Nodes.TimestampLiteral

object LiteralParser {

  val numberLiteral: core.Parser[Nodes.IntegerLiteral, Char, String] =
    P(((plus | minus).? ~ number.rep(min=1)).!).map(x => ast.Nodes.IntegerLiteral(x))

  val decimalLiteral: core.Parser[Nodes.DecimalLiteral, Char, String] =
    P(((plus | minus).? ~ number.rep ~ decimal ~ number.rep).!).map(ast.Nodes.DecimalLiteral)

  val stringLiteral: core.Parser[Nodes.StringLiteral, Char, String] =
    P("'" ~ CharsWhile(_ != '\'').rep.! ~ "'").map(ast.Nodes.StringLiteral)

  val dateLiteral: core.Parser[Nodes.DateLiteral, Char, String] = P(
    IgnoreCase("DATE") ~ whitespace.rep ~ "'" ~/ (number.rep(exactly = 4) ~ "-" ~ number.rep(exactly = 2) ~
      "-" ~ number.rep(exactly = 2)).! ~ "'"
  ).map(x => ast.Nodes.DateLiteral(x))

  val timestampLiteral: core.Parser[Nodes.TimestampLiteral, Char, String] = P(IgnoreCase("TIMESTAMP") ~
    whitespace.rep ~ "'" ~/ (number.rep(exactly = 4) ~ "-" ~ number.rep(exactly = 2) ~ "-" ~
    number.rep(exactly = 2)).! ~ whitespace.rep ~ (number.rep(exactly=2) ~ ":" ~ number.rep(exactly=2) ~ ":" ~
    number.rep(exactly=2)).! ~ "'").map({case (x,y) => TimestampLiteral(s"$x $y")})

  val literal: P[ast.Nodes.Literal] = decimalLiteral | numberLiteral | stringLiteral | dateLiteral | timestampLiteral

}
