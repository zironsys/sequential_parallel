package com.satellites.corresponds

/*
    @author: Shel Burkow
    this file has the resources for comparing satellite systems' jet tracking
    output and times when run sequentially vs in parallel
 */

// needed to transform sequential collections to parallel
// using the .par function (standard prior to 2.13)
import scala.collection.parallel.CollectionConverters._

// companion object
object CorrespondsCheck{

  // set of Tuple2 known correct mappings between two any two output systems
  // see usgae below and in testing environment
  type SystemPairs[A,B] = Set[(A,B)]

  /*---------------*/

  // codes returned from correspondsCheck() in companion class
  val CORRESPONDS_SUCCESS   : Long = -1
  val ARRAY_LENGTH_ERROR    : Long = -2
  val UNKNOWN_PARALLEL_ERROR: Long = -3

  /*---------------*/

  def apply[A,B]( verificationSet: SystemPairs[A,B] ): CorrespondsCheck[A,B] = {
    new CorrespondsCheck[A,B](verificationSet)
  }

  /*---------------*/

  // nice print format
  import java.text.DecimalFormat
  val df = new DecimalFormat("#####.###")

  // @param   resultDummy necessary to initialize var result
  // @result  value is String showing milliseconds and result
  private def timed[T](body: => T)(resultDummy: T): (String,T) = {

    var result: T = resultDummy

    val start = System.nanoTime()
    result = body
    val end = System.nanoTime()
    ( df.format( (((end - start) / 1000) / 1000.0) ), result )
  }

  // a layer over timed() above meant to get the JVM  into steady state
  // before measuring sequential vs parallel times
  private def warmTimed[T](iterations: Short = 200 )
                          (body: => T)(resultDummy: T): (String,T) = {

    for( _ <- 1 to iterations ) body  // should be steady state after this

    timed( body )( resultDummy )
  }
}

import CorrespondsCheck._

class CorrespondsCheck[A,B](verificationSet: SystemPairs[A,B]) {

  // called by client to perform actual corresponds() both sequentially and in parallel
  // @param  jetsA and jetsB: outputs from two separate satellite systems
  // @return left: error code or index of first incorrect pairing
  def correspondsCheck( jetsA: Array[A], jetsB: Array[B] ): Either[Long, (String,String)] = {

    // CUSTOM CORRESPONDS(): sequential is always run first to trap index of first incorrect
    // pair if it exists
    def sequentialCheck: Long = {
      // assume success and change as needed

      var outcome, curIdx: Long = CORRESPONDS_SUCCESS

      val a = jetsA.iterator
      val b = jetsB.iterator

      while (a.hasNext && b.hasNext && (CORRESPONDS_SUCCESS == outcome)) {
        curIdx += 1
        if (!(verificationSet(a.next(), b.next()))) {
          outcome = curIdx // end loop
        }
      }

      // ran to completion and arrays are same length
      if ((CORRESPONDS_SUCCESS == outcome) && !(a.hasNext || b.hasNext)) CORRESPONDS_SUCCESS
      else if (a.hasNext != b.hasNext) ARRAY_LENGTH_ERROR
      // index of first incorrect pair
      else curIdx
    }

    // as noted always run sequential test first
    val (seqTimeMS, seqResult): (String, Long) = warmTimed[Long]()(sequentialCheck)(0L)
    // set up return value and run parallel version if no errors in sequential
    val eitherResult: Either[Long, (String, String)] = {
      if (CORRESPONDS_SUCCESS == seqResult) {
        // run parallel version by transforming arrays to ParArray using .par from parallel library
        val (parTimeMS, parResult) =
        warmTimed[Boolean]()((jetsA.par).corresponds(jetsB.par)((a, b) => verificationSet(a, b)))(true)
        // when sequential succeeds, parallel should also; UNKNOWN_PARALLEL_ERROR shouldn't happen
        if (parResult) Right((seqTimeMS, parTimeMS)) else Left(UNKNOWN_PARALLEL_ERROR)
      }
      else Left(seqResult)  // an error code or index of first mismatched pair
    }
    eitherResult
  }
}

