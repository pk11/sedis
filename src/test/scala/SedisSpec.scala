package org.sedis

import org.junit.Test
import redis.clients.util.SafeEncoder
import redis.clients.jedis._

import com.codahale.simplespec.Spec

class SedisSpec extends Spec {
  class `A Scala redis server` {

    val pool = new Pool(new JedisPool(new JedisPoolConfig(), "localhost", 6379, 2000))
    val j = pool.underlying.getResource
    j.flushAll
    pool.underlying.returnResource(j)

    pool.withClient { client =>
        Dress.up(client).rpush("test", "bar")
        client.rpush("test", "foo") 
        client.set("single", "foo") 
    } 

    @Test def `using Dress up` = {
       pool.withJedisClient { client => 
          Dress.up(client).get("single").isDefined.must(be(true))
          Dress.up(client).get("single").get.must(be("foo"))
          val r: List[String] = Dress.up(client).lrange("test",0,2) 
          r.size.must(be(2))
          r.toString.must(be("List(bar, foo)"))
          val s: List[String] = Dress.up(client).sort("test")
          s.size.must(be(2))
          s.toString.must(be("List(bar, foo)"))
       }
    }
    @Test def `using implicits` = {
       import Dress._
       pool.withClient { client => 
          client.get("single").isDefined.must(be(true))
          client.get("single").get.must(be("foo"))
          client.lindex("test",0).must(be("bar"))
          val r: List[String] = client.lrange("test",0,2) 
          r.size.must(be(2))
          r.toString.must(be("List(bar, foo)"))
          val s: List[String] = client.sort("test")
          s.size.must(be(2))
          s.toString.must(be("List(bar, foo)"))
       }
    }
}
}
// vim: set ts=4 sw=4 et:
