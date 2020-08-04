import sbt.{plugins, AutoPlugin, Plugins}
import sbt.Keys.libraryDependencies

object ProtocRuntime extends AutoPlugin {
  override def requires: Plugins = plugins.JvmPlugin

  override def projectSettings =
    Seq(libraryDependencies ++= Seq(Dependencies.Compile.Lagompb, Dependencies.Runtime.LagompbRuntime))
}
