using System.ComponentModel.DataAnnotations;

namespace WebApi.Models
{
    public class GetUsersRequest
    {
        [Range(1, int.MaxValue, ErrorMessage = "Page must be greater than 0.")]
        public int? Page {  get; set; }
    }
}
