<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${title}</title>
    <link href="/static/css/bootstrap.min.css" rel="stylesheet">
    <link href="/static/css/all.min.css" rel="stylesheet">
    <link href="/static/css/change-password.css" rel="stylesheet">
</head>
<body>
    <nav class="navbar navbar-expand-lg navbar-light fixed-top">
        <div class="container">
            <a class="navbar-brand fw-bold" href="/dashboard?username=${username}">
                <i class="fas fa-shield-alt me-2"></i>Verification System
            </a>
            <div class="navbar-nav ms-auto">
                <span class="navbar-text me-3">
                    <i class="fas fa-user me-2"></i>Welcome, ${username}
                </span>
                <a class="btn btn-outline-danger btn-sm" href="/logout?username=${username}">
                    <i class="fas fa-sign-out-alt me-2"></i>Logout
                </a>
            </div>
        </div>
    </nav>

    <div class="container">
        <div class="row justify-content-center">
            <div class="col-md-6 col-lg-5">
                <div class="password-card p-4">
                    <div class="text-center mb-4">
                        <i class="fas fa-key fa-3x text-primary mb-3"></i>
                        <h2 class="fw-bold text-dark">Change Password</h2>
                        <p class="text-muted">Update your third-party verification service password</p>
                    </div>

                    <div class="info-box bg-light p-3 rounded mb-4">
                        <div class="d-flex align-items-start">
                            <i class="fas fa-info-circle text-primary me-3 mt-1"></i>
                            <div>
                                <h6 class="fw-bold mb-2">Important Information</h6>
                                <p class="mb-0 small">
                                    This will change your password for the third-party verification service. 
                                    After successful password change, your current session will be terminated.
                                </p>
                            </div>
                        </div>
                    </div>

                    <#if error??>
                        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                            <i class="fas fa-exclamation-triangle me-2"></i>
                            ${error}
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                    </#if>

                    <#if success??>
                        <div class="alert alert-success alert-dismissible fade show" role="alert">
                            <i class="fas fa-check-circle me-2"></i>
                            ${success}
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                    </#if>

                    <form id="changePasswordForm" method="POST" action="/change-password">
                        <input type="hidden" name="username" value="${username}">
                        <input type="hidden" name="thirdPartyUsername" value="${thirdPartyUsername}">
                        
                        <div class="mb-3">
                            <label for="currentPassword" class="form-label">
                                <i class="fas fa-lock me-2"></i>Current Password
                            </label>
                            <input type="password" class="form-control" id="currentPassword" name="currentPassword" required>
                        </div>
                        
                        <div class="mb-3">
                            <label for="newPassword" class="form-label">
                                <i class="fas fa-key me-2"></i>New Password
                            </label>
                            <input type="password" class="form-control" id="newPassword" name="newPassword" required>
                            <div class="password-strength" id="passwordStrength"></div>
                        </div>
                        
                        <div class="mb-4">
                            <label for="confirmPassword" class="form-label">
                                <i class="fas fa-check-circle me-2"></i>Confirm New Password
                            </label>
                            <input type="password" class="form-control" id="confirmPassword" name="confirmPassword" required>
                            <div class="form-text" id="passwordMatch"></div>
                        </div>
                        
                        <div class="d-grid gap-2">
                            <button type="submit" class="btn btn-primary" id="submitBtn">
                                <span class="loading">
                                    <span class="spinner-border spinner-border-sm me-2" role="status"></span>
                                    Changing Password...
                                </span>
                                <span class="normal">
                                    <i class="fas fa-save me-2"></i>Change Password
                                </span>
                            </button>
                            
                            <a href="/dashboard?username=${username}" class="btn btn-outline-secondary">
                                <i class="fas fa-arrow-left me-2"></i>Back to Dashboard
                            </a>
                        </div>
                    </form>
                    
                    <div class="text-center mt-4">
                        <small class="text-muted">
                            <i class="fas fa-shield-alt me-1"></i>
                            Secure password change for third-party verification service
                        </small>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="/static/js/bootstrap.bundle.min.js"></script>
    <script src="/static/js/change-password.js"></script>
</body>
</html>