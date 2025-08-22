// ============ 검색기능 ==========================
// 상위공통코드
$(document).ready(function() {
	loadCommonCodes();
	
	$('#codeSearch').on('input', function() {
		const keyword = $(this).val();
		loadCommonCodes(keyword);
	});
});

function loadCommonCodes(keyword = '') {
	$.get('/admin/commonCode', {
		comId: keyword,
		comNm: keyword,
		useYn: keyword
		}, function(data) {
		const tbody = $('#commonTableBody tbody');
		tbody.empty();
		
		if(data.length === 0) {
			tbody.append('<tr><td colspan="4" class="text-center">검색 결과가 없습니다.</td></tr>');
		}
		
		data.forEach(code => {
			const row = `<tr class="master-row" data-id= "${code.comId}">
							<td>${code.comId}</td>
							<td class="comNm-cell">${code.comNm}</td>
							<td class="useYn-cell">${code.useYn}</td>
							<td>${code.createdAt}</td>
						</tr>`;
			tbody.append(row);			
		});
	});
}
