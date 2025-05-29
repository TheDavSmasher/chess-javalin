package service;

import dataaccess.DataAccessException;

@FunctionalInterface
public interface EndpointCall<T> {
    T method() throws ServiceException, DataAccessException;
}
