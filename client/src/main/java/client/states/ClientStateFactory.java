package client.states;

import client.states.ClientStateManager.MenuState;

import utils.EnumObjectFactory;

public class ClientStateFactory extends EnumObjectFactory<MenuState, ChessClientState> {
    private final ClientStateManager clientStateManager;

    public ClientStateFactory(ClientStateManager clientStateManager) {
        super(false);
        this.clientStateManager = clientStateManager;
        generateValues();
    }

    @Override
    protected Class<MenuState> getKeyClass() {
        return MenuState.class;
    }

    @Override
    protected ChessClientState generateValue(MenuState key) {
        return switch (key) {
            case PRE_LOGIN -> new PreLoginClientState(clientStateManager);
            case POST_LOGIN -> new PostLoginClientState(clientStateManager);
            case MID_GAME -> new InGameClientState(clientStateManager);
        };
    }
}
