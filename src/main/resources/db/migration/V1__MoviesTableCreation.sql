CREATE TABLE IF NOT EXISTS movies (
  imdb_id  VARCHAR NOT NULL,
  screen_id VARCHAR NOT NULL,
  movie_title VARCHAR,
  available_seats INT NOT NULL,
  reserved_seats INT DEFAULT 0,
  PRIMARY KEY(imdb_id, screen_id)
);