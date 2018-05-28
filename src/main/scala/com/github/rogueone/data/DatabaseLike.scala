package com.github.rogueone.data

import com.github.rogueone.utils.{DBException, SemanticException, TableException}
import scala.collection.mutable

trait DatabaseLike {

  protected val tables: mutable.ListBuffer[Table] = mutable.ListBuffer[Table]()

  /**
    * save table
    * @param tableData table data
    * @param overwrite overwrite table
    *
    */
  def saveTable(tableData: Table, overwrite: Boolean=false): Unit = {
    if (overwrite | !tables.map(_.name).contains(tableData.name))
      tables.append(tableData)
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
      case None => throw new SemanticException(s"table $name doesn't exists")
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
    tables.zipWithIndex.find({case (x, _) => x.name == name}) match {
      case Some((_, index)) => tables.remove(index)
      case None => new TableException(s"table $name not found")
    }
  }

}
