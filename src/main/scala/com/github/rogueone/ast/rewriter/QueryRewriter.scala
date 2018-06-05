package com.github.rogueone.ast.rewriter

import com.github.rogueone.ast.Nodes.Sql
import com.github.rogueone.ast.rewriter.QueryRewriter.IdentityStrategy
import com.github.rogueone.data.DatabaseLike

class QueryRewriter extends IdentityStrategy {

  self: QueryRewriteStrategy =>

  def rewrite(sql: Sql.Query): Sql.Query = self.rewrite(sql)

}

object QueryRewriter {

  trait IdentityStrategy extends QueryRewriteStrategy {
    override def rewrite(query: Sql.Query, database: DatabaseLike): Sql.Query = query
  }

  new QueryRewriter with AssignFieldPrefix with AssignRelationAlias with ExpandStar

}
