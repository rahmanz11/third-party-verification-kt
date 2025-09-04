/**
 * Fingerprint Capture JavaScript
 * Handles fingerprint device connection, capture operations, and AFIS integration
 */

// Global variables
let deviceConnected = false;
let currentDeviceInfo = null;
let captureResults = [];
let activeCaptures = new Map();
let websocket = null;

// Initialize page
document.addEventListener('DOMContentLoaded', function() {
    initializePage();
    setupEventListeners();
    checkDeviceStatus();
    
    // Add periodic status check to debug connection issues
    setInterval(checkDeviceStatus, 10000); // Check every 10 seconds (reduced frequency)
    
    // Add periodic device status refresh to prevent disconnection
    setInterval(refreshDeviceStatus, 15000); // Refresh every 15 seconds (reduced frequency)
    
    // Lock device status when page loads to ensure stability
    setTimeout(lockDeviceStatus, 2000); // Lock after 2 seconds
});

/**
 * Initialize the page
 */
function initializePage() {
    // Set default date to today
    const today = new Date().toISOString().split('T')[0];
    document.getElementById('dateOfBirth').value = today;
    
    // Initialize quality threshold display
    updateQualityThresholdDisplay();
    
    // Clear old results on page load to start fresh
    clearOldResults();
}

/**
 * Setup event listeners
 */
function setupEventListeners() {
    // Quality threshold slider
    const qualitySlider = document.getElementById('qualityThreshold');
    qualitySlider.addEventListener('input', function() {
        updateQualityThresholdDisplay();
    });
    
    // Finger selection checkboxes
    document.querySelectorAll('.finger-checkbox').forEach(checkbox => {
        checkbox.addEventListener('change', function() {
            updateStartCaptureButton();
        });
    });
    
    // Form validation
    document.getElementById('nid10Digit').addEventListener('input', validateAfisForm);
    document.getElementById('nid17Digit').addEventListener('input', validateAfisForm);
    document.getElementById('dateOfBirth').addEventListener('change', validateAfisForm);
}

/**
 * Update quality threshold display
 */
function updateQualityThresholdDisplay() {
    const slider = document.getElementById('qualityThreshold');
    const display = document.getElementById('qualityValue');
    
    // Defensive checks to prevent null reference errors
    if (!slider || !display) {
        console.error('Quality threshold elements not found:', {
            slider: !!slider,
            display: !!display
        });
        return;
    }
    
    display.textContent = slider.value;
}

/**
 * Update start capture button state
 */
function updateStartCaptureButton() {
    const selectedFingers = getSelectedFingers();
    const startBtn = document.getElementById('captureBtn');
    
    // Defensive check to prevent null reference errors
    if (!startBtn) {
        console.error('Start capture button not found');
        return;
    }
    
    startBtn.disabled = !deviceConnected || selectedFingers.length === 0;
}

/**
 * Get selected fingers
 */
function getSelectedFingers() {
    const selected = [];
    document.querySelectorAll('.finger-checkbox:checked').forEach(checkbox => {
        selected.push(checkbox.value);
    });
    return selected;
}

/**
 * Check device status
 */
async function checkDeviceStatus() {
    try {
        console.log('Checking device status...');
        const response = await fetch('/fingerprint/device/status');
        
        if (!response.ok) {
            console.error('Device status request failed:', response.status, response.statusText);
            updateDeviceStatus({
                connected: false,
                error: `HTTP ${response.status}: ${response.statusText}`
            });
            return;
        }
        
        const status = await response.json();
        console.log('Device status received:', status);
        
        // CRITICAL: Ensure we have the expected data structure
        if (typeof status === 'object' && status !== null) {
            if (typeof status.connected === 'boolean') {
                console.log('Device connected status:', status.connected);
                
                // If device was previously connected but now shows disconnected,
                // try to refresh the status once
                if (!status.connected && deviceConnected) {
                    console.warn('Device status inconsistency detected, attempting refresh...');
                    try {
                        await refreshDeviceStatus();
                        // Check status again after refresh
                        const refreshResponse = await fetch('/fingerprint/device/status');
                        if (refreshResponse.ok) {
                            const refreshStatus = await refreshResponse.json();
                            console.log('Device status after refresh:', refreshStatus);
                            if (refreshStatus.connected) {
                                console.log('Device status restored after refresh');
                                updateDeviceStatus(refreshStatus);
                                return;
                            }
                        }
                    } catch (refreshError) {
                        console.warn('Failed to refresh device status:', refreshError);
                    }
                }
                
                updateDeviceStatus(status);
            } else {
                console.error('Invalid device status format - missing connected field:', status);
                updateDeviceStatus({
                    connected: false,
                    error: 'Invalid status format from server'
                });
            }
        } else {
            console.error('Invalid device status response:', status);
            updateDeviceStatus({
                connected: false,
                error: 'Invalid response from server'
            });
        }
        
    } catch (error) {
        console.error('Error checking device status:', error);
        updateDeviceStatus({
            connected: false,
            error: 'Failed to check device status: ' + error.message
        });
    }
}

