package com.github.rougeone.grammer

import java.text.SimpleDateFormat
import com.github.rogueone.ast
import fastparse.core.Parsed
import org.scalatest.{FlatSpec, MustMatchers}

/**
  * Created by chlr on 3/26/17.
  */
class LexicalSpec extends FlatSpec with MustMatchers {

  "LiteralParser" must "parse all literals" in {
    import com.github.rogueone.grammer.Lexical.LiteralParser._
    numberLiteral.parse("+100").get.value must be (ast.Nodes.IntegerLiteral(100L))
    numberLiteral.parse("-000100").get.value must be (ast.Nodes.IntegerLiteral(-100L))
    decimalLiteral.parse("-10.23").get.value must be (ast.Nodes.DecimalLiteral(-10.23D))
    decimalLiteral.parse("+100.76").get.value must be (ast.Nodes.DecimalLiteral(100.76D))
    stringLiteral.parse("'hello world'").get.value must be (ast.Nodes.StringLiteral("hello world"))
    stringLiteral.parse("'foobar").asInstanceOf[Parsed.Failure[_,_]].index must be (7)
    stringLiteral.parse("foobar'").asInstanceOf[Parsed.Failure[_,_]].index must be (0)
    dateLiteral.parse("date '2015-12-32'").get.value must be (ast.Nodes.DateLiteral(
      new SimpleDateFormat("YYYY-MM-DD").parse("2015-12-32"))
    )
    dateLiteral.parse("date'2015-12-32'").get.value must be (ast.Nodes.DateLiteral(
      new SimpleDateFormat("YYYY-MM-DD").parse("2015-12-32"))
    )
  }

}
