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
        .dashboard-card {
            background: rgba(255, 255, 255, 0.95);
            border-radius: 15px;
            box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
            backdrop-filter: blur(10px);
            border: 1px solid rgba(255, 255, 255, 0.2);
            transition: all 0.3s ease;
        }
        .dashboard-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
        }
        .feature-icon {
            width: 60px;
            height: 60px;
            border-radius: 15px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 24px;
            margin-bottom: 20px;
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
        .btn-success {
            background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
            border: none;
            border-radius: 10px;
            padding: 12px 30px;
            font-weight: 600;
            transition: all 0.3s ease;
        }
        .btn-success:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(40, 167, 69, 0.4);
        }
    </style>
</head>
<body>
    <!-- Navigation -->
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

    <!-- Main Content -->
    <div class="main-content">
        <div class="container">
            <div class="row justify-content-center">
                <div class="col-lg-10">
                    <div class="text-center mb-5">
                        <h1 class="text-white fw-bold mb-3">Welcome to Verification Dashboard</h1>
                        <p class="text-white-50 lead">Manage your verification processes and third-party integrations</p>
                    </div>

                    <div class="row g-4">
                        <!-- Third Party Login Card -->
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

                        <!-- Verification Card -->
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

                        <!-- Status Card -->
                        <div class="col-12">
                            <div class="dashboard-card p-4">
                                <div class="row align-items-center">
                                    <div class="col-md-8">
                                        <h5 class="fw-bold mb-2">
                                            <i class="fas fa-info-circle me-2 text-primary"></i>System Status
                                        </h5>
                                        <p class="text-muted mb-0">
                                            You are logged in as <strong>${username}</strong>. 
                                            To perform verifications, you need to login to the third-party service first.
                                        </p>
                                    </div>
                                    <div class="col-md-4 text-end">
                                        <div class="d-flex align-items-center justify-content-end">
                                            <div class="me-3">
                                                <small class="text-muted d-block">Status</small>
                                                <span class="badge bg-warning">Third Party Login Required</span>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        function checkThirdPartyLogin() {
            // For now, redirect to third-party login
            // In a real implementation, you might check if JWT exists first
            window.location.href = '/third-party-login?username=${username}';
        }
    </script>
</body>
</html>
