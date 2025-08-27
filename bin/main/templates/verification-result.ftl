<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${title}</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        body {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
        }
        .navbar {
            background: rgba(255, 255, 255, 0.95) !important;
            backdrop-filter: blur(10px);
            border-bottom: 1px solid rgba(255, 255, 255, 0.2);
        }
        .main-content {
            padding-top: 80px;
            padding-bottom: 40px;
        }
        .result-card {
            background: rgba(255, 255, 255, 0.95);
            border-radius: 15px;
            box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
            backdrop-filter: blur(10px);
            border: 1px solid rgba(255, 255, 255, 0.2);
        }
        .status-badge {
            font-size: 1rem;
            padding: 8px 16px;
            border-radius: 20px;
        }
        .result-section {
            background: #f8f9fa;
            border-radius: 10px;
            padding: 20px;
            margin-bottom: 20px;
            border-left: 4px solid #667eea;
        }
        .field-result {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 10px 0;
            border-bottom: 1px solid #e9ecef;
        }
        .field-result:last-child {
            border-bottom: none;
        }
        .field-name {
            font-weight: 600;
            color: #495057;
        }
        .field-status {
            display: flex;
            align-items: center;
            gap: 8px;
        }
        .btn-primary {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border: none;
            border-radius: 10px;
            padding: 12px 30px;
            font-weight: 600;
            transition: all 0.3s ease;
        }
        .btn-primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
        }
        .success-icon {
            color: #28a745;
        }
        .error-icon {
            color: #dc3545;
        }
        .warning-icon {
            color: #ffc107;
        }
    </style>
