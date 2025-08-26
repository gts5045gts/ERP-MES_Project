document.addEventListener('DOMContentLoaded', function() {
    // 무한스크롤용 임시 데이터 생성 함수
    function createRows(count = 50) {
        const rows = [];
        for (let i = 0; i < count; i += 1) {
            const row = {
                name: `Name ${Math.floor(Math.random() * 100)}`,
                artist: `Artist ${Math.floor(Math.random() * 100)}`,
                price: Math.floor(Math.random() * 100000),
                release: new Date().toISOString().slice(0, 10),
                genre: `Genre ${Math.floor(Math.random() * 5)}`
            };
            rows.push(row);
        }
        return rows;
    }

    // Grid 컬럼 정의
    const columns = [
        { header: '사원번호', name: 'price', filter: 'number' },
        { header: '이름', name: 'name', filter: { type: 'text', showApplyBtn: true, showClearBtn: true } },
		{ header: '부서', name: 'artist', filter: 'select' },
		{ header: '직급', name: 'artist', filter: 'select' },
        { header: '입사일', name: 'release', filter: { type: 'date', options: { format: 'yyyy.MM.dd' } } },
        { header: '총 휴가일수', name: 'price', filter: 'number' },
        { header: '사용 휴가일수', name: 'price', filter: 'number' },
        { header: '잔여 휴가일수', name: 'price', filter: 'number' },
        { header: '휴가 소멸일', name: 'release', filter: { type: 'date', options: { format: 'yyyy.MM.dd' } } }
    ];

    // Grid 생성 (초기 데이터는 빈 배열)
    const grid = new tui.Grid({
        el: document.getElementById('grid'),
        data: [],
        columns: columns,
        bodyHeight: 300,
        rowHeaders: ['rowNum'],
		scrollX: false      // 가로 스크롤 금지
    });

    // 초기 데이터 한 번 추가
    grid.resetData(createRows());

    // 무한스크롤 이벤트
    grid.on('scrollEnd', () => {
        grid.appendRows(createRows());
    });
});