// chat.js
document.addEventListener('DOMContentLoaded', function() {
    let stompClient = null;
    let userId = null;
    let userName = null;
    let messageForm = document.querySelector('#messageForm');
    let messageInput = document.querySelector('#messageInput');
    let messageArea = document.querySelector('#messageArea');
    const leaveChatBtn = document.querySelector('#leaveChatBtn');
    
	const empIdInput = document.getElementById('currentEmpId');
	const empNameInput = document.getElementById('currentEmpName');

	if (empIdInput && empNameInput) {
	    userId = empIdInput.value;
	    userName = empNameInput.value;
	} else {
	    console.error("사용자 정보를 찾을 수 없습니다. 로그인이 필요합니다.");
	    return;
	}

    function onConnected() {
        console.log("웹소켓 연결 성공. 사용자 ID: " + userId);
        // 공용 채팅방 메시지 구독
        stompClient.subscribe('/topic/publicChat', onMessageReceived);
        // 채팅방 입장 메시지 전송
        stompClient.send("/app/chat.addUser", {}, JSON.stringify({
            senderId: userId,
            senderName: userName,
            type: 'JOIN'
        }));
    }

    function onError(error) {
        console.error("웹소켓 연결 실패: " + error);
    }

    function sendMessage(event) {
        let messageContent = messageInput.value.trim();
        if (messageContent && stompClient && stompClient.connected) {
            let chatMessage = {
                senderId: userId,
                senderName: userName,
                content: messageContent,
                type: 'CHAT'
            };
            stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
            messageInput.value = '';
        }
        event.preventDefault();
    }

    function leaveChat() {
        if (stompClient && stompClient.connected) {
            const leaveMessage = {
                senderId: userId,
                senderName: userName,
                type: 'LEAVE'
            };
            stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(leaveMessage));
            stompClient.disconnect();
            console.log("웹소켓 연결 종료");
            window.location.href = '/main';
        }
        leaveChatBtn.disabled = true;
        messageInput.disabled = true;
        messageForm.removeEventListener('submit', sendMessage, true);
        leaveChatBtn.removeEventListener('click', leaveChat, true);
    }

    function onMessageReceived(payload) {
        let message = JSON.parse(payload.body);
        let messageElement = document.createElement('div');
        messageElement.classList.add('p-2', 'm-1', 'rounded');

        if (message.type === 'JOIN') {
            messageElement.classList.add('bg-success', 'text-white');
            messageElement.textContent = message.senderName + ' 님이 입장하셨습니다.';
        } else if (message.type === 'LEAVE') {
            messageElement.classList.add('bg-danger', 'text-white');
            messageElement.textContent = message.senderName + ' 님이 퇴장하셨습니다.';
        } else {
            if (String(message.senderId) === String(userId)) {
                messageElement.classList.add('bg-primary', 'text-white', 'text-right');
                messageElement.textContent = message.content;
            } else {
                messageElement.classList.add('bg-secondary', 'text-white');
                messageElement.textContent = message.senderName + ': ' + message.content;
            }
        }
        messageArea.appendChild(messageElement);
        messageArea.scrollTop = messageArea.scrollHeight;
    }

    messageForm.addEventListener('submit', sendMessage, true);
    leaveChatBtn.addEventListener('click', leaveChat, true);

    // 웹소켓 연결 시작
    const socket = new SockJS('/ws/chat');
    stompClient = Stomp.over(socket);
    
    // CSRF 토큰을 헤더에 포함
    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
    const headers = {
        [csrfHeader]: csrfToken
    };
    
    stompClient.connect(headers, onConnected, onError);
});