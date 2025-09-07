using System.Text.Json.Serialization;

namespace DigitalPersonaService.Models;

public class ServiceHealth
{
    [JsonPropertyName("status")]
    public string Status { get; set; } = "Healthy";
    
    [JsonPropertyName("timestamp")]
    public string Timestamp { get; set; } = DateTime.UtcNow.ToString("yyyy-MM-ddTHH:mm:ss.fffZ");
    
    [JsonPropertyName("sdkVersion")]
    public string SdkVersion { get; set; } = "Unknown";
    
    [JsonPropertyName("deviceConnected")]
    public bool DeviceConnected { get; set; }
}

public class DeviceStatus
{
    [JsonPropertyName("connected")]
    public bool Connected { get; set; }
    
    [JsonPropertyName("deviceName")]
    public string? DeviceName { get; set; }
    
    [JsonPropertyName("deviceId")]
    public string? DeviceId { get; set; }
    
    [JsonPropertyName("firmwareVersion")]
    public string? FirmwareVersion { get; set; }
    
    [JsonPropertyName("error")]
    public string? Error { get; set; }
    
    [JsonPropertyName("timestamp")]
    public string Timestamp { get; set; } = DateTime.UtcNow.ToString("yyyy-MM-ddTHH:mm:ss.fffZ");
}

public class DeviceConnection
{
    [JsonPropertyName("success")]
    public bool Success { get; set; }
    
    [JsonPropertyName("deviceInfo")]
    public DeviceStatus? DeviceInfo { get; set; }
    
    [JsonPropertyName("error")]
    public string? Error { get; set; }
}

public class FingerprintCapture
{
    [JsonPropertyName("success")]
    public bool Success { get; set; }
    
    [JsonPropertyName("fingerType")]
    public string FingerType { get; set; } = string.Empty;
    
    [JsonPropertyName("imageData")]
    public string? ImageData { get; set; }
    
    [JsonPropertyName("wsqData")]
    public string? WsqData { get; set; }
    
    [JsonPropertyName("qualityScore")]
    public int? QualityScore { get; set; }
    
    [JsonPropertyName("captureTime")]
    public string? CaptureTime { get; set; }
    
    [JsonPropertyName("error")]
    public string? Error { get; set; }
}

public class QualityAssessment
{
    [JsonPropertyName("success")]
    public bool Success { get; set; }
    
    [JsonPropertyName("overallScore")]
    public int OverallScore { get; set; }
    
    [JsonPropertyName("clarity")]
    public int Clarity { get; set; }
    
    [JsonPropertyName("contrast")]
    public int Contrast { get; set; }
    
    [JsonPropertyName("coverage")]
    public int Coverage { get; set; }
    
    [JsonPropertyName("ridgeDefinition")]
    public int RidgeDefinition { get; set; }
    
    [JsonPropertyName("isAcceptable")]
    public bool IsAcceptable { get; set; }
    
    [JsonPropertyName("error")]
    public string? Error { get; set; }
}

public class CaptureRequest
{
    [JsonPropertyName("fingerType")]
    public string FingerType { get; set; } = string.Empty;
    
    [JsonPropertyName("qualityThreshold")]
    public int QualityThreshold { get; set; } = 70;
}

public class BatchCaptureRequest
{
    [JsonPropertyName("fingers")]
    public List<string> Fingers { get; set; } = new();
    
    [JsonPropertyName("qualityThreshold")]
    public int QualityThreshold { get; set; } = 70;
    
    [JsonPropertyName("captureTimeout")]
    public int CaptureTimeout { get; set; } = 60000;
    
    [JsonPropertyName("retryCount")]
    public int RetryCount { get; set; } = 3;
}

public class FingerprintCaptureResponse
{
    [JsonPropertyName("success")]
    public bool Success { get; set; }
    
    [JsonPropertyName("fingerType")]
    public string FingerType { get; set; } = string.Empty;
    
    [JsonPropertyName("imageData")]
    public string? ImageData { get; set; }
    
    [JsonPropertyName("wsqData")]
    public string? WsqData { get; set; }
    
    [JsonPropertyName("qualityScore")]
    public int QualityScore { get; set; }
    
    [JsonPropertyName("captureTime")]
    public string CaptureTime { get; set; } = string.Empty;
    
    [JsonPropertyName("error")]
    public string? Error { get; set; }
}

public class BatchCaptureResponse
{
    [JsonPropertyName("success")]
    public bool Success { get; set; }
    
    [JsonPropertyName("capturedFingers")]
    public List<FingerprintCaptureResponse> CapturedFingers { get; set; } = new();
    
    [JsonPropertyName("failedFingers")]
    public List<string> FailedFingers { get; set; } = new();
    
    [JsonPropertyName("totalTime")]
    public string TotalTime { get; set; } = string.Empty;
    
    [JsonPropertyName("error")]
    public string? Error { get; set; }
}

public class DisconnectResponse
{
    [JsonPropertyName("success")]
    public bool Success { get; set; }
}

public class QualityRequest
{
    [JsonPropertyName("imageData")]
    public string ImageData { get; set; } = string.Empty;
}

// Additional classes needed by DeviceManager
public class FingerprintCaptureResult
{
    [JsonPropertyName("success")]
    public bool Success { get; set; }
    
    [JsonPropertyName("fingerType")]
    public string FingerType { get; set; } = string.Empty;
    
    [JsonPropertyName("imageData")]
    public string? ImageData { get; set; }
    
    [JsonPropertyName("wsqData")]
    public string? WsqData { get; set; }
    
    [JsonPropertyName("qualityScore")]
    public int QualityScore { get; set; }
    
    [JsonPropertyName("captureTime")]
    public string CaptureTime { get; set; } = string.Empty;
    
    [JsonPropertyName("error")]
    public string? Error { get; set; }
}

public class QualityAssessmentResult
{
    [JsonPropertyName("success")]
    public bool Success { get; set; }
    
    [JsonPropertyName("overallScore")]
    public int OverallScore { get; set; }
    
    [JsonPropertyName("clarity")]
    public int Clarity { get; set; }
    
    [JsonPropertyName("contrast")]
    public int Contrast { get; set; }
    
    [JsonPropertyName("coverage")]
    public int Coverage { get; set; }
    
    [JsonPropertyName("ridgeDefinition")]
    public int RidgeDefinition { get; set; }
    
    [JsonPropertyName("isAcceptable")]
    public bool IsAcceptable { get; set; }
    
    [JsonPropertyName("error")]
    public string? Error { get; set; }
}