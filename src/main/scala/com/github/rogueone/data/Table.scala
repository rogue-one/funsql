package com.github.rogueone.data


/**
  *
  * @param fields
  * @param data
  */
class Table(val fields: Seq[Column],
            val data: Array[Array[DataValue]]) {

  /**
    * get column by name
    * @param columnName optional column
    * @return
    */
  def getColumnByName(columnName: String): Option[Column] =
    fields.find({ case Column(`columnName`, _) => true case _ => false })


}
