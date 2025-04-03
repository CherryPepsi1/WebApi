const app = require('./app');
const dataService = require('./services/data-service');
const port = process.env.SERVER_PORT || 8080;

app.listen(port, () => {
  console.log(`Server listening on port: ${port}`);
});

process.on('SIGINT', async () => {
  console.log('Received SIGINT');
  const res = await dataService.finalize();
  process.exit(res ? 0 : 1);
});

process.on('SIGTERM', async () => {
  console.log('Received SIGTERM');
  const res = await dataService.finalize();
  process.exit(res ? 0 : 1);
});