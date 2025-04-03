using MySql.Data.MySqlClient;
using WebApi.Constants;
using WebApi.Exceptions;
using WebApi.Models;

namespace WebApi.Services
{
    public class UserService
    {
        private const int MAX_USERS = 10;

        private readonly ILogger<UserService> _logger;
        private readonly DataService _dataService;
        private readonly DataCallback _dataCallback;

        public class DataCallback : IDataCallback
        {
            public IEnumerable<T> ParseResultSet<T>(MySqlDataReader reader)
            {
                var users = new List<User>();
                while (reader.Read())
                {
                    var user = new User(
                        reader.GetInt32(DataConstants.COLUMN_ID),
                        reader.GetString(DataConstants.COLUMN_NAME),
                        reader.GetInt32(DataConstants.COLUMN_AGE));
                    users.Add(user);
                }
                return (IEnumerable<T>)users;
            }
        }

        public UserService(ILogger<UserService> logger, DataService dataService)
        {
            _logger = logger;
            _dataService = dataService;
            _dataCallback = new DataCallback();
        }

        public async Task<IEnumerable<User>> GetUsersAsync(int? page)
        {
            return await _dataService.QuerySelectAsync<User>(
                _dataCallback,
                DataConstants.TABLE_USERS,
                MAX_USERS,
                page != null ? (page.Value - 1) * MAX_USERS : null,
                DataConstants.COLUMNS_USERS,
                null);
        }

        public async Task<User> GetUserAsync(int id)
        {
            var filters = new Dictionary<string, object>()
            {
                { DataConstants.COLUMN_ID, id },
            };
            var user = await _dataService.QuerySelectFirstAsync<User>(
                _dataCallback,
                DataConstants.TABLE_USERS,
                DataConstants.COLUMNS_USERS,
                filters);

            return user ?? throw new NotFoundException();
        }

        public async Task<User> CreateUserAsync(User user)
        {
            var values = new Dictionary<string, object>()
            {
                { DataConstants.COLUMN_NAME, user.Name },
                { DataConstants.COLUMN_AGE, user.Age }
            };
            user.Id = (int)await _dataService.QueryInsertAsync(
                DataConstants.TABLE_USERS,
                values);

            return user;
        }

        public async Task<User> UpdateUserAsync(int id, User user)
        {
            var values = new Dictionary<string, object>()
            {
                { DataConstants.COLUMN_NAME, user.Name },
                { DataConstants.COLUMN_AGE, user.Age }
            };
            var filters = new Dictionary<string, object>()
            {
                { DataConstants.COLUMN_ID, id },
            };
            user.Id = id;
            var rowCount = await _dataService.QueryUpdateAsync(
                DataConstants.TABLE_USERS,
                values,
                filters);

            return rowCount > 0 ? user : throw new NotFoundException();
        }

        public async Task DeleteUserAsync(int id)
        {
            var filters = new Dictionary<string, object>()
            {
                { DataConstants.COLUMN_ID, id },
            };
            await _dataService.QueryDeleteAsync(
                DataConstants.TABLE_USERS,
                filters);
        }
    }
}
