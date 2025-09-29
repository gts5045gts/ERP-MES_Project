// CSRF 토큰
const csrfToken = $('meta[name="_csrf"]').attr('content');
const csrfHeader = $('meta[name="_csrf_header"]').attr('content');

let grid;
let currentPage = 0;
let totalPages = 1;



// 컬럼 정의
const columns = [
	{ header: 'Result ID', name: 'resultId', hidden: true},
	{ header: '작업지시아이디', name: 'workOrderId', filter: 'select', align: 'center'},
	{ header: '제품명', name: 'productNm', filter: 'select' , align: 'center'},
//    { header: '공정명', name: 'processNm', filter: 'select' },
//    { header: '설비명', name: 'equipmentNm', hidden: true },
	{ header: '목표수량', name: 'planQty' , align: 'center'},
    { header: '생산수량', name: 'goodQty' , align: 'center'},
    { 
		header: '불량수량', 
		name: 'defectQty' , 
		align: 'center',
		formatter: function(props) {
		       let defectQty = props.value; // 기존 불량수량
		       let btnHtml = '';

		       // 작업상태에 따라 버튼 생성
		       if (props.row.workOrderStatus !== '검사대기' && props.row.workOrderStatus !== '작업완료') {
		           btnHtml = `<button class="btn btn-dark btn-sm edit-btn" data-result-id="${props.row.resultId}">수정</button>`;
		       } else {
		           btnHtml = `<button class="btn btn-dark btn-sm edit-btn btn-disabled">수정불가</button>`;
		       }

		       // 불량수량 + 버튼 같이 표시
			   return `
		               <div style="position: relative; width: 100%; text-align: center;">
		                   <span>${defectQty}</span>
		                   <span style="position: absolute; right: 5px; top: 50%; transform: translateY(-50%);">
		                       ${btnHtml}
		                   </span>
		               </div>
			           `;
		   }
	},
    { header: '작업시작시간', name: 'updatedAt', sortable: true , align: 'center'},
    { header: '작업상태', name: 'workOrderStatus', filter: 'select' , align: 'center'},
    { 
		header: '작업완료', 
		name: 'workFinish',
		align: 'center',  
		formatter: function(props) {
			if (props.row.workOrderStatus === '검사대기' || props.row.workOrderStatus === '작업완료') {
				return '<span style="color:#28a745; font-weight:bold; font-size: 15px;">✔ 완료</span>';
			}
			return `<button class="btn btn-danger btn-sm finish-btn" data-result-id="${props.row.resultId}" data-id="${props.row.workOrderId}" data-route-id="${props.row.routeId}">작업완료</button>`;
       }
	}
];

// 그리드 초기화
document.addEventListener('DOMContentLoaded', function() {
    grid = new tui.Grid({
        el: document.getElementById('grid'),
        data: [],
        columns: columns,
		rowKey: 'resultId',
        bodyHeight: 250,
		rowHeaders: ['rowNum'],
		rowAttributes: function(row) {
			return { 'data-result-id': row.resultId }; // 각 tr에 속성 추가
	    },
        scrollX: false,
        emptyMessage: '조회결과가 없습니다.'
    });
	

    // 초기 데이터 로딩
    loadPage(0);

    // 무한스크롤 이벤트
    grid.on('scrollEnd', () => {
        if (currentPage + 1 < totalPages) {
            loadPage(currentPage + 1);
        }
    });
});

// 페이지 로딩 함수
function loadPage(page) {
    fetch(`/pop/workResultList?page=${page}&size=20`)
        .then(res => res.json())
        .then(res => {
            // 무한스크롤용 페이지 정보
            totalPages = res.totalPages || Math.ceil(res.length / 20);
            currentPage = page;
			

            if (page === 0) {
                grid.resetData(res); // 0페이지는 초기화
            } else {
                grid.appendRows(res); // 이후 페이지는 append
            }

        });
}

