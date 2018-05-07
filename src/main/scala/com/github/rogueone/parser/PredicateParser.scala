package com.github.rogueone.parser

import com.github.rogueone.ast
import com.github.rogueone.ast.Nodes
import com.github.rogueone.ast.Nodes.{Exp, Predicate, Sql}
import Parser.White._
import fastparse.noApi._
import com.github.rogueone.utils.Utils._

object PredicateParser {

  val eq: P[Unit] = P { "=" }

  val neq: P[Unit] = P { "!=" | "<>" }

  val gt: P[Unit] = P { ">" }

  val lt: P[Unit] = P { "<" }

  val gteq: P[Unit] = P { ">=" }

  val lteq: P[Unit] = P { "<=" }

  val conditionalOp: P[Unit] = eq | neq | gt | lt | gteq | lteq

  val compoundConditionalOp: P[Unit] = Keyword.Or.parser | Keyword.And.parser

  val setComparison: P[(String, Seq[Exp])] =
    P(Keyword.In.parser.! ~ "(" ~/ (Queries.select | Parser.expression.rep(min = 1, sep = ",")) ~ ")")
    .map({
      case (x, y: Seq[Nodes.Exp @unchecked]) => x -> y
      case (x, y: Sql.Select) => x -> Seq(y)
      case _ => ???
    })

  protected val binaryComparison: P[Exp] =
    P(MathParser.addSub ~ ((conditionalOp.! ~/ MathParser.addSub) | setComparison).rep)
     .map(x => x._1 -> x._2.toList)
     .map({
      case (e: Nodes.Exp, (headOp, headExp: Nodes.Exp) :: tail) =>
        tail.foldLeft(opToPredicate(headOp, e, headExp))({
          case (l, (op, r: Nodes.Exp)) => opToPredicate(op, l, r)
          case (l, (ci"in", (r: Sql.Select) :: Nil)) => Nodes.SubQuery(l, r)
          case (l, ("in", r: Seq[Nodes.Exp @unchecked])) => Nodes.InClause(l, r)
        })
      case (e: Nodes.Exp, (ci"in", (query: Sql.Select):: Nil) :: tail) =>
        tail.foldLeft(Nodes.SubQuery(e, query): Predicate)({
          case (l, (op, r: Nodes.Exp)) => opToPredicate(op, l, r)
          case (l, (ci"in", (r: Sql.Select) :: Nil)) => Nodes.SubQuery(l, r)
          case (l, (ci"in", r: Seq[Nodes.Exp @unchecked])) => Nodes.InClause(l, r)
        })
      case (e: Nodes.Exp, (ci"in", nodes: Seq[Nodes.Exp @unchecked]) :: tail) =>
        tail.foldLeft(Nodes.InClause(e, nodes): Predicate)({
          case (l, (op, r: Nodes.Exp)) => opToPredicate(op, l, r)
          case (l, (ci"in", (r: Sql.Select) :: Nil)) => Nodes.SubQuery(l, r)
          case (l, (ci"in", r: Seq[Nodes.Exp @unchecked])) => Nodes.InClause(l, r)
        })
      case (x, Nil) => x
      case _ => ???
    })

  protected val comparison: P[Exp] = P( Keyword.Not.parser.!.? ~ binaryComparison)
    .map({
      case (Some(_), exp: Predicate) => Nodes.NotCond(exp)
      case (None, exp) => exp
      case x => System.err.println(x.toString()); ???
    })


  val compoundComparison: P[Exp] = P(comparison ~ (compoundConditionalOp.! ~/ comparison).rep)
      .map(x => x._1 -> x._2.toList)
      .map({
        case (head: Predicate, list: List[(String, Predicate) @unchecked]) => list.foldLeft(head)({
          case (rhs: Predicate,(ci"or", lhs: Predicate)) => Nodes.OrCond(rhs, lhs)
          case (rhs: Predicate,(ci"and", lhs: Predicate)) => Nodes.AndCond(rhs, lhs)
        })
        case (head: Exp, Nil) => head
        case _ => ???
      })

  val predicate: P[Exp] = P(Keyword.Not.parser.!.? ~ compoundComparison)
    .map({
        case (Some(_), exp: Predicate) => Nodes.NotCond(exp)
        case (None, exp) => exp
        case _ => ???
      })

  val predicateOnly: P[Nodes.Predicate] = predicate
      .filter({ case x: Predicate => true case _ => false})
      .map({ case x: Predicate => x case _ => ???})


  protected def opToPredicate(operator: String, lhs: Exp, rhs: Exp): Predicate = {
    operator match {
      case "=" => Nodes.Eq(lhs, rhs)
      case "!=" => Nodes.NtEq(lhs, rhs)
      case "<>" => Nodes.NtEq(lhs, rhs)
      case ">" => ast.Nodes.Gt(lhs, rhs)
      case ">=" => ast.Nodes.GtEq(lhs, rhs)
      case "<" => ast.Nodes.Lt(lhs, rhs)
      case "<=" => ast.Nodes.LtEq(lhs, rhs)
    }
  }

}
