document.addEventListener('DOMContentLoaded', function() {
    const token = $("meta[name='_csrf']").attr("content");
    const header = $("meta[name='_csrf_header']").attr("content");

    $(document).ajaxSend(function(e, xhr, options) {
        if (token && header) {
            xhr.setRequestHeader(header, token);
        }
    });

    // 숨겨진 input 필드에서 사용자 정보를 가져옵니다.
    const currentEmpId = document.getElementById('currentEmpId')?.value;
    const currentEmpName = document.getElementById('currentEmpName')?.value;
    const empDeptId = document.getElementById('empDeptId')?.value;
    const empDeptName = document.getElementById('empDeptName')?.value;
    
    // 캘린더 객체는 나중에 할당되므로 let으로 선언하고, 바깥 스코프로 이동시킵니다.
    let calendar1;
    let calendar2;
    
    const calendarEl1 = document.getElementById('calendar1');
    if (calendarEl1) {
        calendar1 = new FullCalendar.Calendar(calendarEl1, {
            initialView: 'dayGridMonth',
            timeZone: 'local',
            locale: 'ko',
            eventSources: [
                { url: '/schedule/events/all', method: 'GET' }
            ],
            dateClick: function(info) {
                const clickedDate = info.dateStr;
                $('#addScheduleModal').modal('show');
                $('#modalStartDate').val(clickedDate);
                $('#modalEndDate').val(clickedDate);
            },
            eventClick: function(info) {
                const eventId = info.event.id;
                $.ajax({
                    url: '/schedule/' + eventId,
                    type: 'GET',
                    success: function(response) {
                        if (response.success) {
                            const schedule = response.schedule;
                            const empName = response.empName;

                            $('#detailTitle').text(schedule.schTitle);
                            $('#detailEmpName').text(empName);
                            $('#detailContent').text(schedule.schContent);
                            $('#detailStartDate').text(schedule.starttimeAt);
                            $('#detailEndDate').text(schedule.endtimeAt);

                            $('#editScheduleId').val(schedule.schId);
                            $('#editSchEmpId').val(schedule.empId);
                            $('#editTitle').val(schedule.schTitle);
                            $('#editContent').val(schedule.schContent);
                            $('#editStartDate').val(schedule.starttimeAt.substring(0, 16));
                            $('#editEndDate').val(schedule.endtimeAt.substring(0, 16));

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
            datesSet: function(info) {
                const year = info.view.currentStart.getFullYear();
                const month = info.view.currentStart.getMonth() + 1;
                const holidaySource = calendar1.getEventSourceById('holiday-source-1');
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

    const calendarEl2 = document.getElementById('calendar2');
    if (calendarEl2) {
        calendar2 = new FullCalendar.Calendar(calendarEl2, {
            initialView: 'dayGridMonth',
            timeZone: 'local',
            locale: 'ko',
            eventSources: [
                {
                    url: '/schedule/events/dept',
                    method: 'GET',
                    extraParams: function() {
                        if (empDeptName) {
                            return { empDeptName: empDeptName };
                        }
                        return {};
                    }
                }
            ],
            dateClick: function(info) {
                const clickedDate = info.dateStr;
                $('#addScheduleModal').modal('show');
                $('#modalStartDate').val(clickedDate);
                $('#modalEndDate').val(clickedDate);
            },
            eventClick: function(info) {
                const eventId = info.event.id;
                $.ajax({
                    url: '/schedule/' + eventId,
                    type: 'GET',
                    success: function(response) {
                        if (response.success) {
                            const schedule = response.schedule;
                            const empName = response.empName;
                            $('#detailTitle').text(schedule.schTitle);
                            $('#detailEmpName').text(empName);
                            $('#detailContent').text(schedule.schContent);
                            $('#detailStartDate').text(schedule.starttimeAt);
                            $('#detailEndDate').text(schedule.endtimeAt);

                            $('#editScheduleId').val(schedule.schId);
                            $('#editSchEmpId').val(schedule.empId);
                            $('#editTitle').val(schedule.schTitle);
                            $('#editContent').val(schedule.schContent);
                            $('#editStartDate').val(schedule.starttimeAt.substring(0, 16));
                            $('#editEndDate').val(schedule.endtimeAt.substring(0, 16));

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
            datesSet: function(info) {
                const year = info.view.currentStart.getFullYear();
                const month = info.view.currentStart.getMonth() + 1;
                const holidaySource = calendar2.getEventSourceById('holiday-source-2');
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

    $('#addScheduleModal').on('show.bs.modal', function() {
        $('#modalAuthor').val(currentEmpName);
        $('#modalEmpId').val(currentEmpId);
    });

    $('#editScheduleBtn').on('click', function() {
        $('#readModeContent').hide();
        $('#editScheduleForm').show();
        $('#editScheduleBtn').hide();
        $('#deleteScheduleBtn').hide();
        $('#saveEditBtn').show();
    });

    $('#scheduleDetailModal').on('hidden.bs.modal', function() {
        $('#readModeContent').show();
        $('#editScheduleForm').hide();
        $('#editScheduleBtn').show();
        $('#deleteScheduleBtn').show();
        $('#saveEditBtn').hide();
    });

    $('#addScheduleModal form').on('submit', function(e) {
        e.preventDefault();
        const startDate = $('#modalStartDate').val();
        const endDate = $('#modalEndDate').val();
        let schTypeVal;
        if ($('#schType').length) {
            schTypeVal = $('#schType').val();
        } else {
            schTypeVal = $('input[name="schType"]').val();
        }

        const formData = {
            schTitle: $('#modalTitle').val(),
            schContent: $('#modalContent').val(),
            starttimeAt: startDate + 'T00:00:00',
            endtimeAt: endDate + 'T23:59:59',
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
                    if (calendar1) { calendar1.refetchEvents(); }
                    if (calendar2) { calendar2.refetchEvents(); }
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
        const formData = {
            schId: $('#editScheduleId').val(),
            schTitle: $('#editTitle').val(),
            schContent: $('#editContent').val(),
            starttimeAt: $('#editStartDate').val(),
            endtimeAt: $('#editEndDate').val(),
            empId: $('#editSchEmpId').val()
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
                    if (calendar1) { calendar1.refetchEvents(); }
                    if (calendar2) { calendar2.refetchEvents(); }
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
        const eventId = $('#editScheduleId').val();
        if (confirm('이 일정을 삭제하시겠습니까?')) {
            $.ajax({
                url: '/schedule/delete/' + eventId,
                type: 'POST',
                success: function(response) {
                    if (response.success) {
                        alert('일정이 성공적으로 삭제되었습니다.');
                        $('#scheduleDetailModal').modal('hide');
                        if (calendar1) { calendar1.refetchEvents(); }
                        if (calendar2) { calendar2.refetchEvents(); }
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

    $('#writeForm').on('submit', function(e) {
        e.preventDefault();
        let schTypeVal;
        if ($('#schType').length) {
            schTypeVal = $('#schType').val();
        } else {
            schTypeVal = $('#schType_hidden').val();
        }
        const formData = {
            schTitle: $('#modalTitle').val(),
            schContent: $('#modalContent').val(),
            starttimeAt: $('#modalStartDate').val() + 'T' + $('#modalStartTime').val(),
            endtimeAt: $('#modalEndDate').val() + 'T' + $('#modalEndTime').val(),
            schType: schTypeVal,
            empId: $('#modalEmpId').val()
        };

        $.ajax({
            url: '/schedule/save',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(formData),
            success: function(response) {
                if (response.success) {
                    alert('일정이 성공적으로 등록되었습니다.');
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
