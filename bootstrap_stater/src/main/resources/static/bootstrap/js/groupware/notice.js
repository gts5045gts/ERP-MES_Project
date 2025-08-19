$(document).ready(function() {
	// DataTables 초기화
	$('#dataTable1').DataTable({
		dom : "<'row mb-3'<'col-sm-6'l><'col-sm-6'f>>"
			+ "<'row'<'col-sm-12'tr>>"
			+ "<'row mt-2'<'col-sm-5'i><'col-sm-7'p>>",
		paging : true,
		searching : true,
		info : true
	});
	$('#dataTable2').DataTable({
		dom : "<'row mb-3'<'col-sm-6'l><'col-sm-6'f>>"
			+ "<'row'<'col-sm-12'tr>>"
			+ "<'row mt-2'<'col-sm-5'i><'col-sm-7'p>>",
		paging : true,
		searching : true,
		info : true
	});

	// 모달에 공지 내용 채우기 (show.bs.modal 이벤트)
	$('#noticeModal').on('show.bs.modal', function(event) {
	    var button = $(event.relatedTarget);
	    var title = button.data('title');
	    var content = button.data('content');
	    var author = button.data('author');
	    var date = button.data('date');

	    var modal = $(this);
	    modal.find('#modalTitle').text(title);
	    modal.find('#modalAuthor').text('작성자: ' + author);
	    modal.find('#modalDate').text('등록일: ' + date);
	    modal.find('#modalContent').text(content);
	});
});