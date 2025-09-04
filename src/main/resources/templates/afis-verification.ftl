<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>AFIS Verification - Verification System</title>
    <link href="/static/css/bootstrap.min.css" rel="stylesheet">
    <link href="/static/css/all.min.css" rel="stylesheet">
    <link href="/static/css/dashboard.css" rel="stylesheet">
    <!-- <link href="/static/css/afis-verification.css" rel="stylesheet"> -->
    <style>
        .fingerprint-capture-section {
            background: #f8f9fa;
            border: 2px solid #dee2e6;
            border-radius: 10px;
            padding: 20px;
            margin: 20px 0;
        }
        .fingerprint-preview {
            background: white;
            border: 1px solid #ced4da;
            border-radius: 8px;
            padding: 15px;
            margin: 15px 0;
            text-align: center;
        }
        .fingerprint-image {
            max-width: 100%;
            max-height: 300px;
            border: 1px solid #ddd;
            border-radius: 5px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
        }
        .capture-status {
            padding: 10px;
            border-radius: 5px;
            margin: 10px 0;
            font-weight: bold;
        }
        .capture-status.success { background-color: #d4edda; color: #155724; }
        .capture-status.error { background-color: #f8d7da; color: #721c24; }
        .capture-status.info { background-color: #d1ecf1; color: #0c5460; }
        .finger-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 20px;
            margin: 15px 0;
        }
        .finger-group {
            margin: 8px 0;
            padding: 8px;
            border: 1px solid #dee2e6;
            border-radius: 5px;
            background: white;
        }
        .finger-group:hover {
            background: #f8f9fa;
            border-color: #007bff;
        }
        .finger-group input[type="checkbox"] {
            margin-right: 8px;
        }
        .workflow-progress {
            background: #e9ecef;
            border-radius: 10px;
            padding: 20px;
            margin: 20px 0;
        }
        .progress-step {
            display: flex;
            align-items: center;
            margin: 15px 0;
            padding: 10px;
            border-radius: 5px;
            background: white;
        }
        .progress-step.completed {
            background: #d4edda;
            border-left: 4px solid #28a745;
        }
        .progress-step.current {
            background: #fff3cd;
            border-left: 4px solid #ffc107;
        }
        .progress-step.pending {
            background: #f8f9fa;
            border-left: 4px solid #6c757d;
        }
        .step-number {
            width: 30px;
            height: 30px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: bold;
            margin-right: 15px;
        }
        .step-number.completed { background: #28a745; color: white; }
        .step-number.current { background: #ffc107; color: #212529; }
        .step-number.pending { background: #6c757d; color: white; }
        .capture-controls {
            display: flex;
            gap: 10px;
            margin: 15px 0;
            flex-wrap: wrap;
        }
        .btn-capture {
            background: #28a745;
            color: white;
            border: none;
            padding: 10px 20px;
            border-radius: 5px;
            cursor: pointer;
        }
        .btn-capture:hover { background: #218838; }
        .btn-capture:disabled { background: #6c757d; cursor: not-allowed; }
        .quality-indicator {
            display: inline-block;
            padding: 5px 10px;
            border-radius: 15px;
            font-size: 12px;
            font-weight: bold;
            margin: 5px;
        }
        .quality-excellent { background: #d4edda; color: #155724; }
        .quality-good { background: #fff3cd; color: #856404; }
        .quality-poor { background: #f8d7da; color: #721c24; }
    </style>
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
        <div class="container mt-4">
            <div class="row">
                <div class="col-md-12">
                    <div class="card">
                        <div class="card-header">
                            <h4><i class="fas fa-fingerprint"></i> AFIS Verification with Fingerprint Capture</h4>
                        </div>
                        <div class="card-body">
                            <!-- Workflow Progress -->
                            <div class="workflow-progress">
                                <h5><i class="fas fa-project-diagram me-2"></i>Verification Workflow</h5>
                                <div class="progress-step current" id="step1">
                                    <div class="step-number current">1</div>
                                    <div>
                                        <strong>Fingerprint Capture</strong><br>
                                        <small>Capture high-quality fingerprints using the Digital Persona scanner</small>
                                    </div>
                                </div>
                                <div class="progress-step pending" id="step2">
                                    <div class="step-number pending">2</div>
                                    <div>
                                        <strong>Data Collection</strong><br>
                                        <small>Enter NID and date of birth information</small>
                                    </div>
                                </div>
                                <div class="progress-step pending" id="step3">
                                    <div class="step-number pending">3</div>
                                    <div>
                                        <strong>AFIS Verification</strong><br>
                                        <small>Submit to third-party AFIS service for verification</small>
                                    </div>
                                </div>
                            </div>

                            <!-- Step 1: Fingerprint Capture -->
                            <div class="fingerprint-capture-section">
                                <h5><i class="fas fa-camera me-2"></i>Step 1: Fingerprint Capture</h5>
                                
                                <!-- Device Status -->
                                <div id="deviceStatus" class="capture-status info">
                                    <i class="fas fa-info-circle me-2"></i>Checking device status...
                                </div>
                                
                                <!-- Finger Selection -->
                                <div class="finger-selection">
                                    <h6>Select Fingers for Capture *</h6>
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
                                    
                                    <div class="mb-3">
                                        <label for="qualityThreshold" class="form-label">Quality Threshold (0-100)</label>
                                        <input type="range" class="form-range" id="qualityThreshold" min="0" max="100" value="70">
                                        <div class="d-flex justify-content-between">
                                            <small>Low Quality</small>
                                            <span id="qualityValue">70</span>
                                            <small>High Quality</small>
                                        </div>
                                    </div>
                                    
                                    <div class="capture-controls">
                                        <button type="button" class="btn-capture" onclick="captureFingerprints()" id="captureBtn">
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
                                
                                <!-- Fingerprint Preview -->
                                <div id="fingerprintPreview" style="display: none;">
                                    <h6><i class="fas fa-eye me-2"></i>Captured Fingerprints</h6>
                                    <div id="previewContainer"></div>
                                </div>
                            </div>

                            <!-- Step 2: Data Collection -->
                            <div class="fingerprint-capture-section" id="dataCollectionSection" style="display: none;">
                                <h5><i class="fas fa-edit me-2"></i>Step 2: Personal Information</h5>
                                <form id="afisVerificationForm">
                                    <div class="row">
                                        <div class="col-md-6">
                                            <div class="mb-3">
                                                <label for="nid10Digit" class="form-label">10-Digit NID</label>
                                                <input type="text" class="form-control" id="nid10Digit" name="nid10Digit" 
                                                       maxlength="10" placeholder="Enter 10-digit NID">
                                            </div>
                                        </div>
                                        <div class="col-md-6">
                                            <div class="mb-3">
                                                <label for="nid17Digit" class="form-label">17-Digit NID</label>
                                                <input type="text" class="form-control" id="nid17Digit" name="nid17Digit" 
                                                       maxlength="17" placeholder="Enter 17-digit NID">
                                            </div>
                                        </div>
                                    </div>
                                    
                                    <div class="mb-3">
                                        <label for="dateOfBirth" class="form-label">Date of Birth *</label>
                                        <input type="date" class="form-control" id="dateOfBirth" name="dateOfBirth" required>
                                    </div>
                                    
                                    <div class="mb-3">
                                        <label for="jwtToken" class="form-label">JWT Token *</label>
                                        <textarea class="form-control" id="jwtToken" rows="3" placeholder="Enter your JWT token for AFIS verification..." required></textarea>
                                    </div>
                                </form>
                            </div>

                            <!-- Step 3: AFIS Verification -->
                            <div class="fingerprint-capture-section" id="verificationSection" style="display: none;">
                                <h5><i class="fas fa-shield-alt me-2"></i>Step 3: AFIS Verification</h5>
                                <div id="verificationStatus" class="capture-status info">
                                    <i class="fas fa-info-circle me-2"></i>Ready to submit for AFIS verification
                                </div>
                                
                                <div class="capture-controls">
                                    <button type="button" class="btn btn-success" onclick="submitForVerification()" id="submitBtn">
                                        <i class="fas fa-paper-plane me-2"></i>Submit for AFIS Verification
                                    </button>
                                    <button type="button" class="btn btn-secondary" onclick="resetWorkflow()">
                                        <i class="fas fa-redo me-2"></i>Start Over
                                    </button>
                                </div>
                            </div>

                            <!-- Results Section -->
                            <div id="resultsSection" style="display: none;">
                                <div class="card">
                                    <div class="card-header">
                                        <h5><i class="fas fa-clipboard-check me-2"></i>Verification Results</h5>
                                    </div>
                                    <div class="card-body">
                                        <div id="resultsContent"></div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Include fingerprint capture functionality -->
    <script src="/static/js/fingerprint-capture.js"></script>
    
    <script>
        let capturedFingerprints = [];
        let currentStep = 1;
        
        // Initialize page
        document.addEventListener('DOMContentLoaded', function() {
            checkDeviceStatus();
            updateQualityDisplay();
            
            // Quality threshold slider
            document.getElementById('qualityThreshold').addEventListener('input', updateQualityDisplay);
        });
        
        function updateQualityDisplay() {
            const value = document.getElementById('qualityThreshold').value;
            document.getElementById('qualityValue').textContent = value;
        }
        
        function clearSelection() {
            document.querySelectorAll('.finger-checkbox').forEach(checkbox => {
                checkbox.checked = false;
            });
        }
        
        function getSelectedFingers() {
            const selected = [];
            document.querySelectorAll('.finger-checkbox:checked').forEach(checkbox => {
                selected.push(checkbox.value);
            });
            return selected;
        }
        
        async function checkDeviceStatus() {
            try {
                const response = await fetch('/afis/device/status');
                const data = await response.json();
                
                const statusDiv = document.getElementById('deviceStatus');
                if (data.afisReady) {
                    statusDiv.className = 'capture-status success';
                    statusDiv.innerHTML = '<i class="fas fa-check-circle me-2"></i>Device Ready - Scanner Connected and AFIS Ready';
                } else {
                    statusDiv.className = 'capture-status error';
                    statusDiv.innerHTML = '<i class="fas fa-exclamation-triangle me-2"></i>Device Not Ready - Please check scanner connection';
                }
            } catch (error) {
                document.getElementById('deviceStatus').className = 'capture-status error';
                document.getElementById('deviceStatus').innerHTML = '<i class="fas fa-times-circle me-2"></i>Device Status Check Failed';
            }
        }
        
        async function captureFingerprints() {
            const selectedFingers = getSelectedFingers();
            if (selectedFingers.length === 0) {
                alert('Please select at least one finger for capture');
                return;
            }
            
            const qualityThreshold = parseInt(document.getElementById('qualityThreshold').value);
            const captureBtn = document.getElementById('captureBtn');
            captureBtn.disabled = true;
            captureBtn.innerHTML = '<i class="fas fa-spinner fa-spin me-2"></i>Capturing...';
            
            try {
                const response = await fetch('/afis/capture-fingerprints', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({
                        fingerEnums: selectedFingers,
                        qualityThreshold: qualityThreshold,
                        captureTimeout: 60000,
                        retryCount: 2
                    })
                });
                
                const result = await response.json();
                
                if (result.success && result.capturedFingerprints.length > 0) {
                    capturedFingerprints = result.capturedFingerprints;
                    displayFingerprintPreview(result);
                    moveToStep(2);
                } else {
                    alert('Fingerprint capture failed: ' + (result.error || 'Unknown error'));
                }
            } catch (error) {
                alert('Capture error: ' + error.message);
            } finally {
                captureBtn.disabled = false;
                captureBtn.innerHTML = '<i class="fas fa-camera me-2"></i>Capture Selected Fingers';
            }
        }
        
        function displayFingerprintPreview(result) {
            const previewDiv = document.getElementById('fingerprintPreview');
            const container = document.getElementById('previewContainer');
            
            let html = `
                <div class="row">
                    <div class="col-md-12">
                        <div class="alert alert-success">
                            <i class="fas fa-check-circle me-2"></i>
                            Successfully captured ${result.capturedFingerprints.length} fingerprints
                        </div>
                    </div>
                </div>
                <div class="row">
            `;
            
            result.capturedFingerprints.forEach(fingerprint => {
                const qualityClass = getQualityClass(fingerprint.qualityScore);
                html += `
                    <div class="col-md-4 mb-3">
                        <div class="fingerprint-preview">
                            <h6>${fingerprint.fingerType}</h6>
                            <div class="quality-indicator ${qualityClass}">
                                Quality: ${fingerprint.qualityScore || 'N/A'}
                            </div>
                            ${fingerprint.hasImageData ? 
                                `<img src="data:image/png;base64,${fingerprint.imageData}" alt="${fingerprint.fingerType}" class="fingerprint-image">` :
                                '<div class="text-muted">No image data</div>'
                            }
                            <div class="mt-2">
                                <small class="text-muted">
                                    Image: ${fingerprint.hasImageData ? 'Available' : 'None'} | 
                                    WSQ: ${fingerprint.hasWsqData ? 'Available' : 'None'}
                                </small>
                            </div>
                        </div>
                    </div>
                `;
            });
            
            html += '</div>';
            container.innerHTML = html;
            previewDiv.style.display = 'block';
        }
        
        function getQualityClass(score) {
            if (!score) return 'quality-poor';
            if (score >= 80) return 'quality-excellent';
            if (score >= 60) return 'quality-good';
            return 'quality-poor';
        }
        
        function moveToStep(step) {
            currentStep = step;
            
            // Update progress indicators
            for (let i = 1; i <= 3; i++) {
                const stepElement = document.getElementById(`step${i}`);
                if (i < step) {
                    stepElement.className = 'progress-step completed';
                    stepElement.querySelector('.step-number').className = 'step-number completed';
                } else if (i === step) {
                    stepElement.className = 'progress-step current';
                    stepElement.querySelector('.step-number').className = 'step-number current';
                } else {
                    stepElement.className = 'progress-step pending';
                    stepElement.querySelector('.step-number').className = 'step-number pending';
                }
            }
            
            // Show/hide sections
            if (step >= 2) {
                document.getElementById('dataCollectionSection').style.display = 'block';
            }
            if (step >= 3) {
                document.getElementById('verificationSection').style.display = 'block';
            }
        }
        
        async function submitForVerification() {
            const form = document.getElementById('afisVerificationForm');
            if (!form.checkValidity()) {
                form.reportValidity();
                return;
            }
            
            const jwt = document.getElementById('jwtToken').value.trim();
            if (!jwt) {
                alert('JWT token is required for AFIS verification');
                return;
            }
            
            const submitBtn = document.getElementById('submitBtn');
            submitBtn.disabled = true;
            submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin me-2"></i>Submitting...';
            
            try {
                const response = await fetch('/afis/verification-with-capture', {
                    method: 'POST',
                    headers: { 
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${jwt}`
                    },
                    body: JSON.stringify({
                        dateOfBirth: document.getElementById('dateOfBirth').value,
                        nid10Digit: document.getElementById('nid10Digit').value.trim() || null,
                        nid17Digit: document.getElementById('nid17Digit').value.trim() || null,
                        fingerEnums: capturedFingerprints.map(f => f.fingerType),
                        qualityThreshold: parseInt(document.getElementById('qualityThreshold').value),
                        captureTimeout: 30000,
                        retryCount: 3
                    })
                });
                
                const result = await response.json();
                displayResults(result);
                
            } catch (error) {
                alert('Verification error: ' + error.message);
            } finally {
                submitBtn.disabled = false;
                submitBtn.innerHTML = '<i class="fas fa-paper-plane me-2"></i>Submit for AFIS Verification';
            }
        }
        
        function displayResults(result) {
            const resultsSection = document.getElementById('resultsSection');
            const resultsContent = document.getElementById('resultsContent');
            
            let html = `
                <div class="row">
                    <div class="col-md-6">
                        <h6>Fingerprint Capture Summary</h6>
                        <ul class="list-group">
                            <li class="list-group-item d-flex justify-content-between">
                                <span>Total Requested:</span>
                                <span class="badge bg-primary">${result.fingerprintCapture.totalRequested}</span>
                            </li>
                            <li class="list-group-item d-flex justify-content-between">
                                <span>Successfully Captured:</span>
                                <span class="badge bg-success">${result.fingerprintCapture.successfullyCaptured}</span>
                            </li>
                            <li class="list-group-item d-flex justify-content-between">
                                <span>Failed:</span>
                                <span class="badge bg-danger">${result.fingerprintCapture.failedFingers.length}</span>
                            </li>
                        </ul>
                    </div>
                    <div class="col-md-6">
                        <h6>AFIS Verification Status</h6>
                        <div class="alert alert-${result.afisVerification.status === 'SUCCESS' ? 'success' : 'warning'}">
                            <strong>${result.afisVerification.status}</strong><br>
                            <small>${result.afisVerification.statusCode || 'N/A'}</small>
                        </div>
                    </div>
                </div>
            `;
            
            if (result.afisVerification.success) {
                html += `
                    <div class="alert alert-success mt-3">
                        <h6><i class="fas fa-check-circle me-2"></i>AFIS Verification Successful</h6>
                        <p>${result.afisVerification.success.data.fingerUploadUrls.length} upload URLs provided for fingerprint submission.</p>
                    </div>
                `;
            }
            
            resultsContent.innerHTML = html;
            resultsSection.style.display = 'block';
            
            // Move to final step
            moveToStep(3);
        }
        
        function resetWorkflow() {
            currentStep = 1;
            capturedFingerprints = [];
            
            // Reset UI
            document.getElementById('fingerprintPreview').style.display = 'none';
            document.getElementById('dataCollectionSection').style.display = 'none';
            document.getElementById('verificationSection').style.display = 'none';
            document.getElementById('resultsSection').style.display = 'none';
            
            // Reset form
            document.getElementById('afisVerificationForm').reset();
            clearSelection();
            
            // Reset progress
            moveToStep(1);
        }
    </script>
</body>
</html>
