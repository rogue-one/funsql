package com.github.rogueone.data


case class Table(fields: Seq[DataType[Any]], data: Array[Array[DataValue]])
