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
  <image src="/img/betgptImage.png" alt="betgpt" />
  <h1>betGpt Beta</h1>
  <p>Hi, I'm betGpt Beta. I'm here to help you with your queries.</p>
  </div>
  `;
    chatContainer.innerHTML = localStorage.getItem('chat-history') || defaultChat;
    chatContainer.scrollTo(0, chatContainer.scrollHeight);
    if (!chatContainer.querySelector('.default-text')) {
        document.querySelector('nav').style.display = 'block';
    }else{
        document.querySelector('nav').style.display = 'none';
    }
};

loadDataFromLocalStorage();

/// TODO: Call API
const getChatResponse = async (incomingChatDiv) => {
    let pElement = null;
    let divElement = document.createElement('div');
    const requestData = {
        "message": userText
    }
    fetch("/chat", {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(requestData)
    })
        .then(response => response.json()) // Parse the response as JSON
        .then(data => {
            // Handle the response data
            incomingChatDiv.querySelector('.typing-animation').remove();
            pElement =  markdownToHtml(data.choices[0].message.content.trim())
            divElement.innerHTML = pElement;
            incomingChatDiv.querySelector('.chat-details').appendChild(divElement);
            hljs.highlightAll();
            localStorage.setItem('chat-history', chatContainer.innerHTML);
        })
        .catch(error => {
            // Handle any errors
            pElement.textContent = 'Sorry, I am not able to process your request at the moment. Please try again later.';
        });
    chatContainer.scrollTo(0, chatContainer.scrollHeight);
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
            <img src="/img/betgptImage.png" alt="user-img" />
            <div class="typing-animation">
              <div class="typing-dot" style="--delay: 0.2s"></div>
              <div class="typing-dot" style="--delay: 0.3s"></div>
              <div class="typing-dot" style="--delay: 0.4s"></div>
            </div>
          </div>
          <span onclick="copyResponse(this)" class="material-symbols-outlined copy-btn">content_copy</span>
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
            <img src="/img/user.jpg" alt="user-img" />
            <p></p>
          </div>
        </div>
    `;
    const outgoingChatDiv = createElement(html, 'outgoing');
    document.querySelector('.default-text')?.remove();
    outgoingChatDiv.querySelector('p').textContent = userText;
    if (!chatContainer.querySelector('.default-text')) {
        document.querySelector('nav').style.display = 'block';
    }else{
        document.querySelector('nav').style.display = 'none';
    }
    chatContainer.appendChild(outgoingChatDiv);
    chatContainer.scrollTo(0, chatContainer.scrollHeight);
    setTimeout(showTypingAnimation, 1000);
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

const markdownToHtml = (markdown) => {
    return marked.parse(markdown);
};
