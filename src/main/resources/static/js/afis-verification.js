/**
 * AFIS Verification JavaScript
 * Handles fingerprint verification, upload, and result checking
 */

// Global variables
let currentJobId = null;
let currentResultCheckUrl = null;

// Initialize form event listeners
document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('afisVerificationForm');
    form.addEventListener('submit', handleAfisVerification);
});

/**
 * Handle AFIS verification form submission
 * @param {Event} e - Form submit event
 */
async function handleAfisVerification(e) {
    e.preventDefault();
    
    // Get form data
    const nid10Digit = document.getElementById('nid10Digit').value.trim();
    const nid17Digit = document.getElementById('nid17Digit').value.trim();
    const dateOfBirth = document.getElementById('dateOfBirth').value;
    
    // Get selected fingers
    const selectedFingers = [];
    document.querySelectorAll('.finger-checkbox:checked').forEach(checkbox => {
        selectedFingers.push(checkbox.value);
    });
    
    // Validate form data
    if (!dateOfBirth) {
        alert('Please select a date of birth');
        return;
    }
    
    if (selectedFingers.length === 0) {
        alert('Please select at least one finger for verification');
        return;
    }
    
    if (!nid10Digit && !nid17Digit) {
        alert('Please provide either 10-digit or 17-digit NID');
        return;
    }
    
    // Prepare request data
    const requestData = {
        dateOfBirth: dateOfBirth,
        fingerEnums: selectedFingers
    };
    
    if (nid10Digit) {
        requestData.nid10Digit = nid10Digit;
    }
    if (nid17Digit) {
        requestData.nid17Digit = nid17Digit;
    }
    
    try {
        // Get JWT token from session
        const jwtResponse = await fetch('/api/jwt-status');
        const jwtData = await jwtResponse.json();
        
        if (!jwtData.jwt) {
            alert('No JWT token found. Please login first.');
            return;
        }
        
        // Call AFIS verification API
        const response = await fetch('/partner-service/rest/afis/verification-secured', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${jwtData.jwt}`
            },
            body: JSON.stringify(requestData)
        });
        
        const result = await response.json();
        
        if (response.ok) {
            displayVerificationResult(result);
        } else {
            displayError(result.error || 'Verification failed');
        }
        
    } catch (error) {
        console.error('Error:', error);
        alert('An error occurred during verification: ' + error.message);
    }
}

/**
 * Display the verification result and show upload options
 * @param {Object} result - The verification response from the API
 */
function displayVerificationResult(result) {
    const resultSection = document.getElementById('resultSection');
    const resultContent = document.getElementById('resultContent');
    
    if (result.status === 'ACCEPTED' && result.success) {
        // Store job ID and result check URL for later use
        currentJobId = extractJobIdFromUrl(result.success.data.resultCheckApi);
        currentResultCheckUrl = result.success.data.resultCheckApi;
        
        let html = `
            <div class="alert alert-success">
                <h6>Verification Accepted!</h6>
                <p><strong>Status:</strong> ${result.status}</p>
                <p><strong>Status Code:</strong> ${result.statusCode}</p>
                <p><strong>Job ID:</strong> ${currentJobId}</p>
            </div>
        `;
        
        // Display fingerprint upload URLs
        if (result.success.data.fingerUploadUrls && result.success.data.fingerUploadUrls.length > 0) {
            html += `
                <div class="mt-3">
                    <h6>Fingerprint Upload URLs:</h6>
                    <div class="table-responsive">
                        <table class="table table-sm">
                            <thead>
                                <tr>
                                    <th>Finger</th>
                                    <th>Upload URL</th>
                                    <th>Action</th>
                                </tr>
                            </thead>
                            <tbody>
            `;
            
            result.success.data.fingerUploadUrls.forEach(finger => {
                html += `
                    <tr>
                        <td><strong>${finger.finger}</strong></td>
                        <td><small class="text-muted">${finger.url.substring(0, 80)}...</small></td>
                        <td>
                            <button class="btn btn-sm btn-outline-primary" onclick="uploadFingerprint('${finger.finger}', '${finger.url}')">
                                Upload
                            </button>
                        </td>
                    </tr>
                `;
            });
            
            html += `
                            </tbody>
                        </table>
                    </div>
                </div>
            `;
        }
        
        resultContent.innerHTML = html;
        resultSection.style.display = 'block';
        
        // Show upload section
        document.getElementById('uploadSection').style.display = 'block';
        
    } else if (result.error) {
        displayError(result.error.message || 'Verification failed');
    } else {
        displayError('Unexpected response format');
    }
}

/**
 * Display error message in the result section
 * @param {string} message - Error message to display
 */
function displayError(message) {
    const resultSection = document.getElementById('resultSection');
    const resultContent = document.getElementById('resultContent');
    
    resultContent.innerHTML = `
        <div class="alert alert-danger">
            <h6>Error</h6>
            <p>${message}</p>
        </div>
    `;
    
    resultSection.style.display = 'block';
    document.getElementById('uploadSection').style.display = 'none';
}

/**
 * Handle fingerprint file upload for a specific finger
 * @param {string} fingerName - Name of the finger (e.g., "RIGHT_THUMB")
 * @param {string} uploadUrl - Pre-signed URL for uploading the fingerprint
 */
async function uploadFingerprint(fingerName, uploadUrl) {
    try {
        // Create a file input for the user to select a fingerprint file
        const fileInput = document.createElement('input');
        fileInput.type = 'file';
        fileInput.accept = '.wsq,.bin,.dat';
        fileInput.style.display = 'none';
        
        fileInput.addEventListener('change', async (e) => {
            const file = e.target.files[0];
            if (!file) return;
            
            // Show upload progress
            const uploadContent = document.getElementById('uploadContent');
            uploadContent.innerHTML = `
                <div class="alert alert-info">
                    <i class="fas fa-spinner fa-spin"></i> Uploading ${fingerName} fingerprint...
                </div>
            `;
            
            try {
                // Read file as binary data
                const arrayBuffer = await file.arrayBuffer();
                const byteArray = new Uint8Array(arrayBuffer);
                
                // Upload to the provided URL
                const response = await fetch(uploadUrl, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/octet-stream'
                    },
                    body: byteArray
                });
                
                if (response.ok) {
                    uploadContent.innerHTML = `
                        <div class="alert alert-success">
                            <i class="fas fa-check"></i> ${fingerName} fingerprint uploaded successfully!
                        </div>
                    `;
                } else {
                    uploadContent.innerHTML = `
                        <div class="alert alert-danger">
                            <i class="fas fa-times"></i> Failed to upload ${fingerName} fingerprint. Status: ${response.status}
                        </div>
                    `;
                }
                
            } catch (error) {
                console.error('Upload error:', error);
                uploadContent.innerHTML = `
                    <div class="alert alert-danger">
                        <i class="fas fa-times"></i> Error uploading ${fingerName} fingerprint: ${error.message}
                    </div>
                `;
            }
        });
        
        // Trigger file selection
        fileInput.click();
        
    } catch (error) {
        console.error('Error:', error);
        alert('Error preparing fingerprint upload: ' + error.message);
    }
}

/**
 * Check the result of the AFIS verification job
 */
async function checkResult() {
    if (!currentResultCheckUrl) {
        alert('No result check URL available. Please complete verification first.');
        return;
    }
    
    try {
        // Get JWT token from session
        const jwtResponse = await fetch('/api/jwt-status');
        const jwtData = await jwtResponse.json();
        
        if (!jwtData.jwt) {
            alert('No JWT token found. Please login first.');
            return;
        }
        
        // Call result check API
        const response = await fetch(`/partner-service/rest/afis/verification/result/${currentJobId}`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${jwtData.jwt}`
            }
        });
        
        const result = await response.json();
        
        if (response.ok) {
            displayResultCheckResult(result);
        } else {
            alert('Failed to check result: ' + (result.error || 'Unknown error'));
        }
        
    } catch (error) {
        console.error('Error:', error);
        alert('An error occurred while checking result: ' + error.message);
    }
}