/**
 * Refresh device status to prevent disconnection
 */
async function refreshDeviceStatus() {
    try {
        console.log('Refreshing device status...');
        const response = await fetch('/fingerprint/device/refresh-status', {
            method: 'POST'
        });
        const result = await response.json();
        
        if (result.success) {
            console.log('Device status refreshed successfully');
            // Don't call checkDeviceStatus here to avoid loops
        } else {
            console.warn('Failed to refresh device status:', result.error);
        }
        
    } catch (error) {
        console.error('Error refreshing device status:', error);
    }
}

/**
 * Lock device status to prevent disconnection during critical operations
 */
async function lockDeviceStatus() {
    try {
        console.log('Locking device status...');
        const response = await fetch('/fingerprint/device/lock-status', {
            method: 'POST'
        });
        const result = await response.json();
        
        if (result.success) {
            console.log('Device status locked successfully');
        } else {
            console.warn('Failed to lock device status:', result.error);
        }
        
    } catch (error) {
        console.error('Error locking device status:', error);
    }
}

/**
 * Check if device is locked
 */
async function checkDeviceLockStatus() {
    try {
        const response = await fetch('/fingerprint/device/lock-status');
        const result = await response.json();
        
        if (result.locked) {
            console.log('Device is locked and stable');
            return true;
        } else {
            console.warn('Device is not locked');
            return false;
        }
        
    } catch (error) {
        console.error('Error checking device lock status:', error);
        return false;
    }
}

/**
 * Force device reconnection if status is inconsistent
 */
async function forceDeviceReconnection() {
    try {
        console.log('Forcing device reconnection due to status inconsistency...');
        
        // First try to disconnect
        try {
            await disconnectDevice();
        } catch (error) {
            console.warn('Error during disconnect:', error);
        }
        
        // Wait a moment
        await new Promise(resolve => setTimeout(resolve, 1000));
        
        // Then try to reconnect
        try {
            await connectDevice();
        } catch (error) {
            console.error('Error during reconnect:', error);
            throw error;
        }
        
        console.log('Device reconnection completed');
        
    } catch (error) {
        console.error('Failed to force device reconnection:', error);
        throw error;
    }
}

/**
 * Update device status display
 */
function updateDeviceStatus(status) {
    console.log('Updating device status display:', status);
    
    // Simple status update without recursive calls
    deviceConnected = status.connected;
    updateDeviceStatusUI(status);
}

/**
 * Update device status UI (separated for better control)
 */
function updateDeviceStatusUI(status) {
    const indicator = document.getElementById('deviceStatusIndicator');
    const statusText = document.getElementById('deviceStatusText');
    const details = document.getElementById('deviceDetails');
    const connectBtn = document.getElementById('connectDeviceBtn');
    const disconnectBtn = document.getElementById('disconnectDeviceBtn');
    
    // Defensive checks to prevent null reference errors
    if (!indicator || !statusText || !details || !connectBtn || !disconnectBtn) {
        console.error('Device status UI elements not found:', {
            indicator: !!indicator,
            statusText: !!statusText,
            details: !!details,
            connectBtn: !!connectBtn,
            disconnectBtn: !!disconnectBtn
        });
        return;
    }
    
    if (status.connected) {
        // Device connected
        indicator.className = 'status-indicator connected';
        indicator.innerHTML = '<i class="fas fa-circle text-success"></i>';
        statusText.textContent = 'Device Connected';
        
        if (status.deviceName) {
            details.style.display = 'block';
            const deviceName = document.getElementById('deviceName');
            const deviceId = document.getElementById('deviceId');
            const firmwareVersion = document.getElementById('firmwareVersion');
            
            if (deviceName) deviceName.textContent = status.deviceName;
            if (deviceId) deviceId.textContent = status.deviceId || 'N/A';
            if (firmwareVersion) firmwareVersion.textContent = status.firmwareVersion || 'N/A';
        }
        
        connectBtn.style.display = 'none';
        disconnectBtn.style.display = 'inline-block';
        
    } else {
        // Device disconnected
        indicator.className = 'status-indicator disconnected';
        indicator.innerHTML = '<i class="fas fa-circle text-danger"></i>';
        statusText.textContent = status.error || 'Device Disconnected';
        
        details.style.display = 'none';
        connectBtn.style.display = 'inline-block';
        disconnectBtn.style.display = 'none';
    }
    
    updateStartCaptureButton();
}

