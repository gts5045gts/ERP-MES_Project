let stompClient = null;
       let userId = null;
       let userName = null;
       let messageForm = document.querySelector('#messageForm');
       let messageInput = document.querySelector('#messageInput');
       let messageArea = document.querySelector('#messageArea');

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

       function onMessageReceived(payload) {
           let message = JSON.parse(payload.body);
           let messageElement = document.createElement('div');
           messageElement.classList.add('p-2', 'm-1', 'rounded');

           if (message.type === 'JOIN') {
               messageElement.classList.add('bg-success', 'text-white');
               messageElement.textContent = message.senderName + ' 님이 입장하셨습니다.';
           } else {
               if (message.senderId === userId) {
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

       connect();