document.addEventListener('DOMContentLoaded', () => {

    // 모달 오픈 이벤트
    workOrderGrid.on('click', ev => {
        if (ev.columnName === 'purchaseRequest') {
            const rowData = workOrderGrid.getRow(ev.rowKey);

            // 모달 열기
            $('#purchaseModal').modal('show');

            // 작업지시 ID 세팅
            document.getElementById('work_order_id').value = rowData.workOrderId;

            // 부족 자재 목록 초기화
            const shortageList = document.getElementById('shortageList');
            shortageList.innerHTML = '';

            // hidden input 초기화
            const materialIdInput = document.getElementById('material_id');
            const requireQtyInput = document.getElementById('require_qty');
            materialIdInput.value = '';
            requireQtyInput.value = '';

            // 부족 자재 데이터를 가져와서 div와 hidden input에 세팅
            fetch(`/pm/purchaseInfo?work_order_id=${rowData.workOrderId}`)
                .then(res => res.json())
                .then(data => {
                    console.log("발주자재 목록:", data);
                    
                    // 보기용 div
                    data.forEach((item, index) => {
                        const div = document.createElement('div');
                        div.textContent = `${item.materialName}(${item.materialId}) : (생산 필요: ${item.totalNeededQuantity}개 / 보유 재고: ${item.stockQuantity}개) => 추가 필요: ${item.requiredQty}개`;
                        shortageList.appendChild(div);
                    });

                    // hidden input에 배열처럼 저장 (콤마로 구분하거나 JSON)
                    materialIdInput.value = data.map(d => d.materialId).join(',');
                    requireQtyInput.value = data.map(d => d.requiredQty).join(',');
                });
        }
    });

    // 발주 확정 버튼
    document.getElementById('purchaseConfirmBtn').addEventListener('click', async () => {
        const workOrderId = document.getElementById('work_order_id').value;
        const materialIds = document.getElementById('material_id').value.split(',');
        const requireQtys = document.getElementById('require_qty').value.split(',');

        // WorkOrderShortageDTO 배열 생성
        const shortages = materialIds.map((id, i) => ({
            workOrderId: workOrderId,
            materialId: id,
            requireQty: Number(requireQtys[i])
            // status는 서버에서 "요청중"으로 처리
        }));

        console.log("전송 데이터:", shortages);

        try {
            const res = await fetch('/pm/purchaseRegist', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    [csrfHeader]: csrfToken
                },
                body: JSON.stringify(shortages)
            });

            if (res.ok) {
                alert('발주 요청 완료!');
                $('#purchaseModal').modal('hide');
				
				// 발주 확정 버튼 비활성화
				const btn = document.getElementById("purchaseConfirmBtn");
				btn.disabled = true;
				btn.textContent = "발주 요청 완료"; 
				
                loadWorkOrder();
            } else {
                alert('등록 실패!');
            }
        } catch (err) {
            console.error(err);
            alert('오류 발생');
        }
    });

});