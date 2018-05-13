package com.github.rogueone.work

import com.github.rogueone.ast.Nodes.ColumnNode
import com.github.rogueone.data.Table

class RelationSelector(protected val tableData: Table,
                       protected val columns: Seq[ColumnNode]) {


}