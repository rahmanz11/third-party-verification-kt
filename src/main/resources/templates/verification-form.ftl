<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${title}</title>
    <link href="/static/css/bootstrap.min.css" rel="stylesheet">
    <link href="/static/css/all.min.css" rel="stylesheet">
    <link href="/static/css/verification-form.css" rel="stylesheet">
    <style>
        .json-panel {
            background: #ffffff;
            border-left: 2px solid #007bff;
            padding: 20px;
            height: 100vh;
            overflow-y: auto;
            position: fixed;
            right: 0;
            top: 0;
            width: 400px;
            z-index: 1040; /* above fixed navbar (1030) */
            box-shadow: -4px 0 20px rgba(0,0,0,0.15);
            transition: all 0.3s ease;
        }
        
        .json-content {
            margin-top: 40px;
        }
        
        .json-panel.collapsed {
            width: 60px;
            border-left: 2px solid #28a745;
        }
        
        .json-panel.collapsed .json-content {
            display: none;
        }
        
        .json-toggle {
            position: absolute;
            left: -24px;
            top: 50%;
            transform: translateY(-50%);
            background: #007bff;
            color: white;
            border: 2px solid #ffffff;
            border-radius: 50%;
            width: 42px;
            height: 42px;
            display: flex;
            align-items: center;
            justify-content: center;
            cursor: pointer;
            z-index: 2000; /* ensure above main content */
            box-shadow: 0 2px 8px rgba(0,0,0,0.2);
            transition: all 0.3s ease;
            font-size: 14px;
        }
        
        .json-toggle:hover {
            background: #0056b3;
            transform: translateY(-50%) scale(1.1);
            box-shadow: 0 4px 12px rgba(0,0,0,0.3);
        }
        
        .json-panel.collapsed .json-toggle {
            left: -20px;
            background: #28a745;
        }
        
        .json-panel.collapsed .json-toggle:hover {
            background: #1e7e34;
        }
        
        .json-section {
            margin-bottom: 20px;
            background: #f8f9fa;
            border-radius: 8px;
            padding: 15px;
            border: 1px solid #e9ecef;
        }
        
        .json-section h6 {
            color: #495057;
            margin-bottom: 10px;
            font-weight: 600;
        }
        
        .json-display {
            background: #ffffff;
            color: #495057;
            border-radius: 6px;
            padding: 12px;
            font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
            font-size: 12px;
            line-height: 1.4;
            overflow-x: auto;
            white-space: pre-wrap;
            word-wrap: break-word;
            border: 1px solid #dee2e6;
        }
        
        .json-display.empty {
            color: #6c757d;
            font-style: italic;
        }
        
        .main-content {
            margin-right: 400px;
            padding-top: 2px;
        }
        
        .main-content.expanded {
            margin-right: 50px;
        }
        
        /* JSON Confirmation Modal Styles */
        .json-preview-container {
            background: #f8f9fa;
            border: 1px solid #dee2e6;
            border-radius: 8px;
            padding: 15px;
            max-height: 400px;
            overflow-y: auto;
        }
        
        .json-preview {
            background: #ffffff;
            color: #495057;
            border-radius: 6px;
            padding: 15px;
            font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
            font-size: 12px;
            line-height: 1.4;
            margin: 0;
            white-space: pre-wrap;
            word-wrap: break-word;
            max-height: 300px;
            overflow-y: auto;
            border: 1px solid #dee2e6;
        }
        
        .json-preview::-webkit-scrollbar {
            width: 8px;
        }
        
        .json-preview::-webkit-scrollbar-track {
            background: #4a5568;
            border-radius: 4px;
        }
        
        .json-preview::-webkit-scrollbar-thumb {
            background: #718096;
            border-radius: 4px;
        }
        
        .json-preview::-webkit-scrollbar-thumb:hover {
            background: #a0aec0;
        }
        
        /* Button style adjustments */
        #resetBtn {
            background-color: transparent !important;
            border: 1px solid #ffc107 !important; /* Bootstrap warning color */
            color: #ffc107 !important;
            font-size: 0.75rem !important;
            padding: 4px 8px !important;
        }
        #resetBtn:hover, #resetBtn:focus {
            background-color: rgba(255, 193, 7, 0.1) !important;
            color: #e0a800 !important; /* darker warning */
            box-shadow: 0 0 0 0.15rem rgba(255, 193, 7, 0.2) !important;
        }
        
        #submitBtn {
            background-color: transparent !important;
            border: 1px solid #0d6efd !important; /* Bootstrap primary color */
            color: #0d6efd !important;
            font-size: 0.75rem !important;
            padding: 4px 8px !important;
        }
        #submitBtn:hover, #submitBtn:focus {
            background-color: rgba(13, 110, 253, 0.08) !important;
            color: #0b5ed7 !important; /* darker primary */
            box-shadow: 0 0 0 0.15rem rgba(13, 110, 253, 0.2) !important;
        }
        
        @media (max-width: 1200px) {
            .json-panel {
                display: none;
            }
            .main-content {
                margin-right: 0;
            }
        }
    </style>
