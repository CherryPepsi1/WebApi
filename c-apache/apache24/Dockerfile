FROM httpd:2.4

# Install build dependencies
RUN apt-get update
RUN apt-get install -y apache2-dev libsqlite3-dev libcjson-dev

# Set working directory
WORKDIR /usr/app

# Copy files
COPY /src/. /usr/app
COPY httpd.conf /usr/local/apache2/conf/httpd.conf

# Set permissions
RUN chmod 777 /usr
RUN chmod 777 /usr/app
RUN chmod 777 /usr/app/web_api.db

# Configure and compile the module
RUN apxs -n mod_web_api -i -c -o mod_web_api.so -l sqlite3 -l cjson web_api.c dal.c