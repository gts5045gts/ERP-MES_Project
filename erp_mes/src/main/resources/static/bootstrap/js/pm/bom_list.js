// BOM Grid
const bomGrid = new tui.Grid({
  el: document.getElementById('bom-grid'),
  columns: [
    { header: '원자재', name: 'matName', sortable: true, align: 'center' },
    { header: '필요 수량', name: 'qty', sortable: true, align: 'center' },
    { header: '등록일', name: 'createdAt', sortable: true, align: 'center' }
  ],
  bodyHeight: 'fitToParent'
});

// 특정 제품의 BOM 로드
async function loadBomByProduct(productId) {
    if (!productId) return;

    try {
        const res = await fetch(`/bom/list?prd_id=${productId}`);
        const bomList = await res.json();

        // BOM Grid 세팅
        bomGrid.resetData(bomList);
        console.log(`BOM for product ${productId}:`, bomList);

    } catch (err) {
        console.error("BOM 로드 실패:", err);
    }
}