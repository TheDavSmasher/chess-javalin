package service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class AppServiceTest extends ServiceTests<AppService> {
    public AppServiceTest() {
        super(daoFactory -> new AppService(daoFactory));
    }

    @Test
    void clearData() {
        Assertions.assertDoesNotThrow(service::clearData);
    }
}