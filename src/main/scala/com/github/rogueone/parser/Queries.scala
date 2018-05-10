package com.github.rogueone.parser

import com.github.rogueone.ast.Nodes.Sql
import com.github.rogueone.ast.Nodes
import com.github.rogueone.ast.Nodes.Sql.Select
import fastparse.noApi._
import com.github.rogueone.parser.Parser.White._

object Queries {

  val basicSelect: P[Sql.BasicSelect] = P(Keyword.Select.parser ~ (Primitives.column | Primitives.star).rep(min=1, sep = ",") ~
    Keyword.From.parser ~ Primitives.relation ~ (Keyword.Where.parser ~ PredicateParser.predicateOnly).? ~
    (Keyword.Group.parser ~ Keyword.By.parser ~ Parser.expression.rep(min=1, sep=",")).?)
    .map({ case (columns: Seq[Nodes.Projection @unchecked], tableName, predicate, groupBy) =>
          Sql.BasicSelect(columns, tableName, predicate, groupBy.getOrElse(Nil)) })

  val selectRelation: P[Sql.SelectRelation] = P("(" ~ basicSelect ~ ")" ~ Primitives.alias)
      .map({ case (x: Sql.BasicSelect, y: Option[String]) => Sql.SelectRelation(x, y)})

  val select: P[Select] = P(basicSelect ~ (Keyword.Limit.parser ~ LiteralParser.numberLiteral).?)
    .map({ case (x,y) => Select(x, y)})

}