</head>
<body>
    <!-- Navigation -->
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

    <!-- Main Content -->
    <div class="main-content">
        <div class="container">
            <div class="row justify-content-center">
                <div class="col-lg-10">
                    <div class="result-card p-4">
                        <div class="text-center mb-4">
                            <#if verificationResponse.verified == true>
                                <i class="fas fa-check-circle fa-3x text-success mb-3"></i>
                                <h2 class="fw-bold text-success">Verification Successful!</h2>
                            <#elseif verificationResponse.verified == false>
                                <i class="fas fa-times-circle fa-3x text-danger mb-3"></i>
                                <h2 class="fw-bold text-danger">Verification Failed</h2>
                            <#else>
                                <i class="fas fa-exclamation-triangle fa-3x text-warning mb-3"></i>
                                <h2 class="fw-bold text-warning">Partial Verification</h2>
                            </#if>
                            <p class="text-muted">Verification result for the submitted person information</p>
                        </div>

                        <!-- Overall Status -->
                        <div class="result-section">
                            <h5 class="fw-bold mb-3">
                                <i class="fas fa-info-circle me-2"></i>Overall Status
                            </h5>
                            <div class="row align-items-center">
                                <div class="col-md-6">
                                    <p class="mb-0"><strong>Status:</strong> ${verificationResponse.status}</p>
                                    <p class="mb-0"><strong>Status Code:</strong> ${verificationResponse.statusCode}</p>
                                </div>
                                <div class="col-md-6 text-end">
                                    <#if verificationResponse.verified == true>
                                        <span class="status-badge bg-success text-white">
                                            <i class="fas fa-check me-2"></i>Verified
                                        </span>
                                    <#elseif verificationResponse.verified == false>
                                        <span class="status-badge bg-danger text-white">
                                            <i class="fas fa-times me-2"></i>Not Verified
                                        </span>
                                    <#else>
                                        <span class="status-badge bg-warning text-dark">
                                            <i class="fas fa-exclamation-triangle me-2"></i>Partial Match
                                        </span>
                                    </#if>
                                </div>
                            </div>
                        </div>

                        <!-- Success Data -->
                        <#if verificationResponse.success??>
                            <div class="result-section">
                                <h5 class="fw-bold mb-3">
                                    <i class="fas fa-check-circle me-2 text-success"></i>Verification Data
                                </h5>
                                <div class="row">
                                    <div class="col-md-6">
                                        <div class="field-result">
                                            <span class="field-name">Request ID:</span>
                                            <span class="text-muted">${verificationResponse.success.data.requestId}</span>
                                        </div>
                                        <div class="field-result">
                                            <span class="field-name">NID:</span>
                                            <span class="text-muted">${verificationResponse.success.data.nationalId}</span>
                                        </div>
                                    </div>
                                    <div class="col-md-6">
                                        <div class="field-result">
                                            <span class="field-name">PIN:</span>
                                            <span class="text-muted">${verificationResponse.success.data.pin}</span>
                                        </div>
                                        <div class="field-result">
                                            <span class="field-name">Photo Available:</span>
                                            <span class="text-muted">
                                                <#if verificationResponse.success.data.photo?? && verificationResponse.success.data.photo != "">
                                                    <i class="fas fa-image text-success"></i> Yes
                                                <#else>
                                                    <i class="fas fa-times text-muted"></i> No
                                                </#if>
                                            </span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </#if>

                        <!-- Field Verification Results -->
                        <#if verificationResponse.fieldVerificationResult??>
                            <div class="result-section">
                                <h5 class="fw-bold mb-3">
                                    <i class="fas fa-list-check me-2 text-primary"></i>Field Verification Results
                                </h5>
                                <div class="field-result">
                                    <span class="field-name">English Name:</span>
                                    <div class="field-status">
                                        <#if verificationResponse.fieldVerificationResult.nameEn>
                                            <i class="fas fa-check success-icon"></i>
                                            <span class="text-success">Verified</span>
                                        <#else>
                                            <i class="fas fa-times error-icon"></i>
                                            <span class="text-danger">Not Verified</span>
                                        </#if>
                                    </div>
                                </div>
                                <div class="field-result">
                                    <span class="field-name">Bengali Name:</span>
                                    <div class="field-status">
                                        <#if verificationResponse.fieldVerificationResult.name>
                                            <i class="fas fa-check success-icon"></i>
                                            <span class="text-success">Verified</span>
                                        <#else>
                                            <i class="fas fa-times error-icon"></i>
                                            <span class="text-danger">Not Verified</span>
                                        </#if>
                                    </div>
                                </div>
                                <div class="field-result">
                                    <span class="field-name">Date of Birth:</span>
                                    <div class="field-status">
                                        <#if verificationResponse.fieldVerificationResult.dateOfBirth>
                                            <i class="fas fa-check success-icon"></i>
                                            <span class="text-success">Verified</span>
                                        <#else>
                                            <i class="fas fa-times error-icon"></i>
                                            <span class="text-danger">Not Verified</span>
                                        </#if>
                                    </div>
                                </div>
                                <div class="field-result">
                                    <span class="field-name">Father's Name:</span>
                                    <div class="field-status">
                                        <#if verificationResponse.fieldVerificationResult.father>
                                            <i class="fas fa-check success-icon"></i>
                                            <span class="text-success">Verified</span>
                                        <#else>
                                            <i class="fas fa-times error-icon"></i>
                                            <span class="text-danger">Not Verified</span>
                                        </#if>
                                    </div>
                                </div>
                                <div class="field-result">
                                    <span class="field-name">Mother's Name:</span>
                                    <div class="field-status">
                                        <#if verificationResponse.fieldVerificationResult.mother>
                                            <i class="fas fa-check success-icon"></i>
                                            <span class="text-success">Verified</span>
                                        <#else>
                                            <i class="fas fa-times error-icon"></i>
                                            <span class="text-danger">Not Verified</span>
                                        </#if>
                                    </div>
                                </div>
                            </div>
                        </#if>

                        <!-- Message -->
                        <#if verificationResponse.message?? && verificationResponse.message != "">
                            <div class="result-section">
                                <h5 class="fw-bold mb-3">
                                    <i class="fas fa-comment me-2 text-info"></i>Message
                                </h5>
                                <p class="mb-0">${verificationResponse.message}</p>
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
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
r