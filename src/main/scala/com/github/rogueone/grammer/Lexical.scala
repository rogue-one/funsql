//package com.github.rogueone.grammer
//
//import java.sql.Timestamp
//import com.github.rogueone.ast
//import com.github.rogueone.ast.Nodes
//import com.github.rogueone.ast.Nodes.TimestampLiteral
//import fastparse.core.Parser
//import fastparse.{all, core}
//
///**
//  * Created by chlr on 4/5/17.
//  */
//object Lexical {
//
//  object Primitives {
//    import fastparse.all._
//    val alphabet: all.Parser[Unit] = P { CharIn('a' to 'z', 'A' to 'Z')  }
//    val number: all.Parser[Unit] = P { CharIn('0' to '9') }
//    val underscore: all.Parser[Unit] = P { "_" }
//    val plus: all.Parser[Unit] = P { "+" }
//    val minus: all.Parser[Unit] = P { "-" }
//    val product = P { "*" }
//    val divide = P { "/" }
//    val decimal: all.Parser[Unit] = P { "." }
//    val whitespace: all.Parser[Unit] = P { CharIn(" \n\t") }
//  }
//
//  /**
//    * identifier
//    * @return
//    */
//  def identifier: Parser[Nodes.Identifier, Char, String] = {
//      import Primitives._
//      import fastparse.all._
//      P(((alphabet | underscore) ~ (alphabet | number | underscore).rep.?).!.opaque("<identifier>"))
//          .filter(!keywords.contains(_)).map(ast.Nodes.Identifier)
//    }
//
//
//  object LiteralParser {
//    import Primitives._
//    import fastparse.all._
//    def literal: P[ast.Nodes.Literal] = LiteralParser.decimalLiteral | LiteralParser.numberLiteral |
//      LiteralParser.stringLiteral | LiteralParser.dateLiteral
//    val numberLiteral: core.Parser[Nodes.IntegerLiteral, Char, String] =
//      P(((plus | minus).? ~ number.rep(min=1)).!).map(x => ast.Nodes.IntegerLiteral(x))
//    def decimalLiteral: core.Parser[Nodes.DecimalLiteral, Char, String] = P(((plus | minus).? ~ number.rep ~ decimal ~ number.rep).!).map(ast.Nodes.DecimalLiteral)
//    def stringLiteral: core.Parser[Nodes.StringLiteral, Char, String] = P("'" ~ CharsWhile(_ != '\'').rep.! ~ "'").map(ast.Nodes.StringLiteral)
//    def dateLiteral: core.Parser[Nodes.DateLiteral, Char, String] = P(
//      IgnoreCase("DATE") ~ whitespace.rep ~ "'" ~/ (number.rep(exactly = 4) ~ "-" ~ number.rep(exactly = 2) ~
//        "-" ~ number.rep(exactly = 2)).! ~ "'"
//    ).map(x => ast.Nodes.DateLiteral(x))
//    def timestampLiteral: core.Parser[Nodes.TimestampLiteral, Char, String] = P(IgnoreCase("TIMESTAMP") ~
//      whitespace.rep ~ "'" ~/ (number.rep(exactly = 4) ~ "-" ~ number.rep(exactly = 2) ~ "-" ~
//      number.rep(exactly = 2)).! ~ whitespace.rep ~ (number.rep(exactly=2) ~ ":" ~ number.rep(exactly=2) ~ ":" ~
//        number.rep(exactly=2)).! ~ "'").map({case (date, time) => TimestampLiteral(s"$date $time")})
//  }
//
//  object Comparison {
//    import fastparse.all._
//    val eq: all.Parser[Unit] = P { "=" }
//    val neq: all.Parser[Unit] = P { "!=" | "<>" }
//    val gt: all.Parser[Unit] = P { ">" }
//    val lt: all.Parser[Unit] = P { "<" }
//    val gteq: all.Parser[Unit] = P { ">=" }
//    val lteq: all.Parser[Unit] = P { "<=" }
//    val condOp: core.Parser[Unit, Char, String] = eq | neq | gt | lt | gteq | lteq | IgnoreCase("or") | IgnoreCase("and")
//  }
//
//  val keywords = Seq("select", "from", "where", "not", "or", "and", "limit", "group", "by")
//
//}
