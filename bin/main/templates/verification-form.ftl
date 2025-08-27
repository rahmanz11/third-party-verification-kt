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
        .form-card {
            background: rgba(255, 255, 255, 0.95);
            border-radius: 15px;
            box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
            backdrop-filter: blur(10px);
            border: 1px solid rgba(255, 255, 255, 0.2);
        }
        .form-control, .form-select {
            border-radius: 10px;
            border: 2px solid #e9ecef;
            padding: 12px 15px;
            transition: all 0.3s ease;
        }
        .form-control:focus, .form-select:focus {
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
        .section-header {
            background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
            border-radius: 10px;
            padding: 15px 20px;
            margin-bottom: 20px;
            border-left: 4px solid #667eea;
        }
        .required-field::after {
            content: " *";
            color: #dc3545;
        }
        .form-label {
            font-weight: 600;
            color: #495057;
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
                    <div class="form-card p-4">
                        <div class="text-center mb-4">
                            <i class="fas fa-user-check fa-3x text-primary mb-3"></i>
                            <h2 class="fw-bold text-dark">Person Verification Form</h2>
                            <p class="text-muted">Enter person details for verification</p>
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

                            <!-- Identification Section -->
                            <div class="section-header">
                                <h5 class="mb-0">
                                    <i class="fas fa-id-card me-2"></i>Identification Information
                                </h5>
                            </div>
                            
                            <div class="row mb-4">
                                <div class="col-md-6">
                                    <label for="nidType" class="form-label required-field">NID Type</label>
                                    <select class="form-select" id="nidType" name="nidType" required>
                                        <option value="">Select NID Type</option>
                                        <option value="10digit">10-digit NID</option>
                                        <option value="17digit">17-digit NID</option>
                                    </select>
                                </div>
                                <div class="col-md-6">
                                    <label for="nidValue" class="form-label required-field">NID Number</label>
                                    <input type="text" class="form-control" id="nidValue" name="nidValue" required>
                                </div>
                            </div>

                            <!-- Personal Information Section -->
                            <div class="section-header">
                                <h5 class="mb-0">
                                    <i class="fas fa-user me-2"></i>Personal Information
                                </h5>
                            </div>
                            
                            <div class="row mb-4">
                                <div class="col-md-6">
                                    <label for="nameEn" class="form-label required-field">English Name</label>
                                    <input type="text" class="form-control" id="nameEn" name="nameEn" required>
                                </div>
                                <div class="col-md-6">
                                    <label for="name" class="form-label required-field">Bengali Name</label>
                                    <input type="text" class="form-control" id="name" name="name" required>
                                </div>
                            </div>

                            <div class="row mb-4">
                                <div class="col-md-6">
                                    <label for="dateOfBirth" class="form-label required-field">Date of Birth</label>
                                    <input type="date" class="form-control" id="dateOfBirth" name="dateOfBirth" required>
                                </div>
                                <div class="col-md-6">
                                    <label for="spouse" class="form-label">Spouse's Name</label>
                                    <input type="text" class="form-control" id="spouse" name="spouse">
                                </div>
                            </div>

                            <div class="row mb-4">
                                <div class="col-md-6">
                                    <label for="father" class="form-label required-field">Father's Name</label>
                                    <input type="text" class="form-control" id="father" name="father" required>
                                </div>
                                <div class="col-md-6">
                                    <label for="mother" class="form-label required-field">Mother's Name</label>
                                    <input type="text" class="form-control" id="mother" name="mother" required>
                                </div>
                            </div>

                            <!-- Permanent Address Section -->
                            <div class="section-header">
                                <h5 class="mb-0">
                                    <i class="fas fa-home me-2"></i>Permanent Address
                                </h5>
                            </div>
                            
                            <div class="row mb-4">
                                <div class="col-md-4">
                                    <label for="permanentDivision" class="form-label required-field">Division</label>
                                    <input type="text" class="form-control" id="permanentDivision" name="permanentDivision" required>
                                </div>
                                <div class="col-md-4">
                                    <label for="permanentDistrict" class="form-label required-field">District</label>
                                    <input type="text" class="form-control" id="permanentDistrict" name="permanentDistrict" required>
                                </div>
                                <div class="col-md-4">
                                    <label for="permanentUpazila" class="form-label required-field">Upazila</label>
                                    <input type="text" class="form-control" id="permanentUpazila" name="permanentUpazila" required>
                                </div>
                            </div>

                            <!-- Present Address Section -->
                            <div class="section-header">
                                <h5 class="mb-0">
                                    <i class="fas fa-map-marker-alt me-2"></i>Present Address
                                </h5>
                            </div>
                            
                            <div class="row mb-4">
                                <div class="col-md-4">
                                    <label for="presentDivision" class="form-label required-field">Division</label>
                                    <input type="text" class="form-control" id="presentDivision" name="presentDivision" required>
                                </div>
                                <div class="col-md-4">
                                    <label for="presentDistrict" class="form-label required-field">District</label>
                                    <input type="text" class="form-control" id="presentDistrict" name="presentDistrict" required>
                                </div>
                                <div class="col-md-4">
                                    <label for="presentUpazila" class="form-label required-field">Upazila</label>
                                    <input type="text" class="form-control" id="presentUpazila" name="presentUpazila" required>
                                </div>
                            </div>

                            <!-- Submit Section -->
                            <div class="d-grid gap-2 d-md-flex justify-content-md-end">
                                <a href="/dashboard?username=${username}" class="btn btn-outline-secondary me-md-2">
                                    <i class="fas fa-arrow-left me-2"></i>Back to Dashboard
                                </a>
                                <button type="submit" class="btn btn-primary" id="submitBtn">
                                    <span class="loading">
                                        <span class="spinner-border spinner-border-sm me-2" role="status"></span>
                                        Verifying...
                                    </span>
                                    <span class="normal">
                                        <i class="fas fa-search me-2"></i>Verify Person
                                    </span>
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Handle NID type selection
        document.getElementById('nidType').addEventListener('change', function() {
            const nidValue = document.getElementById('nidValue');
            const selectedType = this.value;
            
            if (selectedType === '10digit') {
                nidValue.placeholder = 'Enter 10-digit NID';
                nidValue.maxLength = 10;
            } else if (selectedType === '17digit') {
                nidValue.placeholder = 'Enter 17-digit NID';
                nidValue.maxLength = 17;
            } else {
                nidValue.placeholder = 'Enter NID';
                nidValue.maxLength = '';
            }
        });

        // Handle form submission
        document.getElementById('verificationForm').addEventListener('submit', function(e) {
            const btn = document.getElementById('submitBtn');
            const loading = btn.querySelector('.loading');
            const normal = btn.querySelector('.normal');
            
            // Show loading state
            loading.style.display = 'inline';
            normal.style.display = 'none';
            btn.disabled = true;
        });

        // Auto-fill present address with permanent address
        document.getElementById('permanentDivision').addEventListener('change', function() {
            if (document.getElementById('presentDivision').value === '') {
                document.getElementById('presentDivision').value = this.value;
            }
        });
        
        document.getElementById('permanentDistrict').addEventListener('change', function() {
            if (document.getElementById('presentDistrict').value === '') {
                document.getElementById('presentDistrict').value = this.value;
            }
        });
        
        document.getElementById('permanentUpazila').addEventListener('change', function() {
            if (document.getElementById('presentUpazila').value === '') {
                document.getElementById('presentUpazila').value = this.value;
            }
        });
    </script>
</body>
</html>
