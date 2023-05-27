const chatInput = document.querySelector('#chat-input');
const sendButton = document.querySelector('#send-btn');
const chatContainer = document.querySelector('.chat-container');
const themeButton = document.querySelector('#theme-btn');
const deleteButton = document.querySelector('#delete-btn');

let userText = null;
const initialHeight = chatInput.scrollHeight;

const createElement = (html, className) => {
    const chatDiv = document.createElement('div');
    chatDiv.classList.add('chat', className);
    chatDiv.innerHTML = html;
    return chatDiv;
};

// LocalStorage
const loadDataFromLocalStorage = () => {
    const theme = localStorage.getItem('theme-color');
    document.body.classList.toggle('light-mode', theme === 'light_mode');
    themeButton.innerText = document.body.classList.contains('light-mode')
        ? 'dark_mode'
        : 'light_mode';
    const defaultChat = `
  <div class="default-text">
  <h1>betGpt Beta</h1>
  <p>Hi, I'm betGpt Beta. I'm here to help you with your queries.</p>
  </div>
  `;
    chatContainer.innerHTML = localStorage.getItem('chat-history') || defaultChat;
    chatContainer.scrollTo(0, chatContainer.scrollHeight);
};

loadDataFromLocalStorage();

/// TODO: Call API
const getChatResponse = async (incomingChatDiv) => {
    const pElement = document.createElement('p');
    incomingChatDiv.querySelector('.typing-animation').remove();
    pElement.textContent = 'Hello';
    // pElement.textContent = await getResponse(userText);
    incomingChatDiv.querySelector('.chat-details').appendChild(pElement);
    chatContainer.scrollTo(0, chatContainer.scrollHeight);
    localStorage.setItem('chat-history', chatContainer.innerHTML);
};

const copyResponse = (copyButton) => {
    const textToCopy = copyButton.parentElement.querySelector('p');
    navigator.clipboard.writeText(textToCopy.textContent);
    copyButton.textContent = 'done';
    setTimeout(() => (copyButton.textContent = 'content_copy'), 1000);
};

const showTypingAnimation = () => {
    const html = `
    <div class="chat-content">
          <div class="chat-details">
            <img src="./image/chatbot.jpg" alt="user-img" />
            <div class="typing-animation">
              <div class="typing-dot" style="--delay: 0.2s"></div>
              <div class="typing-dot" style="--delay: 0.3s"></div>
              <div class="typing-dot" style="--delay: 0.4s"></div>
            </div>
          </div>
          <span onclick="copyResponse(this)" class="material-symbols-outlined">content_copy</span>
        </div>
    `;
    const incomingChatDiv = createElement(html, 'incoming');
    chatContainer.appendChild(incomingChatDiv);
    chatContainer.scrollTo(0, chatContainer.scrollHeight);
    getChatResponse(incomingChatDiv);
};

const handleOutgoingChat = () => {
    userText = chatInput.value.trim();
    if (!userText) return;
    chatInput.value = '';
    chatInput.style.height = `${initialHeight}px`;
    const html = `
    <div class="chat-content">
          <div class="chat-details">
            <img src="./image/user.jpg" alt="user-img" />
            <p></p>
          </div>
        </div>
    `;
    const outgoingChatDiv = createElement(html, 'outgoing');
    document.querySelector('.default-text')?.remove();
    outgoingChatDiv.querySelector('p').textContent = userText;
    chatContainer.appendChild(outgoingChatDiv);
    chatContainer.scrollTo(0, chatContainer.scrollHeight);
    setTimeout(showTypingAnimation, 500);
};

themeButton.addEventListener('click', () => {
    document.body.classList.toggle('light-mode');
    localStorage.setItem('theme-color', themeButton.innerText);
    themeButton.innerText = document.body.classList.contains('light-mode')
        ? 'dark_mode'
        : 'light_mode';
});

chatInput.addEventListener('input', () => {
    chatInput.style.height = `${initialHeight}px`;
    chatInput.style.height = `${chatInput.scrollHeight}px`;
});

chatInput.addEventListener('keydown', (evt) => {
    if (evt.key === 'Enter' && !evt.shiftKey && window.innerWidth > 800) {
        evt.preventDefault();
        handleOutgoingChat();
    }
});

sendButton.addEventListener('click', handleOutgoingChat);

deleteButton.addEventListener('click', () => {
    if (confirm('Are you sure to delete all chats?')) {
        localStorage.removeItem('chat-history');
        loadDataFromLocalStorage();
    }
});
