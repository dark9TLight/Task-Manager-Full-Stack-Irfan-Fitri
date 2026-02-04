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

cd ~
git clone https://github.com/dark9TLight/Task-Manager-Full-Stack-Irfan-Fitri.git
cd Task-Manager-Full-Stack-Irfan-Fitri
git checkout 01fb819c60a6be5306782cc652df2f9fac7743ce
