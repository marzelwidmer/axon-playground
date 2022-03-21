package ch.keepcalm.coreapi

import org.axonframework.queryhandling.QueryExecutionException

class RentalQueryException(message: String?, cause: Throwable?, details: Any?) : QueryExecutionException(message, cause, details)
