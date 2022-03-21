package ch.keepcalm

import ch.keepcalm.coreapi.ExceptionStatusCode
import ch.keepcalm.coreapi.RentalCommandException
import ch.keepcalm.coreapi.RentalQueryException
import org.axonframework.commandhandling.CommandExecutionException
import org.axonframework.queryhandling.QueryExecutionException

object ExceptionMapper {
    @JvmStatic
    fun mapRemoteException(exception: Throwable): Throwable {
        if (exception is CommandExecutionException) {
            val details = exception.getDetails<Any>()
            if (details.isPresent) {
                val statusCode = details.get() as ExceptionStatusCode
                return RentalCommandException(statusCode.description, null, statusCode)
            }
        } else if (exception is QueryExecutionException) {
            val details = exception.getDetails<Any>()
            if (details.isPresent) {
                val statusCode = details.get() as ExceptionStatusCode
                return RentalQueryException(statusCode.description, null, statusCode)
            }
        }
        return exception
    }
}
