package com.github.rogueone.work

import com.github.rogueone.ast.Nodes.Column
import com.github.rogueone.data.{DataType, DataValue, TableData}

class RelationSelector(protected val tableData: TableData,
                       protected val columns: Seq[Column]) {


}