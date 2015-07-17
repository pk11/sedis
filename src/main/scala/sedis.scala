package org.sedis

import scala.concurrent.duration._

import redis.clients.jedis._
import scala.util.{Failure, Try}
import redis.clients.jedis.exceptions.JedisConnectionException

trait Dress { 
  implicit def delegateToJedis(d: Wrap) = d.j 
  
  implicit def fromJedistoScala(j: Jedis) = up(j) 

  class Wrap(val j: Jedis) {
    import collection.JavaConverters._

    def expire(key: String, time: FiniteDuration) = {
      j.expire(key, time.toSeconds.toInt)
    }

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
   
    def sinter(key: String): Set[String] = {
      j.sinter(key).asScala.toSet
    }

    def sunion(key: String): Set[String] = {
      j.sunion(key).asScala.toSet
    }

    def sdiff(key: String): Set[String] = {
      j.sdiff(key).asScala.toSet
    }

    def zrange(key: String, start: Long, end: Long): Set[String] = {
      j.zrange(key, start, end).asScala.toSet
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

    def blpop(timeout: Int, args: String*): List[String] = {
      j.blpop(timeout, args:_*).asScala.toList
    }
    
    def blpop(args: String*): List[String] = {
      j.blpop(args:_*).asScala.toList
    }
    
    def brpop(timeout: Int, args: String*): List[String] = {
      j.brpop(timeout, args:_*).asScala.toList
    }

    def brpop(args: String*): List[String] = {
      j.brpop(args:_*).asScala.toList
    }
  }
  def up(j: Jedis) = new Wrap(j)
}
object Dress extends Dress



class Pool(val underlying: JedisPool) {

  def withClient[T](body: Dress.Wrap => T): T = {
    withJedisClient({ jedis: Jedis => body(Dress.up(jedis)) })
  }

  def withJedisClient[T](body: Jedis => T): T = {
    val jedis: Jedis = underlying.getResource
    val result = Try(body(jedis))

    result match {
      case Failure(e:JedisConnectionException) => {
        underlying.returnBrokenResource(jedis)
      }
      case _ => {
        underlying.returnResource(jedis)
      }
    }
    result.get
  }
}

class SentinelPool(val underlying: JedisSentinelPool) {

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
