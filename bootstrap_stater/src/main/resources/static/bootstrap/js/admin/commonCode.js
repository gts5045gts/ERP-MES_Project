var csrfToken = $('meta[name="_csrf"]').attr('content');
var csrfHeader = $('meta[name="_csrf_header"]').attr('content');
	
let selectedParentId = null;
let selectedDetailId = null;

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
			$('#detailArea').show(); // 클릭 시 테이블 보이게
			$('#codeDetailSearch').closest('.col-6').show();
		});
	});

	// 상세공통코드 등록 모달
	$('#commonDetailModal').on('show.bs.modal', function(e) {
		if (!selectedParentId) {
			alert("상위 공통 코드를 선택하세요.");
			e.preventDefault();
		} else {
			$('#parentComId').val(selectedParentId);
		}
	});

	// 상세코드 등록
	$('#commonCodeDetailForm').submit(function(e) {
		e.preventDefault();
		$.ajax({
			url: "/admin/comDtRegist",
			type: "POST",
			data: $(this).serialize(),       // form 데이터를 그대로 전송
			beforeSend: function(xhr) {
				xhr.setRequestHeader(csrfHeader, csrfToken); // meta에서 가져온 headerName + token
			},
			success: function(fragment) {
				// 등록 후 상세테이블 갱신
				$('#detailArea').html(fragment);

				// 모달 닫기
				var modalEl = document.getElementById('commonDetailModal');
				var modal = bootstrap.Modal.getInstance(modalEl) || new bootstrap.Modal(modalEl);
				modal.hide();

				// 폼 초기화
				$('#commonCodeDetailForm')[0].reset();
			},
			error: function(xhr) {
				alert("등록 중 오류 발생: " + xhr.status + "\n" + xhr.responseText);
			}
		   });
	});
});

// 공통코드 삭제
$('#btnDeleteCode').click(function() {
	if (!selectedParentId) {
		alert("삭제할 공통코드를 선택하세요.");
		return;
	}
	
	if (!confirm(selectedParentId + "를 삭제하시겠습니까?")) return;
	
	$.ajax({
		url: "/admin/comDelete/" + selectedParentId,
		type: "DELETE",
        headers: {
            'X-CSRF-TOKEN': csrfToken  // CSRF 토큰 헤더
        },
		success: function(result) {
			alert(result);
			if (result === "삭제가 완료되었습니다.") {
				$('.master-row.table-primary').remove();
				selectedParentId = null;
			}
		},
		error: function() {
			alert("삭제 중 오류가 발생했습니다.");
		}
	});
});

// 공통코드 수정
// 등록 버튼 클릭
$('#btnAddCode').on('click', function() {
	openCommonCodeModal(false, {}); // edit=false
});

//수정 버튼 클릭시 모달 열기
$('#btnEditCode').on('click', function() {
	const selectedRow = $('.master-row.table-primary');
	if (!selectedRow.length) {
		alert('수정할 행을 선택하세요.');
		return;
	}
	const data = {
		comId: selectedRow.data('id'),
		comNm: selectedRow.find('.comNm-cell').text(),
		useYn: selectedRow.find('.useYn-cell').text()
	};
	openCommonCodeModal(true, data, selectedRow);
});

function openCommonCodeModal(edit = false, data = {}, selectedRow = null) {
	$('#commonCodeModalLabel').text(edit ? '공통코드 수정' : '공통코드 등록');
	const submitBtn = $('#commonCodeForm button[type="submit"]');

	// 필드 세팅
	$('#comId').val(data.comId || '').prop('disabled', edit); // 수정 시 ID 수정 불가
	$('#comNm').val(data.comNm || '');
	$('.useCodeAt').val(data.useYn || '');
	$('#commonCodeModal').modal('show');

	// 이전 이벤트 제거
	submitBtn.off('click');

	// submit 이벤트
	submitBtn.on('click', function(e) {
		e.preventDefault();

		const payload = {
			comNm: $('#comNm').val(),
			useYn: $('.useCodeAt').val()
		};

		let url = edit ? '/admin/comUpdate/' + data.comId : '/admin/comRegist';
		let type = 'POST';

		$.ajax({
			url: url,
			type: type,
			data: payload,
			headers: {
				'X-CSRF-TOKEN': csrfToken
			},
			success: function(res) {
				alert(res);
				$('#commonCodeModal').modal('hide');

				// 수정 시 선택 행 업데이트
				if (edit && selectedRow && selectedRow.length) {
					selectedRow.find('.comNm-cell').text(payload.comNm);
					selectedRow.find('.useYn-cell').text(payload.useYn);
				}

				// 등록 시 테이블 갱신
				if (!edit) {
					$.get('/admin/list', function(fragment) {
						$('#masterTableArea').html(fragment);
					});
				}
			},
			error: function(err) {
				alert('처리 실패: ' + err.responseText);
			}
		});
	});
}

