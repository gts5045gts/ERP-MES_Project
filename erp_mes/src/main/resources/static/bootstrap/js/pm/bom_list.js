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
        const res = await fetch(`/bomList?product_id=${productId}`);
        const bomList = await res.json();

        window.bomGrid.resetData(bomList);
        console.log(`BOM for product ${productId}:`, bomList);

    } catch (err) {
        console.error("BOM 로드 실패:", err);
    }
}