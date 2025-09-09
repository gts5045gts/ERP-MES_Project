document.addEventListener('DOMContentLoaded', function() {
	const token = $("meta[name='_csrf']").attr("content");
	const header = $("meta[name='_csrf_header']").attr("content");

	$(document).ajaxSend(function(e, xhr, options) {
		if (token && header) {
			xhr.setRequestHeader(header, token);
		}
	});

	// DataTables 초기화 (변경되지 않으므로 그대로 둡니다.)
	$('#dataTable1').DataTable({
		dom: "<'row mb-3'<'col-sm-6'l><'col-sm-6'f>>"
			+ "<'row'<'col-sm-12'tr>>"
			+ "<'row mt-2'<'col-sm-5'i><'col-sm-7'p>>",
		paging: true,
		searching: true,
		info: true,
		"language": {
			"search": "검색:",
			"lengthMenu": "_MENU_개씩 보기",
			"infoEmpty": "데이터가 없습니다.",
			"info": "총 _TOTAL_개 중 _START_에서 _END_까지 표시",
			"infoFiltered": "(_MAX_개 전체 항목에서 필터링됨)",
			"loadingRecords": "로딩 중...",
			"processing": "처리 중...",
			"zeroRecords": "일치하는 데이터가 없습니다.",
			"paginate": {
				"next": "다음",
				"previous": "이전"
			}
		}
	});
	$('#dataTable2').DataTable({
		dom: "<'row mb-3'<'col-sm-6'l><'col-sm-6'f>>"
			+ "<'row'<'col-sm-12'tr>>"
			+ "<'row mt-2'<'col-sm-5'i><'col-sm-7'p>>",
		paging: true,
		searching: true,
		info: true,
		"language": {
			"search": "검색:",
			"lengthMenu": "_MENU_개씩 보기",
			"infoEmpty": "데이터가 없습니다.",
			"info": "총 _TOTAL_개 중 _START_에서 _END_까지 표시",
			"infoFiltered": "(_MAX_개 전체 항목에서 필터링됨)",
			"loadingRecords": "로딩 중...",
			"processing": "처리 중...",
			"zeroRecords": "일치하는 데이터가 없습니다.",
			"paginate": {
				"next": "다음",
				"previous": "이전"
			}
		}
	});

	// 숨겨진 input 필드에서 사용자 ID를 가져옵니다.
	const currentEmpId = document.getElementById('currentEmpId')?.value;

	// 모달을 열 때 이벤트 처리
	$('#noticeModal').on('show.bs.modal', function(event) {
		const button = $(event.relatedTarget);
		const notId = button.data('id');
		const notTitle = button.data('title');
		const notContent = button.data('content');
		const author = button.data('author');
		const authorUsername = button.data('author-username');
		const date = button.data('date');

		// 데이터 표시
		$('#modalTitle').text(notTitle);
		$('#modalAuthor').text(author);
		$('#modalDate').text(date);
		$('#modalContent').text(notContent);

		// 수정/삭제를 위한 hidden 필드 설정
		$('#editNoticeId').val(notId);
		$('#editTitle').val(notTitle);
		$('#editContent').val(notContent);

		// 현재 로그인한 사용자와 작성자가 동일한지 확인
		// 현재 사용자 ID를 비교합니다.
		if (currentEmpId && String(authorUsername) === String(currentEmpId)) {
			$('#editNoticeBtn, #deleteNoticeBtn').show();
		} else {
			$('#editNoticeBtn, #deleteNoticeBtn').hide();
		}
	});

	// 수정 버튼 클릭 시
	$('#editNoticeBtn').on('click', function() {
		$('#readModeContent').hide();
		$('#editForm').show();
		$('#editNoticeBtn, #deleteNoticeBtn').hide();
		$('#saveEditBtn').show();
	});

	// 모달이 닫힐 때 초기화
	$('#noticeModal').on('hidden.bs.modal', function() {
		$('#readModeContent').show();
		$('#editForm').hide();
		$('#editNoticeBtn, #deleteNoticeBtn').show();
		$('#saveEditBtn').hide();
	});

	// 삭제 버튼 클릭 시
	$('#deleteNoticeBtn').on('click', function() {
		const notId = $('#editNoticeId').val();
		if (confirm('정말 이 공지사항을 삭제하시겠습니까?')) {
			$.ajax({
				url: '/notice/ntcDelete',
				type: 'DELETE', // HTTP 메서드를 DELETE로 변경
				data: JSON.stringify({ notId: notId }),
				contentType: 'application/json',
				success: function(response) {
					if (response.success) {
						alert('공지사항이 성공적으로 삭제되었습니다.');
						location.reload();
					} else {
						alert('삭제 실패: ' + response.message);
					}
				},
				error: function(xhr, status, error) {
					alert('삭제 중 오류가 발생했습니다.');
					console.error('Error:', error);
				}
			});
		}
	});

	// 수정 완료 버튼 클릭 시
	$('#saveEditBtn').on('click', function() {
		const formData = {
			notId: $('#editNoticeId').val(),
			notTitle: $('#editTitle').val(),
			notContent: $('#editContent').val()
		};

		$.ajax({
			url: '/notice/ntcUpdate',
			type: 'POST',
			contentType: 'application/json',
			data: JSON.stringify(formData),
			success: function(response) {
				if (response.success) {
					alert('공지사항이 성공적으로 수정되었습니다.');
					location.reload();
				} else {
					alert('수정 실패: ' + response.message);
				}
			},
			error: function(xhr, status, error) {
				alert('수정 중 오류가 발생했습니다.');
				console.error('Error:', error);
			}
		});
	});
});