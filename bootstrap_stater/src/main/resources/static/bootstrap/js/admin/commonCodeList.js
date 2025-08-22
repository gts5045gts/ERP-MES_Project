// ============ 검색기능 ==========================
// 상위공통코드
$('#codeSearch').on('keyup', function() {
	const keyword = $(this).val().trim();
	$.ajax({
		url: '/admin/comSearch',
		method: 'GET',
		data: { keyword: keyword},
		success: function(data) {
			let html = '';
			if(data.length === 0) {
				html = `<tr><td colspan="5" class="text-center">검색결과가 없습니다.</td></tr>`;
			}        
			data.forEach(code => {
						html += `<tr class="master-row" data-id="${code.comId}">
										<td>${code.comId}</td>
										<td class="comNm-cell">${code.comNm}</td>
										<td class="useYn-cell">${code.useYn}</td>
										<td>${code.createdAt}</td>
									</tr>`;
			});
			$('#commonTableBody').html(html);
		},
	});
});

$('#codeDetailSearch').on('keyup', function() {
	const keyword = $(this).val().trim();
	$.ajax({
		url: '/admin/comDtSearch',
		method: 'GET',
		data: { keyword: keyword},
		success: function(data) {
			let html = '';
			if(data.length === 0){
				html = `<tr><td colspan="5" class="text-center">검색결과가 없습니다.</td></tr>`;
			}
			data.forEach(code => {
						html += `<tr class="detail-row" data-id="${code.comDtId}">
										<td class="comDtId-cell">${code.comDtId}</td>
										<td class="comDtNm-cell">${code.comDtNm}</td>
										<td class="useYn-dcell">${code.useYn}</td>
										<td class="comDtOrder-cell">${code.comDtOrder}</td>
										<td>${code.createdAt}</td>
									</tr>`;
			});
			$('#smallTableBody').html(html);
			$('#detailArea').show(); // 검색 시 테이블 영역도 표시
		},
	});
});



