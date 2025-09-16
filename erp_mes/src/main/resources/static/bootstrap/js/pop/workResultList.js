$('#workOrderCheck').on('click', function() {
	const checked = $('#Workgrid1 .work-start:checked');
	
	// 체크된 항목들의 workOrderId만 추출
   	const workOrderIds = checked.map(function() {
		return $(this).data('id');
    }).get();
	
	$.ajax({
		url: `/pop/workList/${workOrderIds}`,
		type: 'POST',
		contentType: 'application/json',
		data: JSON.stringify(workOrderIds),
		success: function(res) {
			// 무한스크롤 그리드에 새 row 추가
			res.forEach(item => grid.appendRow(item));

			// 모달 닫기 & 체크박스 초기화
			$('#popModal').modal('hide');
			$('#Workgrid1 .work-start').prop('checked', false);
		},
		error: function(err) {
			console.error('작업 시작 실패:', err);
		}
	});
});

/* 무한스크롤 */
document.addEventListener('DOMContentLoaded', function() {
	let currentPage = 0;
   	let totalPages = 1; // 초기값
	
    const columns = [
        { header: '공정아이디(변경)', name: 'processId', filter: 'select'},
        { header: '설비아이디(변경)', name: 'equipmentId', filter: 'select'},
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

// 수정 버튼 클릭시 수량 입력
document.getElementById('grid').addEventListener('click', function(e) {
	if (e.target && e.target.classList.contains('edit-btn')) {
    	const modal = new bootstrap.Modal(document.getElementById('workUpdateModal'));
    	modal.show();
  	}
});