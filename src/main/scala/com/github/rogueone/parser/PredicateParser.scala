package com.github.rogueone.parser

import com.github.rogueone.ast
import com.github.rogueone.ast.Nodes
import com.github.rogueone.ast.Nodes.{Exp, Predicate}
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

  val compoundConditionalOp: P[Unit] = IgnoreCase("or") | IgnoreCase("and")

  protected val binaryComparison: P[Exp] = P(MathParser.addSub ~ (conditionalOp.! ~/ MathParser.addSub).rep)
    .map(x => (x._1, x._2.toList) match {
      case (e: Nodes.Exp, (headOp, headExp) :: tail) =>
        tail.foldLeft(opToPredicate(headOp, e, headExp))({ case (l, (op, r)) => opToPredicate(op, l, r)})
      case (x, Nil) => x
    }).log()

  protected val comparison: P[Exp] = P( Keyword.Not.parser ~ binaryComparison | binaryComparison ).log()


  val compoundComparison: P[Exp] = P(comparison ~ (compoundConditionalOp.! ~/ comparison).rep)
      .map(x => x._1 -> x._2.toList match {
        case (head: Predicate, list: List[(String, Predicate) @unchecked]) => list.foldLeft(head)({
          case (rhs: Predicate,(ci"or", lhs: Predicate)) => Nodes.OrCond(rhs, lhs)
          case (rhs: Predicate,(ci"and", lhs: Predicate)) => Nodes.AndCond(rhs, lhs)
        })
        case (head: Exp, Nil) => head
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