/**
 * Connect to fingerprint device
 */
async function connectDevice() {
    try {
        const connectBtn = document.getElementById('connectDeviceBtn');
        connectBtn.disabled = true;
        connectBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Connecting...';
        
        const request = {
            deviceType: 'fingerprint_scanner',
            autoConnect: true,
            connectionTimeout: 10000
        };
        
        const response = await fetch('/fingerprint/device/connect', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(request)
        });
        
        const result = await response.json();
        
        if (result.success) {
            currentDeviceInfo = result.deviceInfo;
            showNotification('Device connected successfully!', 'success');
            await checkDeviceStatus();
        } else {
            showNotification('Failed to connect device: ' + (result.error || 'Unknown error'), 'error');
        }
        
    } catch (error) {
        console.error('Error connecting device:', error);
        showNotification('Error connecting device: ' + error.message, 'error');
    } finally {
        const connectBtn = document.getElementById('connectDeviceBtn');
        connectBtn.disabled = false;
        connectBtn.innerHTML = '<i class="fas fa-plug"></i> Connect Device';
    }
}

/**
 * Disconnect from fingerprint device
 */
async function disconnectDevice() {
    try {
        const disconnectBtn = document.getElementById('disconnectDeviceBtn');
        disconnectBtn.disabled = true;
        disconnectBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Disconnecting...';
        
        const response = await fetch('/fingerprint/device/disconnect', {
            method: 'POST'
        });
        
        const result = await response.json();
        
        if (result.success) {
            currentDeviceInfo = null;
            showNotification('Device disconnected successfully!', 'success');
            await checkDeviceStatus();
        } else {
            showNotification('Failed to disconnect device: ' + (result.error || 'Unknown error'), 'error');
        }
        
    } catch (error) {
        console.error('Error disconnecting device:', error);
        showNotification('Error disconnecting device: ' + error.message, 'error');
    } finally {
        const disconnectBtn = document.getElementById('disconnectDeviceBtn');
        disconnectBtn.disabled = false;
        disconnectBtn.innerHTML = '<i class="fas fa-unlink"></i> Disconnect Device';
    }
}

/**
 * Start fingerprint capture
 */
