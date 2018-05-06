package com.github.rogueone.parser

import java.text.SimpleDateFormat
import com.github.rogueone.parser.LiteralParser._
import com.github.rogueone.{TestSpec, ast}
import fastparse.core.Parsed
import scala.util.Success

class LiteralParserSpec extends TestSpec {

  "LiteralParser" must "parse Integers literals" in {
    val num1  = numberLiteral.parse("+100").get.value
    num1 must be (ast.Nodes.IntegerLiteral("+100"))
    num1.data must be (Success(100L))
    val num2 = numberLiteral.parse("-000100").get.value
    num2 must be (ast.Nodes.IntegerLiteral("-000100"))
    num2.data must be (Success(-100L))
  }

  it must "parse Decimals literals" in {
    val dec1 = decimalLiteral.parse("-10.23").get.value
    dec1 must be (ast.Nodes.DecimalLiteral("-10.23"))
    dec1.data must be (Success(-10.23D))
    val dec2 = decimalLiteral.parse("+100.76").get.value
    dec2 must be (ast.Nodes.DecimalLiteral("+100.76"))
    dec2.data must be (Success(100.76D))
  }

  it must "parse String literals" in {
    val str = stringLiteral.parse("'hello world'").get.value
    str must be (ast.Nodes.StringLiteral("hello world"))
    str.data must be (Success("hello world"))
    stringLiteral.parse("'foobar").asInstanceOf[Parsed.Failure[_,_]].index must be (7)
    stringLiteral.parse("foobar'").asInstanceOf[Parsed.Failure[_,_]].index must be (0)
  }

  it must "parse Date literals" in {
    val date1 = dateLiteral.parse("date '2015-12-31'").get.value
    date1 must be (ast.Nodes.DateLiteral("2015-12-31"))
    date1.data must be (Success(new SimpleDateFormat("yyyy-MM-dd").parse("2015-12-31")))
    val date2 = dateLiteral.parse("date'2015-12-32'").get.value
    date2 must be (ast.Nodes.DateLiteral("2015-12-32"))
    date2.data.failed.get.getMessage must be ("Unparseable date: \"2015-12-32\"")
  }

  it must "parse Timestamp literals" in {
    val date1 = timestampLiteral.parse("timestamp '2015-12-31 12:00:00'").get.value
    date1 must be (ast.Nodes.TimestampLiteral("2015-12-31 12:00:00"))
    date1.data must be (Success(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2015-12-31 12:00:00")))
    val date2 = timestampLiteral.parse("timestamp '2015-12-32 12:00:00'").get.value
    date2 must be (ast.Nodes.TimestampLiteral("2015-12-32 12:00:00"))
    date2.data.failed.get.getMessage must be ("Unparseable date: \"2015-12-32 12:00:00\"")
  }

}
