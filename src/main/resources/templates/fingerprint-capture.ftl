<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Fingerprint Capture - Verification System</title>
    <link href="/static/css/bootstrap.min.css" rel="stylesheet">
    <link href="/static/css/all.min.css" rel="stylesheet">
    <link href="/static/css/dashboard.css" rel="stylesheet">
    <link href="/static/css/fingerprint-capture.css" rel="stylesheet">
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
                <a class="btn btn-outline-danger btn-sm" href="/logout?username=${username}&thirdPartyUsername=${thirdPartyUsername!username}">
                    <i class="fas fa-sign-out-alt me-2"></i>Logout
                </a>
            </div>
        </div>
    </nav>

    <div class="main-content">
        <div class="container mt-4">
            <div class="row">
                <div class="col-md-12">
                    <div class="card">
                        <div class="card-header">
                            <h4><i class="fas fa-fingerprint"></i> Fingerprint Capture & Device Management</h4>
                        </div>
                        <div class="card-body">
                            
                            <!-- Device Status Section -->
                            <div class="device-status-section mb-4">
                                <h5><i class="fas fa-plug"></i> Device Status</h5>
                                <div class="row">
                                    <div class="col-md-6">
                                        <div class="device-info">
                                            <div class="status-indicator" id="deviceStatusIndicator">
                                                <i class="fas fa-circle text-warning"></i>
                                                <span id="deviceStatusText">Checking device status...</span>
                                            </div>
                                            <div class="device-details" id="deviceDetails" style="display: none;">
                                                <p><strong>Device:</strong> <span id="deviceName">-</span></p>
                                                <p><strong>ID:</strong> <span id="deviceId">-</span></p>
                                                <p><strong>Firmware:</strong> <span id="firmwareVersion">-</span></p>
                                            </div>
                                        </div>
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

                            <!-- Fingerprint Capture Section -->
                            <div class="capture-section mb-4">
                                <h5><i class="fas fa-camera"></i> Fingerprint Capture</h5>
                                
                                <!-- Finger Selection -->
                                <div class="finger-selection mb-3">
                                    <h6>Select Fingers for Capture</h6>
                                    <div class="finger-grid">
                                        <div>
                                            <h6 class="text-primary">Right Hand</h6>
                                            <div class="finger-group">
                                                <input type="checkbox" class="finger-checkbox" id="rightThumb" value="RIGHT_THUMB">
                                                <label for="rightThumb">Right Thumb</label>
                                            </div>
                                            <div class="finger-group">
                                                <input type="checkbox" class="finger-checkbox" id="rightIndex" value="RIGHT_INDEX">
                                                <label for="rightIndex">Right Index</label>
                                            </div>
                                            <div class="finger-group">
                                                <input type="checkbox" class="finger-checkbox" id="rightMiddle" value="RIGHT_MIDDLE">
                                                <label for="rightMiddle">Right Middle</label>
                                            </div>
                                            <div class="finger-group">
                                                <input type="checkbox" class="finger-checkbox" id="rightRing" value="RIGHT_RING">
                                                <label for="rightRing">Right Ring</label>
                                            </div>
                                            <div class="finger-group">
                                                <input type="checkbox" class="finger-checkbox" id="rightLittle" value="RIGHT_LITTLE">
                                                <label for="rightLittle">Right Little</label>
                                            </div>
                                        </div>
                                        <div>
                                            <h6 class="text-primary">Left Hand</h6>
                                            <div class="finger-group">
                                                <input type="checkbox" class="finger-checkbox" id="leftThumb" value="LEFT_THUMB">
                                                <label for="leftThumb">Left Thumb</label>
                                            </div>
                                            <div class="finger-group">
                                                <input type="checkbox" class="finger-checkbox" id="leftIndex" value="LEFT_INDEX">
                                                <label for="leftIndex">Left Index</label>
                                            </div>
                                            <div class="finger-group">
                                                <input type="checkbox" class="finger-checkbox" id="leftMiddle" value="LEFT_MIDDLE">
                                                <label for="leftMiddle">Left Middle</label>
                                            </div>
                                            <div class="finger-group">
                                                <input type="checkbox" class="finger-checkbox" id="leftRing" value="LEFT_RING">
                                                <label for="leftRing">Left Ring</label>
                                            </div>
                                            <div class="finger-group">
                                                <input type="checkbox" class="finger-checkbox" id="leftLittle" value="LEFT_LITTLE">
                                                <label for="leftLittle">Left Little</label>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <!-- Capture Controls -->
                                <div class="capture-controls mb-3">
                                    <div class="row">
                                        <div class="col-md-3">
                                            <label for="qualityThreshold" class="form-label">Quality Threshold</label>
                                            <input type="range" class="form-range" id="qualityThreshold" min="0" max="100" value="70">
                                            <small class="text-muted">Current: <span id="qualityThresholdValue">70</span>%</small>
                                        </div>
                                        <div class="col-md-3">
                                            <label for="captureTimeout" class="form-label">Capture Timeout (ms)</label>
                                            <input type="number" class="form-control" id="captureTimeout" value="30000" min="5000" max="60000">
                                        </div>
                                        <div class="col-md-3">
                                            <label for="retryCount" class="form-label">Retry Count</label>
                                            <input type="number" class="form-control" id="retryCount" value="3" min="1" max="5">
                                        </div>
                                        <div class="col-md-3">
                                            <label class="form-label">&nbsp;</label>
                                            <div class="d-grid">
                                                <button class="btn btn-primary" id="startCaptureBtn" onclick="startCapture()" disabled>
                                                    <i class="fas fa-play"></i> Start Capture
                                                </button>
                                            </div>
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
                            </div>

                            <!-- Results Section -->
                            <div class="results-section mb-4">
                                <h5><i class="fas fa-list-check"></i> Capture Results</h5>
                                <div id="captureResults"></div>
                            </div>

                            <!-- AFIS Integration Section -->
                            <div class="afis-integration-section mb-4">
                                <h5><i class="fas fa-share-alt"></i> AFIS Integration</h5>
                                <div class="row">
                                    <div class="col-md-6">
                                        <div class="form-group">
                                            <label for="nid10Digit" class="form-label">10-Digit NID</label>
                                            <input type="text" class="form-control" id="nid10Digit" maxlength="10" placeholder="Enter 10-digit NID">
                                        </div>
                                    </div>
                                    <div class="col-md-6">
                                        <div class="form-group">
                                            <label for="nid17Digit" class="form-label">17-Digit NID</label>
                                            <input type="text" class="form-control" id="nid17Digit" maxlength="17" placeholder="Enter 17-digit NID">
                                        </div>
                                    </div>
                                </div>
                                <div class="form-group mb-3">
                                    <label for="dateOfBirth" class="form-label">Date of Birth *</label>
                                    <input type="date" class="form-control" id="dateOfBirth" required>
                                </div>
                                <button class="btn btn-success" id="submitToAfisBtn" onclick="submitToAfis()" disabled>
                                    <i class="fas fa-paper-plane"></i> Submit to AFIS Verification
                                </button>
                            </div>

                            <!-- Navigation -->
                            <div class="d-grid gap-2 d-md-flex justify-content-md-end">
                                <button type="button" class="btn btn-secondary me-md-2" onclick="window.history.back()">
                                    <i class="fas fa-arrow-left"></i> Back
                                </button>
                                <a href="/afis-verification?username=${username}&thirdPartyUsername=${thirdPartyUsername!username}" class="btn btn-info">
                                    <i class="fas fa-external-link-alt"></i> Go to AFIS Verification
                                </a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <script src="/static/js/bootstrap.bundle.min.js"></script>
    <script src="/static/js/fingerprint-capture.js"></script>
</body>
</html>
