package com.github.rogueone.ast.rewriter

import com.github.rogueone.ast.Nodes.Sql
import com.github.rogueone.data.DatabaseLike

protected[rewriter] trait QueryRewriter {

  def rewrite(query: Sql.Query, database: DatabaseLike): Sql.Query

  def andThen(that: QueryRewriter): QueryRewriter = {
    (query: Sql.Query, database: DatabaseLike) => that.rewrite(this.rewrite(query, database), database)
  }

}

object QueryRewriter {

  val queryReWriters: QueryRewriter = AssignRelationAlias

}
