package com.github.rogueone.data

import java.util.Date


sealed trait DataValue

object DataValue {

  object NullValue extends DataValue

  sealed abstract class BaseDataValue extends DataValue {
    type T
    type This <: BaseDataValue
    val value: T
    def map(f: (T => T)): This = { this.create(f.apply(value)) }
    def flatMap[P <: BaseDataValue](f: (T => P)): BaseDataValue = { f.apply(value) }
    protected def create(input: T): This
  }

  case class IntValue(value: Int) extends BaseDataValue {
    type T = Int
    type This = IntValue
    override protected def create(input: Int): IntValue = IntValue(input)
  }

  case class BigIntValue(value: Long) extends BaseDataValue {
    type T = Long
    type This = BigIntValue
    override protected def create(input: Long): BigIntValue = BigIntValue(input)
  }

  case class StringValue(value: String) extends BaseDataValue {
    type T = String
    type This = StringValue
    override protected def create(input: String): StringValue = StringValue(input)
  }

  case class DecimalValue(value: Double) extends BaseDataValue {
    type T = Double
    type This = DecimalValue
    override protected def create(input: Double): DecimalValue = DecimalValue(input)
  }

  case class DateValue(value: Date) extends BaseDataValue {
    type T = Date
    type This = DateValue
    override protected def create(input: Date): DateValue = DateValue(input)
  }

  case class TimestampValue(value: Date) extends BaseDataValue {
    type T = Date
    type This = TimestampValue
    override protected def create(input: Date): TimestampValue = TimestampValue(input)
  }

}

