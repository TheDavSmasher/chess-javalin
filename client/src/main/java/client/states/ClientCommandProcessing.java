package client.states;

import client.ClientException;
import client.FormatException;

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

        public void process(String[] params, int index) throws ClientException, IOException {
            if (params.length < paramMin || params.length > paramMax) {
                throw new FormatException(onWrong, format(index));
            }
            command.process(params);
        }

        public String getHelp(int index, boolean simple) {
            StringBuilder help = new StringBuilder().append(index).append(" - ").append(option);
            if (!simple) {
                help.append(": ").append(description[0]);
                for (int i = 1; i < description.length; i++) {
                    help.append("\n   ").append(description[i]);
                }
                if (format != null) {
                    help.append("\n   ").append(format(index));
                }
            }
            return help.append("\n").toString();
        }

        private String format(int index) {
            return format != null ? "Format: " + index + " " + format : "";
        }
    }
}
