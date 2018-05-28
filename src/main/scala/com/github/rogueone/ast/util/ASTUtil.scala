package com.github.rogueone.ast.util

import com.github.rogueone.ast.Nodes.Sql.Query
import com.github.rogueone.ast.Nodes._

object ASTUtil {

  implicit class RelationUtil(protected val relation: Relation) extends RelationUtilHelper

  implicit class QueryUtil(protected val query: Query) extends QueryUtilHelper

}
