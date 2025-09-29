/* 작업지시 */
$(document).ready(function() {
	$.ajax({
		url: '/pop/workOrder', 
        type: 'GET',
        dataType: 'json',
        success: function(workOrders) {
			$('#workerNm span').text(workOrders[0].empNm);
			
            const tbody = $('#workOrderBody');
            tbody.empty(); // 기존 내용 초기화
			
			const seenIds = new Set();

            workOrders.forEach(function(item) {
				if (seenIds.has(item.workOrderId)) {
                    return; // 이미 추가된 workOrderId면 스킵
                }
                seenIds.add(item.workOrderId);
				
				// 상태에따른 클래스 변경(색깔구분)
				let statusClass = '';
			    if (item.workOrderStatus === '진행중') {
					statusClass = 'status-progress';
			    } else if (item.workOrderStatus === '미착수') {
			        statusClass = 'status-pending';
			    }
				
                const tr = `
                    <tr data-id="${item.workOrderId}" data-product-id="${item.productId}" data-equipment="${item.equipmentNm}" data-goodqty="${item.goodQty}">
                        <td>${item.workOrderId}</td>
                        <td>${item.productNm}</td>
                        <td>${item.startDate}</td>
                        <td class="${statusClass}">${item.workOrderStatus}</td>
                    </tr>
                `;
                tbody.append(tr);
            });
			
			$.getJSON('/pop/workResultList?page=0&size=20', function(workResults) {
				grid.resetData(workResults);          // 그리드에 세팅
				updateQuantityChart(workResults);     // 생산/불량 도넛
				updateProgressChart(workOrders);     // 전체 진행률 도넛
			});
			
			
        },
        error: function(xhr, status, error) {
            console.error('작업지시 조회 실패:', error);
        }
    });
});


/* BOM 조회 */
const columns1 = [
  	{ header: '공정명', name: 'processNm' },
  	{ header: '설비명', name: 'equipmentNm' },
  	{ header: '자재명', name: 'materialNm' },
  	{ header: '수량', name: 'quantity' },
  	{ header: '공정순서', name: 'proSeq' }
];

// BOM + 설비공정 그리드
const Workgrid1 = new tui.Grid({
  	el: document.getElementById('Workgrid1'),
  	data: [],
  	scrollX: false,
  	scrollY: false,
  	columns: columns1
});

let selectedInput = null;

// 작업지시 행 클릭하면 모달창
$('#workOrderBody').on('click', 'tr', function() {
	
	const productId = $(this).data('product-id');
	const workOrderId = $(this).data('id');
	
	const status = $(this).find('td:last').text().trim(); // 마지막 td가 상태라고 가정

	if (status === '검사대기' || status === '작업완료') {
	    alert('작업완료된 작업지시는 열 수 없습니다.');
	    return;
	}
	
	if (status === '진행중') {
	    $('#workOrderCheck').prop('disabled', true); // 클릭 못하게
	    $('#workOrderCheck').html('<i class="fa-solid fa-ban"></i> 작업시작'); // 클릭 못하게
		
	} else {
	    $('#workOrderCheck').prop('disabled', false); // 활성화
		$('#workOrderCheck').html('작업시작');
	}
	
	const modalEl = document.getElementById('popModal');  // DOM element
	const modal = new bootstrap.Modal(modalEl);
	
    modal.show();
	
	$('#workOrderCheck').data('id', workOrderId);
	
	
	// 모달이 완전히 열린 후 Grid 초기화
	modalEl.addEventListener('shown.bs.modal', function() {
		$.ajax({
			url: `/pop/bom/${productId}`, 
	        type: 'GET',
	        dataType: 'json',
	        success: function(bomData) {
				
	            Workgrid1.resetData(bomData);   // BOM 데이터를 Grid에 세팅
	            Workgrid1.refreshLayout();      // 레이아웃 갱신

				
	        },
	        error: function(xhr, status, error) {
	            console.error('BOM 조회 실패:', error);
	        }
	    });
	}, { once: true });

});


$('#popModal').on('hidden.bs.modal', function () {
	$('#shortageBody').empty();       // tbody 비우기
    $('#shortageList').hide();        // 리스트 숨기기
    selectedInput = null;             // 선택된 input 초기화
    $('#Workgrid1 .material-req').prop('checked', false); // 체크박스 초기화
});


