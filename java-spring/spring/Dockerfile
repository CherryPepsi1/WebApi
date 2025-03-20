FROM sapmachine:21.0.6-jre-alpine

# Set working directory
WORKDIR /usr/app

# Copy files
COPY target/webapi-1.0.0.jar /usr/app

# Run JAR
ENTRYPOINT ["java","-jar","webapi-1.0.0.jar"]