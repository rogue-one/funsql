package com.github.rogueone.grammer

import com.github.rogueone.ast
import com.glide.script.GlideRecord
import fastparse.WhitespaceApi


/**
  * Created by chlr on 3/25/17.
  */

object Parsers {

//
//  val rec = new GlideRecord()
//
//  val White = WhitespaceApi.Wrapper{
//    import fastparse.all._
//    NoTrace(Lexical.Primitives.whitespace.rep)
//  }
//
//  /**
//    * creates a parser for a given keyword.
//    * ensures keyword is not a subset of a larger literal value by negative lookahead (!).
//    * makes keyword case in-sensitive.
//    * @param kw
//    * @return
//    */
//    def keyword(kw: String) = {
//      import Lexical.Primitives._
//      import fastparse.all._
//      P { IgnoreCase(kw) ~ !(alphabet | number | underscore)}
//    }

//  /**
//    * parser for an expression.
//    */
//  val expression: fastparse.all.P[ast.Nodes.Exp] = {
//      import Lexical.{LiteralParser, identifier}
//      import fastparse.all._
//      //function | MathExpressionParser.mathExp | identifier | LiteralParser.literal
//    LiteralParser.literal // delete me
//    }

//  /**
//    * parser for a function call.
//    * @return
//    */
//  def function: fastparse.all.P[ast.Nodes.Function] = {
//      import Lexical.Primitives.whitespace
//      import White._
//      import fastparse.noApi._
//      P (Lexical.identifier ~ !(whitespace) ~ "(" ~/ expression.rep(sep=",") ~ ")") map {
//        case (x, y: Seq[ast.Nodes.Exp]) => ast.Nodes.Function(x, y)
//      }
//    }


    //noinspection ForwardReference
//    object MathExpressionParser {
//      import Lexical.Comparison.condOp
//      import Lexical.LiteralParser._
//      import White._
//      import com.github.rogueone.ast
//      import com.github.rogueone.ast.Nodes.Exp
//      import fastparse.noApi._

//      /**
//        * negate expression
//        * @param exp
//        * @return
//        */
//      def negate(exp: P[Exp]) =  {
//        exp | (keyword("not") ~ exp).map(x => ast.Nodes.Not(x))
//      }


//      val mathExp: P[Exp] = P(comparison)
//      val primary: P[Exp] = P { setComparison | numberLiteral | decimalLiteral | paren | Lexical.identifier }
//      val paren = P( "(" ~/ mathExp ~ ")" )

//      val setComparison = {
//        val set =  P("(" ~ mathExp.rep(min=1, sep=",")  ~ ")")
//        expression ~ keyword("in") ~ (set | Lexical.LiteralParser.literal) filter {
//          case (_, _: Seq[ast.Nodes.Exp] @unchecked) => true
//          case (_, _: ast.Nodes.SetLike) => true
//          case _ => false
//        } map {
//          case (x, y: Seq[ast.Nodes.Exp] @unchecked) => ast.Nodes.SetComparison(x, ast.Nodes.SetExpression(y))
//          case (x, y: ast.Nodes.SetLike) => ast.Nodes.SetComparison(x, y)
//        }
//      }

//      val comparison: P[Exp] = P(negate(addSub) ~ (condOp.! ~/ negate(addSub)).rep ) map {
//        case (e, s) => s.foldLeft(e) {
//          case (r, ("=", l)) => ast.Nodes.Eq(l, r)
//          case (r, ("!=", l)) => ast.Nodes.NtEq(l, r)
//          case (r, (">", l)) => ast.Nodes.Gt(l, r)
//          case (r, (">=", l)) => ast.Nodes.GtEq(l, r)
//          case (r, ("<", l)) => ast.Nodes.Lt(l, r)
//          case (r, ("<=", l)) => ast.Nodes.LtEq(l, r)
//          case (r, (op, l)) if op.toLowerCase == "or" => ast.Nodes.OrCond(l, r)
//          case (r, (op, l)) if op.toLowerCase == "and" => ast.Nodes.AndCond(l, r)
//        }
//      }

//      val addSub: P[Exp] = P( negate(mulDiv) ~ (CharIn("+-").! ~/ negate(mulDiv)).rep) map {
//        case (e, s) => s.foldLeft(e) {
//          case (r, (op, l)) => if(op == "+") ast.Nodes.Add(l, r) else ast.Nodes.Sub(l, r)
//        }
//      }
//      val mulDiv: P[Exp] = P( negate(primary) ~ (CharIn("*/").! ~/ negate(primary)).rep ) map {
//        case (e, s) => s.foldLeft(e) {
//          case (r, (op, l)) => if(op == "*") ast.Nodes.Mul(l, r) else ast.Nodes.Div(l, r)
//        }
//      }
//    }


//  object QueryParser {
//    import MathExpressionParser._
//    import White._
//    import fastparse.noApi._
//    val select = P { Start ~ keyword("SELECT") ~ expression.rep(sep=",") ~ keyword("FROM") ~
//      Lexical.identifier.! ~ (keyword("WHERE") ~ mathExp).?  ~
//      (keyword("GROUP") ~ keyword("BY") ~ Lexical.identifier.rep(sep=",")).? ~
//      (keyword("LIMIT") ~ Lexical.Primitives.number.!).? ~ End
//    } map {
//      case (p, q, r, s, t) => ast.Queries.Select(p, ast.Nodes.Identifier(q), r, s.getOrElse(Nil), t.map(_.toLong))
//    }
//  }


}
