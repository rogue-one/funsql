package com.github.rogueone.grammer

import fastparse.WhitespaceApi
import fastparse.noApi._
import com.github.rogueone.ast.Nodes._

/**
  * Created by chlr on 3/25/17.
  */

object Parsers {

  val White = WhitespaceApi.Wrapper{
    import fastparse.all._
    NoTrace(" |\n|\t".rep)
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
    import fastparse.all._
    val identifier = P { (char ~ (char | number | underscore).rep(0).?).!.opaque("<identifier>") }
    object Literal {
      val numberLiteral = P { (Start ~ (plus | minus).? ~ number.rep ~ End).! }.map(IntegerLiteral)
      val decimalLiteral = P { Start ~ ((plus | minus).? ~ number.rep ~ decimal ~ number.rep).! ~ End }.map(DecimalLiteral)
      val stringLiteral = P { Start ~ "'" ~ CharPred(_ != '\'').rep.! ~ "'" ~ End }.map(IntegerLiteral)
      val dateLiteral = P {
        Start ~ IgnoreCase("DATE") ~ ws ~ "'" ~ (number.rep(exactly = 4) ~ "-" ~ number.rep(exactly = 2) ~
          "-" ~ number.rep(exactly = 2)).! ~ "'"
      }
    }
    val expr = (identifier | (Literal.decimalLiteral | Literal.numberLiteral | Literal.stringLiteral | Literal.dateLiteral))
  }



}
