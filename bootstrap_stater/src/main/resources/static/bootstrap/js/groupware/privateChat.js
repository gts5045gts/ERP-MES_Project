document.addEventListener('DOMContentLoaded', function() {
    // 헤더 스크립트에서 전역 변수를 가져와 사용합니다.
    const stompClient = window.stompClient;
    const userId = window.userId;
    const userName = window.userName;
    
    // 이 스크립트에서만 사용되는 변수들
    const privateMessageForm = document.querySelector('#privateMessageForm');
    const privateMessageInput = document.querySelector('#privateMessageInput');
    const messageArea = document.querySelector('#messageArea');
    const deptSelect = document.getElementById('deptSelect');
    const employeeSelect = document.getElementById('employeeSelect');

    // 웹소켓 연결이 완료될 때까지 기다렸다가 실행
    function onStompConnected() {
        if (stompClient.connected) {
            stompClient.subscribe('/user/queue/private', onMessageReceived);
            console.log("privateChat.js 웹소켓 구독 완료.");
            fetchDepartments();
        } else {
            setTimeout(onStompConnected, 100);
        }
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
            alert(receiverId + "에게 메시지를 전송했습니다.");
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
    
    // 헤더 스크립트의 웹소켓 연결이 완료될 때까지 기다렸다가 페이지 전용 로직 실행
    onStompConnected();
});