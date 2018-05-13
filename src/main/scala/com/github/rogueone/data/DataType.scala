package com.github.rogueone.data

import java.util.Date

sealed class DataType(val name: String) { type T }

object DataType {

  object IntType extends DataType("int") { type T = Int }

  object BigIntType extends DataType("bigint") { type T = Long }

  object DecimalType extends DataType("decimal") { type T = Double }

  object StringType extends DataType("string") { type T = String }

  object DateType extends DataType("date") { type T = Date }

  object TimestampType extends DataType("timestamp") { type T = Date }

}
