
/*
 * 	@author Shel Burkow
 *  @example of invoking corresponds() in the GenTraversable hierarchy 
 *           with sequential and parallel collections
 *           through Scala version 2.12.11
 */
object Correspondence_test extends App{
  
  // correct associations used to create sequential and parallel collections
  // and by corresponds() predicate to validate via set membership
  val correctSet = Set[(String,Char)](("Sukhoi Su-35", 'S'), ("F-35 Lightening II",'F'), 
                                   ("SU30-MKI",'M'), ("F-22 Raptor",'R'), 
                                   ("Chengdu J-20",'C'), ("Rafale",'A'))
  
  // needed to simulate corresponding data from separate systems
  val (strings,chars): (Array[String],Array[Char]) = correctSet.toArray.unzip
 
  // and made into parallel structures
  import scala.collection.parallel.mutable.ParArray
  val strings_par: ParArray[String] = strings.par 
  val chars_par  : ParArray[Char]   = chars.par 
      
  // GenSeq allows testing with either type of collection
  import scala.collection.GenSeq
  def corresponds[A,B](arrayA: GenSeq[A], arrayB: GenSeq[B])
                                      (correctSet: Set[(A,B)]): Boolean = {
    
    arrayA.corresponds(arrayB)((a,b) => correctSet((a,b)))  
  }
  
  println( s"sequential result: " + 
           s"${corresponds[String,Char]( strings, chars )(correctSet)}" )
  println( s"parallel   result: " + 
           s"${corresponds[String,Char]( strings_par, chars_par )(correctSet)}" )
}




