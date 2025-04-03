using System.Data;
using System.Text;
using MySql.Data.MySqlClient;

namespace WebApi.Services
{
    public class DataService : IDisposable
    {
        private const string SEPARATOR = ", ";
        private const string PARAM_PREFIX_FILTER = "@f_";
        private const string PARAM_PREFIX_VALUE = "@v_";

        private readonly ILogger<DataService> _logger;
        private MySqlConnection _connection;
        private bool _disposed;

        public DataService(ILogger<DataService> logger)
        {
            _logger = logger;
            try
            {
                _logger.LogInformation("Openning database connection");
                var connectionString = string.Format(
                    "server={0};port={1};uid={2};pwd={3};database={4}",
                    Environment.GetEnvironmentVariable("DATABASE_HOST"),
                    Environment.GetEnvironmentVariable("DATABASE_PORT"),
                    Environment.GetEnvironmentVariable("DATABASE_USERNAME"),
                    Environment.GetEnvironmentVariable("DATABASE_PASSWORD"),
                    Environment.GetEnvironmentVariable("DATABASE_NAME")
                );
                _connection = new MySqlConnection(connectionString);
                _connection.Open();
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Failed to open database connection");
                _connection = null;
            }
        }

        #region Private Helper Methods
        private static string BuildSelectClause(string[] columns)
        {
            var str = "SELECT ";
            if (columns == null || columns.Length == 0)
            {
                str += "*";
            }
            else
            {
                str += string.Join(SEPARATOR, columns);
            }
            return str;
        }

        private static string BuildWhereClause(IDictionary<string, object> filters)
        {
            if (filters == null || filters.Count == 0) return "";

            var str = " WHERE ";
            var pairs = new List<string>();
            foreach (var key in filters.Keys)
            {
                pairs.Add($"{key} = {PARAM_PREFIX_FILTER}{key}");
            }

            str += string.Join(" AND ", pairs);
            return str;
        }

        private static string BuildValuesClause(IDictionary<string, object> values)
        {
            if (values == null || values.Count == 0) return "";

            var str = " VALUES ";
            var pairs = new List<string>();
            foreach (var key in values.Keys)
            {
                pairs.Add($"{PARAM_PREFIX_VALUE}{key}");
            }

            str += $"({string.Join(SEPARATOR, pairs)})";
            return str;
        }

        private static string BuildSetClause(IDictionary<string, object> values)
        {
            if (values == null || values.Count == 0) return "";

            var str = " SET ";
            var pairs = new List<string>();
            foreach (var key in values.Keys)
            {
                pairs.Add($"{key} = {PARAM_PREFIX_VALUE}{key}");
            }

            str += string.Join(SEPARATOR, pairs);
            return str;
        }

        private static void AddParameterValues(
            MySqlParameterCollection parameters,
            IDictionary<string, object> values = null,
            IDictionary<string, object> filters = null)
        {
            if (values != null)
            {
                foreach (var value in values)
                {
                    parameters.AddWithValue($"{PARAM_PREFIX_VALUE}{value.Key}", value.Value);
                }
            }

            if (filters != null)
            {
                foreach (var filter in filters)
                {
                    parameters.AddWithValue($"{PARAM_PREFIX_FILTER}{filter.Key}", filter.Value);
                }
            }
        }
        #endregion

        #region Public Query Methods
        public async Task<IEnumerable<T>> QuerySelectAsync<T>(
            IDataCallback callback,
            string table,
            int count,
            int? offset = null,
            string[] columns = null,
            IDictionary<string, object> filters = null)
        {
            ArgumentNullException.ThrowIfNull(callback, nameof(callback));
            if (_connection == null)
                throw new InvalidOperationException("Database connection is null");

            try
            {
                var sqlBuilder = new StringBuilder();
                sqlBuilder.Append(BuildSelectClause(columns));
                sqlBuilder.Append($" FROM {table}");
                sqlBuilder.Append(BuildWhereClause(filters));
                sqlBuilder.Append($" LIMIT {count}");
                if (offset != null)
                {
                    sqlBuilder.Append($" OFFSET {offset}");
                }

                var sqlCommand = new MySqlCommand(sqlBuilder.ToString(), _connection);
                AddParameterValues(sqlCommand.Parameters, null, filters);
                using var reader = await sqlCommand.ExecuteReaderAsync(CommandBehavior.Default);
                return callback.ParseResultSet<T>(reader);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Failed to execute select query");
                throw new DataException("Database operation failed", ex);
            }
        }

        public async Task<T> QuerySelectFirstAsync<T>(
            IDataCallback callback,
            string table,
            string[] columns = null,
            IDictionary<string, object> filters = null)
        {
            var results = await QuerySelectAsync<T>(callback, table, 1, null, columns, filters);
            return results != null ? results.FirstOrDefault() : default;
        }

        public async Task<long> QueryInsertAsync(
            string table,
            IDictionary<string, object> values)
        {
            if (values == null || values.Count == 0)
                throw new ArgumentException("Value cannot be null or empty", nameof(values));
            if (_connection == null)
                throw new InvalidOperationException("Database connection is null");

            try
            {
                var sqlBuilder = new StringBuilder();
                sqlBuilder.Append($"INSERT INTO {table} ({string.Join(SEPARATOR, values.Keys)})");
                sqlBuilder.Append(BuildValuesClause(values));

                var sqlCommand = new MySqlCommand(sqlBuilder.ToString(), _connection);
                AddParameterValues(sqlCommand.Parameters, values, null);
                return await sqlCommand.ExecuteNonQueryAsync() > 0
                    ? sqlCommand.LastInsertedId
                    : throw new DataException("Database operation failed");
            }
            catch (DataException)
            {
                throw;
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Failed to execute insert query");
                throw new DataException("Database operation failed", ex);
            }
        }

        public async Task<int> QueryUpdateAsync(
            string table,
            IDictionary<string, object> values,
            IDictionary<string, object> filters = null)
        {
            if (values == null || values.Count == 0)
                throw new ArgumentException("Value cannot be null or empty", nameof(values));
            if (_connection == null)
                throw new InvalidOperationException("Database connection is null");

            try
            {
                var sqlBuilder = new StringBuilder();
                sqlBuilder.Append($"UPDATE {table}");
                sqlBuilder.Append(BuildSetClause(values));
                sqlBuilder.Append(BuildWhereClause(filters));

                var sqlCommand = new MySqlCommand(sqlBuilder.ToString(), _connection);
                AddParameterValues(sqlCommand.Parameters, values, filters);
                return await sqlCommand.ExecuteNonQueryAsync();
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Failed to execute update query");
                throw new DataException("Database operation failed", ex);
            }
        }

        public async Task<int> QueryDeleteAsync(
            string table,
            IDictionary<string, object> filters = null)
        {
            if (_connection == null)
                throw new InvalidOperationException("Database connection is null");

            try
            {
                var sqlBuilder = new StringBuilder();
                sqlBuilder.Append($"DELETE FROM {table}");
                sqlBuilder.Append(BuildWhereClause(filters));

                var sqlCommand = new MySqlCommand(sqlBuilder.ToString(), _connection);
                AddParameterValues(sqlCommand.Parameters, null, filters);
                return await sqlCommand.ExecuteNonQueryAsync();
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Failed to execute delete query");
                throw new DataException("Database operation failed", ex);
            }
        }
        #endregion

        protected virtual void Dispose(bool disposing)
        {
            if (_disposed) return;

            if (disposing)
            {
                _connection?.Close();
                _connection = null;
            }

            _disposed = true;
        }

        public void Dispose()
        {
            Dispose(true);
            GC.SuppressFinalize(this);
        }
    }
}
