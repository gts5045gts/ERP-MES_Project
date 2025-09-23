	
 document.addEventListener('DOMContentLoaded', function() {
	const token = $("meta[name='_csrf']").attr("content");
	const header = $("meta[name='_csrf_header']").attr("content");
	
	$(document).ajaxSend(function(e, xhr, options) {
		if (token && header) {
			xhr.setRequestHeader(header, token);
		}
	});
        // 그리드 초기화
      
	const grid = new tui.Grid({
		el: document.getElementById('grid'), // 그리드가 붙을 엘리먼트
		scrollX: true,
		scrollY: true,
		columns: [
			{ header: '공정번호', name: 'proId' },	
		    { header: '공정명', name: 'proNm' },	
		    { header: '공정 유형', name: 'typeNm' },
		    { header: '공정 설명', name: 'note',whiteSpace: 'normal' },
		    { header: '검사 유형', name: 'inspecNm' },
		],
	});
	fetch('/plant/processGrid', {
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
        
	 
	 /*  모달창으로 대체
	 // 팝업으로 창 띄우기
	 function addProcess(){
		 var url = '/plant/process_newForm';
		 window.open(url, "popup", "width=830,height=450");
	 }
	 */
document.addEventListener("DOMContentLoaded", function () {
	const form = $('#dataForm');
	const modalEl = document.getElementById("dataModal");
	const openBtn = document.getElementById("openModalBtn");
	const myModal = new bootstrap.Modal(modalEl);
	
	// 버튼 클릭 시 모달 열기
	openBtn.addEventListener("click", function () {
		myModal.show();
	});
 
// 폼 제출 이벤트
	$(document).ready(function () {
		
		$("#saveBtn").on("click", function (e) {
			e.preventDefault();
	
			const name = $('#proNm');
	 		const note = $('#note');
	  		const type = $('#typeNm');
			
			if(!name.val()){
				alert("공정명을 입력해주세요");
				name.focus();
				return;
			}else if(!note.val()){
				alert("공정 설명을 적어주세요");
				note.focus();
				return;
			}else if(!type.val()){
				alert("공정 유형을 선택해주세요");
				type.focus();
				return;
			}
	  
	  		const formData = form.serialize();
		    $.post('/plant/processAdd', formData, function(response){
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
	});
  
  //모달 창 닫기 버튼
	$("#closeBtn").on("click", function(e){
		e.preventDefault();
		myModal.hide();
	});
});
  