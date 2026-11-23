CREATE TABLE users (
                       id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                       first_name VARCHAR(100) NOT NULL,
                       last_name VARCHAR(100) NOT NULL
);

CREATE TABLE friendships (
                             id1 BIGINT NOT NULL,
                             id2 BIGINT NOT NULL,
                             friends_from TIMESTAMP NOT NULL,
                             PRIMARY KEY (id1, id2),
                             CONSTRAINT fk_user1 FOREIGN KEY (id1) REFERENCES users(id) ON DELETE CASCADE,
                             CONSTRAINT fk_user2 FOREIGN KEY (id2) REFERENCES users(id) ON DELETE CASCADE,
                             CONSTRAINT chk_id_order CHECK (id1 < id2)
);

-- Optional: date de test pentru paginare
INSERT INTO users (first_name, last_name) VALUES
                                              ('Andrei', 'Biro'),
                                              ('Luca', 'Batori'),
                                              ('Dominic', 'Bejan'),
                                              ('Miruna', 'Pop'),
                                              ('Noel', 'Kovacs'),
                                              ('Cojo', 'Radu');

INSERT INTO friendships (id1, id2, friends_from) VALUES
                                                     (1, 2, NOW()),
                                                     (1, 3, NOW()),
                                                     (1, 4, NOW()),
                                                     (1, 5, NOW()),
                                                     (2, 3, NOW());