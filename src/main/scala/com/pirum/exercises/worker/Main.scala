package com.pirum.exercises.worker

import akka.actor.ActorSystem
import akka.pattern.after
import com.pirum.exercises.worker.Main2.{system, tasks}

import scala.concurrent.Future
import scala.concurrent.duration
import scala.concurrent.duration.*
import scala.util.{Failure, Success}

// NOTE:
// Main1 and Main2 contain the two examples in the original README
// Main3 is an example with 60 tasks and 60 workers

// Main1 is the first example in the original README
object Main1 extends App {

  implicit val system: ActorSystem = ActorSystem()

  import system.dispatcher

  val task1 = FailingTask(TaskId("Task1"), failureAfter = Some(1.seconds))

  val task2 = SuccessfulTask(TaskId("Task2"), successAfter = Some(2.seconds))

  val task3 = FailingTask(TaskId("Task3"), failureAfter = Some(3.seconds))

  val task4 = SuccessfulTask(TaskId("Task4"), successAfter = Some(4.seconds))

  val tasks = List(task1, task2, task3, task4)

  val taskRunner = TaskRunnerImpl()
  val result: Future[ExecutionSummary] = taskRunner.runTasks(tasks, timeout = 8.seconds, numWorkers = 4)
  result.onComplete {
    case Failure(ex) =>
      println("Task runner failed ;-(")
      system.terminate()
    case Success(summary) =>
      println(summary)
      system.terminate()

  }

}


// Main2 is the second example in the original README
object Main2 extends App {

  implicit val system: ActorSystem = ActorSystem()

  import system.dispatcher

  val task1 = SuccessfulTask(TaskId("Task1"), successAfter = Some(1.seconds))

  val task2 = SuccessfulTask(TaskId("Task2"), successAfter = Some(2.seconds))

  val task3 = FailingTask(TaskId("Task3"), failureAfter = Some(3.seconds))

  val task4 = SuccessfulTask(TaskId("Task4"), successAfter = Some(4.seconds))

  val task5 = HangingTask(TaskId("Task5"))

  val tasks = List(task1, task2, task3, task4, task5)

  val taskRunner = TaskRunnerImpl()
  val result: Future[ExecutionSummary] = taskRunner.runTasks(tasks, timeout = 8.seconds, numWorkers = 4)
  result.onComplete {
    case Failure(ex) =>
      println("Task runner failed ;-(")
      system.terminate()
    case Success(summary) =>
      println(summary)
      system.terminate()
  }

}

// Main3 is an example with 60 tasks and 60 workers
object Main3 extends App {

  implicit val system: ActorSystem = ActorSystem()

  import system.dispatcher

  val tasks = (1 to 60).map(item => SuccessfulTask(TaskId(s"Task${item}"), successAfter = Some(5.seconds)))

  val taskRunner = TaskRunnerImpl()
  val result: Future[ExecutionSummary] = taskRunner.runTasks(tasks, timeout = 8.seconds, numWorkers = 60)
  result.onComplete {
    case Failure(ex) =>
      println("Task runner failed ;-(")
      system.terminate()
    case Success(summary) =>
      println(summary)
      system.terminate()
  }

}
