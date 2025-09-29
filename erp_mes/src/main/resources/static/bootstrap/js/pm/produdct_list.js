// 제품 Grid
const prodGrid = new tui.Grid({
  el: document.getElementById('prod-grid'),
  columns: [
    { header: '제품 코드', name: 'productId', sortable: true, align: 'center' },
    { header: '제품명', name: 'productName', sortable: true, align: 'center' },
    { header: '제품 구분', name: 'productType', sortable: true, align: 'center' },
    { header: '가격', name: 'price', sortable: true, align: 'center' },
    { header: '단위', name: 'unit', sortable: true, align: 'center' },
    { header: '등록일', name: 'createdAt', sortable: true, align: 'center' },
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
	
	
// ==================================
	// 제품 클릭 시 BOM 로드
	
	window.selectedProduct = null;

	prodGrid.on('click', ev => {
	    const rowData = prodGrid.getRow(ev.rowKey);
	    if (rowData) {
	        console.log("선택된 제품:", rowData);
	        window.selectedProduct = rowData;
	        window.loadBomByProduct(rowData.productId);
	    }
	});

	
