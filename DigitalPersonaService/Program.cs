using DigitalPersonaService.Services;
using DigitalPersonaService.Models;
using Serilog;

var builder = WebApplication.CreateBuilder(args);

// Configure Serilog
Log.Logger = new LoggerConfiguration()
    .ReadFrom.Configuration(builder.Configuration)
    .Enrich.FromLogContext()
    .WriteTo.Console()
    .WriteTo.File("logs/digitalpersona-service-.log", rollingInterval: RollingInterval.Day)
    .WriteTo.EventLog("DigitalPersonaFingerprintService", manageEventSource: true)
    .CreateLogger();

builder.Host.UseSerilog();

// Add services to the container
builder.Services.AddControllers();
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

// Add CORS
builder.Services.AddCors(options =>
{
    options.AddPolicy("AllowAll", policy =>
    {
        policy.AllowAnyOrigin()
              .AllowAnyMethod()
              .AllowAnyHeader();
    });
});

// Add logging
builder.Services.AddLogging();

// Add fingerprint service
builder.Services.AddSingleton<FingerprintService>();
builder.Services.AddSingleton<DeviceManager>();

// Configure as Windows Service
builder.Services.AddWindowsService(options =>
{
    options.ServiceName = "DigitalPersonaFingerprintService";
});

var app = builder.Build();

// Configure the HTTP request pipeline
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseCors("AllowAll");
app.UseRouting();
app.MapControllers();

// Initialize fingerprint service
var fingerprintService = app.Services.GetRequiredService<FingerprintService>();
await fingerprintService.InitializeAsync();

Log.Information("Digital Persona Fingerprint Service started on port {Port}", 
    builder.Configuration["ServiceSettings:Port"] ?? "5001");

try
{
    app.Run();
}
catch (Exception ex)
{
    Log.Fatal(ex, "Application terminated unexpectedly");
}
finally
{
    Log.CloseAndFlush();
}
