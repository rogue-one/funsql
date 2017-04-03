package com.github.rogueone.grammer

import java.text.SimpleDateFormat

import com.github.rogueone.ast
import fastparse.WhitespaceApi
import fastparse.noApi._

/**
  * Created by chlr on 3/25/17.
  */

object Parsers {

  val White = WhitespaceApi.Wrapper{
    import fastparse.all._
    NoTrace(CharIn(" \n\t").rep)
  }

  object Primitives {
    val char = P { CharIn('a' to 'z', 'A' to 'Z')  }
    val number = P { CharIn('0' to '9') }
    val underscore = P { "_" }
    val plus = P { "+" }
    val minus = P { "-" }
    val decimal = P { "." }
    val ws = P { CharIn(Seq(' ', '\n', '\t')) }
  }

  import Primitives._

  object Nodes {

    val expression: P[ast.Nodes.Exp] = {
      import fastparse.all._
      Identifier.identifier | LiteralParser.literal | MathExpressionParser.mathExp
    }

    object Identifier {
      import fastparse.all._
      def identifier = P {
        ((char | underscore) ~ (char | number | underscore).rep(0).?).!.opaque("<identifier>")
      } map {
        x => ast.Nodes.Identifier(x)
      }
    }

    object LiteralParser {
      import fastparse.all._
      def literal: P[ast.Nodes.Exp] = (LiteralParser.decimalLiteral | LiteralParser.numberLiteral | LiteralParser.stringLiteral
        | LiteralParser.dateLiteral)
      val numberLiteral = P { ((plus | minus).? ~ number.rep(min=1)).! }.map(x => ast.Nodes.IntegerLiteral(x.toLong))
      def decimalLiteral = P { ((plus | minus).? ~ number.rep ~ decimal ~ number.rep).! }
        .map(x => ast.Nodes.DecimalLiteral(x.toDouble))
      def stringLiteral = P {"'" ~ CharPred(_ != '\'').rep.! ~ "'"}.map(ast.Nodes.StringLiteral)
      def dateLiteral = P {
        IgnoreCase("DATE") ~ ws ~ "'" ~ (number.rep(exactly = 4) ~ "-" ~ number.rep(exactly = 2) ~
          "-" ~ number.rep(exactly = 2)).! ~ "'"
      }.map(x => ast.Nodes.DateLiteral(new SimpleDateFormat("YYYY-MM-DD").parse(x)))
    }

    //noinspection ForwardReference
    object MathExpressionParser {
      import LiteralParser._
      import White._
      import com.github.rogueone.ast
      import com.github.rogueone.ast.Nodes.Exp
      import fastparse.noApi._

      val mathExp: P[Exp] = P(addSub)
      val primary: P[Exp] = P( numberLiteral | decimalLiteral | paren | Identifier.identifier)
      val paren = P( "(" ~/ mathExp ~ ")" )
      val addSub: P[Exp] = P( mulDiv ~ (CharIn("+-").! ~/ mulDiv).rep).map {
        case (e, s) => s.foldLeft(e){
          case (r, (op, l)) => if(op == "+") ast.Nodes.Add(l, r) else ast.Nodes.Sub(l, r)
        }
      }
      val mulDiv: P[Exp] = P( primary ~ (CharIn("*/").! ~/ primary).rep ).map {
        case (e, s) => s.foldLeft(e){
          case (r, (op, l)) => if(op == "*") ast.Nodes.Mul(l, r) else ast.Nodes.Div(l, r)
        }
      }
    }

    object Predicates {
      import fastparse.noApi._
      import White._
      import com.github.rogueone.ast

      val predicate: P[ast.Nodes.Predicate] = P(or).log()
      val primary: P[ast.Nodes.Predicate] =
        P { Nodes.expression ~ StringIn("=", ">", "<", "=>", "<=").! ~ Nodes.expression}.log() map {
          case (x,"=",y) => ast.Nodes.Eq(x, y)
          case (x,">",y) => ast.Nodes.Gt(x, y)
          case (x,"=>",y) => ast.Nodes.GtEq(x, y)
          case (x,"<",y) => ast.Nodes.Lt(x, y)
          case (x,"<=",y) => ast.Nodes.LtEq(x, y)
        }
      val paren = P( "(" ~/ predicate ~ ")" ).log()
      val or: P[ast.Nodes.Predicate] = P( and ~ (IgnoreCase("OR") ~/ and).rep).log().map {
        case (e, s) => s.foldLeft(e) {
          case (r, l) => ast.Nodes.OrCondition(l, r)
        }
      }
      val and: P[ast.Nodes.Predicate] = P( primary ~ (IgnoreCase("AND").! ~/ primary).rep ).log().map {
        case (e, s) => s.foldLeft(e) {
          case (r, (op, l)) =>  ast.Nodes.AndCondition(l, r)
        }
      }
    }
  }



  object QueryParser {
    import Nodes.Identifier
    import White._
    import fastparse.noApi._
    val select = P { Start ~ IgnoreCase("SELECT") ~ Identifier.identifier.rep(sep=",").! ~ IgnoreCase("FROM") ~
      Identifier.identifier.!
    } map {
      case (x, y) => ast.Queries.Select(x.split(",").map(ast.Nodes.Identifier), ast.Nodes.Identifier(y))
    }
  }


}
