package com.pirum.exercises.worker

import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration

trait TaskRunner {
  def runTasks(tasks: Seq[Task], timeout: FiniteDuration, workers: Int): Future[ExecutionSummary]
}
