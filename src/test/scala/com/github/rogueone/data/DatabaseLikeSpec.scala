package com.github.rogueone.data

import com.github.rogueone.TestSpec

class DatabaseLikeSpec extends TestSpec {

  "DatabaseLike" must "create table and list it" in {
    val db = new DatabaseLike {}
    db.saveTable(new Table("test_table_1", Seq(Column("col1", DataType.StringType),
      Column("col2", DataType.IntType)), Array(
      Array(DataValue.StringValue("foo"), DataValue.IntValue(10)),
      Array(DataValue.StringValue("bar"), DataValue.IntValue(20))
    )))
    db.saveTable(
      new Table("test_table_2", Seq(Column("col1", DataType.StringType),
      Column("col2", DataType.IntType)), Array(
      Array(DataValue.StringValue("hello"), DataValue.IntValue(10)),
      Array(DataValue.StringValue("world"), DataValue.IntValue(20))
      ))
    )
    db.listTables must be (Seq("test_table_1", "test_table_2"))
    db.dropTable("test_table_1")
    db.listTables must contain only ("test_table_2")
    val table = db.getTable("test_table_2")
    table.name must be ("test_table_2")
    table.fields.map(_.name) must be (Seq("col1", "col2"))
  }

}