async function startCapture() {
    const selectedFingers = getSelectedFingers();
    
    if (selectedFingers.length === 0) {
        showNotification('Please select at least one finger for capture', 'warning');
        return;
    }
    
    if (!deviceConnected) {
        showNotification('Please connect a device first', 'warning');
        return;
    }
    
    try {
        // CRITICAL: Ensure device status is stable before capture
        console.log('Ensuring device status stability before capture...');
        
        // Force a device status check to ensure consistency
        await checkDeviceStatus();
        
        // If device is not connected after check, try to force reconnection
        if (!deviceConnected) {
            console.warn('Device status inconsistent, attempting forced reconnection...');
            try {
                await forceDeviceReconnection();
                // Check status again after reconnection
                await checkDeviceStatus();
                if (!deviceConnected) {
                    showNotification('Device reconnection failed, please check device connection', 'error');
                    return;
                }
            } catch (error) {
                showNotification('Device reconnection failed: ' + error.message, 'error');
                return;
            }
        }
        
        // Lock device status to prevent disconnection during capture
        await lockDeviceStatus();
        
        // Show capture progress
        showCaptureProgress();
        
        // Get capture parameters
        const qualityThreshold = parseInt(document.getElementById('qualityThreshold')?.value || 70);
        const captureTimeout = parseInt(document.getElementById('captureTimeout')?.value || 60000);
        const retryCount = parseInt(document.getElementById('retryCount')?.value || 2);
        
        // Start batch capture
        const request = {
            fingers: selectedFingers,
            qualityThreshold: qualityThreshold,
            captureTimeout: captureTimeout,
            retryCount: retryCount
        };
        
        console.log('Starting capture with request:', request);
        
        const response = await fetch('/fingerprint/capture/batch', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(request)
        });
        
        const result = await response.json();
        console.log('Capture response received:', result);
        console.log('Response type:', typeof result);
        console.log('Response keys:', Object.keys(result));
        console.log('Response success:', result.success);
        console.log('Response capturedFingers:', result.capturedFingers);
        console.log('Response failedFingers:', result.failedFingers);
        console.log('Response fingers:', result.fingers);
        console.log('capturedFingers type:', typeof result.capturedFingers);
        console.log('failedFingers type:', typeof result.failedFingers);
        console.log('capturedFingers isArray:', Array.isArray(result.capturedFingers));
        console.log('failedFingers isArray:', Array.isArray(result.failedFingers));
        
        if (result.success) {
            // Validate response structure before processing
            if (result.capturedFingers !== undefined) {
                // Ensure failedFingers is an array (even if empty)
                const processedResult = {
                    ...result,
                    failedFingers: result.failedFingers || []
                };
                processCaptureResults(processedResult);
            } else if (result.fingers) {
                // Handle case where response has 'fingers' array instead of separate arrays
                console.log('Processing response with fingers array:', result);
                const processedResult = {
                    capturedFingers: result.fingers.filter(f => f.success),
                    failedFingers: result.fingers.filter(f => !f.success)
                };
                processCaptureResults(processedResult);
            } else {
                console.error('Invalid capture response structure:', result);
                showNotification('Capture failed: Invalid response structure from server', 'error');
            }
        } else {
            // Handle failed capture response
            console.log('Processing failed capture response:', result);
            
            // Check if we have failed fingers with specific error messages
            if (result.failedFingers && result.failedFingers.length > 0) {
                // Process the failed captures to show specific error messages
                const processedResult = {
                    capturedFingers: result.capturedFingers || [],
                    failedFingers: result.failedFingers
                };
                processCaptureResults(processedResult);
            } else {
                // Generic error fallback
                const errorMessage = result.error || result.message || 'Unknown error';
                console.error('Capture failed with error:', errorMessage);
                showNotification('Capture failed: ' + errorMessage, 'error');
            }
        }
        
    } catch (error) {
        console.error('Error starting capture:', error);
        showNotification('Error starting capture: ' + error.message, 'error');
    } finally {
        hideCaptureProgress();
    }
}

/**
 * Show capture progress
 */
function showCaptureProgress() {
    const progressSection = document.getElementById('captureProgress');
    const progressBar = document.getElementById('captureProgressBar');
    const statusText = document.getElementById('captureStatus');
    
    // Defensive checks to prevent null reference errors
    if (!progressSection || !progressBar || !statusText) {
        console.error('Capture progress elements not found:', {
            progressSection: !!progressSection,
            progressBar: !!progressBar,
            statusText: !!statusText
        });
        return;
    }
    
    progressSection.style.display = 'block';
    
    // Show initial status
    progressBar.style.width = '0%';
    statusText.textContent = 'Waiting for fingerprint... Place your finger on the sensor';
    
    // Store progress elements globally for updates from WebSocket/backend
    window.captureProgressBar = progressBar;
    window.captureStatusText = statusText;
}

/**
 * Hide capture progress
 */
function hideCaptureProgress() {
    const progressSection = document.getElementById('captureProgress');
    
    // Defensive check to prevent null reference errors
    if (!progressSection) {
        console.error('Capture progress section not found');
        return;
    }
    
    progressSection.style.display = 'none';
}

/**
 * Process capture results
 */
