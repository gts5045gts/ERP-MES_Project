document.addEventListener('DOMContentLoaded', function() {
	const token = $("meta[name='_csrf']").attr("content");
	const header = $("meta[name='_csrf_header']").attr("content");
	
	$(document).ajaxSend(function(e, xhr, options) {
		if (token && header) {
			xhr.setRequestHeader(header, token);
	}
      // 그리드 초기화
	});
    const grid = new tui.Grid({
		el: document.getElementById('grid'), // 그리드가 붙을 엘리먼트
		scrollX: false,
		scrollY: false,
		columns: [
			{ header: '설비코드', name: 'equipId' },	
			{ header: '설비이름', name: 'equipNm' },	
			{ header: '설비타입', name: 'typeNm' },
			{ header: '사용여부', name: 'useYn' },
			{ header: '구입날짜', name: 'purchaseDt' },
			{ header: '설치날짜', name: 'installDt' },
			{ header: '비고', name: 'note' },
		],
    });
    
    fetch('/plant/equipGrid', {
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
      
$(document).ready(function(){
	const form = $('#dataForm');
	const modalEl = document.getElementById("dataModal");
	const openBtn = document.getElementById("openModalBtn");
	const myModal = new bootstrap.Modal(modalEl);
	
	openBtn.addEventListener("click", function () {
		myModal.show();
	});
	
	
	$("#saveBtn").on("click", function(e){
		const equipName = $('#equipNm');
 		const purchaseDt = $('#purchaseDt');
 		const installDt = $('#installDt');
 		const equipType = $('#typeId');
		alert('실행');
 		if(!equipName.val()){
 			alert('설비명을 입력해주세요');
 			equipName.focus();
 			return;
 		}else if(!purchaseDt.val()){
 			alert('구입날짜를 선택 해주세요');
 			purchaseDt.focus();
 			return;
 		}else if(!installDt.val()){
 			alert('설치날짜를 선택 해주세요');
 			installDt.focus();
 			return;
 		}else if(!equipType.val()){
 			alert('설비타입을 선택 해주세요');
 			equipType.focus();
 			return;
 		}
 		
 		const formData = form.serialize();
 		$.post('/plant/equipAdd', formData, function(resoponse){
 			alert('저장이 완료되었습니다.');
		      // 모달 닫기
			myModal.hide();
		      // 폼 초기화
		    location.reload()
		    form.reset();
 		}).fail(function(){
				alert('전송 오류가 발생 했습니다.');
		});

	
	});
	$("#closeBtn").on("click", function(e){
		e.preventDefault();
		myModal.hide();
	});
 
});