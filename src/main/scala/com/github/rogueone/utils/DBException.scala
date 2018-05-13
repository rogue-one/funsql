package com.github.rogueone.utils


sealed class DBException(message: String) extends Exception(message)

class SpoolException(message: String) extends DBException(message)

class TableException(message: String) extends DBException(message)

class SemanticException(message: String) extends DBException(message)