function processCaptureResults(result) {
    console.log('=== PROCESS CAPTURE RESULTS DEBUG ===');
    console.log('Full result object:', JSON.stringify(result, null, 2));
    console.log('Result type:', typeof result);
    console.log('Result keys:', Object.keys(result));
    
    try {
        // Handle successful captures
        if (result.capturedFingers && Array.isArray(result.capturedFingers)) {
            console.log('=== SUCCESSFUL CAPTURES DEBUG ===');
            console.log('Successful captures count:', result.capturedFingers.length);
            
            result.capturedFingers.forEach((finger, index) => {
                console.log(`=== FINGER ${index + 1} DEBUG ===`);
                console.log('Raw finger object:', JSON.stringify(finger, null, 2));
                console.log('Finger type:', finger.fingerType);
                console.log('Quality score:', finger.qualityScore);
                console.log('Image data length:', finger.imageData ? finger.imageData.length : 'N/A');
                console.log('WSQ data length:', finger.wsqData ? finger.wsqData.length : 'N/A');
                console.log('Image data preview:', finger.imageData ? finger.imageData.substring(0, 100) + '...' : 'N/A');
                console.log('WSQ data preview:', finger.wsqData ? finger.wsqData.substring(0, 100) + '...' : 'N/A');
                
                const captureData = {
                    fingerType: finger.fingerType || 'Unknown',
                    success: true,
                    qualityScore: finger.qualityScore || 0,
                    quality: finger.qualityScore || 0,
                    captureTime: finger.captureTime || new Date().toISOString(),
                    wsqData: finger.wsqData || null,
                    imageData: finger.imageData || null
                };
                
                console.log('Processed capture data:', captureData);
                captureResults.push(captureData);
            });
        }
        
        // Skip failed captures display as requested by user
        
        // Handle case where response has different structure
        if (!result.capturedFingers && !result.failedFingers) {
            console.warn('No capturedFingers or failedFingers in response, checking alternative structure...');
            
            // Check if result has a different structure
            if (result.fingers && Array.isArray(result.fingers)) {
                result.fingers.forEach(finger => {
                    if (finger.success) {
                        captureResults.push({
                            fingerType: finger.fingerType || 'Unknown',
                            success: true,
                            qualityScore: finger.qualityScore || 0,
                            quality: finger.qualityScore || 0,
                            captureTime: finger.captureTime || new Date().toISOString(),
                            wsqData: finger.wsqData || null,
                            imageData: finger.imageData || null
                        });
                    }
                    // Skip failed captures display as requested by user
                });
            }
        }
        
        // Store results
        storeResults();
        
        // Show fingerprint preview
        const successfulCaptures = captureResults.filter(r => r.success);
        if (window.showFingerprintPreview && successfulCaptures.length > 0) {
            window.showFingerprintPreview(successfulCaptures);
        }
        
        // Update AFIS form validation
        validateAfisForm();
        
        // Show summary
        const successCount = captureResults.filter(r => r.success).length;
        const totalCount = captureResults.length;
        
        if (totalCount > 0) {
            if (successCount > 0) {
                showNotification(`Capture completed: ${successCount}/${totalCount} fingers captured successfully`, 'success');
            } else {
                // All captures failed - show specific error message
                const firstError = captureResults.find(r => !r.success)?.error || 'Unknown error';
                showNotification(`Capture failed: ${firstError}`, 'error');
            }
        } else {
            showNotification('Capture completed but no results were processed', 'warning');
        }
        
    } catch (error) {
        console.error('Error processing capture results:', error);
        showNotification('Error processing capture results: ' + error.message, 'error');
    }
}

/**
 * Display capture results
 */
// Capture results display removed as requested by user

/**
 * Store results in localStorage
 */
function storeResults() {
    try {
        localStorage.setItem('fingerprintCaptureResults', JSON.stringify(captureResults));
    } catch (error) {
        console.error('Error storing results:', error);
    }
}

/**
 * Clear old results to start fresh
 */
function clearOldResults() {
    console.log('=== CLEAR OLD RESULTS DEBUG ===');
    console.log('Current captureResults before clearing:', captureResults);
    console.log('Number of results before clearing:', captureResults.length);
    
    // Clear the current session results
    captureResults = [];
    
    // Clear localStorage to start fresh
    localStorage.removeItem('fingerprintCaptureResults');
    
    console.log('Results cleared. Current captureResults:', captureResults);
    console.log('localStorage cleared');
}

/**
 * Load stored results from localStorage
 */
