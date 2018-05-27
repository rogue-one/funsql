package com.github.rogueone.ast.semantic

import com.github.rogueone.TestSpec
import com.github.rogueone.ast.Nodes.Sql
import com.github.rogueone.ast.Nodes.Sql.SubQuery
import com.github.rogueone.ast.{InnerJoin, Nodes}
import com.github.rogueone.data.DataType.StringType
import com.github.rogueone.data.{Column, DatabaseLike, Table}
import com.github.rogueone.parser.Queries
import scala.collection.mutable.ArrayBuffer

class FieldAnalyzerSpec extends TestSpec {

  "FieldAnalyzer" must "list all fields" in {
    val table1 = new Table("table1",
      List(Column("col1", StringType), Column("col2", StringType), Column("col3", StringType)), Array())
    val table2 = new Table("table2",
      List(Column("col4", StringType), Column("col5", StringType), Column("col6", StringType)), Array())
    val database = new DatabaseLike {}
    database.saveTable(table1)
    database.saveTable(table2)
    var select = Queries.select.parse(
      """|SELECT col1,col4, max(col3) FROM
         |(select col1, col2, col3 from table1) t0
         |INNER JOIN (select col4, col5, col6 FROM table2) t1
         |ON t0.col3 = t1.col6
         |WHERE col6 = 20 AND col5 IN (10, 20, 30)
         |GROUP BY col1, col4
         |""".stripMargin)
    new AssignFieldPrefix(database).rewrite(select.get.value) must be (
      Nodes.Sql.Select(
        Sql.SelectExpression(
          ArrayBuffer(
            Nodes.ColumnNode(Nodes.Identifier("col1", None), None),
            Nodes.ColumnNode(Nodes.Identifier("col4", None), None),
            Nodes.ColumnNode(Nodes.Function(Nodes.Identifier("max", None), ArrayBuffer(Nodes.Identifier("col3", None))), None)
          ),
          Nodes.JoinedRelation(
            Sql.SubQuery(
              Sql.SelectExpression(
                ArrayBuffer(
                  Nodes.ColumnNode(Nodes.Identifier("col1", None), None),
                  Nodes.ColumnNode(Nodes.Identifier("col2", None), None),
                  Nodes.ColumnNode(Nodes.Identifier("col3", None), None)
                ),
                Nodes.TableNode("table1", None), None, List()
              ), Some("t0")
            ),
            InnerJoin(
              SubQuery(
                Sql.SelectExpression(
                  ArrayBuffer(
                    Nodes.ColumnNode(Nodes.Identifier("col4", None), None),
                    Nodes.ColumnNode(Nodes.Identifier("col5", None), None),
                    Nodes.ColumnNode(Nodes.Identifier("col6", None), None)
                  ),
                  Nodes.TableNode("table2", None), None, List()
                ), Some("t1")
              ),
              Some(Nodes.Eq(Nodes.Identifier("col3", Some("t0")), Nodes.Identifier("col6", Some("t1"))))
            )
          ), Some(
            Nodes.AndCond(
              Nodes.Eq(Nodes.Identifier("col6", None), Nodes.IntegerLiteral("20")),
              Nodes.InClause(
                Nodes.Identifier("col5", None),
                ArrayBuffer(Nodes.IntegerLiteral("10"), Nodes.IntegerLiteral("20"), Nodes.IntegerLiteral("30"))
              )
            )
          ),
          ArrayBuffer(Nodes.Identifier("col1", None), Nodes.Identifier("col4", None))
        ), None)
    )

    //    contain only (
    //      Nodes.Identifier("col1",None), Nodes.Identifier("col2",None), Nodes.Identifier("col3",None),
    //      Nodes.Identifier("col100",None), Nodes.Identifier("col99",None), Nodes.Identifier("col10",Some("t0")),
    //      Nodes.Identifier("col12",Some("t0")), Nodes.Identifier("col4",Some("t1")), Nodes.Identifier("col5",Some("t1")),
    //      Nodes.Identifier("col60",Some("t0")), Nodes.Identifier("col59", Some("t1")), Nodes.Identifier("col89",None),
    //      Nodes.Identifier("col56",None)
    //    )
    //    select = Queries.select.parse (
    //      """SELECT col1, col2, max(col3)
    //        |FROM table WHERE col2 IN (SELECT col4 FROM table2) t0
    //        |GROUP BY col1,col2""".stripMargin
    //    )
    //    new FieldAnalyzer {}.getFields(select.get.value) must contain only (
    //      Nodes.Identifier("col1",None), Nodes.Identifier("col2",None), Nodes.Identifier("col3",None),
    //      Nodes.Identifier("col4",None)
    //    )
  }

}
