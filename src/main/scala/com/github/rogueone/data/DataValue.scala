package com.github.rogueone.data

import java.util.Date


sealed trait DataValue

object DataValue {

  object NullValue extends DataValue

  sealed abstract class BaseDataValue[+T](value: T) extends DataValue

  class IntValue(value: Int) extends BaseDataValue[Int](value)

  class BigIntValue(value: Long) extends BaseDataValue[Long](value)

  class StringValue(value: String) extends BaseDataValue[String](value)

  class DecimalValue(value: Double) extends BaseDataValue[Double](value)

  class DateValue(value: Date) extends BaseDataValue[Date](value)

  class TimestampValue(value: Date) extends BaseDataValue[Date](value)

}

