package com.github.rogueone.data

import com.github.rogueone.utils.SemanticException


/**
  * @param name
  * @param fields
  * @param data
  */
class Table(val name: String,
            val fields: Seq[Column],
            val data: Array[Array[DataValue]]) {

  /**
    * get column by name
    * @param columnName optional column
    * @return
    */
  def getColumnByName(columnName: String): Column =
    fields.find({ case Column(`columnName`, _) => true case _ => false }) match {
      case Some(x) => x
      case None => throw new SemanticException(s"column $columnName not found in table $name")
    }


}
