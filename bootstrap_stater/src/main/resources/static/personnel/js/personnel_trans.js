// 'DOMContentLodaded' -> HTML 구조가 완전히 로드된 후에 자바스크립트 코드가 실행
document.addEventListener('DOMContentLoaded', () => {

    // 1. 주요 HTML 요소들 선택
    const issueBtn = document.getElementById('issueBtn');

    // 2. 이벤트 리스너 정의

    // '발령' 버튼 클릭 시 팝업 윈도우 열기
    issueBtn.addEventListener('click', () => {
		// 팝업 윈도우 설정
		const popupUrl = '/personnel/trans/save'; // 팝업 페이지 URL
		const windowName = 'personnelTransPopup';
		const windowFeatures = 'width=950,height=850,scrollbars=yes,resizable=yes';

		window.open(popupUrl, windowName, windowFeatures);
    });

    // 4. 페이지 로드 시 초기 데이터 로딩 함수 호출
    loadDropdowns();
});