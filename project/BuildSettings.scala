import com.lightbend.sbt.javaagent.JavaAgent.JavaAgentKeys.javaAgents

import com.typesafe.sbt.packager.docker.DockerPlugin.autoImport.{dockerBaseImage, dockerRepository, dockerUsername}
import sbt.{plugins, AutoPlugin, Plugins}
import sbt.Keys.libraryDependencies

object BuildSettings extends AutoPlugin {
  override def requires: Plugins = plugins.JvmPlugin

  override def projectSettings =
    Seq(
      dockerBaseImage := "openjdk:11",
      dockerRepository := Some("registry.namely.land"),
      dockerUsername := Some("namely"),
      javaAgents += Dependencies.Compile.KanelaAgent,
      libraryDependencies ++= Seq(
        Dependencies.Compile.Lagompb,
        Dependencies.Compile.LagompbReadSide,
        Dependencies.Runtime.LagompbRuntime,
        Dependencies.Test.AkkaGrpcTestkit,
        Dependencies.Compile.KamonAkkaGrpc
      )
    )
}