document.addEventListener('DOMContentLoaded', function() {
    let stompClient = null;
    let userId = null;
    let userName = null;
    
    // 이 스크립트에서만 사용되는 변수들
    const privateMessageForm = document.querySelector('#privateMessageForm');
    const privateMessageInput = document.querySelector('#privateMessageInput');
    const messageArea = document.querySelector('#messageArea');
    const deptSelect = document.getElementById('deptSelect');
    const employeeSelect = document.getElementById('employeeSelect');

	const currentEmpIdInput = document.getElementById('currentEmpId');
	const currentEmpNameInput = document.getElementById('currentEmpName');
    
    // 웹소켓 연결 성공 시 실행되는 함수
    function onConnected() {
        console.log("1:1 메신저 웹소켓 연결 성공. 사용자 ID: " + userId);
        // 개인 메시지 채널만 구독
        stompClient.subscribe('/user/queue/private', onMessageReceived);
        // 부서 및 사원 목록 불러오기
        fetchDepartments();
    }
    
    // 웹소켓 연결 실패 시 실행되는 함수
    function onError(error) {
        console.error("1:1 메신저 웹소켓 연결 실패: " + error);
    }
    
    // 메시지 수신 시 실행되는 함수 (채팅창에 메시지 표시)
    function onMessageReceived(payload) {
        const message = JSON.parse(payload.body);
        const messageElement = document.createElement('div');
        const receiverName = employeeSelect.options[employeeSelect.selectedIndex]?.text || message.receiverId;
        messageElement.textContent = `${message.senderName} -> ${receiverName}: ${message.content}`;
        messageArea.appendChild(messageElement);
        messageArea.scrollTop = messageArea.scrollHeight;
    }

    // 메시지 전송 함수
    function sendPrivateMessage(event) {
        const messageContent = privateMessageInput.value.trim();
        const receiverId = employeeSelect.value;
        if (messageContent && receiverId && stompClient && stompClient.connected) {
            const chatMessage = {
                senderId: userId,
                senderName: userName,
                receiverId: receiverId,
                content: messageContent,
                type: 'CHAT'
            };
            stompClient.send("/app/chat.privateMessage", {}, JSON.stringify(chatMessage));
            privateMessageInput.value = '';
//            alert(receiverId + "에게 메시지를 전송했습니다.");
        } else {
            if (!messageContent) {
                alert("메시지를 입력하세요.");
            } else if (!receiverId) {
                alert("메시지를 보낼 사원을 선택하세요.");
            }
        }
        event.preventDefault();
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
        employeeSelect.innerHTML = '<option value="">사원 선택</option>';
        employeeSelect.disabled = true;
        if (!deptId) return;
        fetch(`/personnel/employees?deptId=${deptId}`)
            .then(response => response.json())
            .then(employees => {
                employees.forEach(emp => {
                    const option = document.createElement('option');
                    option.value = emp.empId;
                    option.textContent = emp.name;
                    employeeSelect.appendChild(option);
                });
                employeeSelect.disabled = false;
            })
            .catch(error => console.error('사원 목록을 불러오는 중 오류 발생:', error));
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
	    console.log("로그인된 사용자 ID:", userId);
	    console.log("로그인된 사용자 이름:", userName);
	    
	    // 이 위치에서 웹소켓 연결을 시작해야 합니다.
	    // userId와 userName이 정상적으로 설정된 후에만 연결이 가능하도록 합니다.
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
});