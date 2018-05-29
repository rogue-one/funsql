package com.github.rogueone.ast.rewriter

import com.github.rogueone.ast.Nodes.Sql
import com.github.rogueone.data.DatabaseLike

protected[rewriter] trait QueryRewriteStrategy {

  def rewrite(query: Sql.Query, database: DatabaseLike): Sql.Query

}


