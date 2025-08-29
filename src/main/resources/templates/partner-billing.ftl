<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${title}</title>
    <link href="/static/css/bootstrap.min.css" rel="stylesheet">
    <link href="/static/css/all.min.css" rel="stylesheet">
    <link href="/static/css/partner-billing.css" rel="stylesheet">
    <style>
        .success-rate {
            font-size: 1.2rem;
            font-weight: bold;
            color: #28a745;
        }
        .failure-rate {
            font-size: 1.2rem;
            font-weight: bold;
            color: #dc3545;
        }
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
                    <i class="fas fa-user me-2"></i>Welcome, ${username}
                </span>
                <a class="btn btn-outline-danger btn-sm" href="/logout?username=${username}&thirdPartyUsername=${thirdPartyUsername!username}">
                    <i class="fas fa-sign-out-alt me-2"></i>Logout
                </a>
            </div>
        </div>
    </nav>

    <div class="main-content">
        <div class="container">
            <div class="row">
                <div class="col-12">
                    <div class="d-flex justify-content-between align-items-center mb-4">
                        <div>
                            <h1 class="fw-bold text-dark mb-2">
                                <i class="fas fa-chart-line me-2 text-primary"></i>Partner Billing Report
                            </h1>
                            <p class="text-muted mb-0">View detailed billing information and usage statistics</p>
                        </div>
                        <a href="/dashboard?username=${username}" class="btn btn-outline-secondary">
                            <i class="fas fa-arrow-left me-2"></i>Back to Dashboard
                        </a>
                    </div>
                </div>
            </div>

            <div class="row">
                <div class="col-12">
                    <div class="billing-card p-4">
                        <div class="row mb-4">
                            <div class="col-md-8">
                                <h5 class="fw-bold mb-3">
                                    <i class="fas fa-calendar-alt me-2 text-primary"></i>Generate Billing Report
                                </h5>
                                <p class="text-muted mb-0">Select date range to generate billing report</p>
                            </div>
                        </div>

                        <form id="billingForm" method="POST" action="/partner-billing">
                            <input type="hidden" name="username" value="${username}">
                            <input type="hidden" name="thirdPartyUsername" value="${thirdPartyUsername}">
                            
                            <div class="row">
                                <div class="col-md-4">
                                    <div class="mb-3">
                                        <label for="startDate" class="form-label">
                                            <i class="fas fa-calendar me-2"></i>Start Date
                                        </label>
                                        <input type="date" class="form-control" id="startDate" name="startDate" required>
                                    </div>
                                </div>
                                <div class="col-md-4">
                                    <div class="mb-3">
                                        <label for="endDate" class="form-label">
                                            <i class="fas fa-calendar me-2"></i>End Date
                                        </label>
                                        <input type="date" class="form-control" id="endDate" name="endDate" required>
                                    </div>
                                </div>
                                <div class="col-md-4">
                                    <div class="mb-3">
                                        <label class="form-label">&nbsp;</label>
                                        <button type="submit" class="btn btn-primary w-100" id="generateBtn">
                                            <span class="loading">
                                                <span class="spinner-border spinner-border-sm me-2" role="status"></span>
                                                Generating Report...
                                            </span>
                                            <span class="normal">
                                                <i class="fas fa-chart-bar me-2"></i>Generate Report
                                            </span>
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>

            <#if error??>
                <div class="row">
                    <div class="col-12">
                        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                            <i class="fas fa-exclamation-triangle me-2"></i>
                            ${error}
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                    </div>
                </div>
            </#if>

            <#if billingResponse?? && billingResponse.success??>
                <div class="row">
                    <div class="col-12">
                        <div class="billing-summary">
                            <div class="row">
                                <div class="col-md-6">
                                    <h6 class="fw-bold mb-2">
                                        <i class="fas fa-info-circle me-2 text-primary"></i>Report Information
                                    </h6>
                                    <p class="mb-1"><strong>Generated Time:</strong> ${billingResponse.success.data.generatedTime}</p>
                                    <p class="mb-1"><strong>Partner ID:</strong> ${billingResponse.success.data.partnerId}</p>
                                </div>
                                <div class="col-md-6 text-end">
                                    <h6 class="fw-bold mb-2">
                                        <i class="fas fa-percentage me-2 text-primary"></i>Success Rate
                                    </h6>
                                    <#if billingResponse.success.data.totalCallCount gt 0>
                                        <#assign successRate = (billingResponse.success.data.totalSuccessCount / billingResponse.success.data.totalCallCount * 100)?round>
                                        <#assign failureRate = (billingResponse.success.data.totalFailedCount / billingResponse.success.data.totalCallCount * 100)?round>
                                        <p class="mb-1 success-rate">${successRate}% Success Rate</p>
                                        <p class="mb-0 failure-rate">${failureRate}% Failure Rate</p>
                                    <#else>
                                        <p class="mb-0 text-muted">No calls made in this period</p>
                                    </#if>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="row mb-4">
                    <div class="col-md-3">
                        <div class="stats-card">
                            <div class="stats-number">${billingResponse.success.data.totalCallCount}</div>
                            <div class="stats-label">Total Calls</div>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="stats-card success">
                            <div class="stats-number">${billingResponse.success.data.totalSuccessCount}</div>
                            <div class="stats-label">Successful Calls</div>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="stats-card danger">
                            <div class="stats-number">${billingResponse.success.data.totalFailedCount}</div>
                            <div class="stats-label">Failed Calls</div>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="stats-card info">
                            <div class="stats-number">$${billingResponse.success.data.totalBill}</div>
                            <div class="stats-label">Total Bill</div>
                        </div>
                    </div>
                </div>

                <div class="row">
                    <div class="col-12">
                        <div class="billing-card p-4">
                            <h5 class="fw-bold mb-3">
                                <i class="fas fa-table me-2 text-primary"></i>Detailed Billing Breakdown
                            </h5>
                            
                            <div class="table-responsive">
                                <table class="table table-hover">
                                    <thead>
                                        <tr>
                                            <th>Username</th>
                                            <th>Call Count</th>
                                            <th>Success Count</th>
                                            <th>Failed Count</th>
                                            <th>Processing Count</th>
                                            <th>Bill Amount</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <#list billingResponse.success.data.partnerBillingBeanList as billing>
                                            <tr>
                                                <td>
                                                    <strong>${billing.username}</strong>
                                                    <br>
                                                    <small class="text-muted">${billing.partnerName}</small>
                                                </td>
                                                <td>
                                                    <span class="badge bg-primary">${billing.callCount}</span>
                                                </td>
                                                <td>
                                                    <span class="badge bg-success">${billing.successCount}</span>
                                                </td>
                                                <td>
                                                    <span class="badge bg-danger">${billing.failedCount}</span>
                                                </td>
                                                <td>
                                                    <span class="badge bg-warning">${billing.processingCount}</span>
                                                </td>
                                                <td>
                                                    <strong class="text-primary">$${billing.bill}</strong>
                                                </td>
                                            </tr>
                                        </#list>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
            </#if>
        </div>
    </div>

    <script src="/static/js/bootstrap.bundle.min.js"></script>
    <script src="/static/js/partner-billing.js"></script>
</body>
</html>
