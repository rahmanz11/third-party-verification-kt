using System;
using System.ServiceProcess;
using System.Threading;
using System.Threading.Tasks;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.AspNetCore.Hosting;
using Microsoft.AspNetCore.Builder;
using Microsoft.Extensions.Configuration;

namespace DigitalPersonaService
{
    public class Program
    {
        public static async Task Main(string[] args)
        {
            var host = CreateHostBuilder(args).Build();
            
            if (args.Length > 0 && args[0] == "--console")
            {
                // Run as console app for development
                await host.RunAsync();
            }
            else
            {
                // Run as Windows service
                using (var service = new DigitalPersonaWindowsService(host))
                {
                    ServiceBase.Run(service);
                }
            }
        }

        public static IHostBuilder CreateHostBuilder(string[] args) =>
            Host.CreateDefaultBuilder(args)
                .ConfigureWebHostDefaults(webBuilder =>
                {
                    webBuilder.UseStartup<Startup>();
                    webBuilder.UseUrls("http://localhost:5001");
                })
                .UseWindowsService();
    }

    public class DigitalPersonaWindowsService : ServiceBase
    {
        private readonly IHost _host;
        private IWebHost _webHost;

        public DigitalPersonaWindowsService(IHost host)
        {
            _host = host;
            ServiceName = "DigitalPersonaFingerprintService";
        }

        protected override void OnStart(string[] args)
        {
            _webHost = _host.Services.GetRequiredService<IWebHost>();
            _webHost.Start();
        }

        protected override void OnStop()
        {
            _webHost?.StopAsync().Wait();
        }
    }
}
