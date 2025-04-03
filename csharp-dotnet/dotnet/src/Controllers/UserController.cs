using Microsoft.AspNetCore.Mvc;
using WebApi.Exceptions;
using WebApi.Models;
using WebApi.Services;

namespace WebApi.Controllers
{
    [ApiController]
    [Route("api/users")]
    public class UserController : ControllerBase
    {
        private readonly ILogger<UserController> _logger;
        private readonly UserService _userService;

        public UserController(ILogger<UserController> logger, UserService userService)
        {
            _logger = logger;
            _userService = userService;
        }

        [HttpGet]
        public async Task<IActionResult> GetUsersAsync([FromQuery] GetUsersRequest request)
        {
            try
            {
                var users = await _userService.GetUsersAsync(request.Page);
                return new OkObjectResult(users);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error");
                return new StatusCodeResult(StatusCodes.Status500InternalServerError);
            }
        }

        [HttpGet("{id}")]
        public async Task<IActionResult> GetUserAsync(int id)
        {
            try
            {
                var user = await _userService.GetUserAsync(id);
                return new OkObjectResult(user);
            }
            catch (NotFoundException)
            {
                return new NotFoundResult();
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error");
                return new StatusCodeResult(StatusCodes.Status500InternalServerError);
            }
        }

        [HttpPost]
        public async Task<IActionResult> CreateUserAsync([FromBody] UpsertUserRequest request)
        {
            try
            {
                var user = await _userService.CreateUserAsync(new User(request));
                var uri = new Uri(string.Join("/", Request.Path.Value, user.Id.ToString()), UriKind.Relative);
                return new CreatedResult(uri, user);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error");
                return new StatusCodeResult(StatusCodes.Status500InternalServerError);
            }
        }

        [HttpPut("{id}")]
        public async Task<IActionResult> UpdateUserAsync(int id, [FromBody] UpsertUserRequest request)
        {
            try
            {
                var user = await _userService.UpdateUserAsync(id, new User(request));
                return new OkObjectResult(user);
            }
            catch (NotFoundException)
            {
                return new NotFoundResult();
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error");
                return new StatusCodeResult(StatusCodes.Status500InternalServerError);
            }
        }

        [HttpDelete("{id}")]
        public async Task<IActionResult> DeleteUserAsync(int id)
        {
            try
            {
                await _userService.DeleteUserAsync(id);
                return new NoContentResult();
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error");
                return new StatusCodeResult(StatusCodes.Status500InternalServerError);
            }
        }
    }
}
