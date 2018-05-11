package com.github.rogueone.work

import com.github.rogueone.ast.Plan.LoadTable
import com.github.rogueone.data.Database

class SpoolLoader(loadTable: LoadTable) extends Worker(loadTable) {

  override def work: Unit = {
    //loadTable.projection
    Database.getTable(loadTable.tableName)
  }

}
