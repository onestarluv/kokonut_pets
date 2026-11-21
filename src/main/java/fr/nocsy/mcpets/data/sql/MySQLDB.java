package fr.nocsy.mcpets.data.sql;

import fr.nocsy.mcpets.MCPets;
import kr.gomsv.kokonutCore.api.database.CoreDatabase;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

/**
 * 에고버스: Kokonut-Core 공용 커넥션 풀({@link CoreDatabase}) 위의 얇은 래퍼. 접속 정보는 Core 가 가진다.
 * SELECT 결과는 {@link CachedRowSet} 으로 메모리에 담아 돌려주고 커넥션은 바로 풀에 반납한다
 * (예전처럼 열린 ResultSet 을 들고 있지 않는다).
 */
public class MySQLDB {

    private final CoreDatabase database;

    public MySQLDB(final CoreDatabase database) {
        this.database = database;
    }

    /** 풀에서 커넥션 하나를 빌려 확인한다. 실패하면 false. */
    public boolean init() {
        try (Connection connection = database.openConnection()) {
            return connection.isValid(2);
        } catch (SQLException e) {
            MCPets.getInstance().getLogger().log(Level.SEVERE, "Kokonut-Core DB 에 연결하지 못했습니다.", e);
            return false;
        }
    }

    /** 풀은 Core 소유라 닫지 않는다. */
    public void close() {
    }

    public String tablePrefix() {
        return database.tablePrefix();
    }

    public ResultSet query(final String sql) {
        try (Connection connection = database.openConnection(); Statement stat = connection.createStatement()) {
            if (isSelect(sql)) {
                try (ResultSet rs = stat.executeQuery(sql)) {
                    return cache(rs);
                }
            }
            stat.executeUpdate(sql);
        } catch (SQLException e) {
            MCPets.getInstance().getLogger().log(Level.SEVERE, "SQL query failed: " + sql, e);
        }
        return null;
    }

    public ResultSet preparedQuery(final String sql, final Object... params) {
        try (Connection connection = database.openConnection(); PreparedStatement stat = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) stat.setObject(i + 1, params[i]);
            if (isSelect(sql)) {
                try (ResultSet rs = stat.executeQuery()) {
                    return cache(rs);
                }
            }
            stat.executeUpdate();
        } catch (SQLException e) {
            MCPets.getInstance().getLogger().log(Level.SEVERE, "SQL prepared query failed: " + sql, e);
        }
        return null;
    }

    private static boolean isSelect(final String sql) {
        return sql.trim().toLowerCase().startsWith("select");
    }

    private static ResultSet cache(final ResultSet rs) throws SQLException {
        final CachedRowSet cached = RowSetProvider.newFactory().createCachedRowSet();
        cached.populate(rs);
        return cached;
    }
}
