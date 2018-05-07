package com.github.rogueone.parser

import com.github.rogueone.ast
import com.github.rogueone.ast.Nodes
import com.github.rogueone.parser.Parser.expression
import fastparse.all._

object Primitives {

  val alphabet: P[Unit] = P { CharIn('a' to 'z', 'A' to 'Z')  }
  val number: P[Unit] = P { CharIn('0' to '9') }
  val underscore: P[Unit] = P { "_" }
  val plus: P[Unit] = P { "+" }
  val minus: P[Unit] = P { "-" }
  val product: P[Unit] = P { "*" }
  val divide: P[Unit] = P { "/" }
  val decimal: P[Unit] = P { "." }
  val whitespace: P[Unit] = P { CharIn(" \n\t") }

  def identifier: P[Nodes.Identifier] = {
    P(((Primitives.alphabet | Primitives.underscore) ~ (alphabet | Primitives.number |
      Primitives.underscore).rep ~ !"(").!
      .opaque("<identifier>"))
      .filter(!Keyword.keywords.map(_.word).contains(_)).map(Nodes.Identifier)
  }

  def function: P[Nodes.Function] = {
    P (((Primitives.alphabet | Primitives.underscore) ~ (alphabet | Primitives.number |
      Primitives.underscore).rep).! ~ !Primitives.whitespace ~ "(" ~/ expression.rep(sep=",") ~ ")") map {
      case (x: String, y: Seq[ast.Nodes.Exp]) => ast.Nodes.Function(Nodes.Identifier(x), y)
    }
  }
}
