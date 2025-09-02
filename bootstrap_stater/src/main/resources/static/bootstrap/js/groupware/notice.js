$(document).ready(function() {
    // 변경되지 않는 값들은 const로 선언합니다.
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

    // body 태그에서 현재 로그인한 사용자 ID를 가져옴 (변경되지 않으므로 const)
    const currentUsername = $('body').data('current-user-id');

    // 모달이 열릴 때의 이벤트
    $('#noticeModal').on('show.bs.modal', function(event) {
        // 이벤트와 관련된 객체들은 재할당이 필요 없으므로 const로 선언합니다.
        const button = $(event.relatedTarget);
        const noticeId = button.data('id');
        const title = button.data('title');
        const content = button.data('content');
        const author = button.data('author');
        const date = button.data('date');
        const authorUsername = button.data('author-username');

        const modal = $(this);

        // 읽기 모드에 내용 채우기
        modal.find('#modalTitle').text(title);
        modal.find('#modalAuthor').text('작성자: ' + author);
        modal.find('#modalDate').text('등록일: ' + date);
        modal.find('#modalContent').text(content);


        // '수정' 버튼에 공지사항 ID를 연결
        modal.find('#editNoticeBtn').attr('data-id', noticeId);

        // 삭제
        modal.find('#deleteNoticeBtn').attr('href', '/notice/ntcDelete?id=' + noticeId);

        // 작성자 username과 현재 사용자 username 비교
        if (authorUsername === currentUsername) {
            $('#editNoticeBtn').show();
            $('#deleteNoticeBtn').show();
        } else {
            $('#editNoticeBtn').hide();
            $('#deleteNoticeBtn').hide();
        }

        // 초기 상태는 읽기 모드
        $('#readModeContent').show();
        $('#editForm').hide();
    });

    // 수정 버튼 클릭 시 모달 모드 전환 이벤트
    $('#editNoticeBtn').on('click', function() {
        const noticeId = $(this).attr('data-id');
        const title = $('#modalTitle').text();
        const content = $('#modalContent').text();

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