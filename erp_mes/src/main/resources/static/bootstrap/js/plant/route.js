	 
document.addEventListener('DOMContentLoaded', function() {
	
	const token = $("meta[name='_csrf']").attr("content");
	const header = $("meta[name='_csrf_header']").attr("content");
		// 그리드 초기화
	$(document).ajaxSend(function(e, xhr, options) {
		if (token && header) {
				xhr.setRequestHeader(header, token);
		}
	});
  
	const grid = new tui.Grid({
	el: document.getElementById('grid'), // 그리드가 붙을 엘리먼트
	scrollX: true,
	scrollY: true,
	columns: [
		{ header: '라우트 번호', name: 'routeId' },	
	    { header: '제품 이름', name: 'productNm' },	
	    { header: '공정 이름', name: 'proNm' },
	    { header: '필요 설비', name: 'equipNm' },
	    { header: '자재이름', name: 'materialNm' },
	    { header: '설명', name: 'note' }
	],
});
	fetch('/plant/routeGrid', {
	    method: 'GET',
	    headers: {
	      [header]: token  // 동적으로 headerName을 키로 넣음
	    }
	})
	.then(res => {
		if (!res.ok) throw new Error('서버 응답 에러: ' + res.status);
		return res.json();
	})
	.then(data => {
		grid.resetData(data);
	})
  	.catch(err => console.error('fetch error:', err));
});
    
 
	//모달 1 데이터 생성 부분
 // 팝업으로 창 띄우기
function addRoute(){
	var url = '/plant/route_newForm';
	window.open(url, "popup", "width=830,height=450");
}
 

document.addEventListener("DOMContentLoaded", function () {
	const form = $('#dataForm');
	const modalEl = document.getElementById("dataModal");
	const openBtn = document.getElementById("openModalBtn");
	const myModal = new bootstrap.Modal(modalEl);

	// 버튼 클릭 시 모달 열기
	openBtn.addEventListener("click", function () {
		myModal.show();
	});

	$(document).ready(function(){
		
		$("#saveBtn").on("click", function(e){
			const product = $('#productId');
	 		const process = $('#proId');
	 		const equipment = $('#equipId');
	 		const proNote = $('#note');
	 		
	 		if(!product.val()){
	 			alert('제품이름 선택 해주세요');
	 			product.focus();
	 			return;
	 		}else if(!process.val()  != "" ){
	 			alert('공정명을 선택 해주세요');
	 			process.focus();
	 			return;
	 		}else if(!equipment.val()  != "" ){
	 			alert('필요한 설비를 선택 해주세요');
	 			equipment.focus();
	 			return;
	 		}else if(!proNote.val()  != "" ){
	 			alert('공정 설명을 입력해주세요');
	 			proNote.focus();
	 			return;
	 		}
	 		
	 		
	 		const formData = form.serialize();
	 		$.post('/plant/routeAdd', formData, function(response){
	 			alert('라우팅 정보를 추가합니다');
	 			myModal.hide();
			      // 폼 초기화
				location.reload()
				form.reset();
	 			
	 		}).fail(function(){
					alert('전송 오류가 발생 했습니다.');
	 		});
		});
		//모달 창 닫기 
		 $("#closeBtn ").on("click", function(e){
				e.preventDefault();
				myModal.hide();
		 });
	
	});
		
	$(document).on( "change", "#productId", function(){
		let bomSet = new Set();
		let productId = $(this).val();
		if(productId == "") { $("#hideSelect").css("display", "none"); 	return; }
		$("#hideSelect").css("display", "block");
		
		$.get('/plant/materialInfo', {productId : productId}, function(data){
			$("#bomId").empty();
			$("#bomId").append('<option value="">BOM 선택</option>');
			data.forEach(function(item) {
				if (!bomSet.has(item.bomId)) {
				       $("#bomId").append(
				           $("<option>", {
				               value: item.bomId,
				               text: item.bomId
				           })
				       );
				       bomSet.add(item.bomId);
				   }
				
		});
		
		$("#materialId").empty().append('<option value="">자재 선택</option>');
		data.forEach(function(item) {
						$("#materialId").append(
							$("<option>", {
							value: item.materialId,
							text: item.materialNm,
						
						})
					);
						
				});
		
		
		});
	});	
});	//jquery 준비 끝 부분
