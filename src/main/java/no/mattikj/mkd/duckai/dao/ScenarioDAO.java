package no.mattikj.mkd.duckai.dao;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

import no.mattikj.mkd.duckai.dto.ScenarioDto;
import no.mattikj.mkd.duckai.mapper.ScenarioRowMapper;

/**
 * DAO class for scenario entities.
 *
 * @author Matti Kjellstadli
 * @version 1.2.0
 */
@Repository
public class ScenarioDAO {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public ScenarioDAO(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public List<ScenarioDto> getAllScenarios() {
        String sql = """
            select s.*
            from duck.scenario s
            """;
        return namedParameterJdbcTemplate.query(sql, new ScenarioRowMapper());
    }

    public ScenarioDto getScenario(final Long scenarioId) {
        String sql = """
            select s.*
            from duck.scenario s
            where s.scenario_db_id = :scenarioId
            """;
        var params = new HashMap<String, Object>();
        params.put("scenarioId", scenarioId);
        return namedParameterJdbcTemplate.queryForObject(sql, params, new ScenarioRowMapper());
    }

    public int createScenario(final ScenarioDto scenario) {
        String sql = """
            insert into duck.scenario (prompt, answer)
            values (:prompt, :answer)
            """;
        var params = new HashMap<String, Object>();
        params.put("prompt", scenario.getPrompt());
        params.put("answer", scenario.getAnswer());
        return namedParameterJdbcTemplate.update(sql, params);
    }
}
