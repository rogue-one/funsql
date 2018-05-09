package com.github.rogueone.parser

import com.github.rogueone.TestSpec
import com.github.rogueone.ast.Nodes
import com.github.rogueone.ast.Nodes.Sql
import scala.collection.mutable.ArrayBuffer

class QueriesSpec extends TestSpec {

  "Queries" must "parse a select query" in {
    val sql = "SELECT col1,col3,10,'tango' FROM table_name WHERE col4 = col4 AND func(col1) < 10"
    Queries.select.parse(sql).get.value must be(
      Sql.Select(
        ArrayBuffer(Nodes.Identifier("col1"), Nodes.Identifier("col3"), Nodes.IntegerLiteral("10"),
          Nodes.StringLiteral("tango")),
        Nodes.Table("table_name", None),
        Some(
          Nodes.AndCond(
            Nodes.Eq(Nodes.Identifier("col4"), Nodes.Identifier("col4")),
            Nodes.Lt(
              Nodes.Function(Nodes.Identifier("func"), ArrayBuffer(Nodes.Identifier("col1"))),
              Nodes.IntegerLiteral("10")
            )
          )
        ),
        List(), None, None)
    )
  }

  it must "parse select queries with group by and limit" in {
    val sql = "SELECT col1,max(col3) FROM table_name WHERE col4 = col4 GROUP BY col1 LIMIT 10"
    Queries.select.parse(sql).get.value must be (
      Sql.Select(
        Seq(Nodes.Identifier("col1"), Nodes.Function(Nodes.Identifier("max"), ArrayBuffer(Nodes.Identifier("col3")))),
        Nodes.Table("table_name", None),
        Some(Nodes.Eq(Nodes.Identifier("col4"),Nodes.Identifier("col4"))),
        Seq(Nodes.Identifier("col1")),
        Some(Nodes.IntegerLiteral("10")),
        None
      )
    )
  }

  it must "parse query with sub query" in {
    val sql = "SELECT col1,max(col3) FROM table_name WHERE col4 IN (select col1, col2 FROM table) GROUP BY col1"
    Queries.select.parse(sql).get.value must be {
      Sql.Select(
        Seq(
          Nodes.Identifier("col1"),
          Nodes.Function(Nodes.Identifier("max"), Seq(Nodes.Identifier("col3")))
        ),
        Nodes.Table("table_name", None),
        Some(
          Nodes.SubQuery(
            Nodes.Identifier("col4"),
            Sql.Select(Seq(Nodes.Identifier("col1"), Nodes.Identifier("col2")), Nodes.Table("table", None),
              None, Nil, None, None)
          )
        ),
        Seq(Nodes.Identifier("col1")),
        None,
        None
      )
    }
  }

  it must "parse select * from" in {
    Queries.select.parse("SELECT * FROM table_name").get.value must be (
      Sql.Select(Seq(Sql.Star), Nodes.Table("table_name", None), None, Nil, None, None)
    )
    Queries.select.parse("SELECT col1,* FROM table_name").get.value must be (
      Sql.Select(Seq(Nodes.Identifier("col1"), Sql.Star), Nodes.Table("table_name", None), None, Nil, None, None)
    )
  }

}
