create schema if not exists duck;

create table if not exists duck.scenario (
    scenario_db_id SERIAL PRIMARY KEY,
    prompt       TEXT,
    answer       TEXT
);