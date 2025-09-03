using System;

namespace DigitalPersonaService.Models
{
    public class DeviceStatusResponse
    {
        public bool Connected { get; set; }
        public string DeviceName { get; set; }
        public string DeviceId { get; set; }
        public string FirmwareVersion { get; set; }
        public string Error { get; set; }
        public DateTime Timestamp { get; set; }
    }

    public class DeviceConnectionResponse
    {
        public bool Success { get; set; }
        public DeviceStatusResponse DeviceInfo { get; set; }
        public string Error { get; set; }
    }

    // Updated to match Kotlin FingerprintCaptureRequest
    public class FingerprintCaptureRequest
    {
        public string FingerType { get; set; } // LEFT_THUMB, RIGHT_THUMB, LEFT_INDEX, etc.
        public int QualityThreshold { get; set; } = 50;
        public int CaptureTimeout { get; set; } = 30000; // Changed from TimeoutMs to match Kotlin
        public int RetryCount { get; set; } = 3; // Added to match Kotlin
    }

    public class FingerprintCaptureResponse
    {
        public bool Success { get; set; }
        public string FingerType { get; set; }
        public string ImageData { get; set; } // Base64 encoded image
        public string WsqData { get; set; } // Base64 encoded WSQ if requested
        public int QualityScore { get; set; }
        public DateTime CaptureTime { get; set; }
        public string Error { get; set; }
    }

    // Updated to match Kotlin BatchFingerprintCaptureRequest
    public class BatchCaptureRequest
    {
        public string[] Fingers { get; set; } // Changed from FingerTypes to match Kotlin
        public int QualityThreshold { get; set; } = 50;
        public int CaptureTimeout { get; set; } = 60000; // Changed from TimeoutMs
        public int RetryCount { get; set; } = 2; // Added to match Kotlin
    }

    public class BatchCaptureResponse
    {
        public bool Success { get; set; }
        public FingerprintCaptureResponse[] CapturedFingers { get; set; }
        public string[] FailedFingers { get; set; }
        public TimeSpan TotalTime { get; set; }
        public string Error { get; set; }
    }

    public class QualityAssessmentResponse
    {
        public bool Success { get; set; }
        public int OverallScore { get; set; }
        public int Clarity { get; set; }
        public int Contrast { get; set; }
        public int Coverage { get; set; }
        public int RidgeDefinition { get; set; }
        public bool IsAcceptable { get; set; }
        public string Error { get; set; }
    }
}
