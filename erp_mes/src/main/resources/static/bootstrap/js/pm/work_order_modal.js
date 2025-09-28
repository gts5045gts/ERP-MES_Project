
console.log("CSRF Header:", csrfHeader);
console.log("CSRF Token:", csrfToken);


document.getElementById("planSelect").addEventListener("change", async (e) => {
    const planId = e.target.value;
    if (!planId) return;

    try {
        // 생산계획 기본 정보 가져오기
        const resPlan = await fetch(`/pm/workOrderInfo?plan_id=${planId}`);
        if (!resPlan.ok) throw new Error("서버 오류");
        const dataPlan = await resPlan.json();

        document.getElementById("product_name").value = dataPlan.productName;
        document.getElementById("plan_quantity").value = dataPlan.planQuantity;
		
		// 등록용 히든값들
		document.getElementById("plan_id").value = dataPlan.planId;
		document.getElementById("bom_id").value = dataPlan.bomId;
		console.log("planId:", dataPlan.planId, "bomId:", dataPlan.bomId);

        // BOM + 자재 재고 가져오기
        const resBom = await fetch(`/pm/workOderInventory?plan_id=${planId}`);
        if (!resBom.ok) throw new Error("자재 정보 불러오기 실패");

        const bomList = await resBom.json();
		console.log("bomList:", bomList);

        // div#MATERIAL_CNT 초기화
        const materialDiv = document.getElementById("MATERIAL_CNT");
        materialDiv.innerHTML = ""; // 기존 내용 제거

        // 리스트를 HTML로 생성
		bomList.forEach(bom => {
		    const card = document.createElement("div");
		    card.style.border = "1px solid #ddd";
		    card.style.borderRadius = "8px";
		    card.style.padding = "10px";
		    card.style.marginBottom = "10px";
//		    card.style.backgroundColor = "#fafafa";

		    // 자재명
		    const title = document.createElement("h6");
		    title.textContent = bom.materialName;
		    title.style.fontWeight = "bold";
		    title.style.marginBottom = "6px";
		    card.appendChild(title);

		    // 제품 1개당 필요한 자재 개수
		    const perUnit = document.createElement("p");
		    perUnit.textContent = `- 제품 한개당 필요한 자재 개수 : ${bom.quantity}개`;
		    card.appendChild(perUnit);

		    // 현재 필요한 자재 개수
		    const totalNeeded = document.createElement("p");
		    totalNeeded.textContent = `- 현재 필요한 자재 개수 : ${bom.totalNeededQuantity}개 (생산수량 × ${bom.quantity})`;
		    card.appendChild(totalNeeded);

		    // 현재 재고 개수
		    const stock = document.createElement("p");
		    stock.textContent = `- 현재 재고 개수 : ${bom.stockQuantity}개`;
		    card.appendChild(stock);

		    // 작업 가능 여부
		    const possible = document.createElement("p");
		    possible.textContent = `작업 가능 유무 : ${bom.workPossible}`;
		    possible.style.fontWeight = "bold";
		    possible.style.color = bom.workPossible === "작업 가능" ? "green" : "red";
		    card.appendChild(possible);

		    materialDiv.appendChild(card);
		});

    } catch (err) {
        console.error(err);
        alert("정보를 불러오지 못했습니다.");
    }
});



	// 작업지시서 등록 ajax
	document.getElementById("workOrderRegisterBtn").addEventListener("click", async () => {
	    const form = document.getElementById("workOrderForm");
	    const formData = new FormData(form);

	    // FormData → JSON 변환
	    const data = {};
	    formData.forEach((value, key) => {
			console.log(key, value);
	        data[key] = value;
	    });

	    try {
	        const res = await fetch("/pm/workOrderRegist", {
	            method: "POST",
	            headers: {
	                "Content-Type": "application/json",
					[csrfHeader]: csrfToken
	            },
	            body: JSON.stringify(data)
	        });

	        if (res.ok) {
	            alert("작업지시서가 등록되었습니다!");
	            // 모달 닫기
				$('#workOrderRegisterModal').modal('hide');

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
	
	






