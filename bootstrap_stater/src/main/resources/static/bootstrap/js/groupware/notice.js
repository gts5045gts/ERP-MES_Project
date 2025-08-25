$(document).ready(function() {
	// DataTables 초기화
	$('#dataTable1').DataTable({
		dom: "<'row mb-3'<'col-sm-6'l><'col-sm-6'f>>"
			+ "<'row'<'col-sm-12'tr>>"
			+ "<'row mt-2'<'col-sm-5'i><'col-sm-7'p>>",
		paging: true,
		searching: true,
		info: true
	});
	$('#dataTable2').DataTable({
		dom: "<'row mb-3'<'col-sm-6'l><'col-sm-6'f>>"
			+ "<'row'<'col-sm-12'tr>>"
			+ "<'row mt-2'<'col-sm-5'i><'col-sm-7'p>>",
		paging: true,
		searching: true,
		info: true
	});

	// 모달이 열릴 때의 이벤트
	$('#noticeModal').on('show.bs.modal', function(event) {
		var button = $(event.relatedTarget);
		var noticeId = button.data('id');
		var title = button.data('title');
		var content = button.data('content');
		var author = button.data('author');
		var date = button.data('date');

		var modal = $(this);

		// 읽기 모드에 내용 채우기
		modal.find('#modalTitle').text(title);
		modal.find('#modalAuthor').text('작성자: ' + author);
		modal.find('#modalDate').text('등록일: ' + date);
		modal.find('#modalContent').text(content);

		// '수정' 버튼에 공지사항 ID를 연결합니다.
		modal.find('#editNoticeBtn').attr('data-id', noticeId);
		
		// 삭제
		modal.find('#deleteNoticeBtn').attr('href', '/notice/ntcDelete?id=' + noticeId);

		// 초기 상태는 읽기 모드입니다.
		$('#readModeContent').show();
		$('#editForm').hide();
	});

	// 수정 버튼 클릭 시 모달 모드 전환 이벤트
	$('#editNoticeBtn').on('click', function() {
		var noticeId = $(this).attr('data-id');
		var title = $('#modalTitle').text();
		var content = $('#modalContent').text();

		// 읽기 모드 숨기기
		$('#readModeContent').hide();

		// 수정 폼 보이기
		$('#editForm').show();
		
		// 수정완료 보이기
		$('#saveEditBtn').show();
		
		// '수정, 삭제' 버튼은 숨기고, '닫기' 버튼만 남깁니다.
		$('#editNoticeBtn').hide();
		$('#deleteNoticeBtn').hide();

		// 폼에 기존 내용을 미리 채워넣기
		$('#editNoticeId').val(noticeId);
		$('#editTitle').val(title);
		$('#editContent').val(content); 

	});
	
	// 삭제여부 확인
	$('#deleteNoticeBtn').on('click', function(e) {
	    // a 태그의 기본 동작(페이지 이동)을 막습니다.
	    e.preventDefault();
	    
	    // confirm 창으로 삭제 여부를 물어봅니다.
	    if (confirm("정말 삭제하시겠습니까?")) {
	        // 확인을 누르면 href에 설정된 URL로 이동
	        window.location.href = $(this).attr('href');
	    }
	});

	// ⭐모달이 닫힐 때 원래 상태로 초기화
	$('#noticeModal').on('hidden.bs.modal', function() {
		$('#readModeContent').show();
		$('#editForm').hide();
		$('#saveEditBtn').hide(); 
		$('#editNoticeBtn').show();
		$('#deleteNoticeBtn').show();
	});
});