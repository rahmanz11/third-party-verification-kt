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
                <a class="btn btn-outline-danger btn-sm" href="/logout?username=${username}">
                    <i class="fas fa-sign-out-alt me-2"></i>Logout
                </a>
            </div>
        </div>
    </nav>

    <div class="main-content">
        <div class="container">
            <div class="row justify-content-center">
                <div class="col-lg-12">
                    <div class="result-card p-2">
                        <!-- Verification Status Header -->
                         <#if verificationResponse?? && verificationResponse.verified?? && verificationResponse.verified == true>
                             <div class="verification-status">
                                 <i class="fas fa-check-circle text-success"></i>
                                 <h2 class="fw-bold text-success">Verification Successful!</h2>
                                 <p class="text-muted">All submitted information has been verified successfully</p>
                             </div>
                         <#elseif verificationResponse?? && verificationResponse.verified?? && verificationResponse.verified == false>
                             <div class="verification-status">
                                 <i class="fas fa-times-circle text-danger"></i>
                                 <h2 class="fw-bold text-danger">Verification Failed</h2>
                                 <p class="text-muted">Some information could not be verified</p>
                             </div>
                         <#else>
                             <div class="verification-status">
                                 <i class="fas fa-exclamation-triangle text-warning"></i>
                                 <h2 class="fw-bold text-warning">Verification Status Unknown</h2>
                                 <p class="text-muted">Verification status could not be determined</p>
                             </div>
                         </#if>

                         <!-- Field Verification Results Section -->
                         <#if verificationResponse?? && verificationResponse.fieldVerificationResult??>
                             <div class="preview-section">
                                 <h5><i class="fas fa-list-check me-2"></i>Field Verification Results</h5>
                                 <div class="field-verification-grid">
                                     <#if verificationResponse.fieldVerificationResult.dateOfBirth??>
                                         <div class="field-verification-item">
                                             <#if verificationResponse.fieldVerificationResult.dateOfBirth == true>
                                                 <i class="fas fa-check success-icon"></i>
                                                 <div class="info-label">Date of Birth</div>
                                                 <div class="info-value text-success">Verified</div>
                                             <#else>
                                                 <i class="fas fa-times error-icon"></i>
                                                 <div class="info-label">Date of Birth</div>
                                                 <div class="info-value text-danger">Not Verified</div>
                                             </#if>
                                         </div>
                                     </#if>
                                     <#if verificationResponse.fieldVerificationResult.nameEn??>
                                         <div class="field-verification-item">
                                             <#if verificationResponse.fieldVerificationResult.nameEn == true>
                                                 <i class="fas fa-check success-icon"></i>
                                                 <div class="info-label">English Name</div>
                                                 <div class="info-value text-success">Verified</div>
                                             <#else>
                                                 <i class="fas fa-times error-icon"></i>
                                                 <div class="info-label">English Name</div>
                                                 <div class="info-value text-danger">Not Verified</div>
                                             </#if>
                                         </div>
                                     </#if>
                                     <#if verificationResponse.fieldVerificationResult.name??>
                                         <div class="field-verification-item">
                                             <#if verificationResponse.fieldVerificationResult.name == true>
                                                 <i class="fas fa-check success-icon"></i>
                                                 <div class="info-label">Bengali Name</div>
                                                 <div class="info-value text-success">Verified</div>
                                             <#else>
                                                 <i class="fas fa-times error-icon"></i>
                                                 <div class="info-label">Bengali Name</div>
                                                 <div class="info-value text-danger">Not Verified</div>
                                             </#if>
                                         </div>
                                     </#if>
                                     <#if verificationResponse.fieldVerificationResult.father??>
                                         <div class="field-verification-item">
                                             <#if verificationResponse.fieldVerificationResult.father == true>
                                                 <i class="fas fa-check success-icon"></i>
                                                 <div class="info-label">Father's Name</div>
                                                 <div class="info-value text-success">Verified</div>
                                             <#else>
                                                 <i class="fas fa-times error-icon"></i>
                                                 <div class="info-label">Father's Name</div>
                                                 <div class="info-value text-danger">Not Verified</div>
                                             </#if>
                                         </div>
                                     </#if>
                                     <#if verificationResponse.fieldVerificationResult.mother??>
                                         <div class="field-verification-item">
                                             <#if verificationResponse.fieldVerificationResult.mother == true>
                                                 <i class="fas fa-check success-icon"></i>
                                                 <div class="info-label">Mother's Name</div>
                                                 <div class="info-value text-success">Verified</div>
                                             <#else>
                                                 <i class="fas fa-times error-icon"></i>
                                                 <div class="info-label">Mother's Name</div>
                                                 <div class="info-value text-danger">Not Verified</div>
                                             </#if>
                                         </div>
                                     </#if>
                                     <#if verificationResponse.fieldVerificationResult.spouse??>
                                         <div class="field-verification-item">
                                             <#if verificationResponse.fieldVerificationResult.spouse == true>
                                                 <i class="fas fa-check success-icon"></i>
                                                 <div class="info-label">Spouse Name</div>
                                                 <div class="info-value text-success">Verified</div>
                                             <#else>
                                                 <i class="fas fa-times error-icon"></i>
                                                 <div class="info-label">Spouse Name</div>
                                                 <div class="info-value text-danger">Not Verified</div>
                                             </#if>
                                         </div>
                                     </#if>
                                 </div>
                             </div>
                         </#if>

                                                   <!-- Personal Information Section -->
                         <#if verificationResponse?? && verificationResponse.success?? && verificationResponse.success.data??>
                            <div class="preview-section">
                                <h5><i class="fas fa-user me-2"></i>Personal Information</h5>
                                <div class="info-grid">
                                    <div class="info-item">
                                        <div class="info-label">Full Name (English)</div>
                                        <div class="info-value">${verificationResponse.success.data.nameEn!'N/A'}</div>
                                    </div>
                                    <div class="info-item">
                                        <div class="info-label">Voter Area</div>
                                        <div class="info-value">${verificationResponse.success.data.voterArea!'N/A'}</div>
                                    </div>
                                    <div class="info-item">
                                        <div class="info-label">Mobile Number</div>
                                        <div class="info-value">${verificationResponse.success.data.mobile!'N/A'}</div>
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
                                        <div class="info-value">${verificationResponse.success.data.nidMother!'N/A'}</div>
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
                                            <div class="info-value">${verificationResponse.success.data.permanentAddress.division!'N/A'}</div>
                                        </div>
                                        <div class="info-item">
                                            <div class="info-label">District</div>
                                            <div class="info-value">${verificationResponse.success.data.permanentAddress.district!'N/A'}</div>
                                        </div>
                                        <div class="info-item">
                                            <div class="info-label">RMO</div>
                                            <div class="info-value">${verificationResponse.success.data.permanentAddress.rmo!'N/A'}</div>
                                        </div>
                                        <div class="info-item">
                                            <div class="info-label">Upazila</div>
                                            <div class="info-value">${verificationResponse.success.data.permanentAddress.upozila!'N/A'}</div>
                                        </div>
                                        <div class="info-item">
                                            <div class="info-label">City Corporation/Municipality</div>
                                            <div class="info-value">${verificationResponse.success.data.permanentAddress.cityCorporationOrMunicipality!'N/A'}</div>
                                        </div>
                                        <div class="info-item">
                                            <div class="info-label">Union/Ward</div>
                                            <div class="info-value">${verificationResponse.success.data.permanentAddress.unionOrWard!'N/A'}</div>
                                        </div>
                                        <div class="info-item">
                                            <div class="info-label">Post Office</div>
                                            <div class="info-value">${verificationResponse.success.data.permanentAddress.postOffice!'N/A'}</div>
                                        </div>
                                        <div class="info-item">
                                            <div class="info-label">Postal Code</div>
                                            <div class="info-value">${verificationResponse.success.data.permanentAddress.postalCode!'N/A'}</div>
                                        </div>
                                        <div class="info-item">
                                            <div class="info-label">Ward for Union Porishod</div>
                                            <div class="info-value">${verificationResponse.success.data.permanentAddress.wardForUnionPorishod!'N/A'}</div>
                                        </div>
                                        <div class="info-item">
                                            <div class="info-label">Additional Mouza/Moholla</div>
                                            <div class="info-value">${verificationResponse.success.data.permanentAddress.additionalMouzaOrMoholla!'N/A'}</div>
                                        </div>
                                        <div class="info-item">
                                            <div class="info-label">Additional Village/Road</div>
                                            <div class="info-value">${verificationResponse.success.data.permanentAddress.additionalVillageOrRoad!'N/A'}</div>
                                        </div>
                                        <div class="info-item">
                                            <div class="info-label">Home/Holding No</div>
                                            <div class="info-value">${verificationResponse.success.data.permanentAddress.homeOrHoldingNo!'N/A'}</div>
                                        </div>
                                        <div class="info-item">
                                            <div class="info-label">Region</div>
                                            <div class="info-value">${verificationResponse.success.data.permanentAddress.region!'N/A'}</div>
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



                        <!-- Message Section (if any) -->
                        <#if verificationResponse?? && verificationResponse.message?? && verificationResponse.message != "">
                            <div class="preview-section">
                                <h5><i class="fas fa-comment me-2"></i>Additional Information</h5>
                                <div class="alert alert-info">
                                    <p class="mb-0">${verificationResponse.message}</p>
                                </div>
                            </div>
                        </#if>

                                                 <!-- Additional Verification Details -->
                         <#if verificationResponse?? && verificationResponse.success?? && verificationResponse.success.data??>
                            <div class="preview-section">
                                <h5><i class="fas fa-clipboard-check me-2"></i>Additional Verification Details</h5>
                                <div class="info-grid">
                                    <#if verificationResponse.success.data.requestId??>
                                        <div class="info-item">
                                            <div class="info-label">Request ID</div>
                                            <div class="info-value">${verificationResponse.success.data.requestId}</div>
                                        </div>
                                    </#if>
                                    <#if verificationResponse.success.data.noFingerprint??>
                                        <div class="info-item">
                                            <div class="info-label">Fingerprint Records</div>
                                            <div class="info-value">
                                                <#if verificationResponse.success.data.noFingerprint == 1>
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
                                    </#if>
                                    <#if verificationResponse.success.data.voterArea??>
                                        <div class="info-item">
                                            <div class="info-label">Voter Area</div>
                                            <div class="info-value">${verificationResponse.success.data.voterArea}</div>
                                        </div>
                                    </#if>
                                    <#if verificationResponse.success.data.mobile??>
                                        <div class="info-item">
                                            <div class="info-label">Mobile Number</div>
                                            <div class="info-value">${verificationResponse.success.data.mobile}</div>
                                        </div>
                                    </#if>
                                </div>
                            </div>
                        </#if>

                                                 <!-- Action Buttons -->
                         <div class="d-grid gap-2 d-md-flex justify-content-md-center mt-2">
                             <a href="/verification-form?username=${username}" class="btn btn-primary me-md-2">
                                 <i class="fas fa-plus me-2"></i>New Verification
                             </a>
                             <a href="/dashboard?username=${username}" class="btn btn-outline-secondary me-md-2">
                                 <i class="fas fa-home me-2"></i>Back to Dashboard
                             </a>
                                                           <button onclick="window.print()" class="btn btn-outline-info me-md-2">
                                  <i class="fas fa-print me-2"></i>Print Result
                              </button>
                                                  </div>
                     </div>
                 </div>
             </div>
         </div>
     </div>
     
     <!-- Fallback message if no verification response data -->
     <#if !verificationResponse?? || !verificationResponse.success??>
         <div class="container mt-4">
             <div class="row justify-content-center">
                 <div class="col-lg-8">
                     <div class="alert alert-info text-center">
                         <i class="fas fa-info-circle fa-2x mb-3"></i>
                         <h5>No Verification Results Available</h5>
                         <p class="mb-3">You haven't performed any verifications yet, or the verification data is not available.</p>
                         <a href="/verification-form?username=${username}" class="btn btn-primary">
                             <i class="fas fa-plus me-2"></i>Start New Verification
                         </a>
                     </div>
                 </div>
             </div>
         </div>
     </#if>

    <script src="/static/js/bootstrap.bundle.min.js"></script>
    
    <script>
        // Simple print functionality
        function printResult() {
            window.print();
        }
    </script>
</body>
</html>