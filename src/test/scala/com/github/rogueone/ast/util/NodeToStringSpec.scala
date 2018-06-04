package com.github.rogueone.ast.util

import com.github.rogueone.TestSpec
import com.github.rogueone.parser.Queries
import NodeToString._
import com.github.rogueone.ast.Nodes.Sql
import com.github.rogueone.utils.Utils._

class NodeToStringSpec extends TestSpec {

  "NodeToString" must "" in {
    val sql =
      """SELECT col1, col2 x1, col3 FROM table1
        |INNER JOIN table2 ON col1 = col2
        |LEFT OUTER JOIN (SELECT col10 FROM table3) t10 ON col3 = col4
        |FULL OUTER JOIN table4 ON col5 = col6
        |WHERE col3 = DATE '2017-08-01'""".stripMargin
    val ast = Queries.select.parse(sql).get.value
    stringify[Sql.Query](ast).oneLiner must be ("SELECT `col1`,`col2` AS x1,`col3` FROM table1 INNER JOIN " +
      "table2 ON (`col1` = `col2`) LEFT OUTER JOIN ( SELECT `col10` FROM table3 ) t10 ON (`col3` = `col4`) FULL OUTER " +
      "JOIN table4 ON (`col5` = `col6`) WHERE (`col3` = DATE '2017-08-01')")
  }
}
