package ch.keepcalm

import org.axonframework.commandhandling.CommandExecutionException

class RentalCommandException(message: String?, cause: Throwable?, details: Any?) : CommandExecutionException(message, cause, details)
