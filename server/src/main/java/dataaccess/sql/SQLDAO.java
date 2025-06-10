package dataaccess.sql;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import org.intellij.lang.annotations.Language;
import dataaccess.DataAccessObject.*;

import java.sql.*;

import static java.sql.Types.NULL;

public abstract class SQLDAO implements ChessDAO {
    @Language("SQL")
    protected abstract String getTableName();

    @Language("SQL")
    protected abstract String getTableSetup();

    protected SQLDAO(boolean tableExists) throws DataAccessException {
        if (tableExists) return;
        DatabaseManager.createDatabase();
        try (PreparedStatement preparedStatement =
                     getStatement("CREATE TABLE IF NOT EXISTS " + getTableName() + getTableSetup())) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("could not configure database table " + getTableName(), e);
        }
    }

    @Override
    public void clear() throws DataAccessException {
        tryUpdate("TRUNCATE " + getTableName(), ignored -> {});
    }

    protected interface SqlQuery<T> {
        T execute(ResultSet resultSet) throws SQLException, DataAccessException;
    }

    protected static <T> T tryQuery(@Language("SQL") String sql, SqlQuery<T> query, Object... params) throws DataAccessException {
        try (PreparedStatement statement = getStatement(sql, params); ResultSet rs = statement.executeQuery()) {
            return query.execute(rs);
        } catch (SQLException e) {
            throw new DataAccessException("could not execute query", e);
        }
    }

    protected <T> T trySingleQuery(@Language("SQL") String whereCol, Object whereVal, SqlQuery<T> query) throws DataAccessException {
        return tryQuery("SELECT * FROM " + getTableName() + " WHERE " + whereCol + "=?",
                rs -> rs.next() ? query.execute(rs) : null, whereVal);
    }

    protected interface SqlUpdate {
        void execute(int updated) throws SQLException, DataAccessException;
    }

    protected static void tryUpdate(@Language("SQL") String sql, SqlUpdate update, Object... params) throws DataAccessException {
        try (PreparedStatement statement = getStatement(sql, params)) {
            int result = statement.executeUpdate();

            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next())
                    result = rs.getInt(1);
            }

            update.execute(result);
        } catch (SQLException e) {
            throw new DataAccessException("could not execute update", e);
        }
    }

    protected void tryInsert(@Language("SQL") String rows, SqlUpdate update, Object... values) throws DataAccessException {
        tryUpdate("INSERT INTO " + getTableName() + "(" + rows + ") VALUES (" + "?" + ", ?".repeat(Math.max(0, values.length - 1)) + ")", update, values);
    }

    private static PreparedStatement getStatement(@Language("SQL") String sql, Object... params) throws DataAccessException, SQLException {
        Connection connection = DatabaseManager.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        for (int i = 0; i < params.length;) {
            switch (params[i]) {
                case String s -> statement.setString(++i, s);
                case Integer s -> statement.setInt(++i, s);
                case null, default -> statement.setNull(++i, NULL);
            }
        }
        return statement;
    }

    protected static void confirmUpdate(int updateResult) throws DataAccessException {
        if (updateResult == 0) {
            throw new DataAccessException("No rows were updated");
        }
    }
}
