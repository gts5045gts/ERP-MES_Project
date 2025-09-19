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
    // ... (기존 검색/삭제 로직) ...

    // 오른쪽 모달 ('검사 항목 등록 및 공차 설정')의 '저장' 버튼 클릭 이벤트
    document.getElementById('saveItemBtn').addEventListener('click', function() {
        const targetType = document.querySelector('input[name="targetType"]:checked').value;
        let targetId = null;

        if (targetType === 'material') {
            targetId = document.getElementById('targetCodeMaterial').value;
        } else if (targetType === 'process') {
            targetId = document.getElementById('targetCodeProcess').value;
        } else if (targetType === 'product') { // 제품도 처리하려면 추가
            targetId = document.getElementById('targetCodeProduct').value;
        }

        const formData = {
            materialId: targetType === 'material' ? targetId : null,
            processId: targetType === 'process' ? targetId : null,
            productId: targetType === 'product' ? targetId : null, // 제품도 처리하려면 추가
            inspectionFMId: document.getElementById('inspectionFMId').value,
            toleranceValue: document.getElementById('toleranceValue').value,
            unit: document.getElementById('unit').value
        };

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

    // ... (기존 테이블 검색, 삭제 로직) ...

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