Coding Contest is a suite of services which displays coding "challenges", accepts arbitrary "contest" code answers for execution within an emphermeral environment, and ranks results.

# Code Execution
Code is executed via a throwaway test runner container.  Upon code submission, a coordination service determines the correct test container, creates it, and passes the submission for execution.  This service monitors the test container,
recording cpu and memory usage, as well as bounding test execution to a specified time limit.  Upon hitting a time or memory limit, the container is destroyed and results are recorded.

# Leaderboard
A basic ranking algorithm is provided to "score" results, which is factored using configurable weights:
  ```
  (c)orrectness - 1
  Execution (t)ime (runtime) - .3
  Execution Memory (s)pace complexity - .2  

  Score = c - ((runtime in ms * t) + (memory usage *s)) * difficulty
```

# Sample Environment
Code may be executed
