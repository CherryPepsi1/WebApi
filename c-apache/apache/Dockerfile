FROM httpd:2.4

# Install build dependencies
RUN apt-get update
RUN apt-get install -y apache2-dev libsqlite3-dev libcjson-dev

# Set working directory
WORKDIR /usr/app

# Copy files
COPY db/. /usr/app
COPY src/.libs/mod_web_api.so /usr/local/apache2/modules/mod_web_api.so
COPY httpd.conf /usr/local/apache2/conf/httpd.conf

# Set permissions
RUN chmod 777 /usr
RUN chmod 777 /usr/app
RUN chmod 777 /usr/app/web_api.db

# Configure and compile the module
RUN apachectl restart