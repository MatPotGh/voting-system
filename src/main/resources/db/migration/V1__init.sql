DROP TABLE IF EXISTS votes CASCADE;
DROP TABLE IF EXISTS election_options CASCADE;
DROP TABLE IF EXISTS elections CASCADE;
DROP TABLE IF EXISTS voters CASCADE;
DROP TABLE IF EXISTS candidates CASCADE;

CREATE TABLE voters (
    id BIGSERIAL PRIMARY KEY,
    pesel VARCHAR(11) NOT NULL UNIQUE,
    blocked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE elections (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE candidates (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    age SMALLINT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE election_options (
    id BIGSERIAL PRIMARY KEY,
    election_id BIGINT NOT NULL REFERENCES elections (id) ON DELETE CASCADE,
    candidate_id BIGINT NOT NULL REFERENCES candidates (id) ON DELETE RESTRICT,
    CONSTRAINT uq_option_candidate_per_election UNIQUE (election_id, candidate_id)
);
CREATE INDEX idx_election_options_election ON election_options(election_id);
CREATE INDEX idx_election_options_candidate ON election_options(candidate_id);

CREATE TABLE votes (
    id BIGSERIAL PRIMARY KEY,
    voter_id BIGINT NOT NULL REFERENCES voters (id) ON DELETE CASCADE,
    election_id BIGINT NOT NULL REFERENCES elections (id) ON DELETE CASCADE,
    election_option_id BIGINT NOT NULL REFERENCES election_options (id) ON DELETE RESTRICT,
    cast_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_vote_unique_per_election UNIQUE (voter_id, election_id)
);
CREATE INDEX idx_votes_election ON votes(election_id);
CREATE INDEX idx_votes_voter ON votes(voter_id);
