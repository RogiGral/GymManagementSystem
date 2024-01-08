# Gym management system  - Power Hour

The main goal of this project is to create system that will help managing every kind of gym company.

To initialize docker you neet to write this commands in your cmd terminal:

`docker run --name gymdb -e POSTGRES_PASSWORD=gymDB -d -p 5432:5432 postgres`\
`docker exec -it gymdb bash`\
`psql -U postgres`\
`create database gymdb;`

And that is all