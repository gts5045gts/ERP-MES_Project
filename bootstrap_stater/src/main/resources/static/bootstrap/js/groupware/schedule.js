document.addEventListener('DOMContentLoaded', function() {

	var calendarEl1 = document.getElementById('calendar1');
	if (calendarEl1) {
		var calendar1 = new FullCalendar.Calendar(calendarEl1, {
			initialView: 'dayGridMonth',
			timeZone: 'local',
			locale: 'ko',
			// eventSources 배열을 사용하여 여러 데이터 소스를 관리합니다.
			eventSources: [
				// 1. 전체 일정 데이터 소스
				{
					url: '/schedule/events/all',
					method: 'GET'
				},
				// 2. 공휴일 데이터 소스
				{
					url: '/schedule/holidays',
					method: 'GET',
					extraParams: function() {
										    // events 함수와 달리 eventSources는 extraParams 함수를 지원합니다.
										    // calendar1 객체가 존재할 때만 view 속성에 접근하도록 합니다.
										    if (calendar1 && calendar1.view) {
										        var title = calendar1.view.title;
										        var year = title.split('년')[0];
										        var month = title.split('년')[1].trim().replace('월', '');
										        return { year: year, month: month };
										    }
										    return {};
										},
					className: 'holiday-event',
					color: '#dc3545', // 부트스트랩의 'danger' 색상
					editable: false
				}
			],
			dateClick: function(info) {
				var clickedDate = info.dateStr;
				$('#addScheduleModal').modal('show');
				$('#modalStartDate').val(clickedDate);
				$('#modalEndDate').val(clickedDate);
			},
			eventClick: function(info) {
				var eventId = info.event.id;
				$.ajax({
					url: '/schedule/' + eventId,
					type: 'GET',
					success: function(schedule) {
						$('#detailTitle').text(schedule.schTitle);
						$('#detailContent').text(schedule.schContent);
						$('#detailStartDate').val(schedule.starttimeAt.substring(0, 10));
						$('#detailStartTime').val(schedule.starttimeAt.substring(11, 16));
						$('#detailEndDate').val(schedule.endtimeAt.substring(0, 10));
						$('#detailEndTime').val(schedule.endtimeAt.substring(11, 16));
						$('#scheduleDetailModal').modal('show');
					},
					error: function() {
						alert('일정 정보를 불러오는 중 오류가 발생했습니다.');
					}
				});
			}
		});
		calendar1.render();
	}

	var calendarEl2 = document.getElementById('calendar2');
	if (calendarEl2) {
		var calendar2 = new FullCalendar.Calendar(calendarEl2, {
			initialView: 'dayGridMonth',
			timeZone: 'local',
			locale: 'ko',
			// eventSources 배열을 사용하여 여러 데이터 소스를 관리합니다.
			eventSources: [
				// 1. 부서별 일정 데이터 소스
				{
					url: '/schedule/events/dept',
					method: 'GET'
				},
				// 2. 공휴일 데이터 소스
				{
					url: '/schedule/holidays',
					method: 'GET',
					extraParams: function() {
										    // calendar2 객체가 존재할 때만 view 속성에 접근하도록 합니다.
										    if (calendar2 && calendar2.view) {
										        var title = calendar2.view.title;
										        var year = title.split('년')[0];
										        var month = title.split('년')[1].trim().replace('월', '');
										        return { year: year, month: month };
										    }
										    return {};
										},
					className: 'holiday-event',
					color: '#dc3545',
					editable: false
				}
			],
			dateClick: function(info) {
				var clickedDate = info.dateStr;
				$('#addScheduleModal').modal('show');
				$('#modalStartDate').val(clickedDate);
				$('#modalEndDate').val(clickedDate);
			},
			eventClick: function(info) {
				var eventId = info.event.id;
				$.ajax({
					url: '/schedule/' + eventId,
					type: 'GET',
					success: function(schedule) {
						$('#detailTitle').text(schedule.schTitle);
						$('#detailContent').text(schedule.schContent);
						$('#detailStartDate').text(schedule.starttimeAt);
						$('#detailEndDate').text(schedule.endtimeAt);
						$('#scheduleDetailModal').modal('show');
					},
					error: function() {
						alert('일정 정보를 불러오는 중 오류가 발생했습니다.');
					}
				});
			}
		});
		calendar2.render();
	}

	// =========================================================
	// 모달 관련 이벤트 처리
	// =========================================================

	$('#addScheduleModal form').on('submit', function(e) {
		e.preventDefault();

		var formData = {
			schTitle: $('#modalTitle').val(),
			schContent: $('#modalContent').val(),
			starttimeAt: $('#modalStartDate').val() + 'T' + $('#modalStartTime').val(),
			endtimeAt: $('#modalEndDate').val() + 'T' + $('#modalEndTime').val()
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

		var formData = {
			schId: $('#detailId').val(),
			schTitle: $('#detailTitle').val(),
			schContent: $('#detailContent').val(),
			starttimeAt: $('#detailStartDate').val() + 'T' + $('#detailStartTime').val(),
			endtimeAt: $('#detailEndDate').val() + 'T' + $('#detailEndTime').val()
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
		var eventId = $('#detailId').val();

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
});