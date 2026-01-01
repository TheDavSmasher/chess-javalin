package client.states;

import client.exceptions.ClientException;
import client.exceptions.FormatException;

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
            String onWrong, String format, String... description)
    {
        public ClientCommand(
                CommandConsumer command, String option, int params,
                String onWrong, String format, String... description)
        {
            this(command, option, params, params, format, onWrong, description);
        }

        public ClientCommand(CommandRunnable command, String option, String... description) {
            this(ignored -> command.process(), option, 0,
                    "This command takes no arguments", null, description);
        }

        public void process(String[] params, int index) throws ClientException, IOException {
            if (params.length < paramMin || params.length > paramMax) {
                throw new FormatException(onWrong + (format(index) instanceof String indexFormat ? "\n " + indexFormat : ""));
            }
            command.process(params);
        }

        public StringBuilder getHelp(StringBuilder help, int index, boolean simple) {
            help.append(index).append(" - ").append(option);
            if (!simple) {
                help.append(": ").append(description[0]);
                for (int i = 1; i < description.length; i++) {
                    help.append("\n   ").append(description[i]);
                }
                if (format(index) instanceof String indexFormat) {
                    help.append("\n   ").append(indexFormat);
                }
            }
            return help.append("\n");
        }

        private String format(int index) {
            return format != null ? "Format: " + index + " " + format : null;
        }
    }
}
