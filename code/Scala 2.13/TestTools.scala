package com.satellites.test

import com.satellites.corresponds.CorrespondsCheck

/*
    @author Shel Burkow
    has the resources used by object Corresponds_test to test the system
 */

object TestTools {

  // the system to test
  import com.satellites.corresponds.CorrespondsCheck._

  // ADJUST TO FIND approximate minimal size in test runs
  //                at which parallel algorithm is optimal
  var TEST_DATA_SIZE = 1000

  // type for all test data to build
  type TestData[A,B] = Array[(A,B)]

  /*          TEST DATA RESOURCES          */

  // creates the structure simulating data collected from two distinct systems
  // @param  systemPairs: immutable set of known valid pairings between two systems
  // @return test data to use: Array[Tuple2[A,B]] of always valid associations
  def buildTestData[A,B](noEntries: Int = TEST_DATA_SIZE)
                      (systemPairs: SystemPairs[A,B] ): TestData[A,B] = {
    import scala.util.Random
    import scala.math.abs

    // convert set input to index-supporting array
    val systemPairs_array: TestData[A,B] = systemPairs.toArray[(A,B)]

    // corner case: avoid blowup if Random returns Int.MinValue
    @inline def randInt: Int = Random.nextInt match
                                      { case i if(i < 0) => i + 1
                                        case i => i }

    // build simulation data from known correct pairs
    val testData = Array[(A,B)](

      (for( i <- 0 until noEntries ) yield
         systemPairs_array( abs(randInt) % systemPairs_array.length )): _*
    )
    testData
  }

  var testNo: Int = 0  // incremented before each call tn doTest() and used in printResults()

  // most test run output printing is done here
  // @param  Either[Long,(String,String)]
  //         Left(Long): index of first error tuple or number representing invalid input
  //         Right((String,String)): test times for the sequential and parallel runs
  def printResults( testNo: Int, comparisonTypes: String )
                  ( eitherResult: Either[Long,(String,String)] ): Unit = {

    println
    println( s"Test $testNo  Comparison Types: $comparisonTypes  " +
                                        s"Number of elements: $TEST_DATA_SIZE \n----" )

    // print out each possible outcome in the Either parameter
    println(
      eitherResult match{
        case Left(errorNo) if(ARRAY_LENGTH_ERROR == errorNo) =>
          "FAILURE collections are of different lengths"
        case Left(unknown_parallel) if(UNKNOWN_PARALLEL_ERROR == unknown_parallel) =>
          s"unknown parallel execution error"  // should never happen!
        case Left(failIndex) => s"FAILURE index of first mismatched pair: $failIndex"
        case Right((time_seq,time_par)) =>
          s"sequential time: $time_seq ms \nparallel time  : $time_par ms \n" +
            "sequential minus parallel time: " +
            s"${df.format(time_seq.toDouble - time_par.toDouble)} ms"
      }
    )
  }

  // see usage in doTest();
  // "universe" is entry point into scala runtime reflection (JavaUniverse)
  import scala.reflect.runtime.universe.{ typeOf, TypeTag }

  // invoked by every test to execute the test and print the results
  // @param  arA and arB: the arrays of pairings made in buildTestData() and unzipped
  //                      to simulate input from two separate systems
  // @param  correctPairs: set of correct pairings used to verify data in jetsA/B
  def doTest[A: TypeTag, B: TypeTag](jetsA: Array[A], jetsB: Array[B] )
                                   ( correctPairs: SystemPairs[A,B] ): Unit = {
    // use reflection to get the types used in satellite system comparison
    val systemTypes: String =
      s"(${typeOf[A].typeSymbol.name.toString},${typeOf[B].typeSymbol.name.toString})"

    // create a satellite system object
    val corrCk = CorrespondsCheck[A,B]( correctPairs )

    // returns error value or tuple of sequential and parallel run times
    val eitherResult: Either[Long, (String,String)] = corrCk.correspondsCheck(jetsA, jetsB)

    testNo += 1
    printResults( testNo, systemTypes )( eitherResult )
  }
}



