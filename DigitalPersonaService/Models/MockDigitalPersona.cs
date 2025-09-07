using System;
using System.Collections.Generic;

namespace DigitalPersonaService.Models
{
    // Mock classes to replace Digital Persona SDK dependencies
    public class MockDPFPReader
    {
        public bool IsConnected { get; set; } = false;
        public string DeviceName { get; set; } = "Mock Fingerprint Scanner";
        public string DeviceId { get; set; } = "MOCK_DEVICE_001";
        public string FirmwareVersion { get; set; } = "1.0.0";
        
        public event EventHandler<MockDPFPSample> OnSampleAcquired;
        
        public void StartCapture()
        {
            // Simulate device connection
            IsConnected = true;
        }
        
        public void StopCapture()
        {
            // Simulate device disconnection
            IsConnected = false;
        }
        
        public void SimulateFingerprintCapture()
        {
            // Simulate a fingerprint capture
            var sample = new MockDPFPSample
            {
                ImageData = GenerateMockImageData(),
                Quality = 85,
                Timestamp = DateTime.Now
            };
            
            OnSampleAcquired?.Invoke(this, sample);
        }
        
        private string GenerateMockImageData()
        {
            // Generate a simple base64 encoded mock image
            var mockImageBytes = new byte[1024]; // Small mock image
            for (int i = 0; i < mockImageBytes.Length; i++)
            {
                mockImageBytes[i] = (byte)(i % 256);
            }
            return Convert.ToBase64String(mockImageBytes);
        }
    }
    
    public class MockDPFPSample
    {
        public string ImageData { get; set; } = string.Empty;
        public int Quality { get; set; }
        public DateTime Timestamp { get; set; }
    }
    
    public class MockDPFPFeatureSet
    {
        public string FingerType { get; set; } = string.Empty;
        public string FeatureData { get; set; } = string.Empty;
        public DateTime CreatedAt { get; set; } = DateTime.Now;
    }
    
    public class MockDPFPFeatureExtraction
    {
        public MockDPFPFeatureSet CreateFeatureSet(MockDPFPSample sample, string fingerType)
        {
            return new MockDPFPFeatureSet
            {
                FingerType = fingerType,
                FeatureData = $"MOCK_FEATURES_{fingerType}_{DateTime.Now.Ticks}",
                CreatedAt = DateTime.Now
            };
        }
    }
    
    public class MockDPFPVerification
    {
        public bool Verify(MockDPFPFeatureSet probe, MockDPFPFeatureSet candidate)
        {
            // Mock verification - always returns true for testing
            return true;
        }
        
        public int GetSimilarityScore(MockDPFPFeatureSet probe, MockDPFPFeatureSet candidate)
        {
            // Mock similarity score
            return 95;
        }
    }
    
    public class MockDPFPEnrollment
    {
        public MockDPFPFeatureSet Enroll(MockDPFPSample sample, string fingerType)
        {
            return new MockDPFPFeatureSet
            {
                FingerType = fingerType,
                FeatureData = $"ENROLLED_{fingerType}_{DateTime.Now.Ticks}",
                CreatedAt = DateTime.Now
            };
        }
    }
}
