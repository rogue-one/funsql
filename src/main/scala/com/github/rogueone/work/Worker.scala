package com.github.rogueone.work

import com.github.rogueone.ast.PlanAction

abstract class Worker(plan: PlanAction) {
  def work: Unit
}
