package com.satellites.test

/*
    @author  Shel Burkow
    @example of gauging the (rough) time differences between small and large data sets
             over sequential and parallel structures
 */

object Corresponds_test extends App{

  // has the testing resources needed to run tests here
  import com.satellites.test.TestTools._

  // the system to test
  import com.satellites.corresponds.CorrespondsCheck._

  /*          TEST ONE          */

  // KNOWN CORRECT ASSOCIATIONS between a system using names and one using letters
  val CORRECT_PAIRS: SystemPairs[String,Char] = Set(
                                ("Sukhoi Su-35", 'S'), ("F-35 Lightening II",'F'),
                                ("SU30-MKI",'M'), ("F-22 Raptor",'R'),
                                ("Chengdu J-20",'C'), ("Rafale",'A'))

  // start with 1000 elements of data (default)
  var sysData: (Array[String],Array[Char]) = buildTestData[String,Char]()(CORRECT_PAIRS).unzip
  doTest[String,Char]( sysData._1, sysData._2 )( CORRECT_PAIRS )

 //zz System.gc()

  /*          TEST TWO          */

  TEST_DATA_SIZE = 50000000  // rebuild data for much larger systems' output
  sysData = buildTestData[String,Char]()(CORRECT_PAIRS).unzip
  doTest[String,Char]( sysData._1, sysData._2 )( CORRECT_PAIRS )

/*
  /*          TEST THREE          */

  // error case: alter value pair in one item

  println; println("Test 3 Preparation : create incorrect element")

  val errorIdx = scala.util.Random.between( 0, TEST_DATA_SIZE ).toInt
  println( s"random index: $errorIdx" )

  val ( randomString, randomChar ) = (sysData._1( errorIdx ), sysData._2( errorIdx ) )
  println( s"tuple at index: ($randomString, $randomChar)" )

  val errorChar: Char = CORRECT_PAIRS.filter( _._1 != randomString ).head._2

  sysData._1(errorIdx) = randomString
  sysData._2(errorIdx) = errorChar
  println( s"tuple updated : (${sysData._1(errorIdx)}, ${sysData._2(errorIdx)})" )

  doTest[String,Char]( sysData._1, sysData._2 )( CORRECT_PAIRS )

//zz  System.gc()

  /*          TEST FOUR          */

  // error case: appending additional String to the test data _1 array
  // should return index of first mismatch pair

  println; println( "Test 4 Preparation : make collections of unequal length" )

  // first put back error item in data to correct state
  sysData._2(errorIdx) = randomChar
  println( s"reverted error item: (${sysData._1(errorIdx)}, ${sysData._2(errorIdx)})")

  println( s"length both inputs prior: ${sysData._1.length}" )
  val sysData_1_long: Array[String] = sysData._1 :+ CORRECT_PAIRS.head._1
  println( s"length one input now    : ${sysData_1_long.length}" )

  doTest[String,Char]( sysData_1_long, sysData._2 )( CORRECT_PAIRS )
*/
  /*          TEST FIVE          */

  // test second generic solution: input data changes from Array[String,Char] to Array[Int,String]

  // no longer needed so free up memory
//zz  sysData = null
//zz  System.gc()

  // KNOWN CORRECT ASSOCIATIONS between a system using names and those using letters
  val CORRECT_PAIRS_II: SystemPairs[Int,String] = Set(
              (6708,"Sukhoi Su-35"), (14006,"F-35 Lightening II"), (331,"SU30-MKI"),
              (1217,"F-22 Raptor"), (36620,"Chengdu J-20"), (1442,"Rafale"))

  var sysData_II: (Array[Int],Array[String]) = buildTestData[Int,String]()(CORRECT_PAIRS_II).unzip

  doTest[Int,String]( sysData_II._1, sysData_II._2 )( CORRECT_PAIRS_II )
}

