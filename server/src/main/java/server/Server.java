package server;

import io.javalin.Javalin;
import model.response.result.ServiceException;
import server.handler.ClearHandler;
import server.handler.ExceptionHandler;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        javalin.delete("/db", new ClearHandler());

        javalin.exception(ServiceException.class, new ExceptionHandler());
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}