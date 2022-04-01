package account.infrastructure;


import io.micronaut.context.annotation.Context;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;

@Produces
@Context
@Requires(classes = { ApiException.class, GlobalExceptionHandler.class })
public class GlobalExceptionHandler
        implements ExceptionHandler<ApiException, HttpResponse<ErrorMessage>> {


    @Override
    public HttpResponse<ErrorMessage>
    handle(HttpRequest request, ApiException exception) {
        ErrorMessage message = new ErrorMessage();
        message.setMessage(exception.getMessage());
        message.setStatus(false);
        return HttpResponse.serverError(message).
                status(HttpStatus.BAD_REQUEST);
    }
}
