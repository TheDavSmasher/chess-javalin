package server.handler;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import model.response.ErrorResponse;
import model.response.result.BadRequestException;
import model.response.result.PreexistingException;
import model.response.result.ServiceException;
import model.response.result.UnauthorizedException;
import org.jetbrains.annotations.NotNull;

public class ExceptionHandler implements io.javalin.http.ExceptionHandler<ServiceException> {
    @Override
    public void handle(@NotNull ServiceException e, @NotNull Context context) {
        context.status(getStatusCode(e)).json(new ErrorResponse("Error: " + e.getMessage()));
    }

    private static HttpStatus getStatusCode(ServiceException e) {
        return switch (e) {
            case BadRequestException ignore -> HttpStatus.BAD_REQUEST;
            case UnauthorizedException ignore -> HttpStatus.UNAUTHORIZED;
            case PreexistingException ignore -> HttpStatus.FORBIDDEN;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}
