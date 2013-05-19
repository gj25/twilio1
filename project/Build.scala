import sbt._
import Keys._
import play.Project._

// sbt build file
object ApplicationBuild extends Build {

  val appName         = "twilio1"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    "com.twilio.sdk" % "twilio-java-sdk" % "3.3.9",
    anorm
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here      
  )

}
