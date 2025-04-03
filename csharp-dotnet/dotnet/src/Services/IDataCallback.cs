using MySql.Data.MySqlClient;
using WebApi.Models;

namespace WebApi.Services
{
    public interface IDataCallback
    {
        public IEnumerable<T> ParseResultSet<T>(MySqlDataReader reader);
    }
}
