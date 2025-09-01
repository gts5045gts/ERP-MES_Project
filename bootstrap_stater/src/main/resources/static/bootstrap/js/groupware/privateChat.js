document.addEventListener('DOMContentLoaded', function() {
    // 기존 변수들은 그대로 둡니다.
    let stompClient = null;
    let userId = null;
    let userName = null;
    const privateMessageForm = document.querySelector('#privateMessageForm');
    const privateMessageInput = document.querySelector('#privateMessageInput');
    const messageArea = document.querySelector('#messageArea');
    const deptSelect = document.getElementById('deptSelect');
    const employeeSelect = document.getElementById('employeeSelect');

    // 웹소켓 연결 함수는 그대로 둡니다.
    function connect() {
        userId = document.body.dataset.currentEmpId;
        userName = document.body.dataset.current-emp-name;
        const socket = new SockJS('/ws/chat');
        stompClient = Stomp.over(socket);
        stompClient.connect({}, onConnected, onError);
    }
    
    // 웹소켓 연결 성공 시 실행되는 함수
    function onConnected() {
        stompClient.subscribe('/user/queue/private', onMessageReceived);
        console.log("웹소켓 연결 성공. 사용자 ID: " + userId);
    }

    // 메시지 전송 함수 수정
    function sendPrivateMessage(event) {
        const messageContent = privateMessageInput.value.trim();
        // ⭐ 받는 사람 ID를 드롭다운에서 가져옵니다.
        const receiverId = employeeSelect.value; 

        if (messageContent && receiverId && stompClient) {
            const chatMessage = {
                senderId: userId,
                senderName: userName,
                receiverId: receiverId, // 수정된 부분
                content: messageContent,
                type: 'CHAT'
            };
            stompClient.send("/app/chat.privateMessage", {}, JSON.stringify(chatMessage));
            privateMessageInput.value = '';
        } else {
            // 메시지 내용이나 받는 사람이 선택되지 않았을 경우 알림
            if (!messageContent) {
                alert("메시지를 입력하세요.");
            } else if (!receiverId) {
                alert("메시지를 보낼 사원을 선택하세요.");
            }
        }
        event.preventDefault();
    }
    
    // 메시지 수신 함수는 그대로 둡니다.
    function onMessageReceived(payload) {
        const message = JSON.parse(payload.body);
        const messageElement = document.createElement('div');
        // ⭐ 메시지 출력 형식을 수정 (받는 사람 이름으로 표시)
        const receiverName = employeeSelect.options[employeeSelect.selectedIndex]?.text || message.receiverId;
        messageElement.textContent = `${message.senderName} -> ${receiverName}: ${message.content}`;
        messageArea.appendChild(messageElement);
        messageArea.scrollTop = messageArea.scrollHeight;
    }

    // 부서 목록을 불러오는 함수
    function fetchDepartments() {
        fetch('/api/departments')
            .then(response => response.json())
            .then(departments => {
                departments.forEach(dept => {
                    const option = document.createElement('option');
                    option.value = dept.deptId;
                    option.textContent = dept.deptName;
                    deptSelect.appendChild(option);
                });
            })
            .catch(error => console.error('부서 목록을 불러오는 중 오류 발생:', error));
    }

    // 선택된 부서에 따라 사원 목록을 불러오는 함수
    function fetchEmployeesByDept(deptId) {
        employeeSelect.innerHTML = '<option value="">사원 선택</option>'; // 기존 목록 초기화
        employeeSelect.disabled = true; // 사원 드롭다운 비활성화

        if (!deptId) {
            return;
        }

        fetch(`/api/employees?deptId=${deptId}`)
            .then(response => response.json())
            .then(employees => {
                employees.forEach(emp => {
                    const option = document.createElement('option');
                    option.value = emp.empId;
                    option.textContent = emp.empName;
                    employeeSelect.appendChild(option);
                });
                employeeSelect.disabled = false; // 사원 드롭다운 활성화
            })
            .catch(error => console.error('사원 목록을 불러오는 중 오류 발생:', error));
    }

    // 이벤트 리스너 등록
    deptSelect.addEventListener('change', (event) => {
        const selectedDeptId = event.target.value;
        fetchEmployeesByDept(selectedDeptId);
    });

    privateMessageForm.addEventListener('submit', sendPrivateMessage, true);

    // 페이지 로딩 시 부서 목록을 먼저 불러옵니다.
    fetchDepartments();
    connect();
});