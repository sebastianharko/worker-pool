package com.pirum.exercises.worker

import akka.actor.ActorSystem
import akka.pattern.after
import com.pirum.exercises.worker.Main2.tasks

import scala.concurrent.Future
import scala.concurrent.duration.*
import scala.concurrent.duration
import scala.util.{Failure, Success}

// NOTE:
// Main1 and Main2 contain the two examples in the original README

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
    case Success(summary) =>
      println(summary)
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
    case Success(summary) =>
      println(summary)
  }

}