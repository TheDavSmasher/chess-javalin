package websocket.commands;

import utils.Serializer;

public final class UserCommandDeserializer extends Serializer.Deserializer<UserGameCommand> {
    @Override
    protected String enumField() {
        return "commandType";
    }

    @Override
    protected Class<? extends UserGameCommand> enumClass(String value) {
        return switch (UserGameCommand.CommandType.valueOf(value)) {
            case CONNECT   -> ConnectCommand.class;
            case LEAVE     -> LeaveCommand.class;
            case RESIGN    -> ResignCommand.class;
            case MAKE_MOVE -> MakeMoveCommand.class;
        };
    }
}
