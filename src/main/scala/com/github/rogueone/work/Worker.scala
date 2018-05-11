package com.github.rogueone.work

import com.github.rogueone.ast.Plan

abstract class Worker(plan: Plan) {
  def work: Unit
}
