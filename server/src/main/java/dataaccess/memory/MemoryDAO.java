package dataaccess.memory;

import dataaccess.DataAccessObject.*;

import java.util.HashSet;

public class MemoryDAO<T> implements ChessDAO {
    protected final HashSet<T> data = new HashSet<>();

    @Override
    public void clear() {
        data.clear();
    }
}
