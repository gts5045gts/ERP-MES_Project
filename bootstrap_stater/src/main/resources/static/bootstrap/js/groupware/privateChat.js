let stompClient = null;
      let userId = null;
      let userName = null;
      let privateMessageForm = document.querySelector('#privateMessageForm');
      let privateMessageInput = document.querySelector('#privateMessageInput');
      let receiverIdInput = document.querySelector('#receiverIdInput');
      let messageArea = document.querySelector('#messageArea');

      function connect() {
          userId = document.body.dataset.currentEmpId;
          userName = document.body.dataset.currentEmpName;
          let socket = new SockJS('/ws/chat');
          stompClient = Stomp.over(socket);

          stompClient.connect({}, onConnected, onError);
      }

      function onConnected() {
          // ⭐ 개인 메시지를 받을 구독 채널
          stompClient.subscribe('/user/queue/private', onMessageReceived);
          
          console.log("웹소켓 연결 성공. 사용자 ID: " + userId);
      }

      function onError(error) {
          console.error("웹소켓 연결 실패: " + error);
      }

      function sendPrivateMessage(event) {
          let messageContent = privateMessageInput.value.trim();
          let receiverId = receiverIdInput.value.trim();

          if (messageContent && receiverId && stompClient) {
              let chatMessage = {
                  senderId: userId,
                  senderName: userName,
                  receiverId: receiverId, // ⭐ 수신자 ID 포함
                  content: messageContent,
                  type: 'CHAT'
              };
              // ⭐ 개인 메시지 전송 URL
              stompClient.send("/app/chat.privateMessage", {}, JSON.stringify(chatMessage));
              privateMessageInput.value = '';
          }
          event.preventDefault();
      }
      
      function onMessageReceived(payload) {
          let message = JSON.parse(payload.body);
          let messageElement = document.createElement('div');
          messageElement.textContent = message.senderName + ' -> ' + message.receiverId + ': ' + message.content;
          messageArea.appendChild(messageElement);
          messageArea.scrollTop = messageArea.scrollHeight;
      }

      privateMessageForm.addEventListener('submit', sendPrivateMessage, true);

      connect();