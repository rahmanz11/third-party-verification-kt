<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>AFIS Verification - Verification System</title>
    <link href="/static/css/bootstrap.min.css" rel="stylesheet">
    <link href="/static/css/all.min.css" rel="stylesheet">
    <link href="/static/css/dashboard.css" rel="stylesheet">
</head>
<body data-username="${username}">
    <nav class="navbar navbar-expand-lg navbar-light fixed-top">
        <div class="container">
            <a class="navbar-brand fw-bold" href="#">
                <i class="fas fa-shield-alt me-2"></i>Verification System
            </a>
            <div class="navbar-nav ms-auto">
                <span class="navbar-text me-3">
                    <i class="fas fa-user me-2"></i>Welcome, ${username}
                </span>
                <a class="btn btn-outline-primary btn-sm me-2" href="/dashboard?username=${username}">
                    <i class="fas fa-tachometer-alt me-2"></i>Dashboard
                </a>
                <a class="btn btn-outline-danger btn-sm" href="/logout?username=${username}&thirdPartyUsername=${thirdPartyUsername}">
                    <i class="fas fa-sign-out-alt me-2"></i>Logout
                </a>
            </div>
        </div>
    </nav>

    <div class="main-content">
        <div class="container">
            <div class="row justify-content-center">
                <div class="col-lg-10">
                    <h2><i class="fas fa-fingerprint me-2"></i>AFIS Verification with Fingerprint Capture</h2>
                    
                    <!-- Device Status Section -->
                    <div class="card mb-3">
                        <div class="card-header">
                            <h5><i class="fas fa-cog me-2"></i>Device Status</h5>
                        </div>
                        <div class="card-body">
                            <div class="row">
                                <div class="col-md-6">
                                    <div class="device-status">
                                        <span id="deviceStatusIndicator" class="badge bg-secondary">Unknown</span>
                                        <span id="deviceStatusText" class="ms-2">Checking device status...</span>
                                    </div>
                                    <div id="deviceDetails" class="mt-2 text-muted small"></div>
                                </div>
                                <div class="col-md-6">
                                    <div class="device-controls">
                                        <button class="btn btn-success btn-sm me-2" id="connectDeviceBtn" onclick="connectDevice()">
                                            <i class="fas fa-plug"></i> Connect Device
                                        </button>
                                        <button class="btn btn-danger btn-sm" id="disconnectDeviceBtn" onclick="disconnectDevice()" style="display: none;">
                                            <i class="fas fa-unlink"></i> Disconnect Device
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Fingerprint Capture Section -->
                    <div class="card mb-4">
                        <div class="card-header">
                            <h5><i class="fas fa-camera me-2"></i>Step 1: Fingerprint Capture</h5>
                        </div>
                        <div class="card-body">
                            <div class="row">
                                <div class="col-md-6">
                                    <h6>Select Fingers to Capture:</h6>
                                    <div class="finger-selection">
                                        <div class="form-check">
                                            <input class="form-check-input finger-checkbox" type="checkbox" value="LEFT_THUMB" id="leftThumb">
                                            <label class="form-check-label" for="leftThumb">Left Thumb</label>
                                        </div>
                                        <div class="form-check">
                                            <input class="form-check-input finger-checkbox" type="checkbox" value="RIGHT_THUMB" id="rightThumb">
                                            <label class="form-check-label" for="rightThumb">Right Thumb</label>
                                        </div>
                                        <div class="form-check">
                                            <input class="form-check-input finger-checkbox" type="checkbox" value="LEFT_INDEX" id="leftIndex">
                                            <label class="form-check-label" for="leftIndex">Left Index</label>
                                        </div>
                                        <div class="form-check">
                                            <input class="form-check-input finger-checkbox" type="checkbox" value="RIGHT_INDEX" id="rightIndex">
                                            <label class="form-check-label" for="rightIndex">Right Index</label>
                                        </div>
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <h6>Quality Settings:</h6>
                                    <div class="mb-3">
                                        <label for="qualityThreshold" class="form-label">Quality Threshold: <span id="qualityValue">70</span></label>
                                        <input type="range" class="form-range" min="30" max="100" value="70" id="qualityThreshold">
                                    </div>
                                    <div class="mb-3">
                                        <label for="captureTimeout" class="form-label">Capture Timeout (ms):</label>
                                        <input type="number" class="form-control" id="captureTimeout" value="60000" min="1000" max="120000">
                                    </div>
                                    <div class="mb-3">
                                        <label for="retryCount" class="form-label">Retry Count:</label>
                                        <input type="number" class="form-control" id="retryCount" value="2" min="0" max="5">
                                    </div>
                                    <div class="capture-controls">
                                        <button type="button" class="btn btn-success" onclick="captureFingerprints()" id="captureBtn">
                                            <i class="fas fa-camera me-2"></i>Capture Selected Fingers
                                        </button>
                                        <button type="button" class="btn btn-warning" onclick="clearSelection()">
                                            <i class="fas fa-eraser me-2"></i>Clear Selection
                                        </button>
                                        <button type="button" class="btn btn-info" onclick="checkDeviceStatus()">
                                            <i class="fas fa-cog me-2"></i>Check Device
                                        </button>
                                    </div>
                                </div>
                            </div>
                            
                            <!-- Capture Progress -->
                            <div class="capture-progress mb-3" id="captureProgress" style="display: none;">
                                <h6>Capture Progress</h6>
                                <div class="progress mb-2">
                                    <div class="progress-bar progress-bar-striped progress-bar-animated" 
                                         id="captureProgressBar" role="progressbar" style="width: 0%"></div>
                                </div>
                                <div class="capture-status" id="captureStatus">Ready to capture...</div>
                            </div>
                            
                            <!-- Fingerprint Preview -->
                            <div id="fingerprintPreview" style="display: none;">
                                <h6><i class="fas fa-eye me-2"></i>Captured Fingerprints</h6>
                                <div id="previewContainer"></div>
                            </div>
                        </div>
                    </div>

                    <!-- AFIS Verification Form -->
                    <div class="card">
                        <div class="card-header">
                            <h5><i class="fas fa-id-card me-2"></i>Step 2: AFIS Verification</h5>
                        </div>
                        <div class="card-body">
                            <form id="afisVerificationForm">
                                <div class="row">
                                    <div class="col-md-6">
                                        <div class="mb-3">
                                            <label for="nid10Digit" class="form-label">10-Digit NID</label>
                                            <input type="text" class="form-control" id="nid10Digit" name="nid10Digit" maxlength="10">
                                        </div>
                                    </div>
                                    <div class="col-md-6">
                                        <div class="mb-3">
                                            <label for="nid17Digit" class="form-label">17-Digit NID</label>
                                            <input type="text" class="form-control" id="nid17Digit" name="nid17Digit" maxlength="17">
                                        </div>
                                    </div>
                                </div>
                                <div class="row">
                                    <div class="col-md-6">
                                        <div class="mb-3">
                                            <label for="dateOfBirth" class="form-label">Date of Birth</label>
                                            <input type="date" class="form-control" id="dateOfBirth" name="dateOfBirth" required>
                                        </div>
                                    </div>
                                    <div class="col-md-6">
                                        <div class="mb-3">
                                            <label class="form-label">&nbsp;</label>
                                            <div class="d-grid">
?                                                <button type="submit" class="btn btn-primary" id="submitToAfisBtn">
                                                    <i class="fas fa-paper-plane me-2"></i>Submit to AFIS Verification
                                                </button>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </form>
                        </div>
                    </div>


                    <!-- Results Section -->
                    <div id="resultsSection" class="card mt-4" style="display: none;">
                        <div class="card-header">
                            <h5><i class="fas fa-check-circle me-2"></i>Verification Results</h5>
                        </div>
                        <div class="card-body">
                            <div id="verificationResults"></div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Include fingerprint capture functionality -->
    <script src="/static/js/fingerprint-capture.js"></script>
    <script src="/static/js/afis-verification.js"></script>
    <script src="/static/js/bootstrap.bundle.min.js"></script>
</body>
</html>
