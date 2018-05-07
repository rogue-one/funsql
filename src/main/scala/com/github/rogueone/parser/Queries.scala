package com.github.rogueone.parser

import com.github.rogueone.ast.Sql
import fastparse.noApi._
import com.github.rogueone.parser.Parser.White._

object Queries {

  val select: P[Sql.Select] = P(Keyword.Select.parser ~ Parser.expression.rep(min=1, sep = ",") ~
    Keyword.From.parser ~ Primitives.identifier ~ (Keyword.Where.parser ~ PredicateParser.predicateOnly).? ~
    (Keyword.Group.parser ~ Keyword.By.parser ~ Parser.expression.rep(min=1, sep=",")).? ~
    (Keyword.Limit.parser ~ LiteralParser.numberLiteral).?)
    .map({ case (columns, tableName, predicate, groupBy , limit) =>
      Sql.Select(columns, tableName, predicate, groupBy.getOrElse(Nil) ,limit)})

}