function loadStoredResults() {
    try {
        const stored = localStorage.getItem('fingerprintCaptureResults');
        console.log('=== LOAD STORED RESULTS DEBUG ===');
        console.log('Stored data from localStorage:', stored);
        
        if (stored) {
            const parsedResults = JSON.parse(stored);
            console.log('Parsed stored results:', parsedResults);
            console.log('Number of stored results:', parsedResults.length);
            
            // Clear current results and load stored ones
            captureResults = parsedResults;
            console.log('Current captureResults after loading:', captureResults);
        } else {
            console.log('No stored results found in localStorage');
        }
    } catch (error) {
        console.error('Error loading stored results:', error);
    }
}

/**
 * Validate AFIS form
 */
function validateAfisForm() {
    try {
        const nid10Digit = document.getElementById('nid10Digit')?.value?.trim() || '';
        const nid17Digit = document.getElementById('nid17Digit')?.value?.trim() || '';
        const dateOfBirth = document.getElementById('dateOfBirth')?.value || '';
        const submitBtn = document.getElementById('submitToAfisBtn');
        
        // Defensive check to prevent null reference errors
        if (!submitBtn) {
            console.error('Submit button not found');
            return;
        }
        
        const hasValidNid = nid10Digit.length === 10 || nid17Digit.length === 17;
        const hasValidDob = dateOfBirth.length > 0;
        const hasSuccessfulCaptures = captureResults.some(r => r.success);
        
        submitBtn.disabled = !(hasValidNid && hasValidDob && hasSuccessfulCaptures);
        
    } catch (error) {
        console.error('Error validating AFIS form:', error);
    }
}

/**
 * Submit to AFIS verification
 */
