bintrayRepository := "sedis"
bintrayReleaseOnPublish in ThisBuild := false

organization := "org.sedis"
crossScalaVersions := Seq("2.10.6", "2.11.8")
licenses += ("MIT", url("https://spdx.org/licenses/MIT"))

publishArtifact in Test := false

publishMavenStyle := true
pomIncludeRepository := { _ => false }
pomExtra := (
  <url>https://github.com/pk11/sedis</url>
    <scm>
      <connection>scm:git:github.com/pk11/sedis.git</connection>
      <developerConnection>scm:git:git@github.com:pk11/sedis.git</developerConnection>
      <url>https://github.com/pk11/sedis</url>
    </scm>
    <developers>
      <developer>
        <id>pk11</id>
        <name>Peter Hausel</name>
        <url>https://github.com/pk11</url>
      </developer>
      <developer>
        <id>graingert</id>
        <name>Thomas Grainger</name>
        <url>https://graingert.co.uk/</url>
      </developer>
    </developers>
  )
