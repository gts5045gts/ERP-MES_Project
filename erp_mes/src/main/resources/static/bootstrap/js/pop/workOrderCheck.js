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

            workOrders.forEach(function(item) {
                const tr = `
                    <tr data-id="${item.workOrderId}">
                        <td>${item.workOrderId}</td>
                        <td>${item.processId}</td>
                        <td>${item.startDate}</td>
                        <td>${item.endDate}</td>
                    </tr>
                `;
                tbody.append(tr);
            });
        },
        error: function(xhr, status, error) {
            console.error('작업지시 조회 실패:', error);
        }
    });
});

/* BOM 조회 */
const columns1 = [
  	{ header: '공정아이디(변경)', name: 'processId' },
  	{ header: '설비아이디(변경)', name: 'equipmentId' },
  	{ header: '자재아이디(변경)', name: 'materialId' },
  	{ header: '필요수량', name: 'quantity' },
	{
		header: '재고요청', 
		name: 'materialReq',
		formatter: function(props) {
	      	return `<input type="checkbox" class="material-req" data-row-key="${props.rowKey}"  data-id="${props.row.materialNm}">`;
		}
	}, 
	{ 
		header: '작업시작', 
		name: 'workStart',
		formatter: function(props) {
			return `<input type="checkbox" class="work-start" data-row-key="${props.rowKey}"  data-id="${props.row.workOrderId}">`;
		} 
	}
];

// BOM 그리드
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
	
	const workOrderId = $(this).data('id');
	const modalEl = document.getElementById('popModal');  // DOM element
	const modal = new bootstrap.Modal(modalEl);
	
    modal.show();
	
	// 모달이 완전히 열린 후 Grid 초기화
	modalEl.addEventListener('shown.bs.modal', function() {
		$.ajax({
			url: `/pop/bom/${workOrderId}`, 
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


// 재고요청 체크박스 클릭시 불량 리스트 업데이트
$('#Workgrid1').on('change', '.material-req', function() {
	const materialNm = $(this).data('id');
	const tbody = $('#shortageBody');
	const table = $('#shortageTable');
	
    if (this.checked) {
		$('#shortageList').css('display', 'flex');
		table.show();
		tbody.append(
			`<tr>
				<td>${materialNm}</td>
	           	<td><input type="text" class="shortQty-input"></td>
	       	</tr>`);
		selectedInput = tbody.find('input.shortQty-input').last()[0];
       	selectedInput.focus();
	} else {
		tbody.find(`tr:has(td:contains('${materialNm}'))`).remove();
		if (tbody.children().length === 0) {
			$('#shortageList').hide();
		}
       	selectedInput = null;
	}
});

$('#popModal').on('hidden.bs.modal', function () {
	$('#shortageBody').empty();       // tbody 비우기
    $('#shortageList').hide();        // 리스트 숨기기
    selectedInput = null;             // 선택된 input 초기화
    $('#Workgrid1 .material-req').prop('checked', false); // 체크박스 초기화
});

// input 클릭 시 선택된 input 갱신
$('#shortageTable').on('focus', '.shortQty-input', function() {
    selectedInput = this;
});

// 숫자패드 버튼 클릭
$('.Snum-btn').on('click', function() {
    if (selectedInput) {
        selectedInput.value += $(this).text();
    }
});

$('#deleteBtn').on('click', function() {
    if (selectedInput) selectedInput.value = '';
});

$('#okBtn').on('click', function() {
    if (selectedInput) {
		selectedInput.classList.add('saved');
		selectedInput.blur();
		selectedInput = null;
	}
});

// 다시 클릭하면 저장됨 해제
$('#shortageTable').on('focus', '.shortQty-input.saved', function() {
    this.classList.remove('saved');
    selectedInput = this;
});