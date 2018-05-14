package com.github.rogueone.ast

import com.github.rogueone.TestSpec
import com.github.rogueone.ast.Nodes.Sql
import com.github.rogueone.parser.Queries

class AssignRelationAliasSpec extends TestSpec {

  "AssignRelationAlias" must "assign an alias name to all relations in the query" in {
    val sql =
      """SELECT col1, col2 x1, col3 FROM table1
        |INNER JOIN table2 ON col1 = col2
        |LEFT OUTER JOIN (SELECT col10 FROM table3) t10 ON col3 = col4
        |FULL OUTER JOIN table4 ON col5 = col6
        |WHERE col3 = DATE '2017-08-01'""".stripMargin
    val queryWriter = new AssignRelationAlias() {
      override protected def getAliasName: String = "test"
    }
    val ast = Queries.select.parse(sql).get.value
    queryWriter.rewrite(ast)
    ast must be (
      Sql.Select(
        Sql.SelectExpression(
          Seq(
            Nodes.ColumnNode(Nodes.Identifier("col1",None),None),
            Nodes.ColumnNode(Nodes.Identifier("col2",None),Some("x1")),
            Nodes.ColumnNode(Nodes.Identifier("col3",None),None)
          ),
          Nodes.JoinedRelation(
            Nodes.JoinedRelation(
              Nodes.JoinedRelation(
                Nodes.TableNode("table1",Some("test")),
                InnerJoin(
                  Nodes.TableNode("table2", Some("test")),
                  Some(Nodes.Eq(Nodes.Identifier("col1",None), Nodes.Identifier("col2",None)))
                )
              ),
              LeftJoin(
                Sql.SubQuery(
                  Sql.SelectExpression(
                    Seq(Nodes.ColumnNode(Nodes.Identifier("col10",None),None)),
                    Nodes.TableNode("table3",Some("test")),
                    None,List()
                  ),
                  Some("t10")
                ),
                Nodes.Eq(Nodes.Identifier("col3",None), Nodes.Identifier("col4",None))
              )
            ),
            FullJoin(
              Nodes.TableNode("table4",Some("test")),
              Nodes.Eq(Nodes.Identifier("col5",None), Nodes.Identifier("col6",None))
            )
          ),Some(Nodes.Eq(Nodes.Identifier("col3",None), Nodes.DateLiteral("2017-08-01"))),
          List()),None
        )

    )
  }
}
