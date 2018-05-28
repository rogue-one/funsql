package com.github.rogueone.ast.validator

import com.github.rogueone.ast.Nodes.Sql

import scala.util.Try

trait SemanticAnalyzer {

  self: SemanticAnalyzer =>

  def analyze(query: Sql.Query): Try[Unit]

  def andThen(that: SemanticAnalyzer): SemanticAnalyzer = {
    (query: Sql.Query) => for { _ <- self.analyze(query); _ <- that.analyze(query) } yield ()
  }

}
