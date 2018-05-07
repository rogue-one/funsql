package com.github.rogueone.parser

import com.github.rogueone.TestSpec
import com.github.rogueone.ast.Nodes

class PrimitiveSpec extends TestSpec {

  "Primitives" must "parse identifier" in {
    Parser.expression.parse("col1").get.value must be (Nodes.Identifier("col1"))
  }
}
