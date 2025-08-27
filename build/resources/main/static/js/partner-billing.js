document.addEventListener('DOMContentLoaded', function() {
    const billingForm = document.getElementById('billingForm');
    
    if (billingForm) {
        billingForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            const startDate = document.getElementById('startDate').value;
            const endDate = document.getElementById('endDate').value;
            
            if (!startDate || !endDate) {
                showAlert('Please select both start and end dates', 'danger');
                return;
            }
            
            if (new Date(startDate) > new Date(endDate)) {
                showAlert('Start date cannot be after end date', 'danger');
                return;
            }
            
            const formData = new FormData();
            formData.append('startDate', startDate);
            formData.append('endDate', endDate);
            
            fetch('/partner-billing', {
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
                let errorMessage = 'An error occurred while generating billing report';
                
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
    
    function formatCurrency(amount) {
        return new Intl.NumberFormat('en-US', {
            style: 'currency',
            currency: 'USD'
        }).format(amount);
    }
    
    function formatDate(dateString) {
        return new Date(dateString).toLocaleDateString('en-US', {
            year: 'numeric',
            month: 'short',
            day: 'numeric'
        });
    }
    
    window.formatCurrency = formatCurrency;
    window.formatDate = formatDate;
});
