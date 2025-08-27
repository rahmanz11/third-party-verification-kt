document.addEventListener('DOMContentLoaded', function() {
    const loginForm = document.getElementById('loginForm');
    
    if (loginForm) {
        loginForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            const username = document.getElementById('username').value;
            const password = document.getElementById('password').value;
            
            if (!username || !password) {
                showAlert('Please fill in all fields', 'danger');
                return;
            }
            
            const btn = document.getElementById('loginBtn');
            const loading = btn.querySelector('.loading');
            const normal = btn.querySelector('.normal');
            
            loading.style.display = 'inline';
            normal.style.display = 'none';
            btn.disabled = true;
            
            const formData = new FormData();
            formData.append('username', username);
            formData.append('password', password);
            
            fetch('/login', {
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
                        showAlert(alertElement.textContent.trim(), 'danger');
                        loading.style.display = 'none';
                        normal.style.display = 'inline';
                        btn.disabled = false;
                    }
                }
            })
            .catch(error => {
                console.error('Error:', error);
                showAlert('An error occurred during login', 'danger');
                loading.style.display = 'none';
                normal.style.display = 'inline';
                btn.disabled = false;
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
