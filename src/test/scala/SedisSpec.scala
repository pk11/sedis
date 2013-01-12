package org.sedis


import redis.clients.util.SafeEncoder
import redis.clients.jedis._
import org.scalatest.matchers.ShouldMatchers._
import org.scalatest._

class SedisSpec extends FunSpec{
  describe("A Scala redis server") {

    val pool = new Pool(new JedisPool(new JedisPoolConfig(), "localhost", 6379, 2000))
    val j = pool.underlying.getResource
    j.flushAll
    pool.underlying.returnResource(j)

    pool.withClient { client =>
        Dress.up(client).rpush("test", "bar")
        client.rpush("test", "foo") 
        client.set("single", "foo") 
    } 

    it("using Dress up")  {
       pool.withJedisClient { client => 
          Dress.up(client).get("single").isDefined should be(true)
          Dress.up(client).get("single").get should be("foo")
          val r: List[String] = Dress.up(client).lrange("test",0,2) 
          r.size should be(2)
          r.toString should be("List(bar, foo)")
          val s: List[String] = Dress.up(client).sort("test")
          Dress.up(client).hmset("mymap",Map("1"->"2"))
          Dress.up(client).hgetAll("mymap").toString should be("Map(1 -> 2)")
          s.size should be(2)
          s.toString should be("List(bar, foo)")
       }
    }
    it("using implicits") {
       import Dress._
       pool.withClient { client => 
          client.get("single").isDefined should be(true)
          client.get("single").get should be("foo")
          client.lindex("test",0) should be("bar")
          val r: List[String] = client.lrange("test",0,2) 
          r.size should be(2)
          r.toString should be("List(bar, foo)")
          val s: List[String] = client.sort("test")
          s.size should be(2)
          s.toString should be("List(bar, foo)")
       }
    }
}
}
// vim: set ts=4 sw=4 et:
