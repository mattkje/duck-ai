package no.mattikj.mkd.duckai.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import no.mattikj.mkd.duckai.dto.ScenarioDto;
import org.springframework.jdbc.core.RowMapper;

public class ScenarioRowMapper implements RowMapper<ScenarioDto> {
    @Override
    public ScenarioDto mapRow(final ResultSet rs, final int rowNum) throws SQLException {
        ScenarioDto scenario = new ScenarioDto();
        scenario.setScenarioId(rs.getString("scenario_db_id"));
        scenario.setPrompt(rs.getString("prompt"));
        scenario.setAnswer(rs.getString("answer"));
        return scenario;
    }
}
