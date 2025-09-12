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
  bodyHeight: 'fitToParent'
});

async function loadProducts() {
	const res = await fetch('productList');
	const productList = await res.json();
	
	prodGrid.resetData(productList);
	console.log("productList : " + productList);
	
}


// 제품 클릭 시 BOM 로드
prodGrid.on('click', async ev => {
    const rowData = prodGrid.getRow(ev.rowKey);
    if (!rowData) return;

    const productId = rowData.productId;

    // BOM 리스트 JS에 있는 함수를 호출
    if (typeof loadBomByProduct === "function") {
        loadBomByProduct(productId);
    } else {
        console.warn("loadBomByProduct 함수가 정의되어 있지 않습니다.");
    }
});


// ==================================
// 모달 버튼
	document.addEventListener('DOMContentLoaded', () => {
	    const btn = document.getElementById('openProductModalBtn');
	    const modalEl = document.getElementById('productRegisterModal');
	    const modal = new bootstrap.Modal(modalEl);
	
	    btn.addEventListener('click', () => {
	        modal.show();
	    });
	});

	
loadProducts();
	









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