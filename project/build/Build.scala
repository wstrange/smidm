
import sbt._

class BaseProject(info: ProjectInfo) extends DefaultProject(info) {

  object Versions {
	  val arquillian = "1.0.0.Alpha3"
	  //val glassfish = "3.0.1"
	  val glassfish = "3.1-b36"	  
	  val eclipselink = "2.0.0"
  }
  
  val localMaven = "Local Maven Repository" at "file://" + Path.userHome + "/.m2/repository"
  val localNexus = "local nexus" at "http://localhost:8081/nexus/content/groups/public/"
  
   // This skips adding the default repositories and only uses the ones you added
  // explicitly. --Mark Harrah
  override def repositories = Set(localNexus) 
  override def ivyRepositories = Seq(Resolver.defaultLocal(None)) ++ repositories

  

  /*

 
  val nexus = "nexus" at "http://oss.sonatype.org/"
  val jboss = "jboss" at "http://repository.jboss.org/nexus/content/groups/public-jboss/"
  val glassfish = "glassfish" at "http://download.java.net/maven/glassfish/"
  */

 override def compileClasspath = super.compileClasspath --- (managedDependencyRootPath ** ("*-sources.jar" | "*-javadoc.jar"))
  override def publicClasspath = super.publicClasspath --- (managedDependencyRootPath ** ("*-sources.jar" | "*-javadoc.jar"))
  
   override def libraryDependencies = Set(
    "junit" % "junit" % "4.5" % "test->default",
    "org.scala-tools.testing" %% "specs" % "1.6.6" % "test->default",
    "org.jboss.arquillian" % "arquillian-junit" % Versions.arquillian % "test->default",
    "org.jboss.arquillian.container" % "arquillian-glassfish-embedded-3" % Versions.arquillian % "test->default",
    // should be provided?
    //"org.eclipse.persistence" % "eclipselink" % Versions.eclipselink % "test-default",
    "org.glassfish.extras" % "glassfish-embedded-all" % Versions.glassfish % "test->default",
    "org.scalatest" % "scalatest" % "1.2"   % "test->default",
    //"ch.qos.logback" % "logback-classic" % "0.9.26",
	"net.liftweb" % "lift-common_2.8.0" % "2.1" withSources(),
	"javax" % "javaee-web-api"  % "6.0" % "provided"

  ) ++ super.libraryDependencies
  

}