</head>
<body data-username="${username}">
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

    <!-- JSON Panel -->
    <div class="json-panel" id="jsonPanel">
        <button class="json-toggle" id="jsonToggle" title="Toggle JSON Panel">
            <i class="fas fa-code"></i>
        </button>
        
        <div class="json-content">
            <h5 class="mb-3 text-center">
                <i class="fas fa-code me-2"></i>API Debug Panel
            </h5>
            
            <div class="json-section">
                <h6><i class="fas fa-paper-plane me-2"></i>Request JSON</h6>
                <div class="json-display empty" id="requestJson">No request data yet</div>
            </div>
            
                         <div class="json-section">
                 <h6><i class="fas fa-reply me-2"></i>Response JSON</h6>
                 <div class="json-display empty" id="responseJson">No response data yet</div>
             </div>
        </div>
    </div>

    <div class="main-content" id="mainContent">
        <div class="container">
            <div class="row justify-content-center">
                <div class="col-lg-10">
                    <div class="form-card p-1">
                        <div class="text-center mb-1">
                            <i class="fas fa-user-check text-primary mb-1"></i>
                            <h5 class="fw-bold text-dark mb-1">ব্যক্তি যাচাইকরণ ফর্ম</h5>
                            <p class="text-muted mb-0 small">যাচাইকরণের জন্য ব্যক্তির তথ্য প্রবেশ করান</p>
                        </div>

                        <#if error??>
                            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                                <i class="fas fa-exclamation-triangle me-2"></i>
                                ${error}
                                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                            </div>
                        </#if>

                        <form id="verificationForm" method="POST" action="/verify">
                            <input type="hidden" name="username" value="${username}">
    

                            <div class="section-header">
                                <h6 class="mb-0">
                                    <i class="fas fa-id-card me-1"></i>পরিচয়পত্রের তথ্য
                                </h6>
                            </div>
                            
                            <div class="row mb-1">
                                <div class="col-md-6">
                                    <label for="nidType" class="form-label required-field">এনআইডি ধরন</label>
                                    <select class="form-select" id="nidType" name="nidType" required>
                                        <option value="">এনআইডি ধরন নির্বাচন করুন</option>
                                        <option value="10digit">১০ অঙ্কের এনআইডি</option>
                                        <option value="17digit">১৭ অঙ্কের এনআইডি</option>
                                    </select>
                                </div>
                                <div class="col-md-6">
                                    <label for="nidValue" class="form-label required-field">এনআইডি নম্বর</label>
                                    <input type="text" class="form-control" id="nidValue" name="nidValue" required>
                                </div>
                            </div>

                            <div class="section-header">
                                <h6 class="mb-0">
                                    <i class="fas fa-user me-1"></i>ব্যক্তিগত তথ্য
                                </h6>
                            </div>
                            
                            <div class="row mb-1">
                                <div class="col-md-6">
                                    <label for="nameEn" class="form-label">ইংরেজি নাম</label>
                                    <input type="text" class="form-control" id="nameEn" name="nameEn">
                                </div>
                                <div class="col-md-6">
                                    <label for="name" class="form-label">বাংলা নাম</label>
                                    <input type="text" class="form-control" id="name" name="name">
                                </div>
                            </div>

                            <div class="row mb-1">
                                <div class="col-md-6">
                                    <label for="dateOfBirth" class="form-label required-field">জন্ম তারিখ</label>
                                    <input type="date" class="form-control" id="dateOfBirth" name="dateOfBirth" required>
                                </div>
                                <div class="col-md-6">
                                    <label for="spouse" class="form-label">স্বামী/স্ত্রীর নাম</label>
                                    <input type="text" class="form-control" id="spouse" name="spouse">
                                </div>
                            </div>

                            <div class="row mb-1">
                                <div class="col-md-6">
                                    <label for="father" class="form-label">পিতার নাম</label>
                                    <input type="text" class="form-control" id="father" name="father">
                                </div>
                                <div class="col-md-6">
                                    <label for="mother" class="form-label">মাতার নাম</label>
                                    <input type="text" class="form-control" id="mother" name="mother">
                                </div>
                            </div>

                            <div class="section-header">
                                <h6 class="mb-0">
                                    <i class="fas fa-home me-1"></i>স্থায়ী ঠিকানা
                                </h6>
                            </div>
                            
                            <div class="row mb-1">
                                <div class="col-md-4">
                                    <label for="permanentDivision" class="form-label">বিভাগ</label>
                                    <select class="form-select" id="permanentDivision" name="permanentDivision">
                                        <option value="">বিভাগ নির্বাচন করুন</option>
                                    </select>
                                </div>
                                <div class="col-md-4">
                                    <label for="permanentDistrict" class="form-label">জেলা</label>
                                    <select class="form-select" id="permanentDistrict" name="permanentDistrict" disabled>
                                        <option value="">জেলা নির্বাচন করুন</option>
                                    </select>
                                </div>
                                <div class="col-md-4">
                                    <label for="permanentUpazila" class="form-label">উপজেলা</label>
                                    <select class="form-select" id="permanentUpazila" name="permanentUpazila" disabled>
                                        <option value="">উপজেলা নির্বাচন করুন</option>
                                    </select>
                                </div>
                            </div>

                            <div class="section-header">
                                <h6 class="mb-0">
                                    <i class="fas fa-map-marker-alt me-1"></i>বর্তমান ঠিকানা
                                </h6>
                            </div>
                            
                            <div class="row mb-1">
                                <div class="col-md-4">
                                    <label for="presentDivision" class="form-label">বিভাগ</label>
                                    <select class="form-select" id="presentDivision" name="presentDivision">
                                        <option value="">বিভাগ নির্বাচন করুন</option>
                                    </select>
                                </div>
                                <div class="col-md-4">
                                    <label for="presentDistrict" class="form-label">জেলা</label>
                                    <select class="form-select" id="presentDistrict" name="presentDistrict" disabled>
                                        <option value="">জেলা নির্বাচন করুন</option>
                                    </select>
                                </div>
                                <div class="col-md-4">
                                    <label for="presentUpazila" class="form-label">উপজেলা</label>
                                    <select class="form-select" id="presentUpazila" name="presentUpazila" disabled>
                                        <option value="">উপজেলা নির্বাচন করুন</option>
                                    </select>
                                </div>
                            </div>

                            <div class="d-grid gap-1 d-md-flex justify-content-md-end">
                                <a href="/dashboard?username=${username}" class="btn btn-outline-secondary btn-sm me-md-1">
                                    <i class="fas fa-arrow-left me-1"></i>ড্যাশবোর্ডে ফিরে যান
                                </a>
                                <button type="button" class="btn btn-outline-warning btn-sm me-md-1" id="resetBtn">
                                    <i class="fas fa-undo me-1"></i>ফর্ম রিসেট করুন
                                </button>
                                <button type="submit" class="btn btn-primary btn-sm" id="submitBtn">
                                    <span class="loading">
                                        <span class="spinner-border spinner-border-sm me-1" role="status"></span>
                                        যাচাই করা হচ্ছে...
                                    </span>
                                    <span class="normal">
                                        <i class="fas fa-search me-1"></i>ব্যক্তি যাচাই করুন
                                    </span>
                                </button>
                                <a href="/verification-result?username=${username}" class="btn btn-outline-success btn-sm ms-md-1">
                                    <i class="fas fa-clipboard-check me-1"></i>ফলাফল দেখুন
                                </a>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="modal fade" id="thirdPartyLoginModal" tabindex="-1" aria-labelledby="thirdPartyLoginModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="thirdPartyLoginModalLabel">
                        <i class="fas fa-key text-primary me-2"></i>
                        Third Party Login Required
                    </h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <div class="text-center mb-4">
                        <i class="fas fa-shield-alt fa-2x text-primary mb-3"></i>
                        <p class="text-muted">Please login to the third-party verification service to continue.</p>
                    </div>

                    <div id="loginError" class="alert alert-danger" style="display: none;">
                        <i class="fas fa-exclamation-triangle me-2"></i>
                        <span id="loginErrorMessage"></span>
                    </div>

                    <form id="modalLoginForm">
                        <input type="hidden" name="username" value="${username}">
                        
                        <div class="mb-3">
                            <label for="modalThirdPartyUsername" class="form-label">
                                <i class="fas fa-user me-2"></i>Third Party Username
                            </label>
                            <input type="text" class="form-control" id="modalThirdPartyUsername" name="thirdPartyUsername" required>
                        </div>
                        
                        <div class="mb-4">
                            <label for="modalThirdPartyPassword" class="form-label">
                                <i class="fas fa-lock me-2"></i>Third Party Password
                            </label>
                            <input type="password" class="form-control" id="modalThirdPartyPassword" name="thirdPartyPassword" required>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="button" class="btn btn-primary" id="modalLoginBtn">
                        <span class="loading">
                            <span class="spinner-border spinner-border-sm me-2" role="status"></span>
                            Connecting...
                        </span>
                        <span class="normal">
                            <i class="fas fa-sign-in-alt me-2"></i>Login
                        </span>
                    </button>
                </div>
            </div>
        </div>
    </div>

    <!-- JSON Confirmation Modal -->
    <div class="modal fade" id="jsonConfirmationModal" tabindex="-1" aria-labelledby="jsonConfirmationModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header" style="background-color: #fffacd; border-bottom: 1px solid #f0e68c;">
                    <h5 class="modal-title" id="jsonConfirmationModalLabel">
                        <i class="fas fa-code text-dark me-2"></i>
                        যাচাইকরণ অনুরোধ নিশ্চিত করুন
                    </h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body" style="background-color: #f0fff0;">
                    <div class="text-center mb-4">
                        <i class="fas fa-paper-plane fa-2x text-success mb-3"></i>
                        <p class="text-muted">জমা দেওয়ার আগে যাচাইকরণ অনুরোধের ডেটা পর্যালোচনা করুন:</p>
                    </div>

                    <div class="mb-3">
                        <label class="form-label fw-bold">
                            <i class="fas fa-code me-2"></i>অনুরোধের বিষয়বস্তু (JSON):
                        </label>
                        <div class="json-preview-container">
                            <pre id="jsonPreview" class="json-preview"></pre>
                        </div>
                    </div>

                    <div class="alert alert-info">
                        <i class="fas fa-info-circle me-2"></i>
                        <strong>নোট:</strong> এই অনুরোধটি তৃতীয় পক্ষের যাচাইকরণ সেবায় পাঠানো হবে। 
                        এগিয়ে যাওয়ার আগে সমস্ত তথ্য সঠিক কিনা নিশ্চিত করুন।
                    </div>
                </div>
                <div class="modal-footer" style="background-color: #f0fff0; border-top: 1px solid #90ee90;">
                    <button type="button" class="btn btn-secondary btn-sm" data-bs-dismiss="modal">
                        <i class="fas fa-times me-1"></i>বাতিল
                    </button>
                    <button type="button" class="btn btn-primary btn-sm" id="confirmSubmitBtn">
                        <i class="fas fa-paper-plane me-1"></i>যাচাইকরণ অনুরোধ জমা দিন
                    </button>
                </div>
            </div>
        </div>
    </div>



    <script src="/static/js/bootstrap.bundle.min.js"></script>
    <script src="/static/js/verification-form.js"></script>
</body>
</html>
