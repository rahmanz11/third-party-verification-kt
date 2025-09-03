using System;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Logging;
using DigitalPersonaService.Services;
using DigitalPersonaService.Models;

namespace DigitalPersonaService.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class FingerprintController : ControllerBase
    {
        private readonly IFingerprintService _fingerprintService;
        private readonly ILogger<FingerprintController> _logger;

        public FingerprintController(IFingerprintService fingerprintService, ILogger<FingerprintController> logger)
        {
            _fingerprintService = fingerprintService;
            _logger = logger;
        }

        [HttpGet("health")]
        public async Task<IActionResult> Health()
        {
            try
            {
                var sdkVersion = await _fingerprintService.GetSDKVersionAsync();
                var deviceConnected = await _fingerprintService.IsDeviceConnectedAsync();
                
                return Ok(new
                {
                    Status = "Healthy",
                    Timestamp = DateTime.UtcNow,
                    SdkVersion = sdkVersion,
                    DeviceConnected = deviceConnected
                });
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Health check failed");
                return StatusCode(500, new { Status = "Unhealthy", Error = ex.Message });
            }
        }

        [HttpGet("device/status")]
        public async Task<IActionResult> GetDeviceStatus()
        {
            try
            {
                var status = await _fingerprintService.GetDeviceStatusAsync();
                return Ok(status);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Failed to get device status");
                return StatusCode(500, new { Error = ex.Message });
            }
        }

        [HttpPost("device/connect")]
        public async Task<IActionResult> ConnectDevice()
        {
            try
            {
                var result = await _fingerprintService.ConnectDeviceAsync();
                if (result.Success)
                {
                    return Ok(result);
                }
                else
                {
                    return BadRequest(result);
                }
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Failed to connect device");
                return StatusCode(500, new { Error = ex.Message });
            }
        }

        [HttpPost("device/disconnect")]
        public async Task<IActionResult> DisconnectDevice()
        {
            try
            {
                var result = await _fingerprintService.DisconnectDeviceAsync();
                return Ok(new { Success = result });
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Failed to disconnect device");
                return StatusCode(500, new { Error = ex.Message });
            }
        }

        [HttpPost("capture")]
        public async Task<IActionResult> CaptureFingerprint([FromBody] FingerprintCaptureRequest request)
        {
            try
            {
                if (request == null)
                {
                    return BadRequest(new { Error = "Request body is required" });
                }

                if (string.IsNullOrEmpty(request.FingerType))
                {
                    return BadRequest(new { Error = "FingerType is required" });
                }

                var result = await _fingerprintService.CaptureFingerprintAsync(request);
                if (result.Success)
                {
                    return Ok(result);
                }
                else
                {
                    return BadRequest(result);
                }
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Failed to capture fingerprint");
                return StatusCode(500, new { Error = ex.Message });
            }
        }

        [HttpPost("capture/batch")]
        public async Task<IActionResult> CaptureBatch([FromBody] BatchCaptureRequest request)
        {
            try
            {
                if (request == null)
                {
                    return BadRequest(new { Error = "Request body is required" });
                }

                if (request.Fingers == null || request.Fingers.Length == 0)
                {
                    return BadRequest(new { Error = "Fingers array is required and cannot be empty" });
                }

                var result = await _fingerprintService.CaptureBatchAsync(request);
                if (result.Success)
                {
                    return Ok(result);
                }
                else
                {
                    return BadRequest(result);
                }
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Failed to capture batch fingerprints");
                return StatusCode(500, new { Error = ex.Message });
            }
        }

        [HttpPost("capture/{fingerType}/cancel")]
        public async Task<IActionResult> CancelCapture(string fingerType)
        {
            try
            {
                if (string.IsNullOrEmpty(fingerType))
                {
                    return BadRequest(new { Error = "FingerType is required" });
                }

                var result = await _fingerprintService.CancelCaptureAsync(fingerType);
                return Ok(new { Success = result });
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Failed to cancel capture for {FingerType}", fingerType);
                return StatusCode(500, new { Error = ex.Message });
            }
        }

        [HttpPost("quality/assess")]
        public async Task<IActionResult> AssessQuality([FromBody] QualityAssessmentRequest request)
        {
            try
            {
                if (request == null || string.IsNullOrEmpty(request.ImageData))
                {
                    return BadRequest(new { Error = "ImageData is required" });
                }

                var result = await _fingerprintService.AssessQualityAsync(request.ImageData);
                if (result.Success)
                {
                    return Ok(result);
                }
                else
                {
                    return BadRequest(result);
                }
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Failed to assess quality");
                return StatusCode(500, new { Error = ex.Message });
            }
        }

        [HttpGet("sdk/version")]
        public async Task<IActionResult> GetSDKVersion()
        {
            try
            {
                var version = await _fingerprintService.GetSDKVersionAsync();
                return Ok(new { Version = version });
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Failed to get SDK version");
                return StatusCode(500, new { Error = ex.Message });
            }
        }
    }

    public class QualityAssessmentRequest
    {
        public string ImageData { get; set; }
    }
}
