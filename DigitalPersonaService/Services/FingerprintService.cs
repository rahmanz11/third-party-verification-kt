using DigitalPersonaService.Models;
using Microsoft.Extensions.Logging;
using System.Diagnostics;
using Serilog;

namespace DigitalPersonaService.Services;

public class FingerprintService
{
    private readonly ILogger<FingerprintService> _logger;
    private readonly DeviceManager _deviceManager;
    private readonly IConfiguration _configuration;

    public FingerprintService(ILogger<FingerprintService> logger, DeviceManager deviceManager, IConfiguration configuration)
    {
        _logger = logger;
        _deviceManager = deviceManager;
        _configuration = configuration;
    }

    public async Task InitializeAsync()
    {
        _logger.LogInformation("Initializing Fingerprint Service...");
        await _deviceManager.InitializeAsync();
        _logger.LogInformation("Fingerprint Service initialized");
    }

    public ServiceHealth GetHealth()
    {
        return new ServiceHealth
        {
            Status = "Healthy",
            SdkVersion = "Digital Persona .NET SDK",
            DeviceConnected = _deviceManager.IsDeviceConnected
        };
    }

    public async Task<DeviceStatus> GetDeviceStatusAsync()
    {
        return await _deviceManager.GetDeviceStatusAsync();
    }

    public async Task<DeviceConnection> ConnectDeviceAsync()
    {
        var success = await _deviceManager.ConnectDeviceAsync();
        return new DeviceConnection { Success = success };
    }

    public async Task<bool> DisconnectDeviceAsync()
    {
        return await _deviceManager.DisconnectDeviceAsync();
    }

    public async Task<FingerprintCapture> CaptureFingerprintAsync(CaptureRequest request)
    {
        var qualityThreshold = _configuration.GetValue<int>("DigitalPersona:QualityThreshold", 70);
        var result = await _deviceManager.CaptureFingerprintAsync(request.FingerType, request.QualityThreshold);
        return new FingerprintCapture
        {
            Success = result.Success,
            FingerType = result.FingerType,
            ImageData = result.ImageData,
            WsqData = result.WsqData,
            QualityScore = result.QualityScore,
            CaptureTime = result.CaptureTime,
            Error = result.Error
        };
    }

    public async Task<BatchCaptureResponse> CaptureBatchFingerprintsAsync(BatchCaptureRequest request)
    {
        var stopwatch = Stopwatch.StartNew();
        var capturedFingers = new List<FingerprintCaptureResponse>();
        var failedFingers = new List<string>();

        _logger.LogInformation("Starting batch capture for {FingerCount} fingers", request.Fingers.Count);

        foreach (var fingerType in request.Fingers)
        {
            try
            {
                var captureRequest = new CaptureRequest
                {
                    FingerType = fingerType,
                    QualityThreshold = request.QualityThreshold
                };

                var result = await CaptureFingerprintAsync(captureRequest);

                if (result.Success)
                {
                    capturedFingers.Add(new FingerprintCaptureResponse
                    {
                        Success = true,
                        FingerType = result.FingerType,
                        ImageData = result.ImageData,
                        WsqData = result.WsqData,
                        QualityScore = result.QualityScore ?? 0,
                        CaptureTime = result.CaptureTime ?? DateTime.UtcNow.ToString("yyyy-MM-ddTHH:mm:ss.fffZ")
                    });
                    
                    _logger.LogInformation("Successfully captured {FingerType}", fingerType);
                }
                else
                {
                    failedFingers.Add(fingerType);
                    _logger.LogWarning("Failed to capture {FingerType}: {Error}", fingerType, result.Error);
                }
            }
            catch (Exception ex)
            {
                failedFingers.Add(fingerType);
                _logger.LogError(ex, "Exception during capture of {FingerType}", fingerType);
            }

            // Small delay between captures
            await Task.Delay(500);
        }

        stopwatch.Stop();

        var success = capturedFingers.Count > 0;
        var totalTime = stopwatch.Elapsed.ToString(@"hh\:mm\:ss\.fff");

        _logger.LogInformation("Batch capture completed: {CapturedCount} successful, {FailedCount} failed, Total time: {TotalTime}",
            capturedFingers.Count, failedFingers.Count, totalTime);

        return new BatchCaptureResponse
        {
            Success = success,
            CapturedFingers = capturedFingers,
            FailedFingers = failedFingers,
            TotalTime = totalTime
        };
    }

    public async Task<QualityAssessment> AssessQualityAsync(QualityRequest request)
    {
        var result = await _deviceManager.AssessQualityAsync(request.ImageData);
        return new QualityAssessment
        {
            Success = result.Success,
            OverallScore = result.OverallScore,
            Clarity = result.Clarity,
            Contrast = result.Contrast,
            Coverage = result.Coverage,
            RidgeDefinition = result.RidgeDefinition,
            IsAcceptable = result.IsAcceptable,
            Error = result.Error
        };
    }

    public string GetSdkVersion()
    {
        return "Digital Persona .NET SDK v1.0";
    }
}
