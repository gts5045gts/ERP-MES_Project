
console.log("CSRF Header:", csrfHeader);
console.log("CSRF Token:", csrfToken);


document.addEventListener('DOMContentLoaded', () => {
    let orderGrid; // Grid 변수 미리 선언
    const modalEl = $('#planRegisterModal');
    const btn = $('#planModalBtn');

    btn.on('click', () => {
        modalEl.modal('show'); // 모달 열기
    });

    // 모달 완전히 열린 후
    modalEl.on('shown.bs.modal', function () {
        // Grid가 아직 초기화되지 않았다면 초기화
        if (!orderGrid) {
            orderGrid = new tui.Grid({
                el: document.getElementById('order-grid'),
//				rowHeaders: ['checkbox'],
                columns: [
                    { header: '수주번호', name: 'orderId', sortable: true, align: 'center' },
                    { header: '제품명', name: 'productName', sortable: true, align: 'center' },
                    { header: '총수량', name: 'orderQty', sortable: true, align: 'center' },
                    { header: '상태', name: 'orderDetailStatus', sortable: true, align: 'center' },
                    { header: '납기일', name: 'deliveryDate', sortable: true, align: 'center' },
                ],
                bodyHeight: 180 // 모달 내부라면 고정 높이 필수
            });
        }

        // 데이터 로드
        loadOrders();
    });

    async function loadOrders() {
        const res = await fetch('ordersList');
        const ordersList = await res.json();

        // Grid에 데이터 넣기
        if (orderGrid) orderGrid.resetData(ordersList);
        console.log("ordersList : ", ordersList);
    }
});

// 수주 그리드 클릭 후 제품명 가져오기
orderGrid.on('click', (ev) => {
    const clickedRow = ev.rowKey;            // 클릭한 행 key
    const rowData = orderGrid.getRow(clickedRow); // 클릭한 행 데이터 가져오기

    const orderId = rowData.orderId;         // 클릭한 수주번호

    // AJAX 요청으로 해당 수주번호의 receive 상태 제품 가져오기
    loadProductsForOrder(orderId);
});

async function loadProductsForOrder(orderId) {
    const res = await fetch(`/pm/ordersProduct`); // GET 요청
    const products = await res.json();

    const select = document.querySelector('select[name="productId"]');
    select.innerHTML = '<option value="">선택하세요</option>'; // 초기화

    products.forEach(p => {
        const option = document.createElement('option');
        option.value = p.productId;
        option.text = `${p.productName} (${p.productId})`;
        select.appendChild(option);
    });
}



	// 생산계획 등록 ajax
	document.getElementById("planRegisterBtn").addEventListener("click", async () => {
	    const form = document.getElementById("planForm");
	    const formData = new FormData(form);

	    // FormData → JSON 변환
	    const data = {};
	    formData.forEach((value, key) => {
	        data[key] = value;
	    });

	    try {
	        const res = await fetch("/pm/productPlanRegist", {
	            method: "POST",
	            headers: {
	                "Content-Type": "application/json",
					[csrfHeader]: csrfToken
	            },
	            body: JSON.stringify(data)
	        });

	        if (res.ok) {
	            alert("생산계획이 등록되었습니다!");
	            // 모달 닫기
				$('#planRegisterModal').modal('hide');

	            // 제품 목록 새로고침
	            location.reload();
	        } else {
	            alert("등록 실패!");
	        }
	    } catch (err) {
	        console.error(err);
	        alert("오류 발생");
	    }
	});
	
	






