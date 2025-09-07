using DigitalPersonaService.Models;
using DigitalPersonaService.Services;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Logging;

namespace DigitalPersonaService.Controllers;

[ApiController]
[Route("api/[controller]")]
public class FingerprintController : ControllerBase
{
    private readonly ILogger<FingerprintController> _logger;
    private readonly FingerprintService _fingerprintService;

    public FingerprintController(ILogger<FingerprintController> logger, FingerprintService fingerprintService)
    {
        _logger = logger;
        _fingerprintService = fingerprintService;
    }

    /// <summary>
    /// Get service health status
    /// </summary>
    [HttpGet("health")]
    public ActionResult<ServiceHealth> GetHealth()
    {
        try
        {
            var health = _fingerprintService.GetHealth();
            return Ok(health);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error getting service health");
            return Ok(new ServiceHealth
            {
                Status = "Unhealthy"
            });
        }
    }

    /// <summary>
    /// Get device connection status
    /// </summary>
    [HttpGet("device/status")]
    public async Task<ActionResult<DeviceStatus>> GetDeviceStatus()
    {
        try
        {
            var status = await _fingerprintService.GetDeviceStatusAsync();
            return Ok(status);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error getting device status");
            return StatusCode(500, new DeviceStatus
            {
                Connected = false,
                Error = ex.Message
            });
        }
    }

    /// <summary>
    /// Connect to fingerprint device
    /// </summary>
    [HttpPost("device/connect")]
    public async Task<ActionResult<DeviceConnection>> ConnectDevice()
    {
        try
        {
            _logger.LogInformation("Connecting to fingerprint device...");
            var result = await _fingerprintService.ConnectDeviceAsync();
            
            if (result.Success)
            {
                _logger.LogInformation("Successfully connected to device: {DeviceName}", result.DeviceInfo?.DeviceName);
            }
            else
            {
                _logger.LogWarning("Failed to connect to device: {Error}", result.Error);
            }
            
            return Ok(result);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error connecting to device");
            return StatusCode(500, new DeviceConnection
            {
                Success = false,
                Error = ex.Message
            });
        }
    }

    /// <summary>
    /// Disconnect from fingerprint device
    /// </summary>
    [HttpPost("device/disconnect")]
    public async Task<ActionResult<DisconnectResponse>> DisconnectDevice()
    {
        try
        {
            _logger.LogInformation("Disconnecting from fingerprint device...");
            var success = await _fingerprintService.DisconnectDeviceAsync();
            
            _logger.LogInformation("Device disconnect result: {Success}", success);
            
            return Ok(new DisconnectResponse { Success = success });
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error disconnecting from device");
            return StatusCode(500, new DisconnectResponse { Success = false });
        }
    }

    /// <summary>
    /// Capture single fingerprint
    /// </summary>
    [HttpPost("capture")]
    public async Task<ActionResult<FingerprintCapture>> CaptureFingerprint([FromBody] CaptureRequest request)
    {
        try
        {
            if (string.IsNullOrEmpty(request.FingerType))
            {
                return BadRequest(new FingerprintCapture
                {
                    Success = false,
                    Error = "FingerType is required"
                });
            }

            _logger.LogInformation("Starting fingerprint capture for {FingerType}", request.FingerType);
            
            var result = await _fingerprintService.CaptureFingerprintAsync(request);
            
            if (result.Success)
            {
                _logger.LogInformation("Successfully captured fingerprint for {FingerType} with quality {Quality}",
                    result.FingerType, result.QualityScore);
            }
            else
            {
                _logger.LogWarning("Failed to capture fingerprint for {FingerType}: {Error}",
                    result.FingerType, result.Error);
            }
            
            return Ok(result);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error capturing fingerprint for {FingerType}", request.FingerType);
            return StatusCode(500, new FingerprintCapture
            {
                Success = false,
                FingerType = request.FingerType,
                Error = ex.Message
            });
        }
    }

    /// <summary>
    /// Capture multiple fingerprints in batch
    /// </summary>
    [HttpPost("capture/batch")]
    public async Task<ActionResult<BatchCaptureResponse>> CaptureBatchFingerprints([FromBody] BatchCaptureRequest request)
    {
        try
        {
            if (request.Fingers == null || request.Fingers.Count == 0)
            {
                return BadRequest(new BatchCaptureResponse
                {
                    Success = false,
                    Error = "At least one finger must be specified"
                });
            }

            _logger.LogInformation("Starting batch fingerprint capture for {FingerCount} fingers", request.Fingers.Count);
            
            var result = await _fingerprintService.CaptureBatchFingerprintsAsync(request);
            
            _logger.LogInformation("Batch capture completed: {CapturedCount} successful, {FailedCount} failed",
                result.CapturedFingers.Count, result.FailedFingers.Count);
            
            return Ok(result);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error in batch fingerprint capture");
            return StatusCode(500, new BatchCaptureResponse
            {
                Success = false,
                Error = ex.Message
            });
        }
    }

    /// <summary>
    /// Cancel active capture for specific finger type
    /// </summary>
    [HttpPost("capture/{fingerType}/cancel")]
    public ActionResult<object> CancelCapture(string fingerType)
    {
        try
        {
            _logger.LogInformation("Cancelling capture for {FingerType}", fingerType);
            
            // For now, just return success as cancellation is not fully implemented
            return Ok(new { success = true, message = $"Capture cancelled for {fingerType}" });
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error cancelling capture for {FingerType}", fingerType);
            return StatusCode(500, new { success = false, error = ex.Message });
        }
    }

    /// <summary>
    /// Assess fingerprint quality
    /// </summary>
    [HttpPost("quality/assess")]
    public async Task<ActionResult<QualityAssessment>> AssessQuality([FromBody] QualityRequest request)
    {
        try
        {
            if (string.IsNullOrEmpty(request.ImageData))
            {
                return BadRequest(new QualityAssessment
                {
                    Success = false,
                    Error = "ImageData is required"
                });
            }

            _logger.LogInformation("Assessing fingerprint quality...");
            
            var result = await _fingerprintService.AssessQualityAsync(request);
            
            _logger.LogInformation("Quality assessment completed: Overall score {Score}, Acceptable {Acceptable}",
                result.OverallScore, result.IsAcceptable);
            
            return Ok(result);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error assessing fingerprint quality");
            return StatusCode(500, new QualityAssessment
            {
                Success = false,
                Error = ex.Message
            });
        }
    }

    /// <summary>
    /// Get SDK version information
    /// </summary>
    [HttpGet("sdk/version")]
    public ActionResult<object> GetSdkVersion()
    {
        try
        {
            var version = _fingerprintService.GetSdkVersion();
            return Ok(new { version });
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error getting SDK version");
            return StatusCode(500, new { error = ex.Message });
        }
    }
}
