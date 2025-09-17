// BOM Grid
window.bomGrid = new tui.Grid({
  el: document.getElementById('bom-grid'),
  columns: [
    { header: '원자재', name: 'materialId', sortable: true, align: 'center' },
    { header: '필요 수량', name: 'quantity', sortable: true, align: 'center' },
    { header: '단위', name: 'unit', sortable: true, align: 'center' },
    { header: '등록일', name: 'createdAt', sortable: true, align: 'center' },
    { header: '수정일', name: 'updatedAt', sortable: true, align: 'center' }
  ],
  bodyHeight: 'fitToParent'
});

// 특정 제품의 BOM 로드 (전역 등록)
window.loadBomByProduct = async function(productId) {
    if (!productId) return;

    try {
        const res = await fetch(`/masterData/bomList?product_id=${productId}`);
        if (!res.ok) throw new Error("HTTP 오류 " + res.status);

        const bomList = await res.json();

        // 반드시 배열로 넘어와야 함
        if (!Array.isArray(bomList)) {
            console.error("BOM 데이터가 배열이 아닙니다:", bomList);
            return;
        }

        window.bomGrid.resetData(bomList);
        console.log(`BOM for product ${productId}:`, bomList);

    } catch (err) {
        console.error("BOM 로드 실패:", err);
    }
}

// bom 등록 모달 버튼
	document.addEventListener('DOMContentLoaded', () => {
	    const btn = document.getElementById('bomModalBtn');
	    const modalEl = document.getElementById('bomRegisterModal');
	    const modal = new bootstrap.Modal(modalEl);
	
	    btn.addEventListener('click', () => {
	        modal.show();
	    });
	});