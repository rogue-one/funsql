package com.github.rogueone.parser

import com.github.rogueone.ast.Nodes
import fastparse.all
import fastparse.all._

object Primitives {

  val alphabet: all.Parser[Unit] = P { CharIn('a' to 'z', 'A' to 'Z')  }
  val number: all.Parser[Unit] = P { CharIn('0' to '9') }
  val underscore: all.Parser[Unit] = P { "_" }
  val plus: all.Parser[Unit] = P { "+" }
  val minus: all.Parser[Unit] = P { "-" }
  val product: all.Parser[Unit] = P { "*" }
  val divide: all.Parser[Unit] = P { "/" }
  val decimal: all.Parser[Unit] = P { "." }
  val whitespace: all.Parser[Unit] = P { CharIn(" \n\t") }

  def identifier: P[Nodes.Identifier] = {
    P(((Primitives.alphabet | Primitives.underscore) ~ (alphabet | Primitives.number | Primitives.underscore).rep).!
      .opaque("<identifier>"))
      .filter(!Keyword.keywords.map(_.word).contains(_)).map(Nodes.Identifier)
  }
}
