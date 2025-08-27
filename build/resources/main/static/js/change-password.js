document.addEventListener('DOMContentLoaded', function() {
    const currentPassword = document.getElementById('currentPassword');
    const newPassword = document.getElementById('newPassword');
    const confirmPassword = document.getElementById('confirmPassword');
    const strengthIndicator = document.getElementById('strengthIndicator');
    const matchIndicator = document.getElementById('matchIndicator');
    const submitBtn = document.getElementById('submitBtn');
    
    function checkPasswordStrength(password) {
        let strength = 0;
        let feedback = '';
        
        if (password.length >= 8) strength++;
        if (/[a-z]/.test(password)) strength++;
        if (/[A-Z]/.test(password)) strength++;
        if (/[0-9]/.test(password)) strength++;
        if (/[^A-Za-z0-9]/.test(password)) strength++;
        
        if (strength < 3) {
            feedback = 'Weak';
            strengthIndicator.className = 'password-strength strength-weak';
        } else if (strength < 5) {
            feedback = 'Medium';
            strengthIndicator.className = 'password-strength strength-medium';
        } else {
            feedback = 'Strong';
            strengthIndicator.className = 'password-strength strength-strong';
        }
        
        strengthIndicator.textContent = `Password strength: ${feedback}`;
        return strength >= 3;
    }
    
    function checkPasswordMatch() {
        const newPass = newPassword.value;
        const confirmPass = confirmPassword.value;
        
        if (confirmPass === '') {
            matchIndicator.textContent = '';
            matchIndicator.className = 'password-match';
            return false;
        }
        
        if (newPass === confirmPass) {
            matchIndicator.textContent = 'Passwords match';
            matchIndicator.className = 'password-match match-yes';
            return true;
        } else {
            matchIndicator.textContent = 'Passwords do not match';
            matchIndicator.className = 'password-match match-no';
            return false;
        }
    }
    
    function updateSubmitButton() {
        const currentPass = currentPassword.value;
        const newPass = newPassword.value;
        const confirmPass = confirmPassword.value;
        const isStrong = checkPasswordStrength(newPass);
        const isMatch = checkPasswordMatch();
        
        if (currentPass && newPass && confirmPass && isStrong && isMatch) {
            submitBtn.disabled = false;
        } else {
            submitBtn.disabled = true;
        }
    }
    
    if (currentPassword) {
        currentPassword.addEventListener('input', updateSubmitButton);
    }
    
    if (newPassword) {
        newPassword.addEventListener('input', function() {
            checkPasswordStrength(this.value);
            updateSubmitButton();
        });
    }
    
    if (confirmPassword) {
        confirmPassword.addEventListener('input', function() {
            checkPasswordMatch();
            updateSubmitButton();
        });
    }
    
    const changePasswordForm = document.getElementById('changePasswordForm');
    if (changePasswordForm) {
        changePasswordForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            const formData = new FormData();
            formData.append('currentPassword', currentPassword.value);
            formData.append('newPassword', newPassword.value);
            formData.append('confirmPassword', confirmPassword.value);
            
            fetch('/change-password', {
                method: 'POST',
                body: formData
            })
            .then(response => {
                if (response.redirected) {
                    window.location.href = response.url;
                } else {
                    return response.text();
                }
            })
            .then(html => {
                if (html) {
                    const parser = new DOMParser();
                    const doc = parser.parseFromString(html, 'text/html');
                    const alertElement = doc.querySelector('.alert');
                    
                    if (alertElement) {
                        const message = alertElement.textContent.trim();
                        const isSuccess = alertElement.classList.contains('alert-success');
                        
                        if (isSuccess) {
                            showAlert(message, 'success');
                            setTimeout(() => {
                                window.location.href = '/';
                            }, 2000);
                        } else {
                            showAlert(message, 'danger');
                        }
                    } else {
                        showAlert('An unexpected response was received', 'danger');
                    }
                }
            })
            .catch(error => {
                console.error('Error:', error);
                let errorMessage = 'An error occurred while changing password';
                
                if (error.message && error.message.includes('Failed to fetch')) {
                    errorMessage = 'Network error. Please check your connection and try again.';
                } else if (error.message && error.message.includes('Third party')) {
                    errorMessage = error.message;
                }
                
                showAlert(errorMessage, 'danger');
            });
        });
    }
    
    function showAlert(message, type) {
        const alertContainer = document.getElementById('alertContainer');
        if (alertContainer) {
            alertContainer.innerHTML = `
                <div class="alert alert-${type} alert-dismissible fade show" role="alert">
                    ${message}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            `;
        }
    }
});
