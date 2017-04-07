package com.github.rogueone.grammer

import java.text.SimpleDateFormat

import com.github.rogueone.ast

/**
  * Created by chlr on 4/5/17.
  */
object Lexical {

  object Primitives {
    import fastparse.all._
    val char = P { CharIn('a' to 'z', 'A' to 'Z')  }
    val number = P { CharIn('0' to '9') }
    val underscore = P { "_" }
    val plus = P { "+" }
    val minus = P { "-" }
    val decimal = P { "." }
    val whitespace = P { CharIn(" \n\t") }
  }

  /**
    * identifier
    * @return
    */
  def identifier = {
      import Primitives._
      import fastparse.all._
      P { ((char | underscore) ~ (char | number | underscore).rep(0).?).!.opaque("<identifier>") } filter {
        !keywords.contains(_)
      } map {
        ast.Nodes.Identifier
      }
    }


  object LiteralParser {
    import fastparse.all._
    import Primitives._
    def literal: P[ast.Nodes.Exp] = (LiteralParser.decimalLiteral | LiteralParser.numberLiteral | LiteralParser.stringLiteral
      | LiteralParser.dateLiteral)
    val numberLiteral = P { ((plus | minus).? ~ number.rep(min=1)).! }.map(x => ast.Nodes.IntegerLiteral(x.toLong))
    def decimalLiteral = P { ((plus | minus).? ~ number.rep ~ decimal ~ number.rep).! }
      .map(x => ast.Nodes.DecimalLiteral(x.toDouble))
    def stringLiteral = P {"'" ~ CharPred(_ != '\'').rep.! ~ "'"}.map(ast.Nodes.StringLiteral)
    def dateLiteral = P {
      IgnoreCase("DATE") ~ whitespace.rep ~ "'" ~ (number.rep(exactly = 4) ~ "-" ~ number.rep(exactly = 2) ~
        "-" ~ number.rep(exactly = 2)).! ~ "'"
    }.map(x => ast.Nodes.DateLiteral(new SimpleDateFormat("YYYY-MM-DD").parse(x)))
  }

  object Comparison {
    import fastparse.all._
    val eq = P { "=" }
    val neq = P { "!=" }
    val gt = P { ">" }
    val lt = P { "<" }
    val gteq = P { ">=" }
    val lteq = P { "<=" }
    val condOp = (eq | neq | gt | lt | gteq | lteq | IgnoreCase("or") | IgnoreCase("and"))
  }

  val keywords = Seq("select", "from", "where", "not", "or", "and", "limit", "group", "by")

}
