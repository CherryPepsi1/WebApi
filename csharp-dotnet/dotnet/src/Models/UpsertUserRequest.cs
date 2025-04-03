using System.ComponentModel.DataAnnotations;

namespace WebApi.Models
{
    public class UpsertUserRequest
    {
        [Required(ErrorMessage = "Name is required.")]
        public string Name { get; set; }

        [Required(ErrorMessage = "Age is required.")]
        [Range(0, int.MaxValue, ErrorMessage = "Age must be greater than or equal to 0.")]
        public int? Age { get; set; }
    }
}
