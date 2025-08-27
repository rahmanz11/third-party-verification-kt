<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${title}</title>
    <link href="/static/css/bootstrap.min.css" rel="stylesheet">
    <link href="/static/css/all.min.css" rel="stylesheet">
    <link href="/static/css/verification-form.css" rel="stylesheet">
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

    <div class="main-content">
        <div class="container">
            <div class="row justify-content-center">
                <div class="col-lg-10">
                    <div class="form-card p-4">
                        <div class="text-center mb-4">
                            <i class="fas fa-user-check fa-3x text-primary mb-3"></i>
                            <h2 class="fw-bold text-dark">ব্যক্তি যাচাইকরণ ফর্ম</h2>
                            <p class="text-muted">যাচাইকরণের জন্য ব্যক্তির তথ্য প্রবেশ করান</p>
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
                            <input type="hidden" name="thirdPartyUsername" value="${thirdPartyUsername}">

                            <div class="section-header">
                                <h5 class="mb-0">
                                    <i class="fas fa-id-card me-2"></i>পরিচয়পত্রের তথ্য
                                </h5>
                            </div>
                            
                            <div class="row mb-4">
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
                                <h5 class="mb-0">
                                    <i class="fas fa-user me-2"></i>ব্যক্তিগত তথ্য
                                </h5>
                            </div>
                            
                            <div class="row mb-4">
                                <div class="col-md-6">
                                    <label for="nameEn" class="form-label required-field">ইংরেজি নাম</label>
                                    <input type="text" class="form-control" id="nameEn" name="nameEn" required>
                                </div>
                                <div class="col-md-6">
                                    <label for="name" class="form-label required-field">বাংলা নাম</label>
                                    <input type="text" class="form-control" id="name" name="name" required>
                                </div>
                            </div>

                            <div class="row mb-4">
                                <div class="col-md-6">
                                    <label for="dateOfBirth" class="form-label required-field">জন্ম তারিখ</label>
                                    <input type="date" class="form-control" id="dateOfBirth" name="dateOfBirth" required>
                                </div>
                                <div class="col-md-6">
                                    <label for="spouse" class="form-label">স্বামী/স্ত্রীর নাম</label>
                                    <input type="text" class="form-control" id="spouse" name="spouse">
                                </div>
                            </div>

                            <div class="row mb-4">
                                <div class="col-md-6">
                                    <label for="father" class="form-label required-field">পিতার নাম</label>
                                    <input type="text" class="form-control" id="father" name="father" required>
                                </div>
                                <div class="col-md-6">
                                    <label for="mother" class="form-label required-field">মাতার নাম</label>
                                    <input type="text" class="form-control" id="mother" name="mother" required>
                                </div>
                            </div>

                            <div class="section-header">
                                <h5 class="mb-0">
                                    <i class="fas fa-home me-2"></i>স্থায়ী ঠিকানা
                                </h5>
                            </div>
                            
                            <div class="row mb-4">
                                <div class="col-md-4">
                                    <label for="permanentDivision" class="form-label required-field">বিভাগ</label>
                                    <select class="form-select" id="permanentDivision" name="permanentDivision" required>
                                        <option value="">বিভাগ নির্বাচন করুন</option>
                                    </select>
                                </div>
                                <div class="col-md-4">
                                    <label for="permanentDistrict" class="form-label required-field">জেলা</label>
                                    <select class="form-select" id="permanentDistrict" name="permanentDistrict" required disabled>
                                        <option value="">জেলা নির্বাচন করুন</option>
                                    </select>
                                </div>
                                <div class="col-md-4">
                                    <label for="permanentUpazila" class="form-label required-field">উপজেলা</label>
                                    <select class="form-select" id="permanentUpazila" name="permanentUpazila" required disabled>
                                        <option value="">উপজেলা নির্বাচন করুন</option>
                                    </select>
                                </div>
                            </div>

                            <div class="section-header">
                                <h5 class="mb-0">
                                    <i class="fas fa-map-marker-alt me-2"></i>বর্তমান ঠিকানা
                                </h5>
                            </div>
                            
                            <div class="row mb-4">
                                <div class="col-md-4">
                                    <label for="presentDivision" class="form-label required-field">বিভাগ</label>
                                    <select class="form-select" id="presentDivision" name="presentDivision" required>
                                        <option value="">বিভাগ নির্বাচন করুন</option>
                                    </select>
                                </div>
                                <div class="col-md-4">
                                    <label for="presentDistrict" class="form-label required-field">জেলা</label>
                                    <select class="form-select" id="presentDistrict" name="presentDistrict" required disabled>
                                        <option value="">জেলা নির্বাচন করুন</option>
                                    </select>
                                </div>
                                <div class="col-md-4">
                                    <label for="presentUpazila" class="form-label required-field">উপজেলা</label>
                                    <select class="form-select" id="presentUpazila" name="presentUpazila" required disabled>
                                        <option value="">উপজেলা নির্বাচন করুন</option>
                                    </select>
                                </div>
                            </div>

                            <div class="d-grid gap-2 d-md-flex justify-content-md-end">
                                <a href="/dashboard?username=${username}" class="btn btn-outline-secondary me-md-2">
                                    <i class="fas fa-arrow-left me-2"></i>ড্যাশবোর্ডে ফিরে যান
                                </a>
                                <button type="submit" class="btn btn-primary" id="submitBtn">
                                    <span class="loading">
                                        <span class="spinner-border spinner-border-sm me-2" role="status"></span>
                                        যাচাই করা হচ্ছে...
                                    </span>
                                    <span class="normal">
                                        <i class="fas fa-search me-2"></i>ব্যক্তি যাচাই করুন
                                    </span>
                                </button>
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

    <script src="/static/js/bootstrap.bundle.min.js"></script>
    <script src="/static/js/verification-form.js"></script>
</body>
</html>
