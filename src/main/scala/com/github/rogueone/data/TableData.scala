package com.github.rogueone.data


case class TableData(fields: Seq[DataType[Any]], data: Array[Array[DataValue]])
