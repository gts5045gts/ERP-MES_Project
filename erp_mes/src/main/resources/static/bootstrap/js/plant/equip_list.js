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

	$("#linkBtn").on("click",function(){
		alert("설비 관리 페이지로 이동");
		location.href = "/plant/equipment";
	});


});
      

