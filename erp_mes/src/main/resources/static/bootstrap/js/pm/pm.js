// 제품 Grid
const prodGrid = new tui.Grid({
  el: document.getElementById('prod-grid'),
  columns: [
    { header: '제품명', name: 'prd_nm', sortable: true },
    { header: '제품 구분', name: 'prd_nm', sortable: true },
    { header: '단위', name: 'prd_nm', sortable: true },
  ],
//  rowHeaders: ['rowNum'],
  bodyHeight: 'fitToParent'
});

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