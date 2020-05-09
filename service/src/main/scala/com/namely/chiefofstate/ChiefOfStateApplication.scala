package com.namely.chiefofstate

import akka.actor.CoordinatedShutdown
import akka.grpc.GrpcClientSettings
import com.google.protobuf.any.Any
import com.lightbend.lagom.scaladsl.akka.discovery.AkkaDiscoveryComponents
import com.lightbend.lagom.scaladsl.api.Descriptor
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.server.LagomApplication
import com.lightbend.lagom.scaladsl.server.LagomApplicationContext
import com.lightbend.lagom.scaladsl.server.LagomApplicationLoader
import com.lightbend.lagom.scaladsl.server.LagomServer
import com.namely.lagom.NamelyAggregate
import com.namely.lagom.NamelyCommandHandler
import com.namely.lagom.NamelyEventHandler
import com.namely.lagom.NamelyLagomApplication
import com.namely.protobuf.chief_of_state.handler.HandlerServiceClient
import com.softwaremill.macwire.wire

abstract class ChiefOfStateApplication(context: LagomApplicationContext) extends NamelyLagomApplication(context) {

  private lazy val settings = GrpcClientSettings.fromConfig("chief_of_state.HandlerService")(actorSystem)
  lazy val handlerServiceClient: HandlerServiceClient = HandlerServiceClient(settings)

  //  Register a shutdown task to release resources of the client
  coordinatedShutdown
    .addTask(
      CoordinatedShutdown.PhaseServiceUnbind,
      "shutdown-handler-service-client"
    ) { () =>
      handlerServiceClient.close()
    }

  // wire up the various event and command handler
  lazy val eventHandler: NamelyEventHandler[Any] = wire[ChiefOfStateEventHandler]
  lazy val commandHandler: NamelyCommandHandler[Any] = wire[ChiefOfStateCommandHandler]
  lazy val aggregate: NamelyAggregate[Any] = wire[ChiefOfStateAggregate]

  override def aggregateRoot: NamelyAggregate[_] = aggregate

  override def server: LagomServer =
    serverFor[ChiefOfStateService](wire[ChiefOfStateServiceImpl])
      .additionalRouter(wire[ChiefOfStateGrpcServiceImpl])
}

class ChiefOfStateApplicationLoader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication =
    new ChiefOfStateApplication(context) with AkkaDiscoveryComponents

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new ChiefOfStateApplication(context) with LagomDevModeComponents

  override def describeService: Option[Descriptor] = Some(readDescriptor[ChiefOfStateService])
}
