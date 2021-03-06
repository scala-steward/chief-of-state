/*
 * Copyright 2020 Namely Inc.
 *
 * SPDX-License-Identifier: MIT
 */

package com.namely.chiefofstate

import akka.actor.typed.ActorRef
import com.namely.protobuf.chiefofstate.v1.internal.{CommandReply, SendCommand}

final case class AggregateCommand(
  command: SendCommand,
  replyTo: ActorRef[CommandReply],
  data: Map[String, com.google.protobuf.any.Any]
)
