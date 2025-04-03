using WebApi.Services;

var builder = WebApplication.CreateBuilder(args);

// Add controllers to the container.
builder.Services.AddControllers();

// Add services to the container.
builder.Services.AddSingleton<UserService>();
builder.Services.AddSingleton<DataService>();

var app = builder.Build();

app.UseHttpsRedirection();

app.UseAuthorization();

app.MapControllers();

app.Run();
