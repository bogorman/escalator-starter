import sbtassembly.AssemblyPlugin.autoImport._

import sbt.Keys._
import sbt._

object MergeAssembly {

  val aopMerge = new sbtassembly.MergeStrategy {
    val name = "aopMerge"
    import scala.xml._
    import scala.xml.dtd._

    import java.io.FileInputStream
    import org.xml.sax.InputSource

    val parser = {
      val factory = javax.xml.parsers.SAXParserFactory.newInstance()
      factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
      factory.newSAXParser()
    }

    def apply(tempDir: File, path: String, files: Seq[File]): Either[String, Seq[(File, String)]] = {
      val dt = DocType("aspectj", PublicID("-//AspectJ//DTD//EN", "http://www.eclipse.org/aspectj/dtd/aspectj.dtd"), Nil)
      val file = MergeStrategy.createMergeTarget(tempDir, path)
      // val xmls: Seq[Elem] = files.map(XML.loadFile)
      // val xmls: Seq[Elem] = XML.loadXML(new InputSource(new FileInputStream(f)), parser)
      val xmls: Seq[Elem] = files.map(f => XML.loadXML(new InputSource(new FileInputStream(f)), parser))
      val aspectsChildren: Seq[Node] = xmls.flatMap(_ \\ "aspectj" \ "aspects" \ "_")
      val weaverChildren: Seq[Node] = xmls.flatMap(_ \\ "aspectj" \ "weaver" \ "_")
      val options: String = xmls.map(x => (x \\ "aspectj" \ "weaver" \ "@options").text).mkString(" ").trim
      val weaverAttr = if (options.isEmpty) Null else new UnprefixedAttribute("options", options, Null)
      val aspects = new Elem(null, "aspects", Null, TopScope, false, aspectsChildren: _*)
      val weaver = new Elem(null, "weaver", weaverAttr, TopScope, false, weaverChildren: _*)
      val aspectj = new Elem(null, "aspectj", Null, TopScope, false, aspects, weaver)
      XML.save(file.toString, aspectj, "UTF-8", xmlDecl = false, dt)
      IO.append(file, IO.Newline.getBytes(IO.defaultCharset))
      Right(Seq(file -> path))
    }
  }

  def mergeSettings = assemblyMergeStrategy in assembly := {
    case "reflect.properties"     => MergeStrategy.first 
    case "module-info.class"      => MergeStrategy.first   
    case PathList("META-INF", "versions", "9" , "module-info.class" , xs @ _*)                   => MergeStrategy.first
    case PathList("org", "jline", xs @ _*) => MergeStrategy.first  
    case PathList("google", "protobuf", xs @ _*) => MergeStrategy.first  
    case PathList("META-INF", "io.netty.versions.properties", xs @ _*)                   => MergeStrategy.first
    case x =>
      // println("MERGE CALLED ON OLD:" + x)
      val oldStrategy = (assemblyMergeStrategy in assembly).value
      oldStrategy(x)
  }  

}