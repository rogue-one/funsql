package com.github.rogueone.parser

import com.github.rogueone.TestSpec
import fastparse.core.Parsed.Failure

class QueriesFailSpec extends TestSpec {

  "QueriesFail" must "subqueries without alias should fail" in {
    val sql = "SELECT col1,col2 FROM (SELECT * FROM table1) WHERE col4 = col5"
    Queries.select.parse(sql) match {
      case x: Failure[_, _] => x.msg must be ("all derived table must have an alias:1:23 ...\"(SELECT * \"")
      case _ => ???
    }
  }

}
