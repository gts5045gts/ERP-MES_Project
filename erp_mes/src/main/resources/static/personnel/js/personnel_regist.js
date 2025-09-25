/**
 *   등록 혹은 수정 페이지 js 
 */

// 전화번호 유효성 검사 
	document.getElementById('phone').addEventListener('input', function(e) {
  		let input = e.target.value.replace(/\D/g, ''); // 숫자 이외의 문자는 모두 제거
  
		// 최대 11자리까지 허용 (예: 01012345678)
		if (input.length > 11) {
			input = input.substring(0, 11);
		}
  		
		let formatted = '';
		// 입력된 숫자 길이에 따라 하이픈(-)을 삽입합니다.
		if (input.length < 4) { // 예: 010
			formatted = input;
		} else if (input.length < 7) {	// 예: 010-123
			formatted = input.substring(0, 3) + '-' + input.substring(3);
		} else if (input.length < 11) {	// 예: 010-123-456
			formatted = input.substring(0, 3) + '-' + input.substring(3, 6) + '-' + input.substring(6);
		} else {	// 예: 010-1234-5678 (11자리일 경우)
			formatted = input.substring(0, 3) + '-' + input.substring(3, 7) + '-' + input.substring(7);
		}

		// 포맷팅된 결과를 다시 input에 반영
		e.target.value = formatted;
	});
	
	/**
	// 주민등록 번호
	document.getElementById('emp_bd').addEventListener('input', function(e) {
  		let input = e.target.value.replace(/\D/g, ''); // 숫자 이외의 문자는 모두 제거
  
		// 최대 11자리까지 허용 (예: 01012345678)
		if (input.length > 13) {
			input = input.substring(0, 13);
		}
  		
		let formatted = '';
		// 입력된 숫자 길이에 따라 하이픈(-)을 삽입합니다.
		if (input.length < 6) { // 예: 010
			formatted = input;
		} else  {	// 예: 010-123
			formatted = input.substring(0, 6) + '-' + input.substring(6);
		} 

		// 포맷팅된 결과를 다시 input에 반영
		e.target.value = formatted;
	});
	**/
	document.getElementById('resident').addEventListener('input', function(e) {
	  	let input = e.target.value.replace(/\D/g, ''); // 숫자만 남김

		// 최대 13자리까지만 허용
		if (input.length > 13) {
			input = input.substring(0, 13);
		}

		let formatted = '';
		if (input.length <= 6) {
			// 앞 6자리만 입력 중
			formatted = input;
		} else if (input.length === 7) {
			// 앞 6자리 + '-' + 뒷번호 첫 글자
			formatted = input.substring(0, 6) + '-' + input.charAt(6);
		} else {
			// 앞 6자리 + '-' + 뒷번호 첫 글자 + ****** (항상 6개)
			let front = input.substring(0, 6);
			let firstBack = input.charAt(6);
			formatted = front + '-' + firstBack + '******';
		}

		e.target.value = formatted;
	});
	
	
	// 주소검색
	let btnSearchAddress = document.querySelector('#btnSearchAddress');
	btnSearchAddress.onclick = function() {
		new daum.Postcode({
			oncomplete : function(data) {
				document.querySelector('#emp_pc').value = data.zonecode;

				let addr = data.address;
				if (data.buildingName != "") {
					addr += " (" + data.buildingName + ")";
				}
				document.querySelector('#emp_ad').value = addr;
				document.querySelector('#emp_ba').focus();
			}
		}).open();

	}



	// 사진 등록 함수
	function employeePhoto(event) {
		const file = event.target.files[0]; // 선택된 파일 가져오기
		const reader = new FileReader(); // FileReader 객체 생성

		reader.onload = function() {
			const imgElement = document.getElementById("pFIle");
			imgElement.src = reader.result; // 미리보기 이미지 변경
			imgElement.style.width = "100%"; // 부모 요소에 맞게 조정
			imgElement.style.height = "100%";
			imgElement.style.objectFit = "cover"; // 비율 유지하면서 꽉 차게
		}

		if (file) {
			reader.readAsDataURL(file); // 파일을 읽어서 미리보기
		}
	}
	
// 	  '.notice-title'를 클릭하면 '.notice-list'에 .show 클래스를 토글
	document.querySelector('.notice-title').addEventListener('click', function() {
	document.querySelector('.notice-list').classList.toggle('show');
	});
	
	// 입력안된 것들 검사 
	
	function formBtn(){
		let form = document.getElementById("registForm");
		let name = document.getElementById("name").value;
		let phone = document.getElementById("phone").value;
		let resident = document.getElementById("resident").value;
		let passwd = document.getElementById("passwd").value;
		let staId = document.getElementById("staId").value;
		let dep = document.getElementById("dep").value;
		let pos = document.getElementById("pos").value;
		let level = document.getElementById("level").value;
		let addrNum = document.getElementById("emp_pc").value;
		let addr1 = document.getElementById("emp_ad").value;
		let addr2 = document.getElementById("emp_ba").value;
		let joinDate = document.getElementById("joinDate").value;
		
		if(name === ""){
			alert("이름을 입력해주세요.");
			return
		}else if(phone ===""){
			alert("휴대폰 번호를 입력하세요.");
			return
		}else if(resident ===""){
			alert("주민등록 번호를 입력하세요.");
			return
		}else if(passwd ===""){
			alert("비밀 번호를 입력하세요.");
			return
		}else if(staId ===""){
			alert("재직 현황를 선택하세요.");
			return
		}else if(dep ===""){
			alert("부서를 선택하세요.");
			return
		}else if(pos ===""){
			alert("직책을 선택하세요.");
			return
		}else if(level ===""){
			alert("보안등급을 선택하세요.");
			return
		}else if(addrNum ===""){
			alert("우편번호를 입력하세요.");
			return
		}else if(addr1 ===""){
			alert("주소를 입력하세요.");
			return
		}else if(addr2 ===""){
			alert("상세 주소를 입력하세요.");
			return
		}else if(joinDate ===""){
			alert("입사일을 입력하세요.");
			return
		}
		let formData = form.serialize();
		$.post("/personnel/regist", formData, function(response){
			alert("인사 등록이 완료되었습니다.");
			
		});
		
		
	}
	
	