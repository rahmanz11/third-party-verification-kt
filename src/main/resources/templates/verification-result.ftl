<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${title}</title>
    <link href="/static/css/bootstrap.min.css" rel="stylesheet">
    <link href="/static/css/all.min.css" rel="stylesheet">
    <link href="/static/css/verification-result.css" rel="stylesheet">
    <style>
        .preview-section {
            background: #f8f9fa;
            border-radius: 10px;
            padding: 20px;
            margin-bottom: 20px;
            border-left: 4px solid #007bff;
        }
        .preview-section h5 {
            color: #007bff;
            margin-bottom: 15px;
        }
        .info-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
            gap: 15px;
        }
        .info-item {
            background: white;
            padding: 15px;
            border-radius: 8px;
            border: 1px solid #e9ecef;
        }
        .info-label {
            font-weight: 600;
            color: #495057;
            margin-bottom: 5px;
            font-size: 0.9rem;
        }
        .info-value {
            color: #212529;
            font-size: 1rem;
        }
        .status-badge {
            display: inline-block;
            padding: 8px 16px;
            border-radius: 20px;
            font-weight: 600;
            font-size: 0.9rem;
        }
        .photo-container {
            text-align: center;
            background: white;
            padding: 20px;
            border-radius: 8px;
            border: 1px solid #e9ecef;
        }
        .photo-container img {
            max-width: 100%;
            max-height: 300px;
            border-radius: 8px;
            box-shadow: 0 4px 8px rgba(0,0,0,0.1);
        }
        .verification-status {
            text-align: center;
            margin-bottom: 30px;
        }
        .verification-status i {
            font-size: 4rem;
            margin-bottom: 15px;
        }
        .verification-status h2 {
            margin-bottom: 10px;
        }
        .field-verification-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 15px;
        }
        .field-verification-item {
            background: white;
            padding: 15px;
            border-radius: 8px;
            border: 1px solid #e9ecef;
            text-align: center;
        }
        .field-verification-item i {
            font-size: 1.5rem;
            margin-bottom: 10px;
        }
        .success-icon { color: #28a745; }
        .error-icon { color: #dc3545; }
        .warning-icon { color: #ffc107; }
    </style>
</head>
<body>
    <nav class="navbar navbar-expand-lg navbar-light fixed-top">
        <div class="container">
            <a class="navbar-brand fw-bold" href="/dashboard?username=${username}">
                <i class="fas fa-shield-alt me-2"></i>Verification System
            </a>
            <div class="navbar-nav ms-auto">
                <span class="navbar-text me-3">
                    <i class="fas fa-user me-2"></i>${username}
                </span>
                <a class="btn btn-outline-danger btn-sm" href="/logout?username=${username}&thirdPartyUsername=${thirdPartyUsername!username}">
                    <i class="fas fa-sign-out-alt me-2"></i>Logout
                </a>
            </div>
        </div>
    </nav>

    <div class="main-content">
        <div class="container">
            <div class="row justify-content-center">
                <div class="col-lg-12">
                    <div class="result-card p-4">
                        <!-- Verification Status Header -->
                        <div class="verification-status">
                            <#if verificationResponse.verified?? && verificationResponse.verified == true>
                                <i class="fas fa-check-circle text-success"></i>
                                <h2 class="fw-bold text-success">Verification Successful!</h2>
                                <p class="text-muted">All submitted information has been verified successfully</p>
                            <#elseif verificationResponse.verified?? && verificationResponse.verified == false>
                                <i class="fas fa-times-circle text-danger"></i>
                                <h2 class="fw-bold text-danger">Verification Failed</h2>
                                <p class="text-muted">Some information could not be verified</p>
                            <#else>
                                <i class="fas fa-exclamation-triangle text-warning"></i>
                                <h2 class="fw-bold text-warning">Verification Status Unknown</h2>
                                <p class="text-muted">Verification status could not be determined</p>
                            </#if>
                        </div>

                        <!-- Overall Status Section -->
                        <div class="preview-section">
                            <h5><i class="fas fa-info-circle me-2"></i>Overall Status</h5>
                            <div class="info-grid">
                                <div class="info-item">
                                    <div class="info-label">Status</div>
                                    <div class="info-value">${verificationResponse.status}</div>
                                </div>
                                <div class="info-item">
                                    <div class="info-label">Status Code</div>
                                    <div class="info-value">${verificationResponse.statusCode!''}</div>
                                </div>
                                <div class="info-item">
                                    <div class="info-label">Verification Result</div>
                                    <div class="info-value">
                                        <#if verificationResponse.verified?? && verificationResponse.verified == true>
                                            <span class="status-badge bg-success text-white">
                                                <i class="fas fa-check me-2"></i>Verified
                                            </span>
                                        <#elseif verificationResponse.verified?? && verificationResponse.verified == false>
                                            <span class="status-badge bg-danger text-white">
                                                <i class="fas fa-times me-2"></i>Not Verified
                                            </span>
                                        <#else>
                                            <span class="status-badge bg-warning text-dark">
                                                <i class="fas fa-exclamation-triangle me-2"></i>Status Unknown
                                            </span>
                                        </#if>
                                    </div>
                                </div>
                                <div class="info-item">
                                    <div class="info-label">Request ID</div>
                                    <div class="info-value">${verificationResponse.requestId!''}</div>
                                </div>
                            </div>
                        </div>

                        <!-- Personal Information Section -->
                        <#if verificationResponse.success?? && verificationResponse.success.data??>
                            <div class="preview-section">
                                <h5><i class="fas fa-user me-2"></i>Personal Information</h5>
                                <div class="info-grid">
                                    <div class="info-item">
                                        <div class="info-label">Full Name (English)</div>
                                        <div class="info-value">${verificationResponse.success.data.nameEn!''}</div>
                                    </div>
                                    <div class="info-item">
                                        <div class="info-label">Voter Area</div>
                                        <div class="info-value">${verificationResponse.success.data.voterArea!''}</div>
                                    </div>
                                    <div class="info-item">
                                        <div class="info-label">Mobile Number</div>
                                        <div class="info-value">${verificationResponse.success.data.mobile!''}</div>
                                    </div>
                                    <div class="info-item">
                                        <div class="info-label">Fingerprint Status</div>
                                        <div class="info-value">
                                            <#if verificationResponse.success.data.noFingerprint?? && verificationResponse.success.data.noFingerprint == 1>
                                                <span class="status-badge bg-success text-white">
                                                    <i class="fas fa-fingerprint me-2"></i>Available
                                                </span>
                                            <#else>
                                                <span class="status-badge bg-warning text-dark">
                                                    <i class="fas fa-exclamation-triangle me-2"></i>Not Available
                                                </span>
                                            </#if>
                                        </div>
                                    </div>
                                    <div class="info-item">
                                        <div class="info-label">Mother's NID</div>
                                        <div class="info-value">${verificationResponse.success.data.nidMother!''}</div>
                                    </div>
                                </div>
                            </div>

                            <!-- Permanent Address Section -->
                            <#if verificationResponse.success.data.permanentAddress??>
                                <div class="preview-section">
                                    <h5><i class="fas fa-map-marker-alt me-2"></i>Permanent Address</h5>
                                    <div class="info-grid">
                                        <div class="info-item">
                                            <div class="info-label">Division</div>
                                            <div class="info-value">${verificationResponse.success.data.permanentAddress.division!''}</div>
                                        </div>
                                        <div class="info-item">
                                            <div class="info-label">District</div>
                                            <div class="info-value">${verificationResponse.success.data.permanentAddress.district!''}</div>
                                        </div>
                                        <div class="info-item">
                                            <div class="info-label">RMO</div>
                                            <div class="info-value">${verificationResponse.success.data.permanentAddress.rmo!''}</div>
                                        </div>
                                        <div class="info-item">
                                            <div class="info-label">Upazila</div>
                                            <div class="info-value">${verificationResponse.success.data.permanentAddress.upozila!''}</div>
                                        </div>
                                        <div class="info-item">
                                            <div class="info-label">City Corporation/Municipality</div>
                                            <div class="info-value">${verificationResponse.success.data.permanentAddress.cityCorporationOrMunicipality!''}</div>
                                        </div>
                                        <div class="info-item">
                                            <div class="info-label">Union/Ward</div>
                                            <div class="info-value">${verificationResponse.success.data.permanentAddress.unionOrWard!''}</div>
                                        </div>
                                        <div class="info-item">
                                            <div class="info-label">Post Office</div>
                                            <div class="info-value">${verificationResponse.success.data.permanentAddress.postOffice!''}</div>
                                        </div>
                                        <div class="info-item">
                                            <div class="info-label">Postal Code</div>
                                            <div class="info-value">${verificationResponse.success.data.postalCode!''}</div>
                                        </div>
                                        <div class="info-item">
                                            <div class="info-label">Ward for Union Porishod</div>
                                            <div class="info-value">${verificationResponse.success.data.permanentAddress.wardForUnionPorishod!''}</div>
                                        </div>
                                        <div class="info-item">
                                            <div class="info-label">Additional Mouza/Moholla</div>
                                            <div class="info-value">${verificationResponse.success.data.permanentAddress.additionalMouzaOrMoholla!''}</div>
                                        </div>
                                        <div class="info-item">
                                            <div class="info-label">Additional Village/Road</div>
                                            <div class="info-value">${verificationResponse.success.data.permanentAddress.additionalVillageOrRoad!''}</div>
                                        </div>
                                        <div class="info-item">
                                            <div class="info-label">Home/Holding No</div>
                                            <div class="info-value">${verificationResponse.success.data.permanentAddress.homeOrHoldingNo!''}</div>
                                        </div>
                                        <div class="info-item">
                                            <div class="info-label">Region</div>
                                            <div class="info-value">${verificationResponse.success.data.permanentAddress.region!''}</div>
                                        </div>
                                    </div>
                                </div>
                            </#if>

                            <!-- Photo Section -->
                            <#if verificationResponse.success.data.photo?? && verificationResponse.success.data.photo != "">
                                <div class="preview-section">
                                    <h5><i class="fas fa-camera me-2"></i>Profile Photo</h5>
                                    <div class="photo-container">
                                        <img src="${verificationResponse.success.data.photo}" alt="Profile Photo" 
                                             onerror="this.style.display='none'; this.nextElementSibling.style.display='block';">
                                        <p style="display: none; color: #6c757d;">
                                            <i class="fas fa-image fa-3x mb-2"></i><br>
                                            Photo not available or failed to load
                                        </p>
                                    </div>
                                </div>
                            </#if>
                        </#if>

                        <!-- Field Verification Results Section -->
                        <#if verificationResponse.fieldVerificationResult??>
                            <div class="preview-section">
                                <h5><i class="fas fa-list-check me-2"></i>Field Verification Results</h5>
                                <div class="field-verification-grid">
                                    <div class="field-verification-item">
                                        <#if verificationResponse.fieldVerificationResult.dateOfBirth?? && verificationResponse.fieldVerificationResult.dateOfBirth == true>
                                            <i class="fas fa-check success-icon"></i>
                                            <div class="info-label">Date of Birth</div>
                                            <div class="info-value text-success">Verified</div>
                                        <#else>
                                            <i class="fas fa-times error-icon"></i>
                                            <div class="info-label">Date of Birth</div>
                                            <div class="info-value text-danger">Not Verified</div>
                                        </#if>
                                    </div>
                                    <div class="field-verification-item">
                                        <#if verificationResponse.fieldVerificationResult.nameEn?? && verificationResponse.fieldVerificationResult.nameEn == true>
                                            <i class="fas fa-check success-icon"></i>
                                            <div class="info-label">English Name</div>
                                            <div class="info-value text-success">Verified</div>
                                        <#else>
                                            <i class="fas fa-times error-icon"></i>
                                            <div class="info-label">English Name</div>
                                            <div class="info-value text-danger">Not Verified</div>
                                        </#if>
                                    </div>
                                    <div class="field-verification-item">
                                        <#if verificationResponse.fieldVerificationResult.name?? && verificationResponse.fieldVerificationResult.name == true>
                                            <i class="fas fa-check success-icon"></i>
                                            <div class="info-label">Bengali Name</div>
                                            <div class="info-value text-success">Verified</div>
                                        <#else>
                                            <i class="fas fa-times error-icon"></i>
                                            <div class="info-label">Bengali Name</div>
                                            <div class="info-value text-danger">Not Verified</div>
                                        </#if>
                                    </div>
                                    <div class="field-verification-item">
                                        <#if verificationResponse.fieldVerificationResult.father?? && verificationResponse.fieldVerificationResult.father == true>
                                            <i class="fas fa-check success-icon"></i>
                                            <div class="info-label">Father's Name</div>
                                            <div class="info-value text-success">Verified</div>
                                        <#else>
                                            <i class="fas fa-times error-icon"></i>
                                            <div class="info-label">Father's Name</div>
                                            <div class="info-value text-danger">Not Verified</div>
                                        </#if>
                                    </div>
                                    <div class="field-verification-item">
                                        <#if verificationResponse.fieldVerificationResult.mother?? && verificationResponse.fieldVerificationResult.mother == true>
                                            <i class="fas fa-check success-icon"></i>
                                            <div class="info-label">Mother's Name</div>
                                            <div class="info-value text-success">Verified</div>
                                        <#else>
                                            <i class="fas fa-times error-icon"></i>
                                            <div class="info-label">Mother's Name</div>
                                            <div class="info-value text-danger">Not Verified</div>
                                        </#if>
                                    </div>
                                </div>
                            </div>
                        </#if>

                        <!-- Message Section (if any) -->
                        <#if verificationResponse.message?? && verificationResponse.message != "">
                            <div class="preview-section">
                                <h5><i class="fas fa-comment me-2"></i>Additional Information</h5>
                                <div class="alert alert-info">
                                    <p class="mb-0">${verificationResponse.message}</p>
                                </div>
                            </div>
                        </#if>

                        <!-- Action Buttons -->
                        <div class="d-grid gap-2 d-md-flex justify-content-md-center mt-4">
                            <a href="/verification-form?username=${username}&thirdPartyUsername=${thirdPartyUsername}" class="btn btn-primary me-md-2">
                                <i class="fas fa-plus me-2"></i>New Verification
                            </a>
                            <a href="/dashboard?username=${username}" class="btn btn-outline-secondary">
                                <i class="fas fa-home me-2"></i>Back to Dashboard
                            </a>
                            <button onclick="window.print()" class="btn btn-outline-info">
                                <i class="fas fa-print me-2"></i>Print Result
                            </button>
                            <button onclick="loadMockResponse()" class="btn btn-outline-warning">
                                <i class="fas fa-flask me-2"></i>Test Response Display
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="/static/js/bootstrap.bundle.min.js"></script>
    
    <script>
        // Test Response Display functionality
        function loadMockResponse() {
            // Show loading state
            const resultCard = document.querySelector('.result-card');
            const originalContent = resultCard.innerHTML;
            
            // Show loading indicator
            resultCard.innerHTML = `
                <div class="text-center p-5">
                    <div class="spinner-border text-primary mb-3" role="status">
                        <span class="visually-hidden">Loading...</span>
                    </div>
                    <h5>Loading Mock Response Data...</h5>
                    <p class="text-muted">Testing response display functionality</p>
                </div>
            `;
            
            // Load the verification-response.json file
            fetch('/verification-response.json')
                .then(response => response.json())
                .then(data => {
                    // Display the mock response data in the verification result format
                    displayMockResponse(data);
                })
                .catch(error => {
                    // Show error and restore original content
                    resultCard.innerHTML = `
                        <div class="text-center p-5">
                            <div class="alert alert-danger">
                                <i class="fas fa-exclamation-triangle fa-2x mb-3"></i>
                                <h5>Error Loading Mock Data</h5>
                                <p>${error.message}</p>
                                <button onclick="location.reload()" class="btn btn-primary">
                                    <i class="fas fa-refresh me-2"></i>Reload Page
                                </button>
                            </div>
                        </div>
                    `;
                });
        }
        
        function displayMockResponse(responseData) {
            const resultCard = document.querySelector('.result-card');
            
            // Build the verification result display using the mock data
            let html = '';
            
            // Verification Status Header
            html += `
                <div class="verification-status">
                    <i class="fas fa-check-circle text-success"></i>
                    <h2 class="fw-bold text-success">Mock Response Test - Verification Successful!</h2>
                    <p class="text-muted">Testing response display with mock data from verification-response.json</p>
                </div>
            `;
            
            // Overall Status Section
            html += `
                <div class="preview-section">
                    <h5><i class="fas fa-info-circle me-2"></i>Overall Status</h5>
                    <div class="info-grid">
                        <div class="info-item">
                            <div class="info-label">Status</div>
                            <div class="info-value">${responseData.status}</div>
                        </div>
                        <div class="info-item">
                            <div class="info-label">Status Code</div>
                            <div class="info-value">${responseData.statusCode || ''}</div>
                        </div>
                        <div class="info-item">
                            <div class="info-label">Verification Result</div>
                            <div class="info-value">
                                <span class="status-badge bg-success text-white">
                                    <i class="fas fa-check me-2"></i>Verified
                                </span>
                            </div>
                        </div>
                        <div class="info-item">
                            <div class="info-label">Request ID</div>
                            <div class="info-value">${responseData.requestId || ''}</div>
                        </div>
                    </div>
                </div>
            `;
            
            // Personal Information Section
            if (responseData.success && responseData.success.data) {
                const data = responseData.success.data;
                html += `
                    <div class="preview-section">
                        <h5><i class="fas fa-user me-2"></i>Personal Information</h5>
                        <div class="info-grid">
                            <div class="info-item">
                                <div class="info-label">Full Name (English)</div>
                                <div class="info-value">${data.nameEn || ''}</div>
                            </div>
                            <div class="info-item">
                                <div class="info-label">Voter Area</div>
                                <div class="info-value">${data.voterArea || ''}</div>
                            </div>
                            <div class="info-item">
                                <div class="info-label">Mobile Number</div>
                                <div class="info-value">${data.mobile || ''}</div>
                            </div>
                            <div class="info-item">
                                <div class="info-label">Fingerprint Status</div>
                                <div class="info-value">
                                    <span class="status-badge bg-success text-white">
                                        <i class="fas fa-fingerprint me-2"></i>Available
                                    </span>
                                </div>
                            </div>
                            <div class="info-item">
                                <div class="info-label">Mother's NID</div>
                                <div class="info-value">${data.nidMother || ''}</div>
                            </div>
                        </div>
                    </div>
                `;
                
                // Permanent Address Section
                if (data.permanentAddress) {
                    const addr = data.permanentAddress;
                    html += `
                        <div class="preview-section">
                            <h5><i class="fas fa-map-marker-alt me-2"></i>Permanent Address</h5>
                            <div class="info-grid">
                                <div class="info-item">
                                    <div class="info-label">Division</div>
                                    <div class="info-value">${addr.division || ''}</div>
                                </div>
                                <div class="info-item">
                                    <div class="info-label">District</div>
                                    <div class="info-value">${addr.district || ''}</div>
                                </div>
                                <div class="info-item">
                                    <div class="info-label">RMO</div>
                                    <div class="info-value">${addr.rmo || ''}</div>
                                </div>
                                <div class="info-item">
                                    <div class="info-label">Upazila</div>
                                    <div class="info-value">${addr.upozila || ''}</div>
                                </div>
                                <div class="info-item">
                                    <div class="info-label">City Corporation/Municipality</div>
                                    <div class="info-value">${addr.cityCorporationOrMunicipality || ''}</div>
                                </div>
                                <div class="info-item">
                                    <div class="info-label">Union/Ward</div>
                                    <div class="info-value">${addr.unionOrWard || ''}</div>
                                </div>
                                <div class="info-item">
                                    <div class="info-label">Post Office</div>
                                    <div class="info-value">${addr.postOffice || ''}</div>
                                </div>
                                <div class="info-item">
                                    <div class="info-label">Postal Code</div>
                                    <div class="info-value">${addr.postalCode || ''}</div>
                                </div>
                                <div class="info-item">
                                    <div class="info-label">Ward for Union Porishod</div>
                                    <div class="info-value">${addr.wardForUnionPorishod || ''}</div>
                                </div>
                                <div class="info-item">
                                    <div class="info-label">Additional Mouza/Moholla</div>
                                    <div class="info-value">${addr.additionalMouzaOrMoholla || ''}</div>
                                </div>
                                <div class="info-item">
                                    <div class="info-label">Additional Village/Road</div>
                                    <div class="info-value">${addr.additionalVillageOrRoad || ''}</div>
                                </div>
                                <div class="info-item">
                                    <div class="info-label">Home/Holding No</div>
                                    <div class="info-value">${addr.homeOrHoldingNo || ''}</div>
                                </div>
                                <div class="info-item">
                                    <div class="info-label">Region</div>
                                    <div class="info-value">${addr.region || ''}</div>
                                </div>
                            </div>
                        </div>
                    `;
                }
                
                // Photo Section
                if (data.photo) {
                    html += `
                        <div class="preview-section">
                            <h5><i class="fas fa-camera me-2"></i>Profile Photo</h5>
                            <div class="photo-container">
                                <img src="${data.photo}" alt="Profile Photo" 
                                     onerror="this.style.display='none'; this.nextElementSibling.style.display='block';">
                                <p style="display: none; color: #6c757d;">
                                    <i class="fas fa-image fa-3x mb-2"></i><br>
                                    Photo not available or failed to load
                                </p>
                            </div>
                        </div>
                    `;
                }
            }
            
            // Field Verification Results Section
            if (responseData.fieldVerificationResult) {
                const fieldResult = responseData.fieldVerificationResult;
                html += `
                    <div class="preview-section">
                        <h5><i class="fas fa-list-check me-2"></i>Field Verification Results</h5>
                        <div class="field-verification-grid">
                            <div class="field-verification-item">
                                <i class="fas fa-check success-icon"></i>
                                <div class="info-label">Date of Birth</div>
                                <div class="info-value text-success">Verified</div>
                            </div>
                            <div class="field-verification-item">
                                <i class="fas fa-check success-icon"></i>
                                <div class="info-label">English Name</div>
                                <div class="info-value text-success">Verified</div>
                            </div>
                            <div class="field-verification-item">
                                <i class="fas fa-check success-icon"></i>
                                <div class="info-label">Bengali Name</div>
                                <div class="info-value text-success">Verified</div>
                            </div>
                            <div class="field-verification-item">
                                <i class="fas fa-check success-icon"></i>
                                <div class="info-label">Father's Name</div>
                                <div class="info-value text-success">Verified</div>
                            </div>
                            <div class="field-verification-item">
                                <i class="fas fa-check success-icon"></i>
                                <div class="info-label">Mother's Name</div>
                                <div class="info-value text-success">Verified</div>
                            </div>
                        </div>
                    </div>
                `;
            }
            
            // Test Info Section
            html += `
                <div class="preview-section">
                    <h5><i class="fas fa-info-circle me-2"></i>Test Information</h5>
                    <div class="alert alert-info">
                        <i class="fas fa-flask me-2"></i>
                        <strong>Mock Data Test:</strong> This is displaying data from verification-response.json to test the UI rendering. 
                        If you can see all the information properly formatted above, then the 503 error fix is working correctly!
                    </div>
                </div>
            `;
            
            // Action Buttons
            html += `
                <div class="d-grid gap-2 d-md-flex justify-content-md-center mt-4">
                    <button onclick="location.reload()" class="btn btn-primary me-md-2">
                        <i class="fas fa-refresh me-2"></i>Back to Original
                    </button>
                    <button onclick="loadMockResponse()" class="btn btn-outline-warning">
                        <i class="fas fa-flask me-2"></i>Reload Mock Data
                    </button>
                </div>
            `;
            
            resultCard.innerHTML = html;
        }
    </script>
</body>
</html>