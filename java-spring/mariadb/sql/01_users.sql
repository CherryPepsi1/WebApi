CREATE TABLE users (
    id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(128) NOT NULL,
    age INT NOT NULL,
    PRIMARY KEY (id)
);

INSERT INTO users (name, age) VALUES ('Dylan', 28);