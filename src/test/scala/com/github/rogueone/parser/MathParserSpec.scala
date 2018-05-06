package com.github.rogueone.parser

import com.github.rogueone.TestSpec
import com.github.rogueone.ast.Nodes

class MathParserSpec extends TestSpec {

  "MathParser" must "parse basic math expression" in {
    MathParser.mathExp.parse("((1 + 2) * 10) / 5").get.value must be (Nodes.Div(Nodes.Mul(
      Nodes.Add(Nodes.IntegerLiteral("1"), Nodes.IntegerLiteral("2")),Nodes.IntegerLiteral("10")),
      Nodes.IntegerLiteral("5")))
  }

//  it must "parse predicate expression" in {
//    info(MathParser.mathExp.parse("(1 * 2) > 50 AND 5 > 10").toString)
//  }

}
