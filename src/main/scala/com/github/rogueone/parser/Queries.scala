package com.github.rogueone.parser

import com.github.rogueone.ast.Nodes.{JoinedRelation, Sql}
import com.github.rogueone.ast._
import com.github.rogueone.ast.Nodes.Sql.Select
import fastparse.noApi._
import com.github.rogueone.parser.Parser.White._

object Queries {

  def basicSelect: P[Nodes.SelectExpression] = P(Keyword.Select.parser ~
    (Primitives.column | Primitives.star).rep(min=1, sep = ",") ~
    Keyword.From.parser ~ Primitives.relation ~ joinCond.rep.map(_.toList) ~ (Keyword.Where.parser
    ~ PredicateParser.predicateOnly).? ~
    (Keyword.Group.parser ~ Keyword.By.parser ~ Parser.expression.rep(min=1, sep=",")).?)
    .map({
      case (columns: Seq[Nodes.Projection @unchecked], tableName, Nil ,predicate, groupBy) =>
          Nodes.SelectExpression(columns, tableName, predicate, groupBy.getOrElse(Nil))
      case (columns: Seq[Nodes.Projection @unchecked], tableName, (head :: tail) ,predicate, groupBy) =>
        Nodes.SelectExpression (
          columns,
          tail.foldLeft(JoinedRelation(tableName, head))({ case (acc, join) => JoinedRelation(acc, join) }),
          predicate, groupBy.getOrElse(Nil)
        )
    })

  def selectRelation: P[Sql.SubQuery] = P("(" ~ basicSelect ~/ ")" ~ Primitives.alias)
      .filter({case (_: Nodes.SelectExpression, Some(_)) => true case _ => false })
      .map({ case (x: Nodes.SelectExpression, Some(y)) => Sql.SubQuery(x, Some(y)) case _ => ???})
      .opaque("all derived table must have an alias")

  def select: P[Select] = P(basicSelect ~ (Keyword.Limit.parser ~ LiteralParser.numberLiteral).?)
    .map({ case (x,y) => Select(x, y)})

  def innerJoinCond: P[InnerJoin] = P(Keyword.Inner.parser.? ~ Keyword.Join.parser ~/ Primitives.relation ~
    (Keyword.On.parser ~ PredicateParser.predicateOnly).?).map({case (x,y) => InnerJoin(x, y)})

  def leftJoinCond: P[LeftJoin] = P(Keyword.Left.parser ~ Keyword.Outer.parser ~ Keyword.Join.parser ~/
     Primitives.relation ~ Keyword.On.parser  ~ PredicateParser.predicateOnly).map({ case (x,y) => LeftJoin(x, y) })

  def rightJoinCond: P[RightJoin] =  P(Keyword.Right.parser ~ Keyword.Outer.parser ~ Keyword.Join.parser ~/
    Primitives.relation ~ Keyword.On.parser ~ PredicateParser.predicateOnly).map({ case (x,y) => RightJoin(x, y) })

  def fullJoinCond: P[FullJoin] = P(Keyword.Full.parser ~ Keyword.Outer.parser ~ Keyword.Join.parser ~/
    Primitives.relation ~ Keyword.On.parser ~ PredicateParser.predicateOnly).map({ case (x,y) => FullJoin(x, y) })

  def crossJoinCond: P[CrossJoin] = P(Keyword.Cross.parser ~ Keyword.Join.parser ~/ Primitives.relation)
    .map(x => CrossJoin(x))

  def joinCond: P[Join] = (leftJoinCond | rightJoinCond | fullJoinCond | crossJoinCond | innerJoinCond)

}
