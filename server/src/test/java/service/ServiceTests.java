package service;

import dataaccess.DAOFactory;
import dataaccess.sql.SQLDAOFactory;
import org.junit.jupiter.api.BeforeEach;
import service.exception.ServiceException;

import java.util.function.Function;

public abstract class ServiceTests<S extends Service> {
    protected static final DAOFactory daoFactory = new SQLDAOFactory();
    private static final AppService appService = new AppService(daoFactory);
    protected final S service;

    protected ServiceTests(Function<DAOFactory, S> serviceMaker) {
        this.service = serviceMaker.apply(daoFactory);
    }

    @BeforeEach
    public void setup() throws ServiceException {
        appService.clearData();
    }
}
