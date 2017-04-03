package com.github.rougeone.grammer

import java.text.SimpleDateFormat

import com.github.rogueone.ast
import com.github.rogueone.grammer.Parsers.Nodes.MathExpressionParser
import fastparse.core.Parsed
import org.scalatest.{FlatSpec, MustMatchers}

/**
  * Created by chlr on 3/26/17.
  */
class NodeSpec extends FlatSpec with MustMatchers {

  "LiteralParser" must "parse all literals" in {
    import com.github.rogueone.grammer.Parsers.Nodes.LiteralParser._
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
  }

  "MathParser" must "parse math expression" in {
    import ast.Nodes._

    MathExpressionParser.mathExp.parse("1 + 20").get.value must be (Add(IntegerLiteral(20), IntegerLiteral(1)))

    MathExpressionParser.mathExp.parse("(112 * col1) + 2").get.value mustBe {
      Add(IntegerLiteral(2),Mul(Identifier("col1"),IntegerLiteral(112)))
    }

  }

}
