// 제품 Grid
const prodGrid = new tui.Grid({
  el: document.getElementById('prod-grid'),
  columns: [
    { header: '제품 코드', name: 'productId', sortable: true, align: 'center' },
    { header: '제품명', name: 'productName', sortable: true, align: 'center' },
    { header: '제품 구분', name: 'productType', sortable: true, align: 'center' },
    { header: '단위', name: 'unit', sortable: true, align: 'center' },
    { header: '등록일', name: 'createdAt', sortable: true, align: 'center' },
  ],
//  rowHeaders: ['rowNum'],
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
// 제품 등록 ajax
	document.addEventListener('DOMContentLoaded', () => {
	    const btn = document.getElementById('openProductModalBtn');
	    const modalEl = document.getElementById('productRegisterModal');
	    const modal = new bootstrap.Modal(modalEl);
	
	    btn.addEventListener('click', () => {
	        modal.show();
	    });
	});


//	const productRegisterModalEl = document.getElementById('productRegisterModal');
//	productRegisterModalEl.addEventListener('show.bs.modal', () => {
//		const today = new Date().toISOString().split('T')[0]; // YYYY-MM-DD
//		document.getElementById('created_at').value = today;
//	});
	
	
	









// BOM Grid
const bomGrid = new tui.Grid({
  el: document.getElementById('bom-grid'),
  columns: [
    { header: '원자재', name: 'mat_nm', sortable: true },
    { header: '필요 수량', name: 'qty', sortable: true },
    { header: '등록일', name: 'qty', sortable: true },
    { header: '수정일', name: 'qty', sortable: true }
  ],
//  rowHeaders: ['rowNum'],
  bodyHeight: 'fitToParent'
});

// 제품 클릭 시 BOM 불러오기
prodGrid.on('click', async ev => {
  const rowData = prodGrid.getRow(ev.rowKey);
  if (!rowData) return;

  const prdId = rowData.prd_id;

  // BOM 조회 API 호출
  const res = await fetch(`/bom/list?prd_id=${prdId}`);
  const bomList = await res.json();

  // BOM Grid 세팅
  bomGrid.resetData(bomList);
});