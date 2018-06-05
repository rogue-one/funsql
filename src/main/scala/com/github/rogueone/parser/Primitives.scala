package com.github.rogueone.parser

import com.github.rogueone.ast
import com.github.rogueone.ast.Nodes
import com.github.rogueone.ast.Nodes.{Exp, Identifier, Relation, Sql}
import com.github.rogueone.parser.Parser.expression
import fastparse.all._
import fastparse.core

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
  val star: P[Unit] = P { "*" }

  def protoIdentifier: P[String] = {
    P(((Primitives.alphabet | Primitives.underscore) ~ (alphabet | Primitives.number |
      Primitives.underscore).rep ~ !"(").!
      .opaque("<identifier>"))
      .filter(x => !Keyword.keywords.map(_.word.toLowerCase).contains(x.toLowerCase))
  }

  def identifier: P[Identifier] = P((protoIdentifier ~ ".").? ~ protoIdentifier)
    .map({case (x,y) => Nodes.Identifier(y,x)})

  def function: P[Nodes.Function] = {
    P (((Primitives.alphabet | Primitives.underscore) ~ (alphabet | Primitives.number |
      Primitives.underscore).rep).! ~ !Primitives.whitespace ~ "(" ~/ expression.rep(sep=",") ~ ")") map {
      case (x: String, y: Seq[ast.Nodes.Exp]) => ast.Nodes.Function(Nodes.Identifier(x), y)
    }
  }

  def alias: P[Option[String]] = (Keyword.As.parser.? ~ whitespace.rep ~ identifier.!).?

  def prefixedStar: P[Nodes.Star] = P((protoIdentifier ~ ".").? ~ star).map(x => Nodes.Star(x))

  def column: P[Nodes.Aliasable] = P((Parser.expression ~ whitespace.rep ~ alias) | prefixedStar)
    .map({
      case (exp: Exp, alias: Option[String] @unchecked) => Nodes.ColumnNode(exp, alias)
      case x: Nodes.Star => x
    })

  def relation: P[Nodes.Relation] = {
    import Parser.White._
    import fastparse.noApi._
    ((identifier ~ alias) | Queries.selectRelation).map({
      case (x: Nodes.Identifier, y: Option[String @unchecked]) => Nodes.TableNode(x.value, y)
      case x: Sql.SubQuery => x
      case _ =>  ???
    })
  }

}
