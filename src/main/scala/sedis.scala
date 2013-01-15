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
    
    def hgetAll(key: String): Map[String,String] = {
      j.hgetAll(key).asScala.toMap
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

class Sharded(val underlying: ShardedJedis) {

 def withClient[T](body: Dress.Wrap => T) = {
    try {
      body(Dress.up(underlying))
    } finally {
      underlying.disconnect()
    }
  }

}

class Pool(val underlying: JedisPool) {

  def withClient[T](body: Dress.Wrap => T): T = {
    val jedis: Jedis = underlying.getResource
    try {
      body(Dress.up(jedis))
    } finally {
      underlying.returnResourceObject(jedis)
    }
  }
  def withJedisClient[T](body: Jedis => T): T = {
    val jedis: Jedis = underlying.getResource
    try {
      body(jedis)
    } finally {
      underlying.returnResourceObject(jedis)
    }
  }

}
