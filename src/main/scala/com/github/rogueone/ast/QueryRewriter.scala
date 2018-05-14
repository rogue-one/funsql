package com.github.rogueone.ast

import com.github.rogueone.ast.Nodes.Sql

trait QueryRewriter {

  self: QueryRewriter =>

  def rewrite(query: Sql.Query): Sql.Query

  def andThen(that: QueryRewriter): QueryRewriter = { (query: Sql.Query) => that.rewrite(self.rewrite(query)) }

}