// 작업시작 버튼 클릭
$('#workOrderCheck').on('click', function() {
	const workOrderId = $(this).data('id');
    $.ajax({
        url: `/pop/startWork`,
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify ({ workOrderId: workOrderId }),
        beforeSend: function(xhr) {
            xhr.setRequestHeader(csrfHeader, csrfToken);
        },
        success: function(res) {
            // DB 기준으로 화면 갱신
			res.forEach(item => {
				// 작업 시작 시점에 생산수량을 목표수량으로 세팅
				if (!item.goodQty) {  // 이미 값이 있으면 그대로, 없으면
					item.goodQty = item.planQty || 0;
				}
				grid.appendRow(item);
			});
			
			// 작업지시서의 상태변경
			const workOrderIds = [...new Set(res.map(item => item.workOrderId))];
		    workOrderIds.forEach(id => {
		        const row = $(`#workOrderBody tr[data-id='${id}']`);
		        if(row.length) {
		            row.find('td:last')
		               .text('진행중')
		               .removeClass()
		               .addClass('status-progress');
		        }
		    });
			
			$.ajax({
				url: `/pop/bom/workOrder/${workOrderId}`,
				type: 'GET',
				dataType: 'json',
				success: function(equipData) {
					updateEquipmentChart(equipData); // 바로 그래프 업데이트
				},
				error: function(err) { console.error('BOM 조회 실패:', err); }
			});


			
        // 모달 닫기 및 체크박스 없애기
            $('#popModal').modal('hide');
        },
        error: function(err) {
            console.error('작업 시작 실패:', err);
        }
    });
});

// =============== 작업 수정 버튼 ==============================================
document.getElementById('grid').addEventListener('click', function(e) {
	
    if (e.target && e.target.classList.contains('edit-btn')) {
		const resultId = e.target.getAttribute('data-result-id'); // 문자열이므로 필요하면 parseInt
		const row = grid.getData().find(r => r.resultId == resultId);
		// 모달 input 초기값 세팅
		$('#workUpdateModal').data('rowId', resultId);
        $('#defectQtyInput').val(row.defectQty);
		$('#defectReasonInput').val(row.defectReason || '');
	
        const modal = new bootstrap.Modal(document.getElementById('workUpdateModal'));
        modal.show();
    }
});

// =============== 불량사유 버튼 ==============================


const defectBtnContainer = $('#defectReasonOptions');

$('#defectReasonBtn').on('click', function() {
	$.getJSON('/pop/defectReason', function(data) {
		defectBtnContainer.empty(); // 기존 버튼 제거

		data.forEach(d => {
			const btn = $(`<button class="btn btn-sm btn-secondary defectOption" data-code="${d.comId}">${d.comDtNm}</button>`);
			defectBtnContainer.append(btn);
		});

		defectBtnContainer.toggle(); // 보이기 / 숨기기 토글
	});
});

let selectedDefects = new Set();

$('#defectReasonOptions').on('click', '.defectOption', function() {
	
	// 선택 토글
   	$(this).toggleClass('selected');
	
	const defectReason = $(this).text();
	const input = $('#defectReasonInput');

	if(selectedDefects.has(defectReason)) {
		selectedDefects.delete(defectReason);
	} else {
		selectedDefects.add(defectReason);
	}
	
	input.val(Array.from(selectedDefects).join(' '));

});

// =========== 숫자 패드 ===================================
$('.num-input').on('focus', function() {
    selectedInput = this;
});

// 숫자패드 버튼 클릭
$('.num-btn').on('click', function() {
    if (selectedInput) {
        selectedInput.value += $(this).text();
    }
});

$('#D-deleteBtn').on('click', function() {
    if (selectedInput) selectedInput.value = selectedInput.value.slice(0, -1);
});

$('#D-okBtn').on('click', function() {
    if (selectedInput) {
		selectedInput.classList.add('saved');
		selectedInput.blur();
		selectedInput = null;
	}
});

// 다시 클릭하면 저장됨 해제
$('.num-input.saved').on('focus', function() {
    this.classList.remove('saved');
    selectedInput = this;
});

