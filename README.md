cd ~/task-manager/backend
mvn spring-boot:run


cd ~/task-manager/frontend
npm start
npm install


sudo service postgresql start

psql -U postgres -d taskdb -h localhost
# password: postgres
\dt
SELECT * FROM users;
SELECT * FROM tasks;
\d users
\d tasks
SELECT COUNT(*) FROM users;
SELECT COUNT(*) FROM tasks;
\q

\dt                          # Show tables
SELECT * FROM users;         # Show registered users
SELECT id, title, status, priority FROM tasks;  # Show tasks
