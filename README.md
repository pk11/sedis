Sedis
==========

Sedis is a thin wrapper around  [Jedis](https://github.com/xetorthio/jedis/),
the most commonly-used [Redis](http://redis.io) library on the JVM.


Requirements
------------

* Scala 2.9.1 or 2.8.1
* Jedis 2.0.0


Getting Started
---------------

**First**, specify Sedis as a dependency:

```xml
<repositories>
    <repository>
        <id>org.sedis</id>
        <url>http://guice-maven.googlecode.com/svn/trunk</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>org.sedis</groupId>
        <artifactId>sedis_${scala.version}</artifactId>
        <version>1.0</version>
    </dependency>
</dependencies>
```

**Second**, start using it right away:

```scala
 import org.sedis._
 val pool = Pool(new JedisPool(new JedisPoolConfig(), "localhost", 6379, 2000))

 pool.withJedisClient { client =>· 
   Dress.up(client).get("single").isDefined.must(be(true))
   Dress.up(client).get("single").get.must(be("foo"))
   val r: List[String] = Dress.up(client).lrange("test",0,2)·
   r.size.must(be(2))
   r.toString.must(be("List(bar, foo)"))
   val s: List[String] = Dress.up(client).sort("test")
   s.size.must(be(2))
   s.toString.must(be("List(bar, foo)"))
}   

//or using implicits

import Dress._
pool.withClient { client =>· 
  client.get("single").isDefined.must(be(true))
  client.get("single").get.must(be("foo"))
  client.lindex("test",0).must(be("bar"))
  val r: List[String] = client.lrange("test",0,2)·
  r.size.must(be(2))
  r.toString.must(be("List(bar, foo)"))
  val s: List[String] = client.sort("test")
  s.size.must(be(2))
  s.toString.must(be("List(bar, foo)"))
}
```



License
-------

Published under The MIT License, see LICENSE
