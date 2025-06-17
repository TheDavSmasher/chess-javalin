package client.states;

import client.ClientException;

import java.io.IOException;

public final class ClientCommandProcessing {
    @FunctionalInterface
    public interface CommandConsumer {
        void process(String... params) throws ClientException, IOException;
    }

    @FunctionalInterface
    public interface CommandRunnable {
        void process() throws ClientException, IOException;
    }

    public record ClientCommand(
            CommandConsumer command, String option, int paramMin, int paramMax,
            String onWrong, String format, String... description) {
        public ClientCommand(
                CommandConsumer command, String option, int params, String onWrong, String format, String... description) {
            this(command, option, params, params, format, onWrong, description);
        }

        public ClientCommand(CommandRunnable command, String option, String... description) {
            this(_ -> command.process(), option, 0, "This command takes no arguments", null, description);
        }
    }
}