// ------------------------- 상세코드-----------------------------------

$(document).on('click', '.detail-row', function() {
	selectedDetailId = $(this).attr('data-id');
	$('.detail-row').removeClass('table-primary');
	$(this).addClass('table-primary');
});

// 상세코드 삭제
$(document).ready(function() {
	$('#btnDeleteDetail').click(function() {
		let comDtId = selectedDetailId;
		if (!selectedDetailId) {
			alert("삭제할 상세공통코드를 선택하세요.");
			return;
		}
		if (!confirm(comDtId + "를 삭제하시겠습니까?")) return;

		$.ajax({
			url: "/admin/comDtDelete/" + comDtId,
			type: "DELETE",
	        headers: {
	            'X-CSRF-TOKEN': csrfToken  // CSRF 토큰 헤더
	        },
			success: function(result) {
				alert(result); // 컨트롤러에서 return 값
				$.get("/admin/detail/" + selectedParentId, function(fragment) {
					$('#detailArea').html(fragment);
				});
			},
			error: function(xhr) {
				alert("삭제 중 오류가 발생했습니다.");
			}
		});
	});
});

// 상세코드 수정
// 등록 버튼 클릭
$('#btnAddDetail').on('click', function() {
	openCommonCodeDetailModal(false, {}); // edit=false
});

//수정 버튼 클릭시 모달 열기
$('#btnEditDetail').on('click', function() {
	const selectedRow = $('.detail-row.table-primary');
	if (!selectedRow.length) {
		alert('수정할 행을 선택하세요.');
		return;
	}

	const data = {
		comId: selectedRow.data('id'),
		comDtId: selectedRow.find('.comDtId-cell').text(),
		comDtNm: selectedRow.find('.comDtNm-cell').text(),
		comDtOrder: selectedRow.find('.comDtOrder-cell').text(),
		useYn: selectedRow.find('.useYn-dcell').text()
	};
	openCommonCodeDetailModal(true, data, selectedRow);
});

function openCommonCodeDetailModal(edit = false, data = {}, selectedRow = null) {
	$('#commonCodeDetailModalLabel').text(edit ? '상세공통코드 수정' : '상세공통코드 등록');

	// 필드 세팅
	$('#comDtId').val(data.comDtId || '');
	$('#comDtNm').val(data.comDtNm || '');
	$('#comDtOrder').val(data.comDtOrder || '');
	const defaultUseYn = edit ? data.useYn : 'Y';
	$('.useCodeAt').val(defaultUseYn);
	
	if (edit) {
		$("#comDtId").attr("readonly", true);   // 수정 모드 → ID 수정 불가
	} else {
		$("#comDtId").removeAttr("readonly");   // 등록 모드 → ID 입력 가능
	}
	
	$('#commonDetailModal').modal('show');
	
	const submitBtn = $('#commonCodeDetailForm button[type="submit"]');

	// submit 이벤트
	submitBtn.off('click').on('click', function(e) {
		e.preventDefault();

		const payload = {
			comDtId: $('#comDtId').val(),
			comDtNm: $('#comDtNm').val(),
			comDtOrder: $('#comDtOrder').val() !== '' ? parseInt($('#comDtOrder').val(), 10) : 0,
			useYn: $('#commonCodeDetailForm .useCodeAt').val()
		};
	
		$.ajax({
			url: '/admin/comDtUpdate/' + data.comDtId,
			type: 'POST', // POST 또는 PUT
			data: payload,
			beforeSend: function(xhr) {
				xhr.setRequestHeader(csrfHeader, csrfToken); // CSRF 헤더 추가
			},
			success: function(res) {
				alert(res);
				$('#commonDetailModal').modal('hide');
				
				// 선택 행 즉시 업데이트
				if (selectedRow && selectedRow.length) {
					selectedRow.find('.comDtId-cell').text(payload.comDtId);
					selectedRow.find('.comDtNm-cell').text(payload.comDtNm);
					selectedRow.find('.comDtOrder-cell').text(payload.comDtOrder !== null ? payload.comDtOrder : '');
					selectedRow.find('.useYn-dcell').text(payload.useYn);
				} else {
                    // 등록 후 상세 테이블 갱신
                    $.get("/admin/detail/" + selectedParentId, function(fragment) {
                        $('#detailArea').html(fragment);
                    });
                }
			},
			error: function(err) {
				alert('수정 실패: ' + err.responseText);
			}
			
		});
	});
}
