	
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
	
	
	$("#linkBtn").on("click",function(){
		alert("공정 관리 페이지로 이동");
		location.href = "/plant/process";
	});
	
	
	
	
});
        
	 
  