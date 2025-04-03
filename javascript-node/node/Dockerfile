FROM node:23.10.0-alpine

# Set working directory
WORKDIR /usr/app

# Copy files
COPY package.json /usr/app
COPY src/ /usr/app

# Install dependencies
RUN npm install

# Run node
ENTRYPOINT ["node","index.js"]