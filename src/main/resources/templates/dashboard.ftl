<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${title}</title>
    <link href="/static/css/bootstrap.min.css" rel="stylesheet">
    <link href="/static/css/all.min.css" rel="stylesheet">
    <link href="/static/css/dashboard.css" rel="stylesheet">
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
                    <div class="session-status-card mb-4">
                        <div id="sessionStatus">
                            <div class="d-flex align-items-center">
                                <div class="spinner-border spinner-border-sm me-2" role="status"></div>
                                <span>Checking session status...</span>
                            </div>
                        </div>
                    </div>

                    <div class="text-center mb-4">
                        <h3 class="text-dark fw-bold mb-3">Welcome to Verification Dashboard</h3>
                        <p class="text-muted">Manage your verification processes and third-party integrations</p>
                    </div>

                    <div class="row g-4">
                        <div class="col-md-6">
                            <div class="dashboard-card p-4 h-100">
                                <div class="text-center">
                                    <div class="feature-icon bg-primary bg-gradient text-white mx-auto">
                                        <i class="fas fa-key"></i>
                                    </div>
                                    <h4 class="fw-bold mb-3">Third Party Login</h4>
                                    <p class="text-muted mb-4">
                                        Connect to the verification service with your third-party credentials to access verification features.
                                    </p>
                                    <a href="/third-party-login?username=${username}" class="btn btn-primary">
                                        <i class="fas fa-sign-in-alt me-2"></i>Login to Third Party
                                    </a>
                                </div>
                            </div>
                        </div>

                        <div class="col-md-6">
                            <div class="dashboard-card p-4 h-100">
                                <div class="text-center">
                                    <div class="feature-icon bg-success bg-gradient text-white mx-auto">
                                        <i class="fas fa-user-check"></i>
                                    </div>
                                    <h4 class="fw-bold mb-3">Person Verification</h4>
                                    <p class="text-muted mb-4">
                                        Verify person information using NID and demographic data. Complete verification forms with real-time validation.
                                    </p>
                                    <button class="btn btn-success" onclick="checkThirdPartyLogin()">
                                        <i class="fas fa-search me-2"></i>Start Verification
                                    </button>
                                </div>
                            </div>
                        </div>

                        <div class="col-md-6">
                            <div class="dashboard-card p-4 h-100">
                                <div class="text-center">
                                    <div class="feature-icon bg-warning bg-gradient text-white mx-auto">
                                        <i class="fas fa-key"></i>
                                    </div>
                                    <h4 class="fw-bold mb-3">Change Password</h4>
                                    <p class="text-muted mb-4">
                                        Update your third-party verification service password securely. 
                                        Session will be terminated after successful password change.
                                    </p>
                                    <button class="btn btn-warning" onclick="checkJwtForPasswordChange()">
                                        <i class="fas fa-key me-2"></i>Change Password
                                    </button>
                                </div>
                            </div>
                        </div>

                        <div class="col-md-6">
                            <div class="dashboard-card p-4 h-100">
                                <div class="text-center">
                                    <div class="feature-icon bg-info bg-gradient text-white mx-auto">
                                        <i class="fas fa-chart-line"></i>
                                    </div>
                                    <h4 class="fw-bold mb-3">Billing Report</h4>
                                    <p class="text-muted mb-4">
                                        Generate detailed billing reports and view usage statistics 
                                        for your third-party verification service.
                                    </p>
                                    <button class="btn btn-info" onclick="checkJwtForBilling()">
                                        <i class="fas fa-chart-bar me-2"></i>View Billing
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
    <script src="/static/js/dashboard.js"></script>
</body>
</html>
