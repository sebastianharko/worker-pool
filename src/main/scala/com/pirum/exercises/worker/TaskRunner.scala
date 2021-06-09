package com.pirum.exercises.worker

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.Future
import scala.concurrent.Future.firstCompletedOf
import scala.util.{Failure, Success}
import akka.actor.ActorSystem
import akka.{NotUsed}
import akka.pattern.after
import akka.actor.{ActorSystem, Scheduler}
import akka.stream.scaladsl.{Sink, Source}

case class ExecutionSummary(succesful: List[TaskId], failed: List[TaskId], timedOut: List[TaskId]) {

  def addSuccesful(taskId: TaskId) = {
    this.copy(succesful = taskId :: succesful)
  }

  def addFailed(taskId: TaskId) = {
    this.copy(failed = taskId :: failed)
  }

  def addTimedOut(taskId: TaskId) = {
    this.copy(timedOut = taskId :: timedOut)
  }

  private def mkStringFromList(lst: List[TaskId]) =
    lst.reverse.mkString("[", ", ", "]") // reverse before printing ...because we've been prepending

  override def toString: String = {
    s"""
       |result.successful = ${mkStringFromList(succesful)}
       |result.failed = ${mkStringFromList(failed)}
       |result.timedOut = ${mkStringFromList(timedOut)}
       |""".stripMargin
  }
}

object ExecutionSummary {
  def empty = ExecutionSummary(succesful = Nil, failed = Nil, timedOut = Nil)
}

trait TaskRunner {
  def runTasks(tasks: Seq[Task], timeout: FiniteDuration, workers: Int): Future[ExecutionSummary]
}

// A concise and clear Akka Streams implementation
class TaskRunnerImpl(implicit actorSystem: ActorSystem) extends TaskRunner {

  import actorSystem.dispatcher

  enum ExecutionResult(id: TaskId):
    case Success(id: TaskId) extends ExecutionResult(id)
    case Failed(id: TaskId) extends ExecutionResult(id)
    case TimedOut(id: TaskId) extends ExecutionResult(id)

  case class TaskRunnerTimeout()

  extension (task: Task)
  // execute the task and return an ExecutionResult
    def toExecutionResult() = {
      task.execute.transform {
        case Failure(_) => Success(ExecutionResult.Failed(task.id))
        case Success(_) => Success(ExecutionResult.Success(task.id))
      }
    }

  extension (task: Task)
    def executeWithTimeout(timeoutFuture: Future[TaskRunnerTimeout])(implicit system: ActorSystem): Future[ExecutionResult] = {
      val result: Future[ExecutionResult | TaskRunnerTimeout] = firstCompletedOf(task.toExecutionResult() :: timeoutFuture :: Nil)
      result.map {
        case t: TaskRunnerTimeout => ExecutionResult.TimedOut(task.id) // just so that we can attach the task id
        case result: ExecutionResult => result
      }
    }


  override def runTasks(tasks: Seq[Task], timeout: FiniteDuration, numWorkers: Int): Future[ExecutionSummary] = {
    lazy val timeoutFuture = after(timeout, actorSystem.scheduler)(Future.successful(TaskRunnerTimeout()))
    Source(tasks)
      .mapAsyncUnordered(parallelism = numWorkers)(_.executeWithTimeout(timeoutFuture))
      .wireTap(item => println(item))
      .fold(zero = ExecutionSummary.empty) {
        case (currentSummary: ExecutionSummary, latestReceivedExecResult: ExecutionResult) => {
          latestReceivedExecResult match {
            case ExecutionResult.Success(id) => currentSummary.addSuccesful(id)
            case ExecutionResult.Failed(id) => currentSummary.addFailed(id)
            case ExecutionResult.TimedOut(id) => currentSummary.addTimedOut(id)
          }
        }
      }
      .runWith(Sink.last)
  }


}