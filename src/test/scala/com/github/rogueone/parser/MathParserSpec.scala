package com.github.rogueone.parser

import com.github.rogueone.TestSpec
import com.github.rogueone.ast.Nodes

class MathParserSpec extends TestSpec {

  "MathParser" must "parse basic math expression" in {
    MathParser.mathExp.parse("((1 + 2) * 10) / 5").get.value must be (Nodes.Div(Nodes.Mul(
      Nodes.Add(Nodes.IntegerLiteral("1"), Nodes.IntegerLiteral("2")),Nodes.IntegerLiteral("10")),
      Nodes.IntegerLiteral("5")))
  }

  it must "parse predicate expression" in {
    MathParser.mathExp.parse("(1 * 2) > 50").get.value must be (Nodes.Gt(
          Nodes.Mul(Nodes.IntegerLiteral("1"),Nodes.IntegerLiteral("2")),
          Nodes.IntegerLiteral("50")))

    MathParser.mathExp.parse("(1 * 2) > 50 AND 5 > 10").get.value must be (Nodes.AndCond(Nodes.Gt
    (Nodes.Mul(Nodes.IntegerLiteral("1"),Nodes.IntegerLiteral("2")),Nodes.IntegerLiteral("50")), Nodes.Gt(
      Nodes.IntegerLiteral("5"),Nodes.IntegerLiteral("10"))))
  }

}
