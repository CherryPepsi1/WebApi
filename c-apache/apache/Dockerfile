FROM httpd:2.4.63-alpine

# Install dependencies
RUN apk update
RUN apk add apache2-dev
RUN apk add sqlite-dev
RUN apk add cjson-dev

# Set working directory
WORKDIR /usr/app

# Copy files
COPY db/. /usr/app
COPY src/.libs/mod_web_api.so /usr/local/apache2/modules/mod_web_api.so
COPY httpd.conf /usr/local/apache2/conf/httpd.conf

# Set permissions
RUN chmod 0777 /usr
RUN chmod 0777 /usr/app
RUN chmod 0777 /usr/app/web_api.db

# Configure and compile the module
RUN apachectl restart