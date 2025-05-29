package service;

import dataaccess.DataAccessException;
import model.response.result.ServiceException;

@FunctionalInterface
public interface EndpointCall<T> {
    T method() throws ServiceException, DataAccessException;
}
