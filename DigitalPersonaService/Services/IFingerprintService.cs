using System.Threading.Tasks;
using DigitalPersonaService.Models;

namespace DigitalPersonaService.Services
{
    public interface IFingerprintService
    {
        Task<DeviceStatusResponse> GetDeviceStatusAsync();
        Task<DeviceConnectionResponse> ConnectDeviceAsync();
        Task<bool> DisconnectDeviceAsync();
        Task<FingerprintCaptureResponse> CaptureFingerprintAsync(FingerprintCaptureRequest request);
        Task<BatchCaptureResponse> CaptureBatchAsync(BatchCaptureRequest request);
        Task<bool> CancelCaptureAsync(string fingerType);
        Task<QualityAssessmentResponse> AssessQualityAsync(string imageData);
        Task<bool> IsDeviceConnectedAsync();
        Task<string> GetSDKVersionAsync();
    }
}
