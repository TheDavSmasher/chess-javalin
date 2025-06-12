package server.handler;

import io.javalin.http.Context;
import io.javalin.http.ExceptionHandler;
import io.javalin.http.HttpStatus;
import model.response.ErrorResponse;
import service.*;
import org.jetbrains.annotations.NotNull;

public class ServerExceptionHandler implements ExceptionHandler<ServiceException> {
    @Override
    public void handle(@NotNull ServiceException e, @NotNull Context context) {
        context.status(switch (e) {
            case BadRequestException ignore -> HttpStatus.BAD_REQUEST;
            case UnauthorizedException ignore -> HttpStatus.UNAUTHORIZED;
            case PreexistingException ignore -> HttpStatus.FORBIDDEN;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        }).json(new ErrorResponse("Error: " + e.getMessage()));
    }
}
