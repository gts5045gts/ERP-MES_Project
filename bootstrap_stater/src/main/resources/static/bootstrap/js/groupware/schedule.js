document.addEventListener('DOMContentLoaded', function() {
	var calendarEl1 = document.getElementById('calendar1');
	var calendarEl2 = document.getElementById('calendar2');

	var calendar1 = new FullCalendar.Calendar(calendarEl1, {
		initialView: 'dayGridMonth',
		timeZone:'local',
		locale: 'ko',
		events: '/schedule/events/all', // 모든 일정 데이터를 가져올 URL
		dateClick: function(info) {
			var clickedDate = info.dateStr;
			$('#addScheduleModal').modal('show');
			$('#modalStartDate').val(clickedDate);
			$('#modalEndDate').val(clickedDate);
		},
		eventClick: function(info) {
		        var eventId = info.event.id;
		        
		        // AJAX를 이용해 상세 정보를 가져옵니다.
		        $.ajax({
		            url: '/schedule/' + eventId, // 상세 정보를 가져올 URL
		            type: 'GET',
		            success: function(schedule) {
		                // 모달에 데이터 채우기
		                $('#detailTitle').text(schedule.schTitle);
		                $('#detailContent').text(schedule.schContent);
		                $('#detailStartDate').text(schedule.starttimeAt);
		                $('#detailEndDate').text(schedule.endtimeAt);
		                
		                // 상세정보 모달을 보여줍니다.
		                $('#scheduleDetailModal').modal('show');
		            },
		            error: function() {
		                alert('일정 정보를 불러오는 중 오류가 발생했습니다.');
		            }
		        });
		    }
	});
	calendar1.render();

	var calendar2 = new FullCalendar.Calendar(calendarEl2, {
		initialView: 'dayGridMonth',
		timeZone:'local',
		locale: 'ko',
		events: '/schedule/events/dept', // 부서별 일정 데이터를 가져올 URL
		dateClick: function(info) {
			var clickedDate = info.dateStr;
			$('#addScheduleModal').modal('show');
			$('#modalStartDate').val(clickedDate);
			$('#modalEndDate').val(clickedDate);
		},
		eventClick: function(info) {
		        var eventId = info.event.id;
		        
		        // AJAX를 이용해 상세 정보를 가져옵니다.
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

					calendar1.refetchEvents();
					calendar2.refetchEvents();
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