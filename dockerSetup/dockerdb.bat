@echo off

REM Check if the PostgreSQL container already exists
docker inspect gymdb > nul 2>&1
if errorlevel 1 (
    REM Container does not exist, so start it
    echo Creating PostgreSQL container...
    docker run --name gymdb -e POSTGRES_PASSWORD=gymDB -d -p 5432:5432 postgres

    REM Wait for the container to start (adjust sleep time as needed)
    timeout /t 10 /nobreak
) else (
    REM Container already exists, skipping creation
    echo PostgreSQL container already exists. Skipping creation.
)

REM Check if the database "gymdb" already exists and drop it if it does
docker exec -it gymdb psql -U postgres -lqt | find "gymdb" > nul;
if %errorlevel% equ 0 (
    echo Dropping existing gymdb database...
    docker exec -it gymdb psql -U postgres -c "drop database gymdb;";
)

REM Create the "gymdb" database
echo Creating gymdb database...
docker exec -it gymdb psql -U postgres -c "create database gymdb;";

echo Script completed.