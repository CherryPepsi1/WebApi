const express = require('express');
const pingRoutes = require('./routes/ping-routes');
const userRoutes = require('./routes/user-routes');

const app = express();
app.use(express.json());

app.use('/api/ping', pingRoutes);
app.use('/api/users', userRoutes);

module.exports = app;