package com.github.rogueone.parser

import com.github.rogueone.TestSpec
import com.github.rogueone.ast.Nodes
import fastparse.core.Parsed.Failure

class PrimitiveSpec extends TestSpec {

  "Primitives" must "parse identifier" in {
    Parser.expression.parse("col1").get.value must be (Nodes.Identifier("col1"))
  }

  it must "parse identifier with prefix" in {
    Parser.expression.parse("t1.col1").get.value must be (Nodes.Identifier("col1", Some("t1")))
  }

  it must "not parse keywords as identifier" in {
    Primitives.identifier.parse("Where") mustBe a [Failure[_, _]]
  }
}
