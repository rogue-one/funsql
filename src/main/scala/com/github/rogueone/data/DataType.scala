package com.github.rogueone.data

import java.util.Date

sealed class DataType[+T](val name: String)

object DataType {

  object IntType extends DataType[Int]("int")

  object BigIntType extends DataType[Long]("bigint")

  object DecimalType extends DataType[Double]("decimal")

  object StringType extends DataType[String]("string")

  object DateType extends DataType[Date]("date")

  object TimestampType extends DataType[Date]("timestamp")

}
