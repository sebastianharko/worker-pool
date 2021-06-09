package com.pirum.exercises.worker

case class ExecutionSummary(succesful: Seq[TaskId] = Nil, failed: Seq[TaskId] = Nil, timedOut: Seq[TaskId] = Nil) {

  def addSuccesful(taskId: TaskId) = {
    copy(succesful = taskId +: succesful)
  }

  def addFailed(taskId: TaskId) = {
    copy(failed = taskId +: failed)
  }

  def addTimedOut(taskId: TaskId) = {
    copy(timedOut = taskId +: timedOut)
  }

  private def mkStringFromList(lst: Seq[TaskId]) =
    lst.reverse.mkString("[", ", ", "]") // reverse before printing ...because we've been prepending

  override def toString = {
    s"""|result.successful = ${mkStringFromList(succesful)}
        |result.failed = ${mkStringFromList(failed)}
        |result.timedOut = ${mkStringFromList(timedOut)}""".stripMargin
  }
}