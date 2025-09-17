document.addEventListener('DOMContentLoaded', function() {
	let stompClient = null;
	let userId = null;
	let userName = null;
	let currentReceiverId = null; // 현재 대화 중인 상대방 ID

	// 이 스크립트에서만 사용되는 변수들
	const privateMessageForm = document.querySelector('#privateMessageForm');
	const privateMessageInput = document.querySelector('#privateMessageInput');
	const messageArea = document.querySelector('#messageArea');
	const deptSelect = document.getElementById('deptSelect');
	const employeeList = document.getElementById('employeeList');

	const currentEmpIdInput = document.getElementById('currentEmpId');
	const currentEmpNameInput = document.getElementById('currentEmpName');

	// 웹소켓 연결 성공 시 실행되는 함수
	function onConnected() {
		console.log("1:1 메신저 웹소켓 연결 성공. 사용자 ID: " + userId);
		// 개인 메시지 채널만 구독
		stompClient.subscribe('/user/queue/private', onMessageReceived);
		// 부서 및 사원 목록 불러오기
		fetchDepartments();
		// 웹소켓 연결시 채팅 초기화
		initializeChat();
	}

	// 웹소켓 연결 실패 시 실행되는 함수
	function onError(error) {
		console.error("1:1 메신저 웹소켓 연결 실패: " + error);
	}

	// 메시지 수신 시 실행되는 함수 (채팅창에 메시지 표시)
	function onMessageReceived(payload) {
		const message = JSON.parse(payload.body);
		// 현재 대화 상대의 메시지이거나, 내가 보낸 메시지일 때만 화면에 추가
		if (message.senderId === currentReceiverId || message.receiverId === currentReceiverId) {
			displayMessage(message);
		}
	}

	// 메시지를 화면에 표시하는 헬퍼 함수
	function displayMessage(message) {
		const messageElement = document.createElement('div');
		// 보낸 사람과 받는 사람 구분
		if (message.senderId === userId) {
			messageElement.textContent = `나: ${message.content}`;
			messageElement.style.textAlign = 'right';
		} else {
			messageElement.textContent = `${message.senderName}: ${message.content}`;
			messageElement.style.textAlign = 'left';
		}
		messageArea.appendChild(messageElement);
		messageArea.scrollTop = messageArea.scrollHeight;
	}

	// 메시지 전송 함수
	function sendPrivateMessage(event) {
		const messageContent = privateMessageInput.value.trim();
		if (messageContent && currentReceiverId && stompClient && stompClient.connected) {
			const chatMessage = {
				senderId: userId,
				senderName: userName,
				receiverId: currentReceiverId,
				content: messageContent,
				type: 'CHAT'
			};

			// 보낸 메시지를 화면에 즉시 표시
			displayMessage(chatMessage);

			stompClient.send("/app/chat.privateMessage", {}, JSON.stringify(chatMessage));
			privateMessageInput.value = '';
		} else {
			if (!messageContent) {
				alert("메시지를 입력하세요.");
			} else if (!currentReceiverId) {
				alert("메시지를 보낼 사원을 선택하세요.");
			}
		}
		event.preventDefault();
	}

	function initializeChat() {
		const urlParams = new URLSearchParams(window.location.search);
		const chatPartnerIdFromUrl = urlParams.get('chatPartnerId');

		if (chatPartnerIdFromUrl) {
			// URL 파라미터가 있으면 해당 사용자와의 대화 기록을 바로 로드
			currentReceiverId = chatPartnerIdFromUrl;
			fetchChatHistory(chatPartnerIdFromUrl);

			// UI에서 해당 사원을 선택 상태로 표시 (부서/사원 목록이 로드된 후 실행)
			deptSelect.addEventListener('change', () => fetchEmployeesByDept(event.target.value));
			fetchDepartments(() => {
				const empElement = document.querySelector(`[data-emp-id="${chatPartnerIdFromUrl}"]`);
				if (empElement) {
					empElement.classList.add('active');
				}
			});

			// 헤더의 알림을 초기화하는 함수 호출
			markAllMessagesAsRead();
		} else {
			// URL 파라미터가 없으면 최근 대화 기록을 불러오는 기존 로직 실행
			fetch('/api/chat/recent')
				// ... (기존 initializeChat 로직) ...
				.then(recentMessages => {
					if (recentMessages && recentMessages.length > 0) {
						const recentChatPartnerId = recentMessages[0].senderId === userId ? recentMessages[0].receiverId : recentMessages[0].senderId;
						currentReceiverId = recentChatPartnerId;
						fetchChatHistory(recentChatPartnerId);

						// UI 업데이트
						fetchDepartments(() => {
							const empElement = document.querySelector(`[data-emp-id="${recentChatPartnerId}"]`);
							if (empElement) {
								empElement.classList.add('active');
							}
						});
					} else {
						console.log("최근 대화 기록이 없습니다.");
						messageArea.innerHTML = '<div>최근 대화 기록이 없습니다.</div>';
					}
				})
				.catch(error => console.error("채팅 초기화 오류:", error));
		}
	}

	function markAllMessagesAsRead() {
		const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
		fetch('/api/messages/read', {
			method: 'POST',
			headers: {
				'X-CSRF-TOKEN': csrfToken,
				'Content-Type': 'application/json'
			}
		})
			.then(response => {
				if (!response.ok) {
					throw new Error('읽음 상태 업데이트 실패');
				}
				console.log("읽지 않은 메시지 상태 업데이트 완료");
			})
			.catch(error => {
				console.error('읽음 상태 업데이트 오류:', error);
			});
	}

	// 부서 목록을 불러오는 함수
	function fetchDepartments() {
		fetch('/personnel/departments')
			.then(response => response.json())
			.then(departments => {
				const departmentSelect = document.getElementById('deptSelect');
				departmentSelect.innerHTML = '<option value="">부서 선택</option>';
				departments.forEach(dept => {
					const option = document.createElement('option');
					option.value = dept.comDtId;
					option.textContent = dept.comDtNm;
					deptSelect.appendChild(option);
				});
			})
			.catch(error => console.error('부서 목록을 불러오는 중 오류 발생:', error));
	}

	// 선택된 부서에 따라 사원 목록을 불러오는 함수
	function fetchEmployeesByDept(deptId) {
		employeeList.innerHTML = ''; // 기존 목록 초기화
		if (!deptId) return;
		fetch(`/personnel/employees?deptId=${deptId}`)
			.then(response => response.json())
			.then(employees => {
				employees.forEach(emp => {
					// 로그인한 본인은 목록에서 제외
					if (emp.empId !== userId) {
						const li = document.createElement('li');
						li.className = 'list-group-item list-group-item-action';
						li.textContent = emp.name;
						li.dataset.empId = emp.empId;
						li.addEventListener('click', () => selectEmployee(li, emp.empId));
						employeeList.appendChild(li);
					}
				});
			})
			.catch(error => console.error('사원 목록을 불러오는 중 오류 발생:', error));
	}

	// 사원 선택 시 대화방 표시 및 기록 불러오기
	function selectEmployee(element, empId) {
		// 기존 선택된 항목의 활성화 클래스 제거
		document.querySelectorAll('#employeeList .list-group-item').forEach(item => {
			item.classList.remove('active');
		});
		// 현재 선택된 항목에 활성화 클래스 추가
		element.classList.add('active');

		currentReceiverId = empId;
		messageArea.innerHTML = ''; // 대화창 초기화

		// 선택된 사원과의 대화 기록 불러오기
		fetchChatHistory(empId);
	}

	// 특정 사원과의 대화 기록을 불러오는 함수
	function fetchChatHistory(receiverId) {
		fetch(`/api/chat/messages?receiverId=${receiverId}`)
			.then(response => {
				if (!response.ok) {
					throw new Error('Network response was not ok');
				}
				return response.json();
			})
			.then(messages => {
				messages.forEach(msg => {
					displayMessage(msg); // 메시지를 화면에 표시
				});
				messageArea.scrollTop = messageArea.scrollHeight;
			})
			.catch(error => console.error('대화 기록을 불러오는 중 오류 발생:', error));
	}

	// 이벤트 리스너 등록
	deptSelect.addEventListener('change', (event) => {
		const selectedDeptId = event.target.value;
		fetchEmployeesByDept(selectedDeptId);
	});

	privateMessageForm.addEventListener('submit', sendPrivateMessage, true);

	// 웹소켓 연결 시작
	if (currentEmpIdInput && currentEmpNameInput) {
		userId = currentEmpIdInput.value;
		userName = currentEmpNameInput.value;
		const socket = new SockJS('/ws/chat');
		stompClient = Stomp.over(socket);
		const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
		const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
		const headers = {
			[csrfHeader]: csrfToken
		};
		stompClient.connect(headers, onConnected, onError);
	} else {
		console.error("사용자 정보를 찾을 수 없습니다. HTML을 확인하세요.");
	}

	// 페이지 로드 후 채팅 초기화 함수 호출
	initializeChat();
});