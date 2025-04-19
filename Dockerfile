FROM mysql:8.0.40
COPY ./db/sql-scripts/ /docker-entrypoint-initdb.d/
