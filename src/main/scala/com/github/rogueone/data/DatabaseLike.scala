package com.github.rogueone.data

import com.github.rogueone.utils.{DBException, TableException}

trait DatabaseLike {

  private var tables: List[Table] = List()

  /**
    * save table
    * @param tableData table data
    * @param overwrite overwrite table
    *
    */
  def saveTable(tableData: Table, overwrite: Boolean=false): Unit = {
    if (overwrite | !tables.map(_.name).contains(tableData.name))
      tables = tables :+ tableData
    else
      throw new DBException(s"table ${tableData.name} already exists")
  }

  /**
    * get table by name
    * @param name
    * @return
    */
  def getTable(name: String): Table = {
    tables.find(_.name == name) match {
      case Some(x) => x
      case None => throw new TableException(s"table $name doesn't exists")
    }
  }

  /**
    * list tables
    * @return
    */
  def listTables: Seq[String] = tables.map(_.name)


  /**
    * drop the table
    * @param name
    */
  def dropTable(name: String): Unit = {
    tables.find(_.name == name) match {
      case Some(_) => tables = tables.filterNot(_.name == name)
      case None => new TableException(s"table $name not found")
    }
  }

}
