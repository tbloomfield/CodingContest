Coding Contest is a suite of services which displays coding "challenges", accepts arbitrary "contest" code answers for execution within an emphermeral environment, and ranks results.

# Code Execution
Code is executed via a throwaway test runner container.  Upon code submission, a coordination service determines the correct test container, creates it, and passes the submission for execution.  This service monitors the test container,
recording cpu and memory usage, as well as bounding test execution to a specified time limit.  Upon hitting a time or memory limit, the container is destroyed and results are recorded.

For more information, please see [Java TestContainer Readme](TestContainers/Java/README.md)


# Leaderboard
A basic ranking algorithm is provided to "score" results, which is factored using configurable weights:
  ```
  (c)orrectness - 1
  Execution (t)ime (runtime) - .3
  Execution Memory (s)pace complexity - .2  

  Score = c - ((runtime in ms * t) + (memory usage *s)) * difficulty
```

# Virtual Queue
Contest users are placed into a virtual queue prior to the contest start time.  The queue provides metrics such as user position and time until dequeue using a virtualized thread monitor.  It's expected that users would be connected to the virtual queue via a websocket; the websocket would maintain a users place in the queue, as well as provide updated information about their estimated time until dequeue.

The queue is built as a generic priority blocking queue, allowing for various rules to be provided governing dequeue logic.

For more information, please see [Virtual Queue Readme](VirtualQueue/README.md)

# Sample Environment
Code may be executed
