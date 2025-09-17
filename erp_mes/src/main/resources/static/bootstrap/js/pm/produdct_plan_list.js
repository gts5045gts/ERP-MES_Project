// 제품 Grid
const planGrid = new tui.Grid({
  el: document.getElementById('plan-grid'),
  columns: [
    { header: '생산계획 번호', name: 'productId', sortable: true, align: 'center' },
    { header: '제품코드', name: 'productName', sortable: true, align: 'center' },
    { header: '제품명', name: 'productName', sortable: true, align: 'center' },
    { header: '생산 수량', name: 'productType', sortable: true, align: 'center' },
    { header: '시작일', name: 'unit', sortable: true, align: 'center' },
    { header: '종료일', name: 'createdAt', sortable: true, align: 'center' },
  ],
  bodyHeight: 'fitToParent'
});

async function loadProducts() {
	const res = await fetch('productList');
	const productList = await res.json();
	
	prodGrid.resetData(productList);
	console.log("productList : " + productList);
	
}

loadProducts();

// ==================================
// 제품 등록 모달 버튼
	document.addEventListener('DOMContentLoaded', () => {
	    const btn = document.getElementById('productModalBtn');
	    const modalEl = document.getElementById('productRegisterModal');
	    const modal = new bootstrap.Modal(modalEl);
	
	    btn.addEventListener('click', () => {
	        modal.show();
	    });
	});
	
	
	// 제품 클릭 시 BOM 로드
	prodGrid.on('click', ev => {
	    const rowData = prodGrid.getRow(ev.rowKey);
	    if (rowData && typeof window.loadBomByProduct === "function") {
	        window.loadBomByProduct(rowData.productId);
	    }
	});

	
