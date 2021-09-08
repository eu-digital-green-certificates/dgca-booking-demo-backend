DROP TABLE IF EXISTS passengers;
DROP TABLE IF EXISTS bookings;

CREATE TABLE passengers (
  passenger_id CHAR(36) PRIMARY KEY,
  session_id VARCHAR(255) NOT NULL
);

CREATE TABLE bookings (
  session_id VARCHAR(255) PRIMARY KEY,
  booking VARCHAR(2147483647) DEFAULT NULL
);