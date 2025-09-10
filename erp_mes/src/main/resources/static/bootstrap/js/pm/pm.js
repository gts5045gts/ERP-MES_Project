// 그리드

document.addEventListener('DOMContentLoaded', function() {
    const grid = new tui.Grid({
        el: document.getElementById('grid'),
        columns: [
            { header: '제품코드', name: 'empId' },
            { header: '제품명', name: 'empId' },
            { header: '제품 유형', name: 'empName' },
            { header: '단위', name: 'deptName' },
        ],
        data: [
            { empId: 'E001', empName: '홍길동', deptName: '개발팀', posName: '사원' },
            { empId: 'E002', empName: '김철수', deptName: '영업팀', posName: '대리' },
            { empId: 'E003', empName: '이영희', deptName: '인사팀', posName: '과장' },
            { empId: 'E003', empName: '이영희', deptName: '인사팀', posName: '과장' }
        ],
        bodyHeight: 200,
        rowHeaders: ['rowNum'],  // 왼쪽에 행 번호 표시
        emptyMessage: "조회된 데이터가 없습니다."
    });
});
