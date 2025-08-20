document.addEventListener('DOMContentLoaded', function() {
    var calendarEl1 = document.getElementById('calendar1');
    var calendarEl2 = document.getElementById('calendar2');

    var calendar1 = new FullCalendar.Calendar(calendarEl1, {
        initialView: 'dayGridMonth', // 월별 보기
        locale: 'ko', // 한국어 설정
        // ⭐ 날짜 클릭 이벤트 핸들러
        dateClick: function(info) {
            // 클릭된 날짜의 정보(info)를 받아옵니다.
            var clickedDate = info.dateStr;

            // 1. 모달 띄우기
            $('#addScheduleModal').modal('show');

            // 2. 모달의 시작일과 종료일 필드에 클릭된 날짜를 자동으로 채워줍니다.
            $('#modalStartDate').val(clickedDate);
            $('#modalEndDate').val(clickedDate);
        }
    });
    calendar1.render();

    var calendar2 = new FullCalendar.Calendar(calendarEl2, {
        initialView: 'dayGridMonth',
        locale: 'ko',
        dateClick: function(info) {
            var clickedDate = info.dateStr;
            $('#addScheduleModal').modal('show');
            $('#modalStartDate').val(clickedDate);
            $('#modalEndDate').val(clickedDate);
        }
    });
    calendar2.render();
});