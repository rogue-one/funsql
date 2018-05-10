package com.github.rogueone.parser

import com.github.rogueone.ast.Nodes.Sql
import com.github.rogueone.ast.Nodes
import com.github.rogueone.ast.Nodes.Sql.UberSelect
import fastparse.noApi._
import com.github.rogueone.parser.Parser.White._
import fastparse.noApi

object Queries {

  val select: P[Sql.Select] = P(Keyword.Select.parser ~ (Primitives.column | Primitives.star).rep(min=1, sep = ",") ~
    Keyword.From.parser ~ Primitives.relation ~ (Keyword.Where.parser ~ PredicateParser.predicateOnly).? ~
    (Keyword.Group.parser ~ Keyword.By.parser ~ Parser.expression.rep(min=1, sep=",")).?)
    .map({ case (columns: Seq[Nodes.Projection @unchecked], tableName, predicate, groupBy) =>
          Sql.Select(columns, tableName, predicate, groupBy.getOrElse(Nil)) })

  val selectRelation: P[Sql.SelectRelation] = P("(" ~ select ~ ")" ~ Primitives.alias)
      .map({ case (x: Sql.Select, y: Option[String]) => Sql.SelectRelation(x, y)})

  val uberSelect: P[UberSelect] = P(select ~ (Keyword.Limit.parser ~ LiteralParser.numberLiteral).?)
    .map({ case (x,y) => UberSelect(x, y)})

}