// =========== 불량수량 업데이트 ========================
$('#defectSaveBtn').on('click', function() {
	const resultId = $('#workUpdateModal').data('rowId');
	
    const defectQty = parseInt($('#defectQtyInput').val(), 10);	
	const defectType = $('.defectOption.selected').data('code');
	const defectReason = $('#defectReasonInput').val().trim();
	
	if(defectQty > 0 && !defectReason) {
		alert('불량 사유를 입력하세요');
		return;
	}
	
	const row = grid.getData().find(r => r.resultId == resultId);
	const goodQty = row.planQty - defectQty;
	
	const dto = { 
		resultId: resultId, 
		goodQty: goodQty, 
		defectQty: defectQty,
		defectReason: defectReason,
		empId: row.empId,        // 실제 작업자 ID
		productNm: row.productNm
	};
	
	$.ajax({
		url: '/pop/workResultUpdate',
		type: 'POST',
		contentType: 'application/json',
		data: JSON.stringify(dto),
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeader, csrfToken);
		},
		success: function() {
			if (!row.defectItemId) {
			    const defectDTO = {
			        defectQty: defectQty,
			        defectType: defectType,
			        defectReason: defectReason,
			        empId: row.empId,
			        productNm: row.productNm,
			        workOrderId: row.workOrderId
			    };
			    $.ajax({
			        url: '/pop/saveDefect',
			        type: 'POST',
			        contentType: 'application/json',
			        data: JSON.stringify(defectDTO),
			        beforeSend: function(xhr) { xhr.setRequestHeader(csrfHeader, csrfToken); },
			        success: function(savedId) { 
			            console.log('불량 INSERT 완료, id:', savedId); 
			            // 새로 insert된 defectItemId를 row에 세팅하면 이후 수정 가능
			            row.defectItemId = savedId;
			        }
			    });
			}

			// 2️⃣ 이미 불량이 존재하면 UPDATE
			if (row.workOrderId && row.defectItemId) {
			    const defectDTO = {
			        defectItemId: row.defectItemId,
			        defectQty: defectQty,
			        defectType: defectType,
			        defectReason: defectReason,
			    };
			    $.ajax({
			        url: '/pop/updateDefect',
			        type: 'POST',
			        contentType: 'application/json',
			        data: JSON.stringify(defectDTO),
			        beforeSend: function(xhr) { xhr.setRequestHeader(csrfHeader, csrfToken); },
			        success: function() { console.log('불량 수정 완료'); }
			    });
			}
			// 그리드 갱신
			$.getJSON('/pop/workResultList?page=0&size=20', function(data) {
				grid.resetData(data);
				updateQuantityChart(data);
			});
		},
		error: function(err) { console.error('작업 수정 실패', err); }
	});

	$('#workUpdateModal').modal('hide');
});

// ===================== 작업완료 ===============================
document.getElementById('grid').addEventListener('click', function(e) {
	if (e.target && e.target.classList.contains('finish-btn')) {

		if (!confirm('작업을 종료하시겠습니까?')) return;

		const workOrderId = parseInt(e.target.getAttribute('data-id'));

		$.ajax({
			url: '/pop/workFinish',
			type: 'POST',
			data: JSON.stringify(workOrderId),
			contentType: 'application/json',
			beforeSend: function(xhr) {
				xhr.setRequestHeader(csrfHeader, csrfToken);
			},
			success: function() {
				reloadGrid();
				const $orderRow = $(`#workOrderBody tr[data-id='${workOrderId}']`);
				if ($orderRow.length) {
					$orderRow.find('td:last')
						.text('검사대기')
						.removeClass('status-progress status-pending status-other') // 모든 상태 클래스 제거
						.css('color', '#000');
				}
				
				$.getJSON('/pop/workOrder', function(workOrders) {
				    updateProgressChart(workOrders); // 전체 작업지시 기준
				});
			},
			error: function(err) {
				console.error('작업완료 실패', err);
			}
		});
	}
});

// 그리드 새로고침 함수
function reloadGrid() {
	$.getJSON('/pop/workResultList?page=0&size=20', function(data) {
		grid.resetData(data); // 전체 렌더링
	});
}






