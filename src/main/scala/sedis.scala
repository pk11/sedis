package org.sedis

import redis.clients.jedis._

trait Dress { 
  implicit def delegateToJedis(d: Wrap) = d.j 
  
  implicit def fromJedistoScala(j: JedisCommands) = up(j) 

  class Wrap(val j: JedisCommands) {
    import collection.JavaConverters.asScalaBufferConverter
    
    def get(k: String): Option[String] = {
      val f = j.get(k)
      if (f == null) None else Some(f)
    }
    def lrange(key: String, start: Long, end: Long): List[String] = {
      j.lrange(key,start,end).asScala.toList
    }
    def sort(key: String, params: SortingParams): List[String] = {
      j.sort(key,params).asScala.toList
    }
    def sort(key: String):List[String] = {
      j.sort(key).asScala.toList
    }
    
  }
  def up(j: JedisCommands) = new Wrap(j)
}
object Dress extends Dress

class Pool(val underlying: JedisPool) {

  def withClient[T](body: Dress.Wrap => T) = {
    val jedis = underlying.getResource
    try {
      body(Dress.up(jedis))
    } finally {
      underlying.returnResource(jedis)
    }
  }
  def withJedisClient[T](body: JedisCommands => T) = {
    val jedis = underlying.getResource
    try {
      body(jedis)
    } finally {
      underlying.returnResource(jedis)
    }
  }

}
