package com.github.rogueone.parser

import com.github.rogueone.ast
import com.github.rogueone.ast.Nodes
import com.github.rogueone.ast.Nodes.{Exp, Predicate}
import Parser.White._
import fastparse.noApi._
import com.github.rogueone.utils.Utils._
import fastparse.noApi

object PredicateParser {

  val eq: P[Unit] = P { "=" }

  val neq: P[Unit] = P { "!=" | "<>" }

  val gt: P[Unit] = P { ">" }

  val lt: P[Unit] = P { "<" }

  val gteq: P[Unit] = P { ">=" }

  val lteq: P[Unit] = P { "<=" }

  val conditionalOp: P[Unit] = eq | neq | gt | lt | gteq | lteq

  val compoundConditionalOp: P[Unit] = IgnoreCase("or") | IgnoreCase("and")

  val setComparison: P[(String, Seq[Exp])] = P(Keyword.In.parser.! ~ "(" ~/ MathParser.addSub.rep(min=1, sep=",") ~ ")")

  protected val binaryComparison: P[Exp] = P(MathParser.addSub ~
    ((conditionalOp.! ~/ MathParser.addSub) | setComparison).rep)
    .map(y => y._1 -> y._2.toList match {
      case (e: Nodes.Exp, (headOp, headExp: Nodes.Exp) :: tail) =>
        tail.foldLeft(opToPredicate(headOp, e, headExp))({
          case (l, (op, r: Nodes.Exp)) => opToPredicate(op, l, r)
          case (l, (ci"in", r: Seq[Nodes.Exp @unchecked])) => Nodes.InClause(l, r)
        })
      case (e: Nodes.Exp, (ci"in", nodes: Seq[Nodes.Exp @unchecked]) :: tail) =>
        tail.foldLeft(Nodes.InClause(e, nodes): Predicate)({
          case (l, (op, r: Nodes.Exp)) => opToPredicate(op, l, r)
          case (l, (ci"in", r: Seq[Nodes.Exp @unchecked])) => Nodes.InClause(l, r)
        })
      case (x, Nil) => x
      case _ => ???
    })

  protected val comparison: P[Exp] = P( Keyword.Not.parser.!.? ~ binaryComparison)
    .map({
      case (Some(_), exp: Predicate) => Nodes.NotCond(exp)
      case (None, exp) => exp
      case _ => ???
    })


  val compoundComparison: P[Exp] = P(comparison ~ (compoundConditionalOp.! ~/ comparison).rep)
      .map(x => x._1 -> x._2.toList match {
        case (head: Predicate, list: List[(String, Predicate) @unchecked]) => list.foldLeft(head)({
          case (rhs: Predicate,(ci"or", lhs: Predicate)) => Nodes.OrCond(rhs, lhs)
          case (rhs: Predicate,(ci"and", lhs: Predicate)) => Nodes.AndCond(rhs, lhs)
        })
        case (head: Exp, Nil) => head
        case _ => ???
      })

  val predicate = P (Keyword.Not.parser.!.? ~ compoundComparison).map({
    case (Some(_), exp: Predicate) => Nodes.NotCond(exp)
    case (None, exp) => exp
    case _ => ???
  })

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
