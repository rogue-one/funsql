package com.github.rogueone.parser

import com.github.rogueone.TestSpec
import com.github.rogueone.ast.Nodes
import scala.collection.mutable.ArrayBuffer

class MathParserSpec extends TestSpec {

  "MathParser" must "parse basic math expression" in {
    MathParser.mathExp.parse("((1 + 2) * 10) / 5").get.value must be(
      Nodes.Div(
        Nodes.Mul(
          Nodes.Add(Nodes.IntegerLiteral("1"), Nodes.IntegerLiteral("2")),
          Nodes.IntegerLiteral("10")
        ),
        Nodes.IntegerLiteral("5")
      )
    )
  }

  it must "parse predicate expression" in {
    MathParser.mathExp.parse("(1 * 2) > 50").get.value must be(
      Nodes.Gt(
        Nodes.Mul(Nodes.IntegerLiteral("1"), Nodes.IntegerLiteral("2")),
        Nodes.IntegerLiteral("50"))
    )

    MathParser.mathExp.parse("((1 * 2) > 50 AND 5 > 10) OR 10 = 10").get.value must be(
      Nodes.OrCond(
        Nodes.AndCond(
          Nodes.Gt(
            Nodes.Mul(Nodes.IntegerLiteral("1"), Nodes.IntegerLiteral("2")), Nodes.IntegerLiteral("50")
          ),
          Nodes.Gt(Nodes.IntegerLiteral("5"), Nodes.IntegerLiteral("10"))
        ), Nodes.Eq(Nodes.IntegerLiteral("10"), Nodes.IntegerLiteral("10"))
      )
    )

    MathParser.mathExp.parse("(1 * 2) > 50 AND 5 > 10").get.value must be(
      Nodes.AndCond(
        Nodes.Gt(
          Nodes.Mul(Nodes.IntegerLiteral("1"), Nodes.IntegerLiteral("2")),
          Nodes.IntegerLiteral("50")
        ),
        Nodes.Gt(Nodes.IntegerLiteral("5"), Nodes.IntegerLiteral("10"))
      )
    )

  }

  it must "parse NOT predicate expressions" in {
    MathParser.mathExp.parse("Not (1 * 2) > 50").get.value must be(
      Nodes.NotCond(
        Nodes.Gt(
          Nodes.Mul(Nodes.IntegerLiteral("1"), Nodes.IntegerLiteral("2")),
          Nodes.IntegerLiteral("50")
        )
      )
    )
    MathParser.mathExp.parse("Not ((1 * 2) > 50 OR 5 < (1 + 2))").get.value must be(
      Nodes.NotCond(
        Nodes.OrCond(
          Nodes.Gt(
            Nodes.Mul(Nodes.IntegerLiteral("1"), Nodes.IntegerLiteral("2")),
            Nodes.IntegerLiteral("50")
          ),
          Nodes.Lt(
            Nodes.IntegerLiteral("5"),
            Nodes.Add(Nodes.IntegerLiteral("1"), Nodes.IntegerLiteral("2"))
          )
        )
      )
    )
  }

  it must "parse In clause correctly" in {
    MathParser.mathExp.parse("5 < 10 OR NOT col1 in (1,'lando',col3)").get.value must be(
      Nodes.OrCond(
        Nodes.Lt(Nodes.IntegerLiteral("5"), Nodes.IntegerLiteral("10")),
        Nodes.NotCond(
          Nodes.InClause(
            Nodes.Identifier("col1"),
            ArrayBuffer(Nodes.IntegerLiteral("1"), Nodes.StringLiteral("lando"), Nodes.Identifier("col3"))
          )
        )
      )
    )

    MathParser.mathExp.parse("col1 in (1,'lando',col3)").get.value must be(
      Nodes.InClause(
        Nodes.Identifier("col1"),
        ArrayBuffer(Nodes.IntegerLiteral("1"), Nodes.StringLiteral("lando"), Nodes.Identifier("col3"))
      )
    )
  }


  it must "handle functions in expressions" in {
    Parser.expression.parse("foo(10) < 10 OR NOT col1 in (1,'lando',col3)").get.value must be(
      Nodes.OrCond(
        Nodes.Lt(
          Nodes.Function(Nodes.Identifier("foo"), ArrayBuffer(Nodes.IntegerLiteral("10"))),
          Nodes.IntegerLiteral("10")
        ),
        Nodes.NotCond(
          Nodes.InClause(
            Nodes.Identifier("col1"),
            ArrayBuffer(Nodes.IntegerLiteral("1"), Nodes.StringLiteral("lando"), Nodes.Identifier("col3"))
          )
        )
      )
    )
  }

}