/**
 * Display the result of the AFIS verification result check
 * @param {Object} result - The result check response from the API
 */
function displayResultCheckResult(result) {
    const resultContent = document.getElementById('resultContent');
    
    if (result.status === 'OK' && result.success) {
        const data = result.success.data;
        let html = `
            <div class="alert alert-info">
                <h6>Result Check Response</h6>
                <p><strong>Job ID:</strong> ${data.jobId}</p>
                <p><strong>Result:</strong> ${data.result}</p>
        `;
        
        if (data.result === 'MATCH FOUND' && data.verificationResponse) {
            html += `
                <p><strong>Voter ID:</strong> ${data.verificationResponse.voterInfo.id}</p>
                <p><strong>National ID:</strong> ${data.verificationResponse.voterInfo.nationalId}</p>
            `;
        } else if (data.result === 'NO MATCH FOUND' && data.errorReason) {
            html += `<p><strong>Error Reason:</strong> ${data.errorReason}</p>`;
        }
        
        html += `</div>`;
        
        resultContent.innerHTML = html;
    } else if (result.error) {
        resultContent.innerHTML = `
            <div class="alert alert-danger">
                <h6>Error</h6>
                <p>${result.error.message || 'Unknown error'}</p>
            </div>
        `;
    }
}

/**
 * Extract job ID from the result check API URL
 * @param {string} url - The result check API URL
 * @returns {string|null} The extracted job ID or null if not found
 */
function extractJobIdFromUrl(url) {
    const match = url.match(/\/result\/([^\/]+)$/);
    return match ? match[1] : null;
}
