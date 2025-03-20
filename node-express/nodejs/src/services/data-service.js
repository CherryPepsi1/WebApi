const { Client } = require('pg');
const { DataError } = require('../errors/data-error');

const client = new Client({
  host: process.env.DATABASE_HOST,
  port: process.env.DATABASE_PORT,
  user: process.env.DATABASE_USER,
  password: process.env.DATABASE_PASSWORD,
  database: process.env.DATABASE_NAME,
});

const SEPARATOR = ", ";

const initialize = async () => {
  try {
    console.log("Opening database connection");
    await client.connect();
  } catch (err) {
    console.error("Failed to open database connection: ", err);
  }
}

const finalize = async () => {
  try {
    console.log('Closing datbase connection');
    await client.end();
    return true;
  } catch (err) {
    console.error("Failed to close database connection: ", err);
    return false;
  }
}

const buildSelectClause = (columns) => {
  var str = "SELECT ";
  if (columns == null || columns.length == 0) {
    str += "*";
  } else {
    str += columns.join(SEPARATOR);
  }

  return str;
}

const buildWhereClause = (filter, i = 1) => {
  var str = " WHERE ";
  let pairs = [];
  Object.keys(filter).forEach(key => {
    pairs.push(`${key} = $${i}`);
    i++;
  });
  str += pairs.join(" AND ");

  return str;
}

const buildValuesClause = (values) => {
  var str = " VALUES ";
  let params = [];
  let i = 1;
  Object.keys(values).forEach(key => {
    params.push(`$${i}`);
    i++;
  });
  str += `(${params.join(SEPARATOR)})`;

  return str;
}

const buildReturningClause = (columns) => {
  var str = " RETURNING ";
  str += columns.join(SEPARATOR);

  return str;
}

const buildSetClause = (values) => {
  var str = " SET ";
  let pairs = [];
  let i = 1;
  Object.keys(values).forEach(key => {
    pairs.push(`${key} = $${i}`);
    i++;
  });
  str += pairs.join(SEPARATOR);

  return str;
}

const querySelect = async (table, count, offset = null, columns = null, filter = null) => {
  var sql = buildSelectClause(columns);
  sql += ` FROM ${table}`;
  if (filter != null && Object.keys(filter).length > 0) {
    sql += buildWhereClause(filter);
  }
  sql += ` LIMIT ${count}`;
  if (offset != null) {
    sql += ` OFFSET ${offset}`;
  }

  try {
    const res = await client.query(sql, filter != null
      ? Object.values(filter) : null);
    return res.rows;
  } catch (err) {
    console.error("Failed to execute select query: " + err);
    throw new DataError("Database operation failed", { cause: err });
  }
}

const querySelectFirst = async (table, columns = null, filter = null) => {
  const rows = await querySelect(table, 1, null, columns, filter);
  return rows.length > 0 ? rows[0] : null;
}

const queryInsert = async (table, values, retColumns = null) => {
  if (values == null || Object.keys(values).length == 0) {
    throw new DataError("Values cannot be null or empty");
  }

  var sql = `INSERT INTO ${table} (${Object.keys(values).join(SEPARATOR)})`;
  sql += buildValuesClause(values);
  if (retColumns != null) {
    sql += buildReturningClause(retColumns);
  }

  try {
    const res = await client.query(sql, Object.values(values));
    if (retColumns != null) {
      return res.rows.length > 0 ? res.rows[0] : null;
    } else {
      return res.rowCount;
    }
  } catch (err) {
    console.error("Failed to execute insert query: " + err);
    throw new DataError("Database operation failed", { cause: err });
  }
}

const queryUpdate = async (table, values, filter = null, retColumns = null) => {
  if (values == null || Object.keys(values).length == 0) {
    throw new DataError("Values cannot be null or empty");
  }

  var sql = `UPDATE ${table}`;
  sql += buildSetClause(values);
  if (filter != null && Object.keys(filter).length > 0) {
    sql += buildWhereClause(filter, Object.keys(values).length + 1);
  }
  if (retColumns != null) {
    sql += buildReturningClause(retColumns);
  }

  try {
    const res = await client.query(sql, filter != null
      ? Object.values(values).concat(Object.values(filter)) : Object.values(values));
    if (retColumns != null) {
      return res.rows.length > 0 ? res.rows[0] : null;
    } else {
      return res.rowCount;
    }
  } catch (err) {
    console.error("Failed to execute update query: " + err);
    throw new DataError("Database operation failed", { cause: err });
  }
}

const queryDelete = async (table, filter = null) => {
  var sql = `DELETE FROM ${table}`;
  if (filter != null && Object.keys(filter).length > 0) {
    sql += buildWhereClause(filter);
  }

  try {
    const res = await client.query(sql, filter != null
      ? Object.values(filter) : null);
    return res.rowCount;
  } catch (err) {
    console.error("Failed to execute delete query: " + err);
    throw new DataError("Database operation failed", { cause: err });
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