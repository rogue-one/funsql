package com.github.rogueone.parser

import com.github.rogueone.ast
import com.github.rogueone.ast.Nodes.Exp
import fastparse.noApi._
import com.github.rogueone.parser.Parser.White._
import LiteralParser._
import PredicateParser._

object MathParser {

  private val primary: P[Exp] = P { numberLiteral | decimalLiteral | parentheses | Parser.identifier }

  val parentheses: P[Exp] = P( "(" ~/ mathExp ~ ")" )

//  protected val setComparison: core.Parser[Nodes.InClause, Char, String] = {
//    val set =  P("(" ~ mathExp.rep(min=1, sep=",")  ~ ")")
//    (Parser.expression ~ Keyword.keyword("in") ~ set)
//      .filter({ case (_, _: Seq[ast.Nodes.Exp] @unchecked) => true case _ => false})
//      .map({case (x, y: Seq[ast.Nodes.Exp] @unchecked) => ast.Nodes.InClause(x, y)})
//  }


  val addSub: P[Exp] = P( mulDiv ~ (CharIn("+-").! ~/ mulDiv).rep).map({
    case (e, s) => s.foldLeft(e)({
      case (l, (op, r)) => if(op == "+") ast.Nodes.Add(l, r) else ast.Nodes.Sub(l, r)
    })
  }).log()

  protected val mulDiv: P[Exp] = P( primary ~ (CharIn("*/").! ~/ primary).rep ).map({
    case (e: Exp, s: Seq[(String, Exp)]) => s.foldLeft(e) {
      case (l, (op, r)) => if(op == "*") ast.Nodes.Mul(l, r) else ast.Nodes.Div(l, r)
    }
  }).log()

  val mathExp: P[Exp] = compoundComparison

}
