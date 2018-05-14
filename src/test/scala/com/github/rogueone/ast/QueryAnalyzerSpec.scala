package com.github.rogueone.ast

import com.github.rogueone.TestSpec
import com.github.rogueone.ast.Nodes.Sql
import com.github.rogueone.parser.Queries

class QueryAnalyzerSpec extends TestSpec {

  "QueryAnalyzer" must "parse get all tables in query" in {
    val sql =
      """SELECT col1, col2 x1, col3 FROM table1
        |INNER JOIN table2 ON col1 = col2
        |LEFT OUTER JOIN (SELECT col10 FROM table3) t10 ON col3 = col4
        |FULL OUTER JOIN table4 ON col5 = col6
        |WHERE col3 = DATE '2017-08-01'""".stripMargin
    val query: Sql.Select = Queries.select.parse(sql).get.value
    val analyzer = new QueryAnalyzer(query)
    analyzer.getTables must contain only (Nodes.TableNode("table1",None), Nodes.TableNode("table2",None),
      Nodes.TableNode("table3",None), Nodes.TableNode("table4",None))
    new QueryAnalyzer(Queries.select.parse("SELECT * FROM table1").get.value).getTables must
      contain only (Nodes.TableNode("table1",None))
  }

}
