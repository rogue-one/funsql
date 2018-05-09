package com.github.rogueone.parser

import com.github.rogueone.ast
import com.github.rogueone.ast.Nodes
import com.github.rogueone.ast.Nodes.{Exp, Identifier, Relation, Sql}
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
  val star: P[Exp] = P { "*" }.map(_ => Sql.Star)

  def identifier: P[Nodes.Identifier] = {
    P(((Primitives.alphabet | Primitives.underscore) ~ (alphabet | Primitives.number |
      Primitives.underscore).rep ~ !"(").!
      .opaque("<identifier>"))
      .filter(x => !Keyword.keywords.map(_.word.toLowerCase).contains(x.toLowerCase))
      .map(Nodes.Identifier)
  }

  def function: P[Nodes.Function] = {
    P (((Primitives.alphabet | Primitives.underscore) ~ (alphabet | Primitives.number |
      Primitives.underscore).rep).! ~ !Primitives.whitespace ~ "(" ~/ expression.rep(sep=",") ~ ")") map {
      case (x: String, y: Seq[ast.Nodes.Exp]) => ast.Nodes.Function(Nodes.Identifier(x), y)
    }
  }

  protected val identifierWithAlias: P[(Nodes.Identifier, Option[String])] =
    P(identifier ~ whitespace.rep ~ (Keyword.As.parser.? ~ whitespace.rep ~ identifier).!.?)

  def tableName: P[Nodes.Table] = identifierWithAlias.map({ case (name, alias) => Nodes.Table(name.value, alias) })

  def columnName: P[Nodes.Column] = identifierWithAlias.map({ case (name, alias) => Nodes.Column(name.value, alias) })

  def relation: P[Relation] = {
    import Parser.White._
    import fastparse.noApi._
    ((identifier.map(x => Nodes.Table(x.value, None)) | ("(" ~ Queries.select ~ ")").map({ x: Relation => x })) ~
      (Keyword.As.parser.? ~ identifier).?)
        .map({
          case (x: Nodes.Table, alias: Option[Identifier]) => x.copy(alias = alias.map(_.value))
          case (x: Sql.Select, alias: Option[Identifier]) => x.copy(alias = alias.map(_.value))
        })
  }

}
