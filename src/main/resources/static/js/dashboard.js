document.addEventListener('DOMContentLoaded', function() {
    
    loadSessionStatus();
    
    function loadSessionStatus() {
        const username = document.body.getAttribute('data-username');
        const sessionStatusDiv = document.getElementById('sessionStatus');
        
        if (username) {
            fetch(`/api/jwt-status?username=${encodeURIComponent(username)}`)
                .then(response => response.json())
                .then(data => {
                    if (data.isValid) {
                        const timeRemaining = calculateTimeRemaining(data.expiresAt);
                        sessionStatusDiv.innerHTML = `
                            <div class="d-flex align-items-center">
                                <i class="fas fa-check-circle text-success me-2"></i>
                                <span class="text-success fw-bold">Session Active</span>
                                <span class="text-muted ms-2">• Expires in ${timeRemaining}</span>
                            </div>
                        `;
                    } else {
                        sessionStatusDiv.innerHTML = `
                            <div class="d-flex align-items-center">
                                <i class="fas fa-exclamation-triangle text-warning me-2"></i>
                                <span class="text-warning fw-bold">Session Not Available - Please login to third-party service to use their APIs.</span>
                            </div>
                        `;
                    }
                })
                .catch(error => {
                    console.error('Error loading session status:', error);
                    sessionStatusDiv.innerHTML = `
                        <div class="d-flex align-items-center">
                            <i class="fas fa-exclamation-triangle text-danger me-2"></i>
                            <span class="text-danger fw-bold">Error Loading Status</span>
                            <span class="text-muted ms-2">• Please refresh the page</span>
                        </div>
                    `;
                });
        }
    }
    
    function calculateTimeRemaining(expiresAt) {
        if (!expiresAt) return 'Unknown';
        
        const now = new Date();
        const expiry = new Date(expiresAt);
        const diffMs = expiry - now;
        
        if (diffMs <= 0) return 'Expired';
        
        const diffHours = Math.floor(diffMs / (1000 * 60 * 60));
        const diffMinutes = Math.floor((diffMs % (1000 * 60 * 60)) / (1000 * 60));
        
        if (diffHours > 0) {
            return `${diffHours}h ${diffMinutes}m`;
        } else {
            return `${diffMinutes}m`;
        }
    }
    
    function checkThirdPartyLogin() {
        const username = document.body.getAttribute('data-username');
        
        if (username) {
            window.location.href = '/verification-form?username=' + encodeURIComponent(username);
        } else {
            window.location.href = '/';
        }
    }
    
    function checkJwtForPasswordChange() {
        const username = document.body.getAttribute('data-username');
        
        if (username) {
            fetch(`/api/jwt-status?username=${encodeURIComponent(username)}`)
                .then(response => {
                    if (!response.ok) {
                        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
                    }
                    return response.json();
                })
                .then(data => {
                    if (data.isValid) {
                        window.location.href = '/change-password?username=' + encodeURIComponent(username) + '&thirdPartyUsername=' + encodeURIComponent(data.thirdPartyUsername);
                    } else {
                        alert('Login session expired. Please login to third-party service first.');
                    }
                })
                .catch(error => {
                    console.error('Error checking JWT status:', error);
                    if (error.message && error.message.includes('401')) {
                        window.location.href = '/';
                    } else {
                        alert('Error checking JWT status. Please try again.');
                    }
                });
        } else {
            alert('Username not found. Please login first.');
        }
    }
    
    function checkJwtForBilling() {
        const username = document.body.getAttribute('data-username');
        
        if (username) {
            fetch(`/api/jwt-status?username=${encodeURIComponent(username)}`)
                .then(response => {
                    if (!response.ok) {
                        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
                    }
                    return response.json();
                })
                .then(data => {
                    if (data.isValid) {
                        window.location.href = '/partner-billing?username=' + encodeURIComponent(username) + '&thirdPartyUsername=' + encodeURIComponent(data.thirdPartyUsername);
                    } else {
                        alert('Login session expired. Please login to third-party service first.');
                    }
                })
                .catch(error => {
                    console.error('Error checking JWT status:', error);
                    if (error.message && error.message.includes('401')) {
                        window.location.href = '/';
                    } else {
                        alert('Error checking JWT status. Please try again.');
                    }
                });
        } else {
            alert('Username not found. Please login first.');
        }
    }
    
    function thirdPartyLogout() {
        const username = document.body.getAttribute('data-username');
        
        if (!username) {
            alert('Username not found. Please login first.');
            return;
        }
        
        // Show confirmation dialog
        if (!confirm('Are you sure you want to logout from the third-party service? This will invalidate your JWT token.')) {
            return;
        }
        
        // Get third-party username from session status
        fetch(`/api/jwt-status?username=${encodeURIComponent(username)}`)
            .then(response => response.json())
            .then(data => {
                const thirdPartyUsername = data.thirdPartyUsername || username;
                
                // Create form data for the logout request
                const formData = new FormData();
                formData.append('username', username);
                formData.append('thirdPartyUsername', thirdPartyUsername);
                
                // Send logout request
                return fetch('/third-party-logout', {
                    method: 'POST',
                    body: formData
                });
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    alert('Successfully logged out from third-party service!');
                    // Reload session status to reflect the change
                    loadSessionStatus();
                } else {
                    alert('Logout failed: ' + (data.message || 'Unknown error'));
                }
            })
            .catch(error => {
                console.error('Error during third-party logout:', error);
                alert('Error during logout. Please try again.');
            });
    }
    
    function checkJwtForAfisVerification() {
        const username = document.body.getAttribute('data-username');
        
        if (username) {
            fetch(`/api/jwt-status?username=${encodeURIComponent(username)}`)
                .then(response => {
                    if (!response.ok) {
                        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
                    }
                    return response.json();
                })
                .then(data => {
                    if (data.isValid) {
                        window.location.href = '/afis-verification?username=' + encodeURIComponent(username) + '&thirdPartyUsername=' + encodeURIComponent(data.thirdPartyUsername);
                    } else {
                        alert('Login session expired. Please login to third-party service first.');
                    }
                })
                .catch(error => {
                    console.error('Error checking JWT status:', error);
                    if (error.message && error.message.includes('401')) {
                        window.location.href = '/';
                    } else {
                        alert('Error checking JWT status. Please try again.');
                    }
                });
        } else {
            alert('Username not found. Please login first.');
        }
    }
    
    function goToAfisVerification() {
        const username = document.body.getAttribute('data-username');
        
        if (username) {
            // Directly navigate to AFIS verification page without JWT check
            window.location.href = '/afis-verification?username=' + encodeURIComponent(username);
        } else {
            alert('Username not found. Please login first.');
        }
    }
    
    
    window.checkThirdPartyLogin = checkThirdPartyLogin;
    window.checkJwtForPasswordChange = checkJwtForPasswordChange;
    window.checkJwtForBilling = checkJwtForBilling;
    window.thirdPartyLogout = thirdPartyLogout;
    window.goToAfisVerification = goToAfisVerification;
});
