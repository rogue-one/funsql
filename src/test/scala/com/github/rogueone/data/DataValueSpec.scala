package com.github.rogueone.data

import com.github.rogueone.TestSpec
import com.github.rogueone.data.DataValue.{IntValue, StringValue}

class DataValueSpec extends TestSpec {

  "DataValue" must "map and flatMap work" in {
    val value = IntValue(10)
    value.map(_ * 10) must be (IntValue(100))
    value.flatMap(x => StringValue(x.toString)) must be (StringValue("10"))
  }

}
