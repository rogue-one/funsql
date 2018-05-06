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

  protected val binaryComparison: P[Predicate] = P(MathParser.addSub ~ (conditionalOp.! ~/ MathParser.addSub).rep)
    .filter({case (_, Nil) => false case _ => true})
    .map({
      case (e: Nodes.Exp, (headOp, headExp) :: tail) =>
        tail.foldLeft(opToPredicate(headOp, e, headExp))({ case (l, (op, r)) => opToPredicate(op, l, r)})
      case _ => ???
    }).log()

  protected val comparison: P[Predicate] = P( Keyword.Not.parser ~ binaryComparison | binaryComparison ).log()


  val compoundComparison: P[Predicate] = P(comparison ~ (compoundConditionalOp.! ~/ comparison).rep)
      .map({ case (head, list) => list.foldLeft(head)({
        case (rhs,(ci"or", lhs)) => Nodes.OrCond(rhs, lhs)
        case (rhs,(ci"and", lhs)) => Nodes.AndCond(rhs, lhs)
        })
      }).log()

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