async function submitToAfis() {
    try {
        const submitBtn = document.getElementById('submitToAfisBtn');
        submitBtn.disabled = true;
        submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Submitting...';
        
        // Get form data
        const nid10Digit = document.getElementById('nid10Digit').value.trim();
        const nid17Digit = document.getElementById('nid17Digit').value.trim();
        const dateOfBirth = document.getElementById('dateOfBirth').value;
        
        // Get successful captures
        const successfulCaptures = captureResults.filter(r => r.success);
        const fingerEnums = successfulCaptures.map(r => r.fingerType);
        
        // Prepare AFIS request
        const afisRequest = {
            dateOfBirth: dateOfBirth,
            fingerEnums: fingerEnums
        };
        
        if (nid10Digit) {
            afisRequest.nid10Digit = nid10Digit;
        }
        if (nid17Digit) {
            afisRequest.nid17Digit = nid17Digit;
        }
        
        // Get JWT token
        const jwtResponse = await fetch('/api/jwt-status');
        const jwtData = await jwtResponse.json();
        
        if (!jwtData.jwt) {
            showNotification('No JWT token found. Please login to third-party service first.', 'warning');
            return;
        }
        
        // Submit to AFIS
        const response = await fetch('/partner-service/rest/afis/verification-secured', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${jwtData.jwt}`
            },
            body: JSON.stringify(afisRequest)
        });
        
        const result = await response.json();
        
        if (response.ok && result.status === 'ACCEPTED') {
            showNotification('Successfully submitted to AFIS verification!', 'success');
            
            // Redirect to AFIS verification page to handle uploads
            const username = document.body.getAttribute('data-username');
            window.location.href = `/afis-verification?username=${username}&fromCapture=true`;
            
        } else {
            const errorMsg = result.error?.message || 'AFIS verification failed';
            showNotification('AFIS verification failed: ' + errorMsg, 'error');
        }
        
    } catch (error) {
        console.error('Error submitting to AFIS:', error);
        showNotification('Error submitting to AFIS: ' + error.message, 'error');
    } finally {
        const submitBtn = document.getElementById('submitToAfisBtn');
        submitBtn.disabled = false;
        submitBtn.innerHTML = '<i class="fas fa-paper-plane"></i> Submit to AFIS Verification';
    }
}

/**
 * Show notification
 */
function showNotification(message, type = 'info') {
    // Create notification element
    const notification = document.createElement('div');
    notification.className = `alert alert-${type} alert-dismissible fade show position-fixed`;
    notification.style.cssText = 'top: 80px; right: 20px; z-index: 9999; min-width: 300px;';
    notification.innerHTML = `
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
    
    // Add to page
    document.body.appendChild(notification);
    
    // Auto-remove after 5 seconds
    setTimeout(() => {
        if (notification.parentNode) {
            notification.remove();
        }
    }, 5000);
}

/**
 * Initialize WebSocket connection
 */
function initializeWebSocket() {
    try {
        const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
        const wsUrl = `${protocol}//${window.location.host}/fingerprint/ws`;
        
        websocket = new WebSocket(wsUrl);
        
        websocket.onopen = function(event) {
            console.log('WebSocket connection established');
        };
        
        websocket.onmessage = function(event) {
            try {
                const message = JSON.parse(event.data);
                handleWebSocketMessage(message);
            } catch (error) {
                console.error('Error parsing WebSocket message:', error);
            }
        };
        
        websocket.onclose = function(event) {
            console.log('WebSocket connection closed');
            // Attempt to reconnect after 5 seconds
            setTimeout(initializeWebSocket, 5000);
        };
        
        websocket.onerror = function(error) {
            console.error('WebSocket error:', error);
        };
        
    } catch (error) {
        console.error('Error initializing WebSocket:', error);
    }
}

/**
 * Handle WebSocket messages
 */
function handleWebSocketMessage(message) {
    // Handle different message types
    console.log('WebSocket message received:', message);
    
    // Handle capture progress updates
    if (message.type === 'capture_progress') {
        updateCaptureProgress(message.progress, message.status);
    } else if (message.type === 'capture_sample_received') {
        updateCaptureProgress(50, 'Fingerprint sample received, processing...');
    } else if (message.type === 'capture_quality_check') {
        updateCaptureProgress(75, 'Checking sample quality...');
    } else if (message.type === 'capture_complete') {
        updateCaptureProgress(100, 'Capture completed successfully!');
    }
}

/**
 * Update capture progress based on real events
 */
function updateCaptureProgress(progress, status) {
    const progressBar = window.captureProgressBar;
    const statusText = window.captureStatusText;
    
    if (progressBar && statusText) {
        progressBar.style.width = progress + '%';
        statusText.textContent = status;
        
        // Add progress bar color based on progress
        if (progress < 30) {
            progressBar.className = 'progress-bar bg-warning';
        } else if (progress < 70) {
            progressBar.className = 'progress-bar bg-info';
        } else if (progress < 100) {
            progressBar.className = 'progress-bar bg-primary';
        } else {
            progressBar.className = 'progress-bar bg-success';
        }
    }
}

// Initialize WebSocket when page loads
setTimeout(initializeWebSocket, 1000);

// Export functions to global scope for use in HTML onclick handlers
window.captureFingerprints = startCapture;
window.clearSelection = function() {
    document.querySelectorAll('.finger-checkbox:checked').forEach(checkbox => {
        checkbox.checked = false;
    });
    updateStartCaptureButton();
};
window.checkDeviceStatus = checkDeviceStatus;
window.connectDevice = connectDevice;
window.disconnectDevice = disconnectDevice;

// Add quality threshold update function
window.updateQualityDisplay = function() {
    const slider = document.getElementById('qualityThreshold');
    const display = document.getElementById('qualityValue');
    
    if (slider && display) {
        display.textContent = slider.value;
    }
};

// Initialize quality display when page loads
document.addEventListener('DOMContentLoaded', function() {
    const qualitySlider = document.getElementById('qualityThreshold');
    if (qualitySlider) {
        qualitySlider.addEventListener('input', updateQualityDisplay);
        updateQualityDisplay(); // Set initial value
    }
});

// Add fingerprint preview functionality
window.showFingerprintPreview = function(fingerprints) {
    console.log('=== SHOW FINGERPRINT PREVIEW DEBUG ===');
    console.log('Fingerprints to display:', fingerprints);
    console.log('Number of fingerprints:', fingerprints.length);
    console.log('Fingerprints type:', typeof fingerprints);
    console.log('Fingerprints isArray:', Array.isArray(fingerprints));
    
    const previewContainer = document.getElementById('previewContainer');
    const fingerprintPreview = document.getElementById('fingerprintPreview');
    
    console.log('Preview container found:', !!previewContainer);
    console.log('Fingerprint preview element found:', !!fingerprintPreview);
    
    if (!previewContainer || !fingerprintPreview) return;
    
    if (fingerprints && fingerprints.length > 0) {
        let html = '<div class="row">';
        fingerprints.forEach((fingerprint, index) => {
            html += `
                <div class="col-md-3 mb-3">
                    <div class="card">
                        <div class="card-body text-center">
                            <h6 class="card-title">${fingerprint.fingerType}</h6>
                            <div class="fingerprint-preview">
                                ${fingerprint.imageData && fingerprint.imageData !== 'SimulatedImageData' 
                                    ? `<img src="data:image/png;base64,${fingerprint.imageData}" 
                                           class="img-fluid" style="max-height: 150px; border: 1px solid #ddd;" 
                                           alt="Fingerprint ${fingerprint.fingerType}">`
                                    : `<div class="bg-light border rounded d-flex align-items-center justify-content-center" 
                                           style="height: 150px; width: 150px; margin: 0 auto;">
                                           <i class="fas fa-fingerprint fa-2x text-muted"></i>
                                       </div>`
                                }
                            </div>
                            
                            <!-- File Info and Download Options -->
                            ${fingerprint.imageData && fingerprint.imageData !== 'SimulatedImageData' 
                                ? `<div class="mt-2">
                                     <div class="d-flex justify-content-between align-items-center mb-1">
                                       <small class="text-muted">Image Size: ${formatFileSize(fingerprint.imageData)}</small>
                                       <button class="btn btn-sm btn-outline-primary" 
                                               onclick="downloadImage('${fingerprint.fingerType}', '${fingerprint.imageData}')">
                                         <i class="fas fa-download me-1"></i>Download PNG
                                       </button>
                                     </div>
                                   </div>`
                                : ''
                            }
                            
                            ${fingerprint.wsqData && fingerprint.wsqData !== 'SimulatedWsqData' 
                                ? `<div class="mt-2">
                                     <div class="d-flex justify-content-between align-items-center">
                                       <small class="text-muted">WSQ Size: ${formatFileSize(fingerprint.wsqData)}</small>
                                       <button class="btn btn-sm btn-outline-success" 
                                               onclick="downloadWSQ('${fingerprint.fingerType}', '${fingerprint.wsqData}')">
                                         <i class="fas fa-download me-1"></i>Download WSQ
                                       </button>
                                     </div>
                                   </div>`
                                : ''
                            }
                            <small class="text-muted">Quality: ${fingerprint.quality || 'N/A'}</small>
                        </div>
                    </div>
                </div>
            `;
        });
        html += '</div>';
        
        previewContainer.innerHTML = html;
        fingerprintPreview.style.display = 'block';
    } else {
        fingerprintPreview.style.display = 'none';
    }
}

// Utility function to format file size
function formatFileSize(base64String) {
    try {
        // Calculate the actual byte size of the base64 data
        const base64Length = base64String.length;
        const padding = (base64String.match(/=/g) || []).length;
        const actualSize = (base64Length * 3) / 4 - padding;
        
        if (actualSize < 1024) {
            return actualSize + ' B';
        } else if (actualSize < 1024 * 1024) {
            return (actualSize / 1024).toFixed(1) + ' KB';
        } else {
            return (actualSize / (1024 * 1024)).toFixed(1) + ' MB';
        }
    } catch (error) {
        console.error('Error calculating file size:', error);
        return 'Unknown';
    }
}

// Download image as PNG file
function downloadImage(fingerType, base64Data) {
    try {
        const link = document.createElement('a');
        link.href = 'data:image/png;base64,' + base64Data;
        link.download = `fingerprint_${fingerType}_${new Date().toISOString().slice(0, 19).replace(/:/g, '-')}.png`;
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        
        showNotification(`Downloaded ${fingerType} fingerprint image`, 'success');
    } catch (error) {
        console.error('Error downloading image:', error);
        showNotification('Error downloading image', 'error');
    }
}

// Download WSQ data as file
function downloadWSQ(fingerType, base64Data) {
    try {
        const link = document.createElement('a');
        link.href = 'data:application/octet-stream;base64,' + base64Data;
        link.download = `fingerprint_${fingerType}_${new Date().toISOString().slice(0, 19).replace(/:/g, '-')}.wsq`;
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        
        showNotification(`Downloaded ${fingerType} WSQ data`, 'success');
    } catch (error) {
        console.error('Error downloading WSQ:', error);
        showNotification('Error downloading WSQ data', 'error');
    }
}

// Expose functions to global scope
window.formatFileSize = formatFileSize;
window.downloadImage = downloadImage;
window.downloadWSQ = downloadWSQ;;