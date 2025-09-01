document.addEventListener('DOMContentLoaded', function() {
	let token = $("meta[name='_csrf']").attr("content");
	let header = $("meta[name='_csrf_header']").attr("content");

	$(document).ajaxSend(function(e, xhr, options) {
		if (token && header) {
			xhr.setRequestHeader(header, token);
		}
	});

	// body 태그에서 현재 로그인한 사용자 ID를 가져옴
	let currentEmpId = $('body').data('current-emp-id');
	let currentEmpName = $('body').data('current-emp-name');
	let empDeptId = $('body').data('emp-dept-id');
	let empDeptName = $('body').data('emp-dept-name');

	let calendarEl1 = document.getElementById('calendar1');
	if (calendarEl1) {
		let calendar1 = new FullCalendar.Calendar(calendarEl1, {
			initialView: 'dayGridMonth',
			timeZone: 'local',
			locale: 'ko',
			// eventSources 배열을 사용하여 여러 데이터 소스를 관리합니다.
			eventSources: [
				// 1. 전체 일정 데이터 소스
				{
					url: '/schedule/events/all',
					method: 'GET'
				}
				// 2. 공휴일 데이터 소스
//				{
//					url: '/schedule/holidays',
//					method: 'GET',
//					className: 'holiday-event',
//					color: '#dc3545', 
//					editable: false
//				}
			],
			dateClick: function(info) {
				let clickedDate = info.dateStr;
				$('#addScheduleModal').modal('show');
				$('#modalStartDate').val(clickedDate);
				$('#modalEndDate').val(clickedDate);
			},
			eventClick: function(info) {
				let eventId = info.event.id;
				$.ajax({
					url: '/schedule/' + eventId,
					type: 'GET',
					success: function(response) {
						if (response.success) {
							let schedule = response.schedule;
							let empName = response.empName;

							// 상세 정보 표시
							$('#detailTitle').text(schedule.schTitle);
							$('#detailEmpName').text(empName);
							$('#detailContent').text(schedule.schContent);
							$('#detailStartDate').text(schedule.starttimeAt);
							$('#detailEndDate').text(schedule.endtimeAt);

							// 수정 폼에 데이터 채우기
							$('#editScheduleId').val(schedule.schId);
							$('#editSchEmpId').val(schedule.empId);
							$('#editTitle').val(schedule.schTitle);
							$('#editContent').val(schedule.schContent);
							// datetime-local 포맷에 맞게 변환
							$('#editStartDate').val(schedule.starttimeAt.substring(0, 16));
							$('#editEndDate').val(schedule.endtimeAt.substring(0, 16));

							// 권한에 따라 버튼 표시/숨김
							if (String(schedule.empId) === String(currentEmpId)) {
								$('#editScheduleBtn').show();
								$('#deleteScheduleBtn').show();
							} else {
								$('#editScheduleBtn').hide();
								$('#deleteScheduleBtn').hide();
							}

							$('#scheduleDetailModal').modal('show');
						} else {
							alert(response.message);
						}
					},
					error: function() {
						alert('일정 정보를 불러오는 중 오류가 발생했습니다.');
					}
				});
			},
			// datesSet 이벤트 핸들러 추가
			datesSet: function(info) {
				let year = info.view.currentStart.getFullYear();
				let month = info.view.currentStart.getMonth() + 1;
				let holidaySource = calendar1.getEventSourceById('holiday-source-1');
				if (holidaySource) {
					holidaySource.remove();
				}
				calendar1.addEventSource({
					id: 'holiday-source-1',
					url: '/schedule/holidays',
					method: 'GET',
					extraParams: { year: year, month: month },
					className: 'holiday-event',
					color: '#dc3545',
					editable: false
				});
			}
		});
		calendar1.render();
	}

	let calendarEl2 = document.getElementById('calendar2');
	if (calendarEl2) {
		let calendar2 = new FullCalendar.Calendar(calendarEl2, {
			initialView: 'dayGridMonth',
			timeZone: 'local',
			locale: 'ko',
			// eventSources 배열을 사용하여 여러 데이터 소스를 관리합니다.
			eventSources: [
				// 1. 부서별 일정 데이터 소스
				{
					url: '/schedule/events/dept',
					method: 'GET',
					extraParams: function(fetchInfo) {
						if (empDeptName) {
							return { empDeptName: empDeptName };
						}
						return {};
					}
				},
				// 2. 공휴일 데이터 소스
//				{
//					url: '/schedule/holidays',
//					method: 'GET',
//					className: 'holiday-event',
//					color: '#dc3545',
//					editable: false
//				}
			],
			dateClick: function(info) {
				let clickedDate = info.dateStr;
				$('#addScheduleModal').modal('show');
				$('#modalStartDate').val(clickedDate);
				$('#modalEndDate').val(clickedDate);
			},
			eventClick: function(info) {
				let eventId = info.event.id;
				$.ajax({
					url: '/schedule/' + eventId,
					type: 'GET',
					success: function(response) {
						if (response.success) {
							let schedule = response.schedule;
							let empName = response.empName;
							// 상세 정보 표시
							$('#detailTitle').text(schedule.schTitle);
							$('#detailEmpName').text(empName);
							$('#detailContent').text(schedule.schContent);
							$('#detailStartDate').text(schedule.starttimeAt);
							$('#detailEndDate').text(schedule.endtimeAt);

							// 수정 폼에 데이터 채우기
							$('#editScheduleId').val(schedule.schId);
							$('#editSchEmpId').val(schedule.empId);
							$('#editTitle').val(schedule.schTitle);
							$('#editContent').val(schedule.schContent);
							// datetime-local 포맷에 맞게 변환
							$('#editStartDate').val(schedule.starttimeAt.substring(0, 16));
							$('#editEndDate').val(schedule.endtimeAt.substring(0, 16));

							// ⭐ 권한에 따라 버튼 표시/숨김
							if (String(schedule.empId) === String(currentEmpId)) {
								$('#editScheduleBtn').show();
								$('#deleteScheduleBtn').show();
							} else {
								$('#editScheduleBtn').hide();
								$('#deleteScheduleBtn').hide();
							}
							console.log(empName);

							$('#scheduleDetailModal').modal('show');
						} else {
							alert(response.message);
						}
					},
					error: function() {
						alert('일정 정보를 불러오는 중 오류가 발생했습니다.');
					}
				});
			},
			// datesSet 이벤트 핸들러 추가
			datesSet: function(info) {
				let year = info.view.currentStart.getFullYear();
				let month = info.view.currentStart.getMonth() + 1;
				let holidaySource = calendar2.getEventSourceById('holiday-source-2');
				if (holidaySource) {
					holidaySource.remove();
				}
				calendar2.addEventSource({
					id: 'holiday-source-2',
					url: '/schedule/holidays',
					method: 'GET',
					extraParams: { year: year, month: month },
					className: 'holiday-event',
					color: '#dc3545',
					editable: false
				});
			}
		});
		calendar2.render();
	}

	// =========================================================
	// 모달 관련 이벤트 처리
	// =========================================================

	// 모달이 열릴 때 이벤트
	$('#addScheduleModal').on('show.bs.modal', function() {
		$('#modalAuthor').val(currentEmpName);
		$('#modalEmpId').val(currentEmpId);
	});

	// 수정 버튼 클릭 이벤트
	$('#editScheduleBtn').on('click', function() {
		$('#readModeContent').hide();
		$('#editScheduleForm').show();
		$('#editScheduleBtn').hide();
		$('#deleteScheduleBtn').hide();
		$('#saveEditBtn').show();
	});

	// ⭐ 모달 닫힐 때 원래 상태로 초기화
	$('#scheduleDetailModal').on('hidden.bs.modal', function() {
		$('#readModeContent').show();
		$('#editScheduleForm').hide();
		$('#editScheduleBtn').show();
		$('#deleteScheduleBtn').show();
		$('#saveEditBtn').hide();
	});

	$('#addScheduleModal form').on('submit', function(e) {
		e.preventDefault();
		let startDate = $('#modalStartDate').val();
		let endDate = $('#modalEndDate').val();
		let schTypeVal;
		if ($('#schType').length) { // #schType(드롭다운)이 존재하면
			schTypeVal = $('#schType').val();
		} else {
			schTypeVal = $('input[name="schType"]').val(); // 숨겨진 필드의 값을 가져옴
		}

		let formData = {
			schTitle: $('#modalTitle').val(),
			schContent: $('#modalContent').val(),
			starttimeAt: startDate + 'T00:00:00', // 날짜 + 자정 시간
			endtimeAt: endDate + 'T23:59:59',   // 날짜 + 하루의 마지막 시간
			schType: schTypeVal,
			empId: currentEmpId
		};

		$.ajax({
			url: '/schedule/save',
			type: 'POST',
			contentType: 'application/json',
			data: JSON.stringify(formData),
			success: function(response) {
				if (response.success) {
					alert('일정이 성공적으로 등록되었습니다.');
					$('#addScheduleModal').modal('hide');

					if (calendarEl1) { calendar1.refetchEvents(); }
					if (calendarEl2) { calendar2.refetchEvents(); }
				} else {
					alert('일정 등록 실패: ' + response.message);
				}
			},
			error: function() {
				alert('일정 등록 중 오류가 발생했습니다.');
			}
		});
	});

	$('#editScheduleForm').on('submit', function(e) {
		e.preventDefault();

		let formData = {
			schId: $('#editScheduleId').val(),
			schTitle: $('#editTitle').val(),
			schContent: $('#editContent').val(),
			starttimeAt: $('#editStartDate').val(),
			endtimeAt: $('#editEndDate').val(),
			empId: $('#editSchEmpId').val() // 수정 시 empId 포함
		};

		$.ajax({
			url: '/schedule/update',
			type: 'POST',
			contentType: 'application/json',
			data: JSON.stringify(formData),
			success: function(response) {
				if (response.success) {
					alert('일정이 성공적으로 수정되었습니다.');
					$('#scheduleDetailModal').modal('hide');

					if (calendarEl1) { calendar1.refetchEvents(); }
					if (calendarEl2) { calendar2.refetchEvents(); }
				} else {
					alert('일정 수정 실패: ' + response.message);
				}
			},
			error: function() {
				alert('일정 수정 중 오류가 발생했습니다.');
			}
		});
	});

	$('#deleteScheduleBtn').on('click', function() {
		let eventId = $('#editScheduleId').val(); // 숨겨진 폼에서 ID 가져오기

		if (confirm('이 일정을 삭제하시겠습니까?')) {
			$.ajax({
				url: '/schedule/delete/' + eventId,
				type: 'POST',
				success: function(response) {
					if (response.success) {
						alert('일정이 성공적으로 삭제되었습니다.');
						$('#scheduleDetailModal').modal('hide');

						if (calendarEl1) { calendar1.refetchEvents(); }
						if (calendarEl2) { calendar2.refetchEvents(); }
					} else {
						alert('일정 삭제 실패: ' + response.message);
					}
				},
				error: function() {
					alert('일정 삭제 중 오류가 발생했습니다.');
				}
			});
		}
	});

	// schWrite.html 페이지의 폼 제출 처리
	$('#writeForm').on('submit', function(e) {
		e.preventDefault(); // 기본 폼 제출 동작을 막음

		let schTypeVal;
		// isAdmin에 따라 schType 값을 다르게 가져오는 로직 추가
		if ($('#schType').length) {
			schTypeVal = $('#schType').val();
		} else {
			schTypeVal = $('#schType_hidden').val();
		}
		// 폼 데이터 구성
		let formData = {
			schTitle: $('#modalTitle').val(),
			schContent: $('#modalContent').val(),
			starttimeAt: $('#modalStartDate').val() + 'T' + $('#modalStartTime').val(),
			endtimeAt: $('#modalEndDate').val() + 'T' + $('#modalEndTime').val(),
			schType: schTypeVal,
			empId: $('#modalEmpId').val() // 작성자 ID
		};

		// AJAX 요청
		$.ajax({
			url: '/schedule/save',
			type: 'POST',
			contentType: 'application/json',
			data: JSON.stringify(formData),
			success: function(response) {
				if (response.success) {
					alert('일정이 성공적으로 등록되었습니다.');
					// 성공하면 일정 목록 페이지로 이동
					window.location.href = '/schedule';
				} else {
					alert('일정 등록 실패: ' + response.message);
				}
			},
			error: function() {
				alert('일정 등록 중 오류가 발생했습니다.');
			}
		});
	});
});