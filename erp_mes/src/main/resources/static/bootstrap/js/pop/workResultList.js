/* 무한스크롤 */
document.addEventListener('DOMContentLoaded', function() {
	let currentPage = 0;
   	let totalPages = 1; // 초기값
	
    const columns = [
        { header: '공정명', name: 'processNm', filter: 'select'},
        { header: '설비명', name: 'equipmentNm', filter: 'select'},
        { header: '생산수량', name: 'goodQty'},
        { header: '불량수량', name: 'defectQty'},
        { header: '기입시간', name: 'updatedAt', sortable: true},
        { header: '작업상태', name: 'workOrderStatus', filter: 'select'},
        { header: '작업수정버튼', name: 'workUpdate'},
		{ header: '작업완료버튼', name: 'workFinish'}
    ];

	grid = new tui.Grid({
        el: document.getElementById('grid'),
        data: [],
        columns: columns,
        bodyHeight: 250,
        rowHeaders: ['rowNum'],
        scrollX: false,
        emptyMessage: '조회결과가 없습니다.'
    });
	
	// ✅ 강제 데이터
	const testData = [
	  { processNm: '조립', equipmentNm: '설비 A', goodQty: 120, defectQty: 2, updatedAt: '2025-09-12 10:23', workOrderStatus: '진행중'
		, workUpdate: `<button class="btn btn-dark btn-sm">수정</button>`, workFinish: `<button class="btn btn-danger btn-sm">작업완료</button>` },
	  { processNm: '검수', equipmentNm: '설비 B', goodQty: 98, defectQty: 1, updatedAt: '2025-09-12 11:15', workOrderStatus: '진행중'
		, workUpdate: `<button class="btn btn-dark btn-sm">수정</button>`, workFinish: `<button class="btn btn-danger btn-sm">작업완료</button>` },
	  { processNm: '포장', equipmentNm: '설비 C', goodQty: 50, defectQty: 0, updatedAt: '2025-09-12 12:05', workOrderStatus: '진행중'
		, workUpdate: `<button class="btn btn-dark btn-sm">수정</button>`, workFinish: `<button class="btn btn-danger btn-sm">작업완료</button>` }
	];

	// 초기 데이터 세팅
	grid.resetData(testData);

    function loadPage(page) {
        fetch(``)
            .then(res => res.json())
            .then(res => {
                totalPages = res.totalPages;
                currentPage = res.page;

                if (page === 0) {
                    grid.resetData(res.data);
                } else {
                    grid.appendRows(res.data);
                }
            });
    }

    // 초기 데이터 로딩
    loadPage(0);

    // 무한스크롤 이벤트
	grid.on('scrollEnd', () => {
		if (currentPage + 1 < totalPages) {
		    loadPage(currentPage + 1);
		}
	});
});
