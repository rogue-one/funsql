package com.github.rogueone.data

import com.github.rogueone.utils.DBException

import scala.collection.mutable

object Database {

  private val tables: mutable.Map[String, Table] = mutable.Map()

  def saveTable(name: String, table: Table, overwrite: Boolean): Unit = {
    if (overwrite | !tables.contains(name))
        tables.update(name, table)
    else
      throw new DBException(s"table $name already exists")
  }

}
