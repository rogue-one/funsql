package com.github.rogueone.parser

import com.github.rogueone.ast
import com.github.rogueone.ast.Nodes.Exp
import fastparse.noApi._
import com.github.rogueone.parser.Parser.White._

object MathParser {

  val mathExp: P[Exp] = PredicateParser.predicate

  val parentheses: P[Exp] = P( "(" ~/ mathExp ~ ")" )

  val primary: P[Exp] = P { LiteralParser.literal  | parentheses | Parser.function | Parser.identifier }

  protected val mulDiv: P[Exp] = P( Parser.expression ~ (CharIn("*/").! ~/ Parser.expression).rep ).map({
    case (e: Exp, s: Seq[(String, Exp)]) => s.foldLeft(e) {
      case (l, (op, r)) => if(op == "*") ast.Nodes.Mul(l, r) else ast.Nodes.Div(l, r)
    }
  })

  val addSub: P[Exp] = P( mulDiv ~ (CharIn("+-").! ~/ mulDiv).rep).map({
    case (e, s) => s.foldLeft(e)({
      case (l, (op, r)) => if(op == "+") ast.Nodes.Add(l, r) else ast.Nodes.Sub(l, r)
    })
  })

}
