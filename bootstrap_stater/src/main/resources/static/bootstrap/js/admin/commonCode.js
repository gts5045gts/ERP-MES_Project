let selectedParentId = null;

$(document).ready(function() {
    // 공통코드 클릭 시
    $(document).on('click', '.master-row', function() {
        selectedParentId = $(this).data('id');

        // 이전 선택 제거
        $('.master-row').removeClass('table-primary');

        // 클릭한 row 선택
        $(this).addClass('table-primary');

        // 상세공통코드 로딩
        $.get("/admin/detail/" + selectedParentId, function(fragment) {
            $('#detailArea').html(fragment);
        });
    });

    // 상세공통코드 등록 모달
    $('#commonDetailModal').on('show.bs.modal', function(e) {
        if(!selectedParentId) {
            alert("상위 공통 코드를 선택하세요.");
            e.preventDefault();
        } else {
            $('#parentComId').val(selectedParentId);
        }
    });

    // 상세코드 등록
    $('#commonCodeDetailForm').submit(function(e) {
        e.preventDefault();
        $.post("/admin/comDtRegist", $(this).serialize(), function() {
            // 등록 후 상세테이블 갱신
            $.get("/admin/detail/" + selectedParentId, function(fragment) {
                $('#detailArea').html(fragment);
            });
		

            // 모달 닫기
            var modalEl = document.getElementById('commonDetailModal');
            var modal = bootstrap.Modal.getInstance(modalEl) || new bootstrap.Modal(modalEl);
            modal.hide();

            // 폼 초기화
            $('#commonCodeDetailForm')[0].reset();
        });
    });
});
