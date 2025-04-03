const { Client } = require('pg');
const { DataError } = require('../errors/data-error');

const client = new Client({
  host: process.env.DATABASE_HOST,
  port: process.env.DATABASE_PORT,
  user: process.env.DATABASE_USER,
  password: process.env.DATABASE_PASSWORD,
  database: process.env.DATABASE_NAME,
});

const SEPARATOR = ', ';
const PARAM_PREFIX = '$'

const initialize = async () => {
  try {
    console.log('Opening database connection');
    await client.connect();
  } catch (err) {
    console.error('Failed to open database connection: ', err);
  }
}

const finalize = async () => {
  try {
    console.log('Closing datbase connection');
    await client.end();
    return true;
  } catch (err) {
    console.error('Failed to close database connection: ', err);
    return false;
  }
}

const buildSelectClause = (columns) => {
  var str = 'SELECT ';
  if (columns == null || columns.length == 0) {
    str += '*';
  } else {
    str += columns.join(SEPARATOR);
  }

  return str;
}

const buildWhereClause = (filters, i = 1) => {
  if (filters == null || Object.keys(filters).length == 0) {
    return '';
  }

  var str = ' WHERE ';
  let pairs = [];
  Object.keys(filters).forEach(key => {
    pairs.push(`${key} = ${PARAM_PREFIX}${i}`);
    i++;
  });
  str += pairs.join(' AND ');

  return str;
}

const buildValuesClause = (values, i = 1) => {
  if (values == null || Object.keys(values).length == 0) {
    return '';
  }

  var str = ' VALUES ';
  let pairs = [];
  Object.keys(values).forEach(key => {
    pairs.push(`${PARAM_PREFIX}${i}`);
    i++;
  });
  str += `(${pairs.join(SEPARATOR)})`;

  return str;
}

const buildReturningClause = (columns) => {
  if (columns == null || columns.length == 0) {
    return '';
  }

  var str = ' RETURNING ';
  str += columns.join(SEPARATOR);

  return str;
}

const buildSetClause = (values, i = 1) => {
  if (values == null || Object.keys(values).length == 0) {
    return '';
  }

  var str = ' SET ';
  let pairs = [];
  Object.keys(values).forEach(key => {
    pairs.push(`${key} = ${PARAM_PREFIX}${i}`);
    i++;
  });
  str += pairs.join(SEPARATOR);

  return str;
}

const querySelect = async (table, count, offset = null, columns = null, filters = null) => {
  var sql = buildSelectClause(columns);
  sql += ` FROM ${table}`;
  sql += buildWhereClause(filters);
  sql += ` LIMIT ${count}`;
  if (offset != null) {
    sql += ` OFFSET ${offset}`;
  }

  try {
    const res = await client.query(sql, filters != null
      ? Object.values(filters) : null);
    return res.rows;
  } catch (err) {
    console.error('Failed to execute select query: ' + err);
    throw new DataError('Database operation failed', { cause: err });
  }
}

const querySelectFirst = async (table, columns = null, filters = null) => {
  const rows = await querySelect(table, 1, null, columns, filters);
  return rows.length > 0 ? rows[0] : null;
}

const queryInsert = async (table, values, retColumns = null) => {
  if (values == null || Object.keys(values).length == 0) {
    throw new DataError('Values cannot be null or empty');
  }

  var sql = `INSERT INTO ${table} (${Object.keys(values).join(SEPARATOR)})`;
  sql += buildValuesClause(values);
  sql += buildReturningClause(retColumns);

  try {
    const res = await client.query(sql, Object.values(values));
    if (retColumns != null && res.rows.length > 0) {
      return res.rows[0];
    } else if (res.rowCount > 0) {
      return res.rowCount;
    }
  } catch (err) {
    console.error('Failed to execute insert query: ' + err);
    throw new DataError('Database operation failed', { cause: err });
  }

  throw new DataError('Database operation failed');
}

const queryUpdate = async (table, values, filters = null, retColumns = null) => {
  if (values == null || Object.keys(values).length == 0) {
    throw new DataError('Values cannot be null or empty');
  }

  var sql = `UPDATE ${table}`;
  sql += buildSetClause(values);
  sql += buildWhereClause(filters, Object.keys(values).length + 1);
  sql += buildReturningClause(retColumns);

  try {
    const res = await client.query(sql, filters != null
      ? Object.values(values).concat(Object.values(filters)) : Object.values(values));
    if (retColumns != null) {
      return res.rows.length > 0 ? res.rows[0] : null;
    } else {
      return res.rowCount;
    }
  } catch (err) {
    console.error('Failed to execute update query: ' + err);
    throw new DataError('Database operation failed', { cause: err });
  }
}

const queryDelete = async (table, filters = null) => {
  var sql = `DELETE FROM ${table}`;
  sql += buildWhereClause(filters);

  try {
    const res = await client.query(sql, filters != null
      ? Object.values(filters) : null);
    return res.rowCount;
  } catch (err) {
    console.error('Failed to execute delete query: ' + err);
    throw new DataError('Database operation failed', { cause: err });
  }
}

initialize();

module.exports = {
  finalize,
  querySelect,
  querySelectFirst,
  queryInsert,
  queryUpdate,
  queryDelete
};