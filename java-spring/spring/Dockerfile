FROM sapmachine:21.0.6-jre-ubuntu-24.04

# Environment variables
ENV DATABASE_URL=jdbc:mariadb://mariadb:3306/webapi
ENV DATABASE_USERNAME=root
ENV DATABASE_PASSWORD=de4ac6d26c0542c98ce978f7eb3b8aa7

# Set working directory
WORKDIR /usr/app

# Copy files
COPY target/webapi-0.0.1.jar /usr/app

# Run JAR
ENTRYPOINT ["java","-jar","webapi-0.0.1.jar"]