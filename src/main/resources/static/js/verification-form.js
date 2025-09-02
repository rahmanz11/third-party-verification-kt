document.addEventListener('DOMContentLoaded', function() {
    const username = document.body.getAttribute('data-username');
    console.log('Extracted username:', username);
    
    if (!username) {
        console.error('Username not found in body data-username attribute');
        return;
    }
    
    initializeGeographicDropdowns();
    initializeJsonPanel();
    
    document.getElementById('nidType').addEventListener('change', function() {
        const nidValue = document.getElementById('nidValue');
        const selectedType = this.value;
        
        if (selectedType === '10digit') {
            nidValue.placeholder = 'Enter 10-digit NID number';
            nidValue.maxLength = 10;
        } else if (selectedType === '17digit') {
            nidValue.placeholder = 'Enter 17-digit NID number';
            nidValue.maxLength = 17;
        }
    });
    
    document.getElementById('verificationForm').addEventListener('submit', function(e) {
        e.preventDefault();
        
        if (!validateForm()) {
            return;
        }
        
        // Show JSON confirmation modal before submitting
        showJsonConfirmationModal();
    });
    
    document.getElementById('resetBtn').addEventListener('click', function() {
        resetForm();
    });
    

    

    
    function initializeJsonPanel() {
        const jsonToggle = document.getElementById('jsonToggle');
        const jsonPanel = document.getElementById('jsonPanel');
        const mainContent = document.getElementById('mainContent');
        
        jsonToggle.addEventListener('click', function() {
            jsonPanel.classList.toggle('collapsed');
            mainContent.classList.toggle('expanded');
            
            const icon = this.querySelector('i');
            if (jsonPanel.classList.contains('collapsed')) {
                icon.className = 'fas fa-chevron-left';
            } else {
                icon.className = 'fas fa-code';
            }
        });
    }
    
    function submitFormViaAjax() {
        const form = document.getElementById('verificationForm');
        const formData = new FormData(form);
        const submitBtn = document.getElementById('submitBtn');
        
        // Show loading state
        submitBtn.querySelector('.loading').style.display = 'inline-block';
        submitBtn.querySelector('.normal').style.display = 'none';
        submitBtn.disabled = true;
        
        // Build request object for display
        const requestData = buildRequestObject(formData);
        displayRequestJson(requestData);
        

        
        // Submit form via AJAX
        fetch('/verify', {
            method: 'POST',
            body: formData,
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            }
        })
        .then(response => {
            if (response.redirected) {
                // Handle redirect (e.g., to third-party login)
                window.location.href = response.url;
                return;
            }
            
            return response.text();
        })
        .then(data => {
            if (data) {
                try {
                    // Try to parse as JSON
                    const jsonData = JSON.parse(data);
                    displayResponseJson(jsonData);
                    
                    // If it's a verification result, redirect to result page
                    if (jsonData.verificationResponse) {
                        window.location.href = `/verification-result?username=${username}&thirdPartyUsername=${formData.get('thirdPartyUsername')}`;
                    }
                } catch (e) {
                    // If not JSON, it might be HTML (success case)
                    displayResponseJson({ message: 'Response received (HTML content)', rawData: data.substring(0, 200) + '...' });
                    
                    // Redirect to result page
                    setTimeout(() => {
                        window.location.href = `/verification-result?username=${username}&thirdPartyUsername=${formData.get('thirdPartyUsername')}`;
                    }, 1000);
                }
            }
        })
        .catch(error => {
            console.error('Error:', error);
            displayResponseJson({ error: error.message, type: 'NetworkError' });
        })
        .finally(() => {
            // Reset button state
            submitBtn.querySelector('.loading').style.display = 'none';
            submitBtn.querySelector('.normal').style.display = 'inline-block';
            submitBtn.disabled = false;
        });
    }
    
    function buildRequestObject(formData) {
        const request = {
            identify: {
                nid10Digit: formData.get('nidType') === '10digit' ? formData.get('nidValue') : null,
                nid17Digit: formData.get('nidType') === '17digit' ? formData.get('nidValue') : null
            },
            verify: {
                nameEn: formData.get('nameEn') || '',
                name: formData.get('name') || '',
                dateOfBirth: formData.get('dateOfBirth') || '',
                father: formData.get('father') || '',
                mother: formData.get('mother') || '',
                spouse: formData.get('spouse') || '',
                permanentAddress: {
                    division: getSelectedText('permanentDivision') || '',
                    district: getSelectedText('permanentDistrict') || '',
                    upozila: getSelectedText('permanentUpazila') || ''
                },
                presentAddress: {
                    division: getSelectedText('presentDivision') || '',
                    district: getSelectedText('presentDistrict') || '',
                    upozila: getSelectedText('presentUpazila') || ''
                }
            }
        };
        
        // Remove empty fields for cleaner display
        return cleanObject(request);
    }
    
    function getSelectedText(selectId) {
        const select = document.getElementById(selectId);
        if (!select || !select.value) {
            return '';
        }
        
        const selectedOption = select.options[select.selectedIndex];
        return selectedOption ? selectedOption.text : '';
    }
    
    function cleanObject(obj) {
        const cleaned = {};
        for (const [key, value] of Object.entries(obj)) {
            if (value !== null && value !== undefined && value !== '') {
                if (typeof value === 'object' && !Array.isArray(value)) {
                    const cleanedNested = cleanObject(value);
                    if (Object.keys(cleanedNested).length > 0) {
                        cleaned[key] = cleanedNested;
                    }
                } else {
                    cleaned[key] = value;
                }
            }
        }
        return cleaned;
    }
    
    function displayRequestJson(data) {
        const requestJson = document.getElementById('requestJson');
        requestJson.textContent = JSON.stringify(data, null, 2);
        requestJson.classList.remove('empty');
    }
    
    function displayResponseJson(data) {
        const responseJson = document.getElementById('responseJson');
        responseJson.textContent = JSON.stringify(data, null, 2);
        responseJson.classList.remove('empty');
    }
    

    

    
    function resetForm() {
        // Reset form fields
        document.getElementById('verificationForm').reset();
        
        // Reset geographic dropdowns
        const geographicSelects = [
            'permanentDivision', 'permanentDistrict', 'permanentUpazila',
            'presentDivision', 'presentDistrict', 'presentUpazila'
        ];
        
        geographicSelects.forEach(selectId => {
            const select = document.getElementById(selectId);
            if (select) {
                select.innerHTML = '<option value="">নির্বাচন করুন</option>';
                select.disabled = selectId.includes('Division') ? false : true;
            }
        });
        
        // Clear JSON panels
        clearJsonPanels();
        

    }
    
    function clearJsonPanels() {
        const requestJson = document.getElementById('requestJson');
        const responseJson = document.getElementById('responseJson');
        
        requestJson.textContent = 'No request data yet';
        requestJson.classList.add('empty');
        
        responseJson.textContent = 'No response data yet';
        responseJson.classList.add('empty');
    }
    
    function initializeGeographicDropdowns() {        
        console.log('Initializing geographic dropdowns...');
        loadDivisions('permanentDivision');
        loadDivisions('presentDivision');
        
        setupCascadingDropdowns('permanent');
        setupCascadingDropdowns('present');
        console.log('Geographic dropdowns initialized');
    }
    
    function cleanupSearchableSelect(selectId) {
        const select = document.getElementById(selectId);
        if (!select) return;
        
        const parentContainer = select.parentElement;
        const existingWrappers = parentContainer.querySelectorAll('.searchable-select-wrapper');
                
        existingWrappers.forEach(wrapper => {
            
            if (wrapper.originalSelect) {
                const originalSelect = wrapper.originalSelect;
                originalSelect.style.display = 'block';
                originalSelect.style.position = 'static';
                originalSelect.style.left = 'auto';
                originalSelect.style.visibility = 'visible';
                
                parentContainer.replaceChild(originalSelect, wrapper);
            } else {
                const hiddenInput = wrapper.querySelector('input[type="hidden"][id="' + selectId + '"]');
                if (hiddenInput) {
                    const newSelect = document.createElement('select');
                    newSelect.id = selectId;
                    newSelect.name = hiddenInput.name;
                    newSelect.className = 'form-select';
                    newSelect.disabled = true;
                    newSelect.innerHTML = '<option value="">জেলা নির্বাচন করুন</option>';
                    
                    parentContainer.replaceChild(newSelect, wrapper);
                } else {
                    wrapper.remove();
                }
            }
        });
        
        if (select.searchableSelect) {
            select.searchableSelect = null;
        }
    }
    
    function createSearchableSelect(selectId, placeholder) {
        const originalSelect = document.getElementById(selectId);
        if (!originalSelect) return null;
        
        const container = originalSelect.parentElement;
        
        const existingWrapper = container.querySelector('.searchable-select-wrapper');
        if (existingWrapper) {
            existingWrapper.remove();
        }
        
        const wrapper = document.createElement('div');
        wrapper.className = 'searchable-select-wrapper position-relative';
        wrapper.style.position = 'relative';
        wrapper.style.width = '100%';
        
        const displayDiv = document.createElement('div');
        displayDiv.className = 'form-select searchable-display';
        displayDiv.style.cssText = `
            cursor: pointer;
            user-select: none;
            position: relative;
            width: 100%;
            background: white;
            border: 1px solid #ced4da;
            border-radius: 0.375rem;
            padding: 0.375rem 0.75rem;
            font-size: 1rem;
            line-height: 1.5;
            color: #212529;
        `;
        displayDiv.textContent = placeholder;
        
        const dropdown = document.createElement('div');
        dropdown.className = 'searchable-dropdown';
        dropdown.style.cssText = `
            position: absolute;
            top: 100%;
            left: 0;
            right: 0;
            background: white;
            border: 1px solid #ced4da;
            border-top: none;
            border-radius: 0 0 0.375rem 0.375rem;
            max-height: 200px;
            overflow-y: auto;
            z-index: 1000;
            display: none;
        `;
        
        const searchInput = document.createElement('input');
        searchInput.type = 'text';
        searchInput.className = 'form-control';
        searchInput.placeholder = 'Search...';
        searchInput.style.cssText = `
            border: none;
            border-bottom: 1px solid #dee2e6;
            border-radius: 0;
            margin: 0;
        `;
        
        const optionsContainer = document.createElement('div');
        optionsContainer.className = 'searchable-options';
        
        // Create hidden input to store the actual value
        const hiddenInput = document.createElement('input');
        hiddenInput.type = 'hidden';
        hiddenInput.id = selectId;
        hiddenInput.name = originalSelect.name;
        hiddenInput.value = originalSelect.value;
        
        // Populate options
        populateSearchableOptions(originalSelect, optionsContainer, displayDiv, hiddenInput);
        
        // Add event listeners
        displayDiv.addEventListener('click', function() {
            const isVisible = dropdown.style.display === 'block';
            dropdown.style.display = isVisible ? 'none' : 'block';
            
            if (!isVisible) {
                searchInput.focus();
            }
        });
        
        searchInput.addEventListener('input', function() {
            const searchTerm = this.value.toLowerCase();
            const options = optionsContainer.querySelectorAll('.searchable-option');
            
            options.forEach(option => {
                const text = option.textContent.toLowerCase();
                option.style.display = text.includes(searchTerm) ? 'block' : 'none';
            });
        });
        
        // Close dropdown when clicking outside
        document.addEventListener('click', function(e) {
            if (!wrapper.contains(e.target)) {
                dropdown.style.display = 'none';
            }
        });
        
        // Assemble the wrapper
        dropdown.appendChild(searchInput);
        dropdown.appendChild(optionsContainer);
        wrapper.appendChild(displayDiv);
        wrapper.appendChild(dropdown);
        wrapper.appendChild(hiddenInput);
        
        // Replace original select
        originalSelect.style.display = 'none';
        container.appendChild(wrapper);
        
        // Store reference for cleanup
        wrapper.originalSelect = originalSelect;
        
        return wrapper;
    }
    
    function populateSearchableOptions(originalSelect, container, displayDiv, hiddenInput) {
        container.innerHTML = '';
        
        Array.from(originalSelect.options).forEach(option => {
            if (option.value) {
                const optionDiv = document.createElement('div');
                optionDiv.className = 'searchable-option';
                optionDiv.textContent = option.textContent;
                optionDiv.style.cssText = `
                    padding: 0.5rem 0.75rem;
                    cursor: pointer;
                    border-bottom: 1px solid #f8f9fa;
                `;
                
                optionDiv.addEventListener('mouseenter', function() {
                    this.style.backgroundColor = '#f8f9fa';
                });
                
                optionDiv.addEventListener('mouseleave', function() {
                    this.style.backgroundColor = 'white';
                });
                
                optionDiv.addEventListener('click', function() {
                    displayDiv.textContent = option.textContent;
                    hiddenInput.value = option.value;
                    hiddenInput.setAttribute('data-id', option.value);
                    container.parentElement.style.display = 'none';
                    
                    // Trigger change event
                    const event = new Event('change', { bubbles: true });
                    hiddenInput.dispatchEvent(event);
                });
                
                container.appendChild(optionDiv);
            }
        });
    }
    
    function loadDivisions(selectId) {
        const select = document.getElementById(selectId);
        if (!select) return;
        
        console.log('Loading divisions for:', selectId, 'Username:', username);
        
        fetch(`/api/geo/divisions?username=${encodeURIComponent(username)}`)
            .then(response => {
                console.log('Divisions response status:', response.status);
                return response.json();
            })
            .then(data => {
                console.log('Divisions data:', data);
                if (data.success && data.data) {
                    select.innerHTML = '<option value="">বিভাগ নির্বাচন করুন</option>';
                    data.data.forEach(division => {
                        const option = document.createElement('option');
                        option.value = division.id;
                        option.textContent = division.bn_name || division.name;
                        select.appendChild(option);
                    });
                    select.disabled = false;
                    console.log('Loaded', data.data.length, 'divisions');
                } else {
                    console.error('Divisions data not in expected format:', data);
                }
            })
            .catch(error => {
                console.error('Error loading divisions:', error);
            });
    }
    
    function loadDistricts(divisionId, selectId) {
        const select = document.getElementById(selectId);
        if (!select) return;
        
        select.innerHTML = '<option value="">জেলা নির্বাচন করুন</option>';
        select.disabled = true;
        
        if (!divisionId) return;
        
        fetch(`/api/geo/districts?username=${encodeURIComponent(username)}&divisionId=${encodeURIComponent(divisionId)}`)
            .then(response => response.json())
            .then(data => {
                if (data.success && data.data) {
                    data.data.forEach(district => {
                        const option = document.createElement('option');
                        option.value = district.id;
                        option.textContent = district.bn_name || district.name;
                        select.appendChild(option);
                    });
                    select.disabled = false;
                }
            })
            .catch(error => {
                console.error('Error loading districts:', error);
            });
    }
    
    function loadUpazilas(districtId, selectId) {
        const select = document.getElementById(selectId);
        if (!select) return;
        
        select.innerHTML = '<option value="">উপজেলা নির্বাচন করুন</option>';
        select.disabled = true;
        
        if (!districtId) return;
        
        fetch(`/api/geo/upazilas?username=${encodeURIComponent(username)}&districtId=${encodeURIComponent(districtId)}`)
            .then(response => response.json())
            .then(data => {
                if (data.success && data.data) {
                    data.data.forEach(upazila => {
                        const option = document.createElement('option');
                        option.value = upazila.id;
                        option.textContent = upazila.bn_name || upazila.name;
                        select.appendChild(option);
                    });
                    select.disabled = false;
                }
            })
            .catch(error => {
                console.error('Error loading upazilas:', error);
            });
    }
    
    function setupCascadingDropdowns(prefix) {
        const divisionSelect = document.getElementById(`${prefix}Division`);
        const districtSelect = document.getElementById(`${prefix}District`);
        const upazilaSelect = document.getElementById(`${prefix}Upazila`);
        
        if (divisionSelect) {
            divisionSelect.addEventListener('change', function() {
                const divisionId = this.value;
                
                if (districtSelect) {
                    districtSelect.innerHTML = '<option value="">জেলা নির্বাচন করুন</option>';
                    districtSelect.disabled = true;
                }
                
                if (upazilaSelect) {
                    upazilaSelect.innerHTML = '<option value="">উপজেলা নির্বাচন করুন</option>';
                    upazilaSelect.disabled = true;
                }
                
                cleanupSearchableSelect(`${prefix}District`);
                
                if (divisionId) {
                    loadDistricts(divisionId, `${prefix}District`);
                }
            });
        }
        
        if (districtSelect) {
            districtSelect.addEventListener('change', function() {
                const districtId = this.value;
                
                if (upazilaSelect) {
                    upazilaSelect.innerHTML = '<option value="">উপজেলা নির্বাচন করুন</option>';
                    upazilaSelect.disabled = true;
                }
                
                cleanupSearchableSelect(`${prefix}Upazila`);
                
                if (districtId) {
                    loadUpazilas(districtId, `${prefix}Upazila`);
                }
            });
        }
        
        document.addEventListener('change', function(e) {
            if (e.target.id === `${prefix}District` && e.target.type === 'hidden') {
                const districtId = e.target.getAttribute('data-id');
                
                if (districtId && districtId !== '') {
                    const upazilaSelect = document.getElementById(`${prefix}Upazila`);
                    
                    if (upazilaSelect) {
                        upazilaSelect.innerHTML = '<option value="">উপজেলা নির্বাচন করুন</option>';
                        upazilaSelect.disabled = true;
                    }
                    
                    cleanupSearchableSelect(`${prefix}Upazila`);
                    
                    loadUpazilas(districtId, `${prefix}Upazila`);
                }
            }
        });
    }
    
    function showJsonConfirmationModal() {
        // Build the request object for display
        const form = document.getElementById('verificationForm');
        const formData = new FormData(form);
        const requestData = buildRequestObject(formData);
        
        // Display the JSON in the modal
        const jsonPreview = document.getElementById('jsonPreview');
        jsonPreview.textContent = JSON.stringify(requestData, null, 2);
        
        // Also update the API debug panel request JSON
        displayRequestJson(requestData);
        
        // Show the modal
        const modal = new bootstrap.Modal(document.getElementById('jsonConfirmationModal'));
        modal.show();
    }
    
    function validateForm() {
        const requiredFields = [
            { id: 'nidType', label: 'এনআইডি ধরন' },
            { id: 'nidValue', label: 'এনআইডি নম্বর' },
            { id: 'dateOfBirth', label: 'জন্ম তারিখ' }
        ];
        
        for (const field of requiredFields) {
            const fieldElement = document.getElementById(field.id);
            if (!fieldElement.value.trim()) {
                alert(`অনুগ্রহ করে ${field.label} ফিল্ডটি পূরণ করুন।`);
                fieldElement.focus();
                return false;
            }
        }
        
        return true;
    }
    
    const modalLoginBtn = document.getElementById('modalLoginBtn');
    const modalLoginForm = document.getElementById('modalLoginForm');
    const loginError = document.getElementById('loginError');
    const loginErrorMessage = document.getElementById('loginErrorMessage');
    
    if (modalLoginBtn) {
        modalLoginBtn.addEventListener('click', function() {
            const formData = new FormData(modalLoginForm);
            
            this.querySelector('.loading').style.display = 'inline-block';
            this.querySelector('.normal').style.display = 'none';
            this.disabled = true;
            
            loginError.style.display = 'none';
            
            fetch('/third-party-login', {
                method: 'POST',
                body: formData,
                headers: {
                    'X-Requested-With': 'XMLHttpRequest'
                }
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    const modal = bootstrap.Modal.getInstance(document.getElementById('thirdPartyLoginModal'));
                    modal.hide();
                    window.location.reload();
                } else {
                    loginErrorMessage.textContent = data.error || 'Login failed';
                    loginError.style.display = 'block';
                }
            })
            .catch(error => {
                console.error('Error:', error);
                loginErrorMessage.textContent = 'Network error. Please try again.';
                loginError.style.display = 'block';
            })
            .finally(() => {
                this.querySelector('.loading').style.display = 'none';
                this.querySelector('.normal').style.display = 'inline-block';
                this.disabled = false;
            });
        });
    }
    
    // Add event listener for the JSON confirmation modal submit button
    const confirmSubmitBtn = document.getElementById('confirmSubmitBtn');
    if (confirmSubmitBtn) {
        confirmSubmitBtn.addEventListener('click', function() {
            // Hide the modal
            const modal = bootstrap.Modal.getInstance(document.getElementById('jsonConfirmationModal'));
            modal.hide();
            
            // Submit the form via AJAX
            submitFormViaAjax();
        });
    }
    

});
