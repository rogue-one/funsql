package com.github.rogueone.parser

import com.github.rogueone.TestSpec
import com.github.rogueone.ast.Nodes
import com.github.rogueone.ast.Nodes.{Identifier, IntegerLiteral, Sql}

import scala.collection.mutable.ArrayBuffer

class QueriesSpec extends TestSpec {

  "Queries" must "parse a select query" in {
    val sql = "SELECT col1,col3,10,'tango' FROM table_name WHERE col4 = col4 AND func(col1) < 10"
    Queries.basicSelect.parse(sql).get.value must be(
      Sql.BasicSelect(
        Seq(
          Nodes.Column(Nodes.Identifier("col1")),
          Nodes.Column(Nodes.Identifier("col3")),
          Nodes.Column(Nodes.IntegerLiteral("10")),
          Nodes.Column(Nodes.StringLiteral("tango"))
        ),
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
        Nil)
    )
  }

  it must "parse a uber select" in {
    val sql = "SELECT col1,max(col3) as test1 FROM table_name t0 WHERE col4 = col4 GROUP BY col1 LIMIT 10"
    Queries.select.parse(sql).get.value must be(
      Sql.Select(
        Sql.BasicSelect(
          Seq(
            Nodes.Column(Nodes.Identifier("col1")),
            Nodes.Column(Nodes.Function(Nodes.Identifier("max"), ArrayBuffer(Nodes.Identifier("col3"))), Some("test1"))
          ),
          Nodes.Table("table_name", Some("t0")),
          Some(Nodes.Eq(Nodes.Identifier("col4"), Nodes.Identifier("col4"))),
          Seq(Nodes.Identifier("col1"))
        ),
        Some(IntegerLiteral("10"))
      )
    )
  }

  it must "parse query with set comparison with sub query" in {
    val sql = "SELECT col1,max(col3) FROM table_name WHERE col4 IN (select col1, col2 FROM table) GROUP BY col1"
    Queries.basicSelect.parse(sql).get.value must be {
      Sql.BasicSelect(
        Seq(
          Nodes.Column(Nodes.Identifier("col1")),
          Nodes.Column(Nodes.Function(Nodes.Identifier("max"), Seq(Nodes.Identifier("col3"))))
        ),
        Nodes.Table("table_name", None),
        Some(
          Nodes.SubQuery(
            Nodes.Identifier("col4"),
            Sql.BasicSelect(
              Seq(Nodes.Column(Nodes.Identifier("col1")), Nodes.Column(Nodes.Identifier("col2"))),
              Nodes.Table("table", None), None, Nil)
          )
        ),
        Seq(Nodes.Identifier("col1"))
      )
    }
  }

  it must "parse query with subquery" in {
    val sql = "SELECT col1,col2 FROM (SELECT * FROM table1) WHERE col4 = col5"
    Queries.select.parse(sql).get.value must be (
      Sql.Select(
        Sql.BasicSelect(
          Seq(Nodes.Column(Nodes.Identifier("col1")), Nodes.Column(Nodes.Identifier("col2"))),
          Sql.SelectRelation(Sql.BasicSelect(Seq(Nodes.Star), Nodes.Table("table1"), None, Nil), None),
          Some(Nodes.Eq(Nodes.Identifier("col4"), Nodes.Identifier("col5"))), Nil
        ), None
      )
    )
  }

  it must "parse select * from" in {
    Queries.basicSelect.parse("SELECT col1 as x1,* FROM table_name").get.value must be(
      Sql.BasicSelect(
        Seq(
          Nodes.Column(Nodes.Identifier("col1"), Some("x1")),
          Nodes.Star
        ),
        Nodes.Table("table_name", None), None, Nil)
    )
    Queries.basicSelect.parse("SELECT col1,* FROM table_name").get.value must be (
      Sql.BasicSelect(
        Seq(Nodes.Column(Nodes.Identifier("col1")), Nodes.Star),
        Nodes.Table("table_name", None),
        None, Nil)
    )
  }

}
