<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>AFIS Verification - Verification System</title>
    <link href="/static/css/bootstrap.min.css" rel="stylesheet">
    <link href="/static/css/all.min.css" rel="stylesheet">
    <link href="/static/css/dashboard.css" rel="stylesheet">
    <link href="/static/css/afis-verification.css" rel="stylesheet">
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
                        <h4><i class="fas fa-fingerprint"></i> AFIS Verification</h4>
                    </div>
                    <div class="card-body">
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
                            
                            <div class="finger-selection">
                                <h6>Select Fingers for Verification *</h6>
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
                            
                            <div class="d-grid gap-2 d-md-flex justify-content-md-end">
                                <button type="button" class="btn btn-secondary me-md-2" onclick="window.history.back()">
                                    <i class="fas fa-arrow-left"></i> Back
                                </button>
                                <button type="submit" class="btn btn-primary">
                                    <i class="fas fa-search"></i> Verify
                                </button>
                            </div>
                        </form>
                        
                        <!-- Result Section -->
                        <div id="resultSection" class="result-section">
                            <h5><i class="fas fa-info-circle"></i> Verification Result</h5>
                            <div id="resultContent"></div>
                        </div>
                        
                        <!-- Upload Section -->
                        <div id="uploadSection" class="upload-section">
                            <h5><i class="fas fa-upload"></i> Fingerprint Upload</h5>
                            <div id="uploadContent"></div>
                            <div class="mt-3">
                                <button type="button" class="btn btn-success" onclick="checkResult()">
                                    <i class="fas fa-sync"></i> Check Result
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    </div>
    
    <script src="/static/js/bootstrap.bundle.min.js"></script>
    <script src="/static/js/afis-verification.js"></script>
</body>
</html>
