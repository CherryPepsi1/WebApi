using System.Text.Json.Serialization;

namespace WebApi.Models
{
    public class ErrorResponse
    {
        [JsonRequired]
        public string Title { get; set; }

        [JsonIgnore(Condition = JsonIgnoreCondition.WhenWritingNull)]
        public int Status { get; set; }

        [JsonIgnore(Condition = JsonIgnoreCondition.WhenWritingNull)]
        public string Error { get; set; }
    }
}
