package io.pivotal.pal.tracker;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import java.sql.*;
import java.util.List;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class JdbcTimeEntryRepository implements TimeEntryRepository {
    private final JdbcTemplate jdbcTemplate;

    public JdbcTimeEntryRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public TimeEntry create(TimeEntry timeEntry) {
        GeneratedKeyHolder gkh = new GeneratedKeyHolder();
        String sql = "INSERT INTO time_entries " +
                "(project_id, user_id, date, hours) " +
                "VALUES (?, ?, ?, ?)";

        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql,
                    RETURN_GENERATED_KEYS
            );
            statement.setLong(1, timeEntry.getProjectId());
            statement.setLong(2, timeEntry.getUserId());
            statement.setString(3, timeEntry.getDate());
            statement.setInt(4, timeEntry.getHours());
            return statement;
        }, gkh);

        timeEntry.setId(gkh.getKey().intValue());
        return timeEntry;
    }

    @Override
    public TimeEntry find(Long id) {
        String sql = "SELECT * from time_entries WHERE id=" + id + ";";

        List<TimeEntry> timeEntries = jdbcTemplate.query(sql, timeEntryRowMapper);

        if (timeEntries.isEmpty()) {
            return null;
        } else { // list contains exactly 1 element
            return timeEntries.get(0);
        }
    }

    @Override
    public List<TimeEntry> list() {
        String sql = "SELECT * from time_entries;";

        List<TimeEntry> timeEntries = jdbcTemplate.query(sql, timeEntryRowMapper);

        if (timeEntries.isEmpty()) {
            return null;
        } else { // list contains exactly 1 element
            return timeEntries;
        }
    }

    @Override
    public TimeEntry update(Long id, TimeEntry timeEntry) {
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE time_entries " +
                            "SET user_id=?, project_id=?, date=?, hours=? " +
                            "WHERE id=?",
                    RETURN_GENERATED_KEYS
            );
            statement.setLong(1, timeEntry.getUserId());
            statement.setLong(2, timeEntry.getProjectId());
            statement.setString(3, timeEntry.getDate());
            statement.setInt(4, timeEntry.getHours());
            statement.setLong(5, id);
            return statement;
        });

        return find(id);
    }

    @Override
    public void delete(Long id) {
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    "DELETE FROM time_entries WHERE ID=?",
                    RETURN_GENERATED_KEYS
            );
            statement.setLong(1, id);
            return statement;
        });
    }

    private final RowMapper<TimeEntry> timeEntryRowMapper = (rs, rowNum) -> {
        Long id1 = rs.getLong("id");
        Long userId = rs.getLong("user_id");
        Long projectId = rs.getLong("project_id");
        String date = rs.getString("date");
        int hours = rs.getInt("hours");
        return new TimeEntry(id1, projectId, userId, date, hours);
    };
}
