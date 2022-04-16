package rpc.server

import context.Context
import rpc.ContextHandlers

val contextHandler = ContextHandlers<Context>()

class ContextHandler