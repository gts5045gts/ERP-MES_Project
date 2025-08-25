document.addEventListener('DOMContentLoaded', function () {
  const calendarEl1 = document.getElementById('calendar1');
  const calendar1 = new FullCalendar.Calendar(calendarEl1, {
    initialView: 'dayGridMonth',
    locale: 'ko',
    headerToolbar: {
      left: 'prev,next today',
      center: 'title',
      right: 'dayGridMonth,timeGridWeek,timeGridDay'
    },
    events: [
      {
        title: '전체 회의',
        start: '2025-08-14'
      },
      {
        title: '전체 마감',
        start: '2025-08-20'
      }
    ]
  });
  calendar1.render();

  const calendarEl2 = document.getElementById('calendar2');
  const calendar2 = new FullCalendar.Calendar(calendarEl2, {
    initialView: 'dayGridMonth',
    locale: 'ko',
    headerToolbar: {
      left: 'prev,next today',
      center: 'title',
      right: 'dayGridMonth,timeGridWeek,timeGridDay'
    },
    events: [
      {
        title: '부서 회의',
        start: '2025-08-15'
      },
      {
        title: '부서 마감',
        start: '2025-08-22'
      }
    ]
  });
  calendar2.render();
});
