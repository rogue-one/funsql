package com.github.rogueone.data

import com.github.rogueone.utils.{DBException, TableException}

import scala.collection.mutable

object Database {

  private val tables: mutable.Map[String, TableData] = mutable.Map()

  /**
    * save table
    * @param name name of the table
    * @param tableData table data
    * @param overwrite overwrite table
    */
  def saveTable(name: String, tableData: TableData, overwrite: Boolean): Unit = {
    if (overwrite | !tables.contains(name))
        tables.update(name, tableData)
    else
      throw new DBException(s"table $name already exists")
  }

  /**
    * get table by name
    * @param name
    * @return
    */
  def getTable(name: String): TableData = {
    tables.get(name) match {
      case Some(x) => x
      case None => throw new TableException(s"table $name doesn't exists")
    }
  }

}
