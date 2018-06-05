package com.github.rogueone.ast.rewriter

import com.github.rogueone.ast.Nodes
import com.github.rogueone.ast.Nodes.Sql
import com.github.rogueone.data.DatabaseLike

trait ExpandStar extends QueryRewriteStrategy {

  abstract override def rewrite(query: Sql.Query, database: DatabaseLike): Sql.Query = {
    Nodes.Sql.Select(Nodes.SelectExpression(Seq(), Nodes.TableNode("", None), None, Seq()), None)
  }

  private class Processor(query: Sql.Select, database: DatabaseLike) {

    protected def process(sql: Sql.Query): Sql.Query = {
      sql match {
        case x: Sql.Select => x
        case x: Sql.SubQuery => x
      }
    }

  }

}
