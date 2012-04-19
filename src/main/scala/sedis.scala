package org.sedis

import redis.clients.jedis._

trait Dress { 
  implicit def delegateToJedis(d: Wrap) = d.j 
  
  implicit def fromJedistoScala(j: JedisCommands) = up(j) 

  class Wrap(val j: JedisCommands) {
    import collection.JavaConverters._

    def hmset(key: String, values: Map[String, String]) = {
      j.hmset(key,values.asJava)
    }

    def hmget(key: String, values: String*): List[String] = {
      j.hmget(key,values: _*).asScala.toList
    }
    def smembers(key: String):Set[String] = {
      j.smembers(key).asScala.toSet
    }

    def hkeys(key: String): Set[String] = {
      j.hkeys(key).asScala.toSet
    }

    def hvals(key: String): List[String] = {
      j.hvals(key).asScala.toList
    }

    def get(k: String): Option[String] = {
      Option(j.get(k))
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
