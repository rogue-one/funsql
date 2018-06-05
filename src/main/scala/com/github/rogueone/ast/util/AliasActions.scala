package com.github.rogueone.ast.util

import com.github.rogueone.ast.Nodes.Aliasable


object AliasActions {

  trait EditAlias {

    self: Aliasable =>

    /**
      * set alias if not already sey
      * @param name
      */
    def setAliasName(name: String): Unit = alias match { case Some(_) => () case None => alias = Some(name) }

  }

  trait ReadAlias {

    self: Aliasable =>

    /**
      *
      * @return
      */
    def getAliasName: Option[String] = alias
  }

}





