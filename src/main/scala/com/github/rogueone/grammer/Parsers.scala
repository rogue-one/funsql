package com.github.rogueone.grammer

import com.github.rogueone.ast
import fastparse.WhitespaceApi


/**
  * Created by chlr on 3/25/17.
  */

object Parsers {

  val White = WhitespaceApi.Wrapper{
    import fastparse.all._
    NoTrace(CharIn(" \n\t").rep)
  }


    val expression: fastparse.all.P[ast.Nodes.Exp] = {
      import Lexical.{LiteralParser, identifier}
      import fastparse.all._
      (function | identifier | LiteralParser.literal | MathExpressionParser.mathExp).log()
    }

    def function: fastparse.all.P[ast.Nodes.Function] = {
      import White._
      import fastparse.noApi._
      P (Lexical.identifier.log() ~ "(" ~ expression.rep(sep=",") ~ ")") map {
        case (x, y: Seq[ast.Nodes.Exp]) => ast.Nodes.Function(x, y)
      }
    }

    //noinspection ForwardReference
    object MathExpressionParser {
      import Lexical.Comparison.condOp
      import Lexical.LiteralParser._
      import White._
      import com.github.rogueone.ast
      import com.github.rogueone.ast.Nodes.Exp
      import fastparse.noApi._

      val mathExp: P[Exp] = P(comparison)
      val primary: P[Exp] = P( numberLiteral | decimalLiteral | paren | Lexical.identifier)
      val paren = P( "(" ~/ mathExp ~ ")" )
      val comparison: P[Exp] = P(addSub ~ (condOp.! ~/ addSub).rep ) map {
        case (e, s) => s.foldLeft(e) {
          case (r, ("=", l)) => ast.Nodes.Eq(l, r)
          case (r, ("!=", l)) => ast.Nodes.NtEq(l, r)
          case (r, (">", l)) => ast.Nodes.Gt(l, r)
          case (r, (">=", l)) => ast.Nodes.GtEq(l, r)
          case (r, ("<", l)) => ast.Nodes.Lt(l, r)
          case (r, ("<=", l)) => ast.Nodes.LtEq(l, r)
          case (r, (op, l)) if op.toLowerCase == "or" => ast.Nodes.OrCond(l, r)
          case (r, (op, l)) if op.toLowerCase == "and" => ast.Nodes.AndCond(l, r)
        }
      }
      val addSub: P[Exp] = P( mulDiv ~ (CharIn("+-").! ~/ mulDiv).rep) map {
        case (e, s) => s.foldLeft(e) {
          case (r, (op, l)) => if(op == "+") ast.Nodes.Add(l, r) else ast.Nodes.Sub(l, r)
        }
      }
      val mulDiv: P[Exp] = P( primary ~ (CharIn("*/").! ~/ primary).rep ) map {
        case (e, s) => s.foldLeft(e) {
          case (r, (op, l)) => if(op == "*") ast.Nodes.Mul(l, r) else ast.Nodes.Div(l, r)
        }
      }
    }

    object Predicates {
      import White._
      import com.github.rogueone.ast
      import fastparse.noApi._

      val predicate: P[ast.Nodes.Exp] = P(or).log()
      val primary: P[ast.Nodes.Exp] = MathExpressionParser.mathExp
      val paren = P( "(" ~/ predicate ~ ")" ).log()
      val or: P[ast.Nodes.Exp] = P( and ~ (IgnoreCase("OR") ~/ and).rep).log().map {
        case (e, s) => s.foldLeft(e) {
          case (r, l) => ast.Nodes.OrCond(l, r)
        }
      }
      val and: P[ast.Nodes.Exp] = P( primary ~ (IgnoreCase("AND").! ~/ primary).rep ).log().map {
        case (e, s) => s.foldLeft(e) {
          case (r, (op: String, l)) =>  ast.Nodes.AndCond(l, r)
        }
      }
    }



  object QueryParser {
    import MathExpressionParser._
    import White._
    import fastparse.noApi._
    val select = P { Start ~ IgnoreCase("SELECT") ~ expression.rep(sep=",") ~ IgnoreCase("FROM") ~
      Lexical.identifier.! ~ (IgnoreCase("WHERE") ~ mathExp).?  ~
      (IgnoreCase("GROUP") ~ "BY" ~ Lexical.identifier.rep(sep=",")).? ~
      (IgnoreCase("LIMIT") ~ Lexical.Primitives.number.!).? ~ End
    } map {
      case (p, q, r, s, t) => ast.Queries.Select(p, ast.Nodes.Identifier(q), r, s.getOrElse(Nil), t.map(_.toLong))
    }
  }


}
