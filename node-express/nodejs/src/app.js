const express = require('express');
const pingRoutes = require('./routes/ping-routes');
const userRoutes = require('./routes/user-routes');

const app = express();
app.use(express.json());

const pathPrefix = "/api";
app.use(`${pathPrefix}/ping`, pingRoutes);
app.use(`${pathPrefix}/users`, userRoutes);

module.exports = app;