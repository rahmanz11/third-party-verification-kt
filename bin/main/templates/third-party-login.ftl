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
        }
        .login-card {
            background: rgba(255, 255, 255, 0.95);
            border-radius: 15px;
            box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
            backdrop-filter: blur(10px);
            border: 1px solid rgba(255, 255, 255, 0.2);
        }
        .form-control {
            border-radius: 10px;
            border: 2px solid #e9ecef;
            padding: 12px 15px;
            transition: all 0.3s ease;
        }
        .form-control:focus {
            border-color: #667eea;
            box-shadow: 0 0 0 0.2rem rgba(102, 126, 234, 0.25);
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
        .alert {
            border-radius: 10px;
            border: none;
        }
        .loading {
            display: none;
        }
        .spinner-border-sm {
            width: 1rem;
            height: 1rem;
        }
        .info-box {
            background: linear-gradient(135deg, #e3f2fd 0%, #bbdefb 100%);
            border-radius: 10px;
            padding: 20px;
            margin-bottom: 20px;
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
                <div class="col-md-8 col-lg-6">
                    <div class="login-card p-4">
                        <div class="text-center mb-4">
                            <i class="fas fa-key fa-3x text-primary mb-3"></i>
                            <h2 class="fw-bold text-dark">Third Party Login</h2>
                            <p class="text-muted">Enter your third-party verification service credentials</p>
                        </div>

                        <div class="info-box">
                            <div class="d-flex align-items-start">
                                <i class="fas fa-info-circle text-primary me-3 mt-1"></i>
                                <div>
                                    <h6 class="fw-bold mb-2">Important Information</h6>
                                    <p class="mb-0 small">
                                        These credentials will be used to authenticate with the third-party verification service. 
                                        The JWT token will be stored securely in memory for 12 hours.
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

                        <form id="thirdPartyLoginForm" method="POST" action="/third-party-login">
                            <input type="hidden" name="username" value="${username}">
                            
                            <div class="mb-3">
                                <label for="thirdPartyUsername" class="form-label">
                                    <i class="fas fa-user me-2"></i>Third Party Username
                                </label>
                                <input type="text" class="form-control" id="thirdPartyUsername" name="thirdPartyUsername" required>
                                <div class="form-text">
                                    <i class="fas fa-info-circle me-1"></i>
                                    Your username for the verification service
                                </div>
                            </div>
                            
                            <div class="mb-4">
                                <label for="thirdPartyPassword" class="form-label">
                                    <i class="fas fa-lock me-2"></i>Third Party Password
                                </label>
                                <input type="password" class="form-control" id="thirdPartyPassword" name="thirdPartyPassword" required>
                                <div class="form-text">
                                    <i class="fas fa-info-circle me-1"></i>
                                    Your password for the verification service
                                </div>
                            </div>
                            
                            <div class="d-grid gap-2">
                                <button type="submit" class="btn btn-primary" id="loginBtn">
                                    <span class="loading">
                                        <span class="spinner-border spinner-border-sm me-2" role="status"></span>
                                        Connecting to Third Party...
                                    </span>
                                    <span class="normal">
                                        <i class="fas fa-sign-in-alt me-2"></i>Connect to Third Party
                                    </span>
                                </button>
                                
                                <#if showVerificationLink?? && showVerificationLink>
                                    <a href="/verification-form?username=${username}&thirdPartyUsername=${thirdPartyUsername}" class="btn btn-success">
                                        <i class="fas fa-check-circle me-2"></i>Proceed to Verification
                                    </a>
                                </#if>
                                
                                <a href="/dashboard?username=${username}" class="btn btn-outline-secondary">
                                    <i class="fas fa-arrow-left me-2"></i>Back to Dashboard
                                </a>
                            </div>
                        </form>
                        
                        <div class="text-center mt-4">
                            <small class="text-muted">
                                <i class="fas fa-shield-alt me-1"></i>
                                Secure connection to third-party verification service
                            </small>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        document.getElementById('thirdPartyLoginForm').addEventListener('submit', function(e) {
            const btn = document.getElementById('loginBtn');
            const loading = btn.querySelector('.loading');
            const normal = btn.querySelector('.normal');
            
            // Show loading state
            loading.style.display = 'inline';
            normal.style.display = 'none';
            btn.disabled = true;
        });
    </script>
</body>
</html>
