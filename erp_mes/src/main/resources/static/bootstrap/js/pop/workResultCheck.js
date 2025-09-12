// 모달 그리드
const testData2 = [
  { processNm: 'Rolling in the Deep', equipmentNm: 'Adele', materialNm: '자재1', quantity : '5', materialReq: 'Album', workStart : '5'},
  { processNm: 'Levitating', equipmentNm: 'Dua Lipa', materialNm: '자재2', quantity : '5', materialReq: 'Single', workStart : '5'},
  { processNm: 'Bad Guy', equipmentNm: 'Billie Eilish', materialNm: '자재3', quantity : '5', materialReq: 'Single', workStart : '5'}
];


/* 작업지시 */
const workOrders = [
  { workOrderId: 'WO-001', processNm: '조립', workStartAt: '2025-09-12', workEndAt: '2025-09-20'},
  { workOrderId: 'WO-002', processNm: '검수', workStartAt: '2025-09-12', workEndAt: '2025-09-12'}
];

function renderTable(data) {
	const tbody = document.getElementById('workOrderBody');
	tbody.innerHTML = data.map(item => `
    <tr data-id="${item.workOrderId}">
		<td>${item.workOrderId}</td>
      	<td>${item.processNm}</td>
      	<td>${item.workStartAt}</td>
		<td>${item.workEndAt}</td>
    </tr>
  `).join('');
}

// 초기 렌더링
renderTable(workOrders);

const columns1 = [
  	{ header: '공정명', name: 'processNm' },
  	{ header: '설비명', name: 'equipmentNm' },
  	{ header: '자재명', name: 'materialNm' },
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
			return `<input type="checkbox" class="work-start" data-row-key="${props.rowKey}"  data-id="${props.row.materialNm}">`;
		} 
	}
];

const columns2 = [
  	{ header: '자재명', name: 'materialNm' },
  	{ header: '부족수량', editor: 'text', name: 'shortQty' }
];

// BOM 그리드
const Workgrid1 = new tui.Grid({
  	el: document.getElementById('Workgrid1'),
  	data: [],
  	scrollX: false,
  	scrollY: false,
  	columns: columns1
});

// 불량상품 그리드 
const Workgrid2 = new tui.Grid({
  	el: document.getElementById('Workgrid2'),
  	data: [],
  	scrollX: false,
  	scrollY: false,
  	columns: columns2
});



// 작업지시 행 클릭하면 모달창
document.getElementById('workOrderBody').addEventListener('click', function(e) {
	
	const tr = e.target.closest('tr');
	if(!tr) return;
	workOrderId = tr.dataset.id;
		
	const modalEl = document.getElementById('popModal');  // DOM element
	const modal = new bootstrap.Modal(modalEl);
	
    modal.show();
	
	// 모달이 완전히 열린 후 Grid 초기화
	modalEl.addEventListener('shown.bs.modal', function() {
		Workgrid1.resetData(testData2);  // 데이터 세팅
       	Workgrid1.refreshLayout();        // 레이아웃 갱신
   	}, { once: true });

});


// 재고요청 체크박스 클릭시 불량 리스트 업데이트
document.getElementById('Workgrid1').addEventListener('change', function(e) {
	if (e.target.classList.contains('material-req')) {
		const materialNm = e.target.dataset.id;

	    if (e.target.checked) {
			Workgrid2.appendRow({ materialNm: materialNm, shortQty: 0 });
	    } else {
	      // 체크 해제 → 삭제
	      const targetRow = Workgrid2.getData().find(r => r.materialNm === materialNm);
	      if (targetRow) { Workgrid2.removeRow(targetRow.rowKey); }
	    }
		Workgrid2.refreshLayout(); // 화면 갱신
	}
});




