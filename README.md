## Notes for _A Simulation Using Sequential and Parallel Collections in Scala 2.13_

File GenSeq_test is the Scala file shown in Figure 1, section _The Central Tactic: Scala 2.12_.  Because trait GenSeq is deprecated and package parallel removed in 2.13.x and forward, the code will run in versions no later than 2.12.x.

The three other Scala files comprise the entire hypothetical system.  It is made only to run in 2.13.x.  CorrespondsCheck is the actual library.  Notice that method "warmTimed" has parameter "iterations" of default value 200.  At bottom of file are the method's two invocations, seen as using this default.  If you run it, you may want to pass in twenty or less or the execution could take considerable time.

File Corresponds_test has the app main entry point.  It sets up the five test runs.  TestTools has the resources it needs to do so.

enjoy!
Shel
