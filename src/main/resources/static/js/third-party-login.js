document.getElementById('thirdPartyLoginForm').addEventListener('submit', function(e) {
    const btn = document.getElementById('loginBtn');
    const loading = btn.querySelector('.loading');
    const normal = btn.querySelector('.normal');
    
    loading.style.display = 'inline';
    normal.style.display = 'none';
    btn.disabled = true;
});

async function checkExistingJwt() {
    try {
        const response = await fetch('/api/jwt-status?username=' + encodeURIComponent(username), {
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            }
        });
        const data = await response.json();
        
        if (data.isValid && data.hasStoredJwt) {
            window.location.href = '/verification-form?username=' + encodeURIComponent(username) + '&thirdPartyUsername=' + encodeURIComponent(data.thirdPartyUsername);
        }
    } catch (error) {
        console.error('Error checking JWT status:', error);
    }
}

document.addEventListener('DOMContentLoaded', function() {
    checkExistingJwt();
});
