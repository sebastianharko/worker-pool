package com.pirum.exercises.worker

import akka.actor.ActorSystem
import akka.pattern.after
import scala.concurrent.Future
import scala.concurrent.duration.*
import scala.util.{Failure, Success}

opaque type TaskId = String // Tiny type

object TaskId {
  def apply(id: String): TaskId = id
}

// A task that either succeeds after n seconds, fails after n seconds, or never terminates
trait Task {

  val id: TaskId

  def execute: Future[Unit]
}


// Some implementations (useful for testing)

class FailingTask(taskId: TaskId, failureAfter: Option[FiniteDuration])(implicit system: ActorSystem) extends Task {

  import system.dispatcher

  override val id: TaskId = taskId

  override def execute: Future[Unit] = {
    failureAfter match {
      case Some(time) => after(duration = time, system.scheduler)(
        Future.failed(Exception(s"I like to throw after ${time}"))
      )
      case None => Future.failed(Exception("I like to throw an exception"))
    }

  }

}

class SuccessfulTask(taskId: TaskId, successAfter: Option[FiniteDuration])(implicit system: ActorSystem) extends Task {

  import system.dispatcher

  override val id: TaskId = taskId

  override def execute: Future[Unit] = successAfter match {
    case Some(time) => after(duration = time, system.scheduler) {
      Future.successful(())
    }
    case None => Future.successful(())
  }
}

class HangingTask(taskId: TaskId)(implicit system: ActorSystem) extends Task {

  import system.dispatcher

  override val id: TaskId = taskId

  override def execute: Future[Unit] = after(duration = 42.days, system.scheduler)(
    Future.successful(())
  )

}
