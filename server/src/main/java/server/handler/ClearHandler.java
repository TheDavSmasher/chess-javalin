package server.handler;

public class ClearHandler extends ObjectSerializer<EmptyResponse> {
    @Override
    public EmptyResponse serviceHandle(Context ignored) throws ServiceException {
        return AppService.clearData();
    }
}
