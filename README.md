# Workers (Solution)

 A minimalistic and pragmatic solution implemented using Akka Streams
 and Scala 3.
 
Note regarding one of the requirements:
"When there is no hanging task, the program should return after a time not significantly longer than the duration of the longest running task. For example, when the longest running task takes 3 seconds to execute, it should return after 3 seconds + maybe some small additional time."

My understanding is that this is only possible if numWorkers is equal to
the number of tasks. For example, if you only have one worker and 5 tasks and each task takes 10 seconds to complete, it'll take 50 seconds.
Of course, it depends on how you define 'execution' (if your definition of execution includes queue time or not).

