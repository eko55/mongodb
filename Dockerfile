FROM amazoncorretto:17

#install
RUN wget -qO - https://www.mongodb.org/static/pgp/server-8.0.asc | sudo apt-key add -