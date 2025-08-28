document.addEventListener('DOMContentLoaded', function() {
    const username = document.body.getAttribute('data-username');
    
    initializeGeographicDropdowns();
    
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
        if (!validateForm()) {
            e.preventDefault();
            return;
        }
                
        const submitBtn = document.getElementById('submitBtn');
        submitBtn.querySelector('.loading').style.display = 'inline-block';
        submitBtn.querySelector('.normal').style.display = 'none';
        submitBtn.disabled = true;
    });
    
    function initializeGeographicDropdowns() {        
        loadDivisions('permanentDivision');
        loadDivisions('presentDivision');
        
        setupCascadingDropdowns('permanent');
        setupCascadingDropdowns('present');
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
        `;
        displayDiv.innerHTML = '<span class="selected-text">' + placeholder + '</span><i class="fas fa-chevron-down position-absolute" style="right: 10px; top: 50%; transform: translateY(-50%);"></i>';
        
        const searchInput = document.createElement('input');
        searchInput.type = 'text';
        searchInput.className = 'form-control searchable-input';
        searchInput.placeholder = placeholder;
        searchInput.style.display = 'none';
        
        const dropdownList = document.createElement('div');
        dropdownList.className = 'searchable-dropdown-list';
        dropdownList.style.cssText = `
            position: absolute;
            top: 100%;
            left: 0;
            right: 0;
            max-height: 200px;
            overflow-y: auto;
            background: white;
            border: 1px solid #ced4da;
            border-top: none;
            border-radius: 0 0 0.375rem 0.375rem;
            z-index: 1000;
            display: none;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        `;
        
        const hiddenInput = document.createElement('input');
        hiddenInput.type = 'hidden';
        hiddenInput.name = originalSelect.name;
        hiddenInput.id = originalSelect.id;
        
        wrapper.appendChild(displayDiv);
        wrapper.appendChild(searchInput);
        wrapper.appendChild(dropdownList);
        wrapper.appendChild(hiddenInput);
        
        container.replaceChild(wrapper, originalSelect);
        
        wrapper.originalSelect = originalSelect;
        
        displayDiv.addEventListener('click', function() {
            if (originalSelect.disabled) return;
            searchInput.style.display = 'block';
            displayDiv.style.display = 'none';
            searchInput.focus();
            showDropdown();
        });
        
        searchInput.addEventListener('input', function() {
            filterOptions(this.value);
        });
        
        searchInput.addEventListener('blur', function() {
            setTimeout(() => {
                searchInput.style.display = 'none';
                displayDiv.style.display = 'block';
                dropdownList.style.display = 'none';
            }, 200);
        });
        
        searchInput.addEventListener('keydown', function(e) {
            if (e.key === 'Enter') {
                e.preventDefault();
                const firstOption = dropdownList.querySelector('.dropdown-option:not(.hidden)');
                if (firstOption) {
                    selectOption(firstOption);
                }
            }
        });
        
        function showDropdown() {
            const options = Array.from(originalSelect.options).slice(1); // Skip first empty option
            dropdownList.innerHTML = '';
            
            options.forEach(option => {
                const optionDiv = document.createElement('div');
                optionDiv.className = 'dropdown-option';
                optionDiv.style.cssText = `
                    padding: 8px 12px;
                    cursor: pointer;
                    border-bottom: 1px solid #f0f0f0;
                `;
                optionDiv.textContent = option.textContent;
                optionDiv.setAttribute('data-value', option.value);
                optionDiv.setAttribute('data-id', option.getAttribute('data-id'));
                
                optionDiv.addEventListener('click', function() {
                    selectOption(this);
                });
                
                optionDiv.addEventListener('mouseenter', function() {
                    this.style.backgroundColor = '#f8f9fa';
                });
                
                optionDiv.addEventListener('mouseleave', function() {
                    this.style.backgroundColor = 'white';
                });
                
                dropdownList.appendChild(optionDiv);
            });
            
            dropdownList.style.display = 'block';
        }
        
        function filterOptions(searchTerm) {
            const options = dropdownList.querySelectorAll('.dropdown-option');
            const term = searchTerm.toLowerCase();
            
            options.forEach(option => {
                const text = option.textContent.toLowerCase();
                if (text.includes(term)) {
                    option.classList.remove('hidden');
                    option.style.display = 'block';
                } else {
                    option.classList.add('hidden');
                    option.style.display = 'none';
                }
            });
        }
        
        function selectOption(optionElement) {
            const value = optionElement.getAttribute('data-value');
            const text = optionElement.textContent;
            const id = optionElement.getAttribute('data-id');
            
            hiddenInput.value = value;
            hiddenInput.setAttribute('data-id', id);
            displayDiv.querySelector('.selected-text').textContent = text;
            displayDiv.setAttribute('data-id', id);
            
            searchInput.style.display = 'none';
            displayDiv.style.display = 'block';
            dropdownList.style.display = 'none';
            searchInput.value = '';
            
            const event = new Event('change', { bubbles: true });
            hiddenInput.dispatchEvent(event);
        }
        
        function updateDisabledState() {
            if (originalSelect.disabled) {
                displayDiv.classList.add('disabled');
                displayDiv.style.cursor = 'not-allowed';
            } else {
                displayDiv.classList.remove('disabled');
                displayDiv.style.cursor = 'pointer';
            }
        }
        
        updateDisabledState();
        
        const observer = new MutationObserver(updateDisabledState);
        observer.observe(originalSelect, { attributes: true, attributeFilter: ['disabled'] });
        
        return {
            getValue: () => hiddenInput.value,
            getSelectedId: () => displayDiv.getAttribute('data-id'),
            setValue: (value, text, id) => {
                hiddenInput.value = value;
                displayDiv.querySelector('.selected-text').textContent = text;
                displayDiv.setAttribute('data-id', id);
            },
            reset: () => {
                hiddenInput.value = '';
                hiddenInput.removeAttribute('data-id');
                displayDiv.querySelector('.selected-text').textContent = placeholder;
                displayDiv.removeAttribute('data-id');
            },
            setDisabled: (disabled) => {
                originalSelect.disabled = disabled;
                updateDisabledState();
            }
        };
    }
    
    function loadDivisions(selectId) {
        const select = document.getElementById(selectId);
                
        if (!select) {
            return;
        }
        
        fetch(`/api/geo/divisions?username=${encodeURIComponent(username)}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP ${response.status}: ${response.statusText}`);
                }
                return response.json();
            })
            .then(data => {
                if (data.success && data.data) {
                    select.innerHTML = '<option value="">বিভাগ নির্বাচন করুন</option>';
                    
                    data.data.forEach(division => {
                        const option = document.createElement('option');
                        option.value = division.bn_name;
                        option.textContent = division.bn_name;
                        option.setAttribute('data-id', division.id);
                        select.appendChild(option);
                    });
                    
                } else {
                    console.error('Failed to load divisions:', data.error);
                }
            })
            .catch(error => {
                console.error('Error loading divisions:', error);
            });
    }
    
    function loadDistricts(divisionId, districtSelectId) {
        const select = document.getElementById(districtSelectId);
        
        cleanupSearchableSelect(districtSelectId);
        
        select.innerHTML = '<option value="">জেলা নির্বাচন করুন</option>';
        select.disabled = true;
        
        if (!divisionId) return;
        
        fetch(`/api/geo/districts?username=${encodeURIComponent(username)}&divisionId=${encodeURIComponent(divisionId)}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP ${response.status}: ${response.statusText}`);
                }
                return response.json();
            })
            .then(data => {
                if (data.success && data.data) {
                    select.innerHTML = '<option value="">জেলা নির্বাচন করুন</option>';
                    
                    data.data.forEach(district => {
                        const option = document.createElement('option');
                        option.value = district.bn_name;
                        option.textContent = district.bn_name;
                        option.setAttribute('data-id', district.id);
                        select.appendChild(option);
                    });
                    
                    select.disabled = false;
                    
                    const searchableSelect = createSearchableSelect(districtSelectId, 'জেলা নির্বাচন করুন');
                    select.searchableSelect = searchableSelect;
                } else {
                    console.error('Failed to load districts:', data.error);
                }
            })
            .catch(error => {
                console.error('Error loading districts:', error);
            });
    }
    
    function loadUpazilas(districtId, upazilaSelectId) {
        const select = document.getElementById(upazilaSelectId);
        
        cleanupSearchableSelect(upazilaSelectId);
        
        select.innerHTML = '<option value="">উপজেলা নির্বাচন করুন</option>';
        select.disabled = true;
        
        if (!districtId) return;
        
        fetch(`/api/geo/upazilas?username=${encodeURIComponent(username)}&districtId=${encodeURIComponent(districtId)}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP ${response.status}: ${response.statusText}`);
                }
                return response.json();
            })
            .then(data => {
                if (data.success && data.data) {
                    select.innerHTML = '<option value="">উপজেলা নির্বাচন করুন</option>';
                    
                    data.data.forEach(upazila => {
                        const option = document.createElement('option');
                        option.value = upazila.bn_name;
                        option.textContent = upazila.bn_name;
                        option.setAttribute('data-id', upazila.id);
                        select.appendChild(option);
                    });
                    
                    select.disabled = false;
                    
                    const searchableSelect = createSearchableSelect(upazilaSelectId, 'উপজেলা নির্বাচন করুন');
                    select.searchableSelect = searchableSelect;
                } else {
                    console.error('Failed to load upazilas:', data.error);
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
                const selectedOption = this.options[this.selectedIndex];
                const divisionId = selectedOption.getAttribute('data-id');
                
                if (districtSelect) {
                    districtSelect.innerHTML = '<option value="">জেলা নির্বাচন করুন</option>';
                    districtSelect.disabled = true;
                }
                if (upazilaSelect) {
                    upazilaSelect.innerHTML = '<option value="">উপজেলা নির্বাচন করুন</option>';
                    upazilaSelect.disabled = true;
                }
                
                cleanupSearchableSelect(`${prefix}District`);
                cleanupSearchableSelect(`${prefix}Upazila`);
                
                if (divisionId) {
                    loadDistricts(divisionId, `${prefix}District`);
                }
            });
        }
        
        // Add event listener for district change
        if (districtSelect) {
            districtSelect.addEventListener('change', function() {
                const selectedOption = this.options[this.selectedIndex];
                const districtId = selectedOption.getAttribute('data-id');
                
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
});
