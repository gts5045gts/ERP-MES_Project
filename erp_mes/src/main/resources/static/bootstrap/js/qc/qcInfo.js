document.addEventListener('DOMContentLoaded', function() {
    // CSRF 토큰
    const token = document.querySelector('meta[name="_csrf"]').content;
    const header = document.querySelector('meta[name="_csrf_header"]').content;

    // HTML에 선언된 pageData 객체에서 데이터를 가져옵니다.
    const { processes, materials, inspectionFMs, units, products } = pageData;

    // HTML 엘리먼트 참조
    const targetTypeRadios = document.querySelectorAll('input[name="targetType"]');
    const materialDropdownContainer = document.getElementById('materialDropdownContainer');
    const processDropdownContainer = document.getElementById('processDropdownContainer');
    const targetCodeMaterial = document.getElementById('targetCodeMaterial');
    const targetCodeProcess = document.getElementById('targetCodeProcess');
    const targetCodeProduct = document.getElementById('targetCodeProduct'); // 제품 드롭다운도 사용하려면 추가

	// 왼쪽 테이블 (dataTable1) 검색 기능
	const searchInput1 = document.querySelector('#dataTable1').closest('.card-body').querySelector('input[type="text"]');
	const searchBtn1 = document.querySelector('#dataTable1').closest('.card-body').querySelector('#searchBtn1');
	const tableBody1 = document.querySelector('#dataTable1 tbody');
	const rows1 = tableBody1.querySelectorAll('tr');

	const filterTable1 = () => {
	    const searchText = searchInput1.value.toLowerCase();
	    rows1.forEach(row => {
	        const rowData = Array.from(row.cells).map(cell => cell.textContent.toLowerCase()).join(' ');
	        row.style.display = rowData.includes(searchText) ? '' : 'none';
	    });
	};
	searchBtn1.addEventListener('click', filterTable1);
	searchInput1.addEventListener('keyup', (event) => {
	    if (event.key === 'Enter') { filterTable1(); }
	});

	// 오른쪽 테이블 (dataTable2) 검색 기능
	const searchInput2 = document.querySelector('#dataTable2').closest('.card-body').querySelector('input[type="text"]');
	const searchBtn2 = document.querySelector('#dataTable2').closest('.card-body').querySelector('#searchBtn2');
	const tableBody2 = document.querySelector('#dataTable2 tbody');
	const rows2 = tableBody2.querySelectorAll('tr');

	const filterTable2 = () => {
	    const searchText = searchInput2.value.toLowerCase();
	    rows2.forEach(row => {
	        const rowData = Array.from(row.cells).map(cell => cell.textContent.toLowerCase()).join(' ');
	        row.style.display = rowData.includes(searchText) ? '' : 'none';
	    });
	};
	searchBtn2.addEventListener('click', filterTable2);
	searchInput2.addEventListener('keyup', (event) => {
	    if (event.key === 'Enter') { filterTable2(); }
	});

	// 왼쪽 모달 ('검사 유형별 기준 등록')의 '저장' 버튼 클릭 이벤트
	document.getElementById('saveRecordBtn').addEventListener('click', function() {
	    const formData = {
	        inspectionType: document.getElementById('inspectionTypeId').value,
	        itemName: document.getElementById('itemName_record').value,
	        methodName: document.getElementById('methodName').value
	    };
	    fetch('/quality/fm', {
	        method: 'POST',
	        headers: { 'Content-Type': 'application/json', [header]: token },
	        body: JSON.stringify(formData)
	    })
	    .then(response => response.json())
	    .then(data => {
	        alert(data.message);
	        if (data.success) { window.location.reload(); }
	    })
	    .catch(error => { console.error('Error:', error); alert('등록 실패: 서버 연결 또는 응답 오류'); });
	});

    // 오른쪽 모달 ('검사 항목 등록 및 공차 설정')의 '저장' 버튼 클릭 이벤트
    document.getElementById('saveItemBtn').addEventListener('click', function() {
        const targetType = document.querySelector('input[name="targetType"]:checked').value;
        let targetId = null;

		if (targetType === 'material') {
		    targetId = document.getElementById('targetCodeMaterial').value;
		} else if (targetType === 'process') {
		    targetId = document.getElementById('targetCodeProcess').value;
		}
		// targetId가 빈 문자열이면 null로 처리하는 로직을 추가
		if (targetId === "") {
		    targetId = null;
		}

        const formData = {
            materialId: targetType === 'material' ? targetId : null,
            proId: targetType === 'process' ? targetId : null,
            inspectionFMId: document.getElementById('inspectionFMId').value,
            toleranceValue: document.getElementById('toleranceValue').value,
            unit: document.getElementById('unit').value
        };
		
		console.log('전송될 formData:', formData);

        fetch('/quality/item', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json', [header]: token },
            body: JSON.stringify(formData)
        })
        .then(response => response.json())
        .then(data => {
            alert(data.message);
            if (data.success) { window.location.reload(); }
        })
        .catch(error => { console.error('Error:', error); alert('등록 실패: 서버 연결 또는 응답 오류'); });
    });

	// 테이블 행 클릭 시 선택 상태 토글 (왼쪽 테이블)
	document.getElementById('dataTable1').addEventListener('click', function(event) {
	    const row = event.target.closest('tr');
	    if (row?.parentNode.tagName === 'TBODY') { row.classList.toggle('selected'); }
	});

	// '삭제' 버튼 클릭 이벤트 (왼쪽 테이블)
	document.getElementById('deleteFmBtn').addEventListener('click', function() {
	    const selectedRows = document.querySelectorAll('#dataTable1 tbody tr.selected');
	    if (selectedRows.length === 0) { alert('삭제할 항목을 선택해주세요.'); return; }
	    if (!confirm('선택된 항목을 정말 삭제하시겠습니까?')) { return; }
	    const idsToDelete = Array.from(selectedRows).map(row => row.dataset.id);
	    fetch('/quality/fm', {
	        method: 'DELETE',
	        headers: { 'Content-Type': 'application/json', [header]: token },
	        body: JSON.stringify(idsToDelete)
	    })
	    .then(response => response.json())
	    .then(data => {
	        alert(data.message);
	        if (data.success) { window.location.reload(); }
	    })
	    .catch(error => { console.error('Error:', error); alert('삭제 실패: 서버 연결 또는 응답 오류'); });
	});

	// 테이블 행 클릭 시 선택 상태 토글 (오른쪽 테이블)
	document.getElementById('dataTable2').addEventListener('click', function(event) {
	    const row = event.target.closest('tr');
	    if (row?.parentNode.tagName === 'TBODY') { row.classList.toggle('selected'); }
	});

	// '삭제' 버튼 클릭 이벤트 (오른쪽 테이블)
	document.getElementById('deleteItemBtn').addEventListener('click', function() {
	    const selectedRows = document.querySelectorAll('#dataTable2 tbody tr.selected');
	    if (selectedRows.length === 0) { alert('삭제할 항목을 선택해주세요.'); return; }
	    if (!confirm('선택된 항목을 정말 삭제하시겠습니까?')) { return; }
	    const idsToDelete = Array.from(selectedRows).map(row => row.dataset.id);
	    fetch('/quality/item', {
	        method: 'DELETE',
	        headers: { 'Content-Type': 'application/json', [header]: token },
	        body: JSON.stringify(idsToDelete)
	    })
	    .then(response => response.json())
	    .then(data => {
	        alert(data.message);
	        if (data.success) { window.location.reload(); }
	    })
	    .catch(error => { console.error('Error:', error); alert('삭제 실패: 서버 연결 또는 응답 오류'); });
	});

    // 검사 대상 라디오 버튼 변경 이벤트 리스너
    targetTypeRadios.forEach(radio => {
        radio.addEventListener('change', (event) => {
            const selectedValue = event.target.value;
            // 모든 드롭다운 컨테이너를 숨김
            materialDropdownContainer.style.display = 'none';
            processDropdownContainer.style.display = 'none';
            // 제품 드롭다운도 있다면 추가
            // productDropdownContainer.style.display = 'none';
            
            // 선택된 라디오 버튼에 해당하는 드롭다운만 표시
            if (selectedValue === 'material') {
                materialDropdownContainer.style.display = 'block';
            } else if (selectedValue === 'process') {
                processDropdownContainer.style.display = 'block';
            }
        });
    });

    // 모달이 열릴 때 이벤트 리스너 추가
    $('#standardModal').on('show.bs.modal', function() {
        const inspectionFMDropdown = document.getElementById('inspectionFMId');
        const unitDropdown = document.getElementById('unit');

        // 초기화
        inspectionFMDropdown.innerHTML = '<option value="">선택</option>';
        unitDropdown.innerHTML = '<option value="">선택</option>';

        // 공정 드롭다운 채우기
        const targetCodeProcess = document.getElementById('targetCodeProcess');
        targetCodeProcess.innerHTML = '<option value="">선택</option>';
        if (processes && processes.length > 0) {
            processes.forEach(proc => {
                const option = document.createElement('option');
                option.value = proc.proId;
                option.textContent = proc.proNm;
                targetCodeProcess.appendChild(option);
            });
        }

        // 자재 드롭다운 채우기
        const targetCodeMaterial = document.getElementById('targetCodeMaterial');
        targetCodeMaterial.innerHTML = '<option value="">선택</option>';
        if (materials && materials.length > 0) {
            materials.forEach(mat => {
                const option = document.createElement('option');
                option.value = mat.materialId;
                option.textContent = mat.materialName;
                targetCodeMaterial.appendChild(option);
            });
        }
        
        // 검사 유형 드롭다운 채우기
        if (inspectionFMs && inspectionFMs.length > 0) {
            inspectionFMs.forEach(fm => {
                const option = document.createElement('option');
                option.value = fm.inspectionFMId;
                option.textContent = `${fm.inspectionType} - ${fm.itemName}`;
                inspectionFMDropdown.appendChild(option);
            });
        }

        // 단위 드롭다운 채우기
        if (units && units.length > 0) {
            units.forEach(unit => {
                const option = document.createElement('option');
                option.value = unit.comDtNm;
                option.textContent = unit.comDtNm;
                unitDropdown.appendChild(option);
            });
        }
    });

    // 모달이 닫힐 때 폼 초기화
    $('#standardModal').on('hidden.bs.modal', function() {
        document.getElementById('item-form').reset();
    });
});