package com.github.rogueone.data

import com.github.rogueone.ast.Plan.Spool
import com.github.rogueone.utils.SpoolException

import scala.collection.mutable

class SpoolHolder {

  private val spoolData: mutable.Map[Spool, TableData] = mutable.Map()

  def loadTableInSpool(name: Spool, tableData: TableData): Unit = {
    spoolData.get(name) match {
      case None => spoolData.update(name, tableData)
      case Some(_) => throw new SpoolException(s"spool with name $name already exists")
    }
  }

  def updateSpool(name: Spool, tableData: TableData): Unit = {
    spoolData.update(name, tableData)
  }

  def getSpool(name: Spool): TableData = {
    spoolData.get(name) match {
      case Some(x) => x
      case None => throw new SpoolException(s"spool with name $name already exists")
    }
  }

}
