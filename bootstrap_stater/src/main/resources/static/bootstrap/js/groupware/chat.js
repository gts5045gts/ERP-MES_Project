let stompClient = null;
let userId = null;
let userName = null;
let messageForm = document.querySelector('#messageForm');
let messageInput = document.querySelector('#messageInput');
let messageArea = document.querySelector('#messageArea');
const leaveChatBtn = document.querySelector('#leaveChatBtn');

function connect() {
	// HTML body의 data 속성에서 사용자 정보 가져오기
	userId = document.body.dataset.currentEmpId;
	userName = document.body.dataset.currentEmpName;
	let socket = new SockJS('/ws/chat');
	stompClient = Stomp.over(socket);

	stompClient.connect({}, onConnected, onError);
}

function onConnected() {
	stompClient.subscribe('/topic/publicChat', onMessageReceived);
	stompClient.send("/app/chat.addUser", {}, JSON.stringify({
		senderId: userId,
		senderName: userName,
		type: 'JOIN'
	}));
	console.log("웹소켓 연결 성공. 사용자 ID: " + userId);
}

function onError(error) {
	console.error("웹소켓 연결 실패: " + error);
}

function sendMessage(event) {
	let messageContent = messageInput.value.trim();
	if (messageContent && stompClient) {
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

// 퇴장 메시지를 보내고 웹소켓 연결을 끊는 함수
function leaveChat() {
	if (stompClient) {
		const leaveMessage = {
			senderId: userId,
			senderName: userName,
			type: 'LEAVE' // 메시지 타입을 'LEAVE'로 설정
		};
		// 서버에 퇴장 메시지 전송
		stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(leaveMessage));
		// 웹소켓 연결 종료
		stompClient.disconnect();
		console.log("웹소켓 연결 종료");
		
		window.location.href = '/main';
	}
	// 나가기 버튼을 숨기고 메시지 입력창을 비활성화
	leaveChatBtn.disabled = true;
	messageInput.disabled = true;
	messageForm.removeEventListener('submit', sendMessage, true);
	leaveChatBtn.removeEventListener('click', leaveChat, true);
}

function onMessageReceived(payload) {
	let message = JSON.parse(payload.body);
	let messageElement = document.createElement('div');
	messageElement.classList.add('p-2', 'm-1', 'rounded');

	// 메시지 타입에 따라 다른 메시지 표시
	if (message.type === 'JOIN') {
		messageElement.classList.add('bg-success', 'text-white');
		messageElement.textContent = message.senderName + ' 님이 입장하셨습니다.';
	} else if (message.type === 'LEAVE') { // 'LEAVE' 타입일 때 퇴장 메시지 표시
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

connect();