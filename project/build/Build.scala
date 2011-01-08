
import sbt._

class VaadinSkeleton(info: ProjectInfo) extends DefaultProject(info) {

  object Versions {
   
  }


  val localMaven = "Local Maven Repository" at "file://" + Path.userHome + "/.m2/repository"

 override def compileClasspath = super.compileClasspath --- (managedDependencyRootPath ** ("*-sources.jar" | "*-javadoc.jar"))
  override def publicClasspath = super.publicClasspath --- (managedDependencyRootPath ** ("*-sources.jar" | "*-javadoc.jar"))

 
  
  
   override def libraryDependencies = Set(
    "junit" % "junit" % "4.5" % "test->default",
    "org.scala-tools.testing" %% "specs" % "1.6.6" % "test->default",
   "org.scalatest" % "scalatest" % "1.2"   % "test->default",
    "ch.qos.logback" % "logback-classic" % "0.9.26",
	"net.liftweb" % "lift-common_2.8.0" % "2.1" withSources(),
	"javax" % "javaee-web-api"  % "6.0" % "provided",
	"org.squeryl" % "squeryl_2.8.1" % "0.9.4-RC3"
  ) ++ super.libraryDependencies
  

}
