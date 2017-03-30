package com.github.rougeone.grammer

import com.github.rogueone.ast.Nodes.{DecimalLiteral, IntegerLiteral, StringLiteral}
import fastparse.core.Parsed
import org.scalatest.{FlatSpec, MustMatchers}

/**
  * Created by chlr on 3/26/17.
  */
class NodeSpec extends FlatSpec with MustMatchers {

  "NumberLiteral" must "parser number literals" in {
    import com.github.rogueone.grammer.Parsers.Nodes.Literal._
    numberLiteral.parse("+100").get.value must be (IntegerLiteral("+100"))
    numberLiteral.parse("-000100").get.value must be (IntegerLiteral("-000100"))
    numberLiteral.parse("1000a").asInstanceOf[Parsed.Failure[_,_]].index must be (4)
    decimalLiteral.parse("-10.23").get.value must be (DecimalLiteral("-10.23"))
    decimalLiteral.parse("+100.76").get.value must be (DecimalLiteral("+100.76"))
    stringLiteral.parse("'hello world'").get.value must be (StringLiteral("hello world"))
    stringLiteral.parse("'foobar").asInstanceOf[Parsed.Failure[_,_]].index must be (7)
    stringLiteral.parse("foobar'").asInstanceOf[Parsed.Failure[_,_]].index must be (0)
    dateLiteral.parse(" DATE '2015-12-32'") must be ("2015-12-32")
  }

}
