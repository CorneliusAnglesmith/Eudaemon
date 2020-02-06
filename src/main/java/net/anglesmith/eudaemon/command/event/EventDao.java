package net.anglesmith.eudaemon.command.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class EventDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventDao.class);

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public EventDao(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public void testConnection() {
        final String sql = "SELECT 1 FROM SYSIBM.SYSDUMMY1;";

        this.namedParameterJdbcTemplate.query(sql, (rs, i) -> rs.first());
    }
}
