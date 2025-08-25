document.addEventListener('DOMContentLoaded', function() {
    // Main page DOM elements
    const tableBody = document.getElementById('personnelOrdersTableBody');
    const searchBtn = document.getElementById('searchBtn');
    const issueBtn = document.getElementById('issueBtn');

    const orderTypeSelect = document.getElementById('orderTypeSelect');
    const orderDateStart = document.getElementById('orderDateStart');
    const orderDateEnd = document.getElementById('orderDateEnd');
    const paginationInfo = document.getElementById('paginationInfo');
    const prevPageBtn = document.getElementById('prevPageBtn');
    const nextPageBtn = document.getElementById('nextPageBtn');

    // Modal related DOM elements
    const registerModal = new bootstrap.Modal(document.getElementById('registerModal'));
    const modalDeptSelect = document.getElementById('modalDeptSelect');
    const modalPositionSelect = document.getElementById('modalPositionSelect');
    const modalEmployeeSearchInput = document.getElementById('modalEmployeeSearchInput');
    const modalSearchEmployeeBtn = document.getElementById('modalSearchEmployeeBtn');
    const modalEmployeeTableBody = document.getElementById('modalEmployeeTableBody');

    const selectedEmployeeNameSpan = document.getElementById('selectedEmployeeName');
    const selectedEmployeeIdSpan = document.getElementById('selectedEmployeeId');
    const selectedEmployeeCurrentDeptSpan = document.getElementById('selectedEmployeeCurrentDept');
    const selectedEmployeeCurrentPositionSpan = document.getElementById('selectedEmployeeCurrentPosition');
    const hiddenSelectedEmployeeDbId = document.getElementById('hiddenSelectedEmployeeDbId');

    const newOrderTypeSelect = document.getElementById('newOrderType');
    const newOrderDateInput = document.getElementById('newOrderDate');
    const newAssignedDepartmentSelect = document.getElementById('newAssignedDepartment');
    const newAssignedPositionSelect = document.getElementById('newAssignedPosition');
    const newNoteInput = document.getElementById('newNote');
    const confirmNewOrderBtn = document.getElementById('confirmNewOrderBtn');

    // Global variables
    let currentPage = 1;
    const itemsPerPage = 10;
    let totalEntries = 0;
    let allPersonnelOrders = [];
    let allModalEmployees = [];
    let selectedModalEmployee = null;

    // Dropdown options data (Should be loaded from backend API)

    // --- Mock Data (To be replaced by actual backend API calls) ---
    // Added 'id' field for unique identification, important for backend operations
    let mockPersonnelOrders = [ // 'let'으로 변경하여 삭제 등 수정 가능하게 함

    ];

    // Modal employees Mock Data
    const mockEmployees = [
        
    ];
    // --- // Mock Data ---

    // --- Main table related functions ---

    // Function to load and filter personnel orders
    function loadPersonnelOrders(filters = {}) {
        // TODO: Replace with actual backend API call
        // fetch('/api/personnel-orders?' + new URLSearchParams(filters).toString())
        // .then(response => response.json())
        // .then(data => {
        //     allPersonnelOrders = data;
        //     totalEntries = allPersonnelOrders.length;
        //     currentPage = 1;
        //     renderPersonnelOrderTable();
        // })
        // .catch(error => console.error('Error fetching personnel orders:', error));

        const filteredData = mockPersonnelOrders.filter(order => {
            const matchesType = (filters.orderType === '발령구분' || filters.orderType === 'ALL' || order.orderType === filters.orderType);
            const orderDate = new Date(order.orderDate);
            const startDate = filters.startDate ? new Date(filters.startDate) : null;
            const endDate = filters.endDate ? new Date(filters.endDate) : null;

            const matchesDate = (!startDate || orderDate >= startDate) && (!endDate || orderDate <= endDate);

            return matchesType && matchesDate;
        });

        allPersonnelOrders = filteredData;
        totalEntries = allPersonnelOrders.length;
        currentPage = 1;
        renderPersonnelOrderTable();
    }

    // Function to render the main personnel order table
    function renderPersonnelOrderTable() {
        tableBody.innerHTML = ''; // Clear existing rows
        // selectedRows.clear(); // Checkbox removed
        // selectAllCheckbox.checked = false; // Checkbox removed

        const startIndex = (currentPage - 1) * itemsPerPage;
        const endIndex = Math.min(startIndex + itemsPerPage, totalEntries);
        const currentData = allPersonnelOrders.slice(startIndex, endIndex);

        if (currentData && currentData.length > 0) {
            currentData.forEach((order, index) => {
                const row = document.createElement('tr');
                // Removed data-id as rows are no longer directly editable
                row.innerHTML = `
                    <td>${startIndex + index + 1}</td>
                    <td>${order.employeeId}</td>
                    <td>${order.employeeName}</td>
                    <td>${order.orderType}</td>
                    <td>${order.orderDate}</td>
                    <td>${order.position}</td>
                    <td>${order.department}</td>
                    <td>${order.createdAt}</td>
                `;
                tableBody.appendChild(row);

                // Removed checkbox event listener as checkboxes are removed
            });
        } else {
            tableBody.innerHTML = '<tr><td colspan="8" class="text-center py-4">데이터가 없습니다.</td></tr>';
        }
        updatePaginationInfo();
    }

    // Update pagination information display
    function updatePaginationInfo() {
        const startEntry = Math.min((currentPage - 1) * itemsPerPage + 1, totalEntries);
        const endEntry = Math.min(currentPage * itemsPerPage, totalEntries);
        paginationInfo.textContent = `Showing ${startEntry} to ${endEntry} of ${totalEntries} entries`;

        prevPageBtn.parentElement.classList.toggle('disabled', currentPage === 1);
        nextPageBtn.parentElement.classList.toggle('disabled', currentPage * itemsPerPage >= totalEntries);
    }

    // Removed selectAllCheckbox and updateSelectAllCheckbox functions as checkboxes are removed


    // --- Modal related functions ---

    // Populate department and position dropdowns in the modal
    function populateModalDropdowns() {
        modalDeptSelect.innerHTML = '<option value="">부서 선택</option>' + ALL_DEPARTMENTS.map(dept => `<option value="${dept}">${dept}</option>`).join('');
        modalPositionSelect.innerHTML = '<option value="">직급 선택</option>' + ALL_POSITIONS.map(pos => `<option value="${pos}">${pos}</option>`).join('');
        newAssignedDepartmentSelect.innerHTML = '<option value="">부서를 선택하세요</option>' + ALL_DEPARTMENTS.map(dept => `<option value="${dept}">${dept}</option>`).join('');
        newAssignedPositionSelect.innerHTML = '<option value="">직급을 선택하세요</option>' + ALL_POSITIONS.map(pos => `<option value="${pos}">${pos}</option>`).join('');
        newOrderTypeSelect.innerHTML = '<option value="">발령구분 선택</option>' + ORDER_TYPES.map(type => `<option value="${type}">${type}</option>`).join(''); // 발령구분 옵션 제한
    }

    // Search and render employees in the modal
    function searchAndRenderModalEmployees() {
        modalEmployeeTableBody.innerHTML = '<tr><td colspan="5" class="text-center">사원 검색 중...</td></tr>';
        const searchDept = modalDeptSelect.value;
        const searchPosition = modalPositionSelect.value;
        const searchKeyword = modalEmployeeSearchInput.value.toLowerCase();

        // TODO: Replace with actual backend API call for employee search
        // fetch('/api/employees/search?dept=' + searchDept + '&pos=' + searchPosition + '&keyword=' + searchKeyword)
        // .then(response => response.json())
        // .then(data => {
        //     allModalEmployees = data;
        //     renderModalEmployeeTable(allModalEmployees);
        // })
        // .catch(error => console.error('Error searching employees:', error));

        const filteredEmployees = mockEmployees.filter(emp => {
            const matchesDept = !searchDept || emp.department === searchDept;
            const matchesPosition = !searchPosition || emp.position === searchPosition;
            const matchesKeyword = !searchKeyword || emp.name.toLowerCase().includes(searchKeyword) || emp.empNo.toLowerCase().includes(searchKeyword);
            return matchesDept && matchesPosition && matchesKeyword;
        });
        allModalEmployees = filteredEmployees;
        renderModalEmployeeTable(allModalEmployees);
    }

    // Render employee table in the modal
    function renderModalEmployeeTable(employees) {
        modalEmployeeTableBody.innerHTML = '';
        if (employees && employees.length > 0) {
            employees.forEach(emp => {
                const row = document.createElement('tr');
                row.dataset.id = emp.id;
                row.dataset.empNo = emp.empNo;
                row.dataset.name = emp.name;
                row.dataset.department = emp.department;
                row.dataset.position = emp.position;
                row.innerHTML = `
                    <td>${emp.empNo}</td>
                    <td>${emp.name}</td>
                    <td>${emp.department}</td>
                    <td>${emp.position}</td>
                    <td>${emp.phone}</td>
                `;
                modalEmployeeTableBody.appendChild(row);

                // Employee selection event listener
                row.addEventListener('click', function() {
                    const prevSelected = modalEmployeeTableBody.querySelector('.selected-row');
                    if (prevSelected) {
                        prevSelected.classList.remove('selected-row');
                    }
                    this.classList.add('selected-row');

                    selectedModalEmployee = {
                        id: parseInt(this.dataset.id),
                        empNo: this.dataset.empNo,
                        name: this.dataset.name,
                        department: this.dataset.department,
                        position: this.dataset.position
                    };
                    populateSelectedEmployeeInfo(selectedModalEmployee);
                });
            });
        } else {
            modalEmployeeTableBody.innerHTML = '<tr><td colspan="5" class="text-center">사원 검색 결과가 없습니다.</td></tr>';
        }
    }

    // Display selected employee information in the modal (Image 3)
    function populateSelectedEmployeeInfo(employee) {
        if (employee) {
            selectedEmployeeNameSpan.textContent = employee.name;
            selectedEmployeeIdSpan.textContent = employee.empNo;
            selectedEmployeeCurrentDeptSpan.textContent = employee.department;
            selectedEmployeeCurrentPositionSpan.textContent = employee.position;
            hiddenSelectedEmployeeDbId.value = employee.id;
        } else {
            selectedEmployeeNameSpan.textContent = '선택되지 않음';
            selectedEmployeeIdSpan.textContent = '';
            selectedEmployeeCurrentDeptSpan.textContent = '';
            selectedEmployeeCurrentPositionSpan.textContent = '';
            hiddenSelectedEmployeeDbId.value = '';
        }
    }

    // Reset modal content and form fields
    function resetModal() {
        modalDeptSelect.value = '';
        modalPositionSelect.value = '';
        modalEmployeeSearchInput.value = '';
        renderModalEmployeeTable([]);
        populateSelectedEmployeeInfo(null);

        newOrderTypeSelect.value = '';
        newOrderDateInput.value = new Date().toISOString().slice(0, 10);
        newAssignedDepartmentSelect.value = '';
        newAssignedPositionSelect.value = '';
        newNoteInput.value = '';

        selectedModalEmployee = null;
    }

    // --- Event Listeners ---

    // Main page: Search button
    searchBtn.addEventListener('click', function() {
        const filters = {
            orderType: orderTypeSelect.value,
            startDate: orderDateStart.value,
            endDate: orderDateEnd.value
        };
        loadPersonnelOrders(filters);
    });

    // Main page: "발령" button (Open modal)
    issueBtn.addEventListener('click', function() { // "registerBtn" -> "issueBtn"
        resetModal();
        populateModalDropdowns();
        searchAndRenderModalEmployees();
        registerModal.show();
    });

    // Modal: Employee search button
    modalSearchEmployeeBtn.addEventListener('click', searchAndRenderModalEmployees);

    // Modal: Confirm button (Register new personnel order)
    confirmNewOrderBtn.addEventListener('click', function() {
        if (!selectedModalEmployee) {
            alert('발령할 사원을 먼저 선택해주세요.');
            return;
        }

        const newOrderData = {
            employeeDbId: selectedModalEmployee.id,
            employeeId: selectedModalEmployee.empNo,
            employeeName: selectedModalEmployee.name,
            orderType: newOrderTypeSelect.value,
            orderDate: newOrderDateInput.value,
            position: newAssignedPositionSelect.value,
            department: newAssignedDepartmentSelect.value,
            note: newNoteInput.value // 비고 필드는 여전히 입력 가능하도록 유지
        };

        // Validation (check required fields)
        if (!newOrderData.orderType || !newOrderData.orderDate || !newOrderData.department || !newOrderData.position) {
            alert('발령구분, 발령일자, 발령부서, 발령직급은 필수 입력 항목입니다.');
            return;
        }

        console.log('새로운 인사발령 데이터:', newOrderData);
        // Add to mock data (In real application, this is handled by backend and then reloaded)
        const mockNewId = mockPersonnelOrders.length > 0 ? Math.max(...mockPersonnelOrders.map(o => o.id)) + 1 : 1;
        mockPersonnelOrders.push({
            id: mockNewId,
            employeeId: newOrderData.employeeId,
            employeeName: newOrderData.employeeName,
            orderType: newOrderData.orderType,
            orderDate: newOrderData.orderDate,
            position: newOrderData.position,
            department: newOrderData.department,
            note: newOrderData.note,
            createdAt: new Date().toISOString().slice(0, 10)
        });
        alert('ex');
        registerModal.hide();
        loadPersonnelOrders();
    });

    // Main page: Previous/Next page buttons
    prevPageBtn.addEventListener('click', function(e) { e.preventDefault(); if (currentPage > 1) { currentPage--; renderPersonnelOrderTable(); } });
    nextPageBtn.addEventListener('click', function(e) { e.preventDefault(); if (currentPage * itemsPerPage < totalEntries) { currentPage++; renderPersonnelOrderTable(); } });

    // Removed Save and Delete button event listeners as buttons are removed

    // Initial data load for the main table
    loadPersonnelOrders();
});