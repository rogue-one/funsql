package com.github.rogueone.ast.semantic

import com.github.rogueone.ast.Nodes.Sql
import com.github.rogueone.data.Database
import scala.util.Try

object TableValidator extends SemanticAnalyzer {

  override def analyze(query: Sql.Query): Try[Unit] = {
      Try(QueryAnalyzer.getTables(query).foreach({ x => Database.getTable(x.name)}))
  }

}
