create table Sets (
	id INTEGER GENERATED BY DEFAULT AS IDENTITY(START WITH 1) PRIMARY KEY,
	name VARCHAR(30) NOT NULL,
	abbreviation VARCHAR(5) NOT NULL,
	language CHAR(2) NOT NULL
)