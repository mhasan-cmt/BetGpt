import { deepCompare, randomString } from '../js/utils.js'

class SettingsView extends HTMLElement {
  #controller

  // TODO: Make these names CONSTANTS somewhere?
  #lsKeyName = 'openai-apikey'
  #lsOrgIDName = 'openai-organizationid'

  #iconEye = `<chatty-icon name="eye" class="h-6 w-6"></chatty-icon>`
  #iconEyeSlash = `<chatty-icon name="eye-slash" class="h-6 w-6"></chatty-icon>`

  constructor () {
    super()

    this.#controller = new AbortController()

    this.attachShadow({ mode: 'open' })
  }

  updateView () {
    this.shadowRoot.innerHTML = `
<link href="css/global.min.css" rel="stylesheet">

<div class="bg-white shadow sm:rounded-lg">
</div>
`
  }

  connectedCallback () {
    this.updateView()

    const saveButton = this.shadowRoot.querySelector('button#save')
    const importInput = this.shadowRoot.querySelector('input#import')
    const exportButton = this.shadowRoot.querySelector('button#export')

    const iconEye = this.shadowRoot.querySelector('#icon-eye')
    // Setup the input eye icon
    if (localStorage.getItem(this.#lsKeyName)) {
      iconEye.innerHTML = this.#iconEye
    } else {
      iconEye.innerHTML = this.#iconEyeSlash
    }
    iconEye.addEventListener('click', this.#toggleApiKeyVisibility.bind(this), { signal: this.#controller.signal })

    saveButton.addEventListener('click', this.#save.bind(this), { signal: this.#controller.signal })
    // importButton.addEventListener('click', this.#save.bind(this), { signal: this.#controller.signal })
    exportButton.addEventListener('click', this.#exportChatHistory.bind(this), { signal: this.#controller.signal })

    importInput.addEventListener('change', this.#importChatHistory.bind(this), { signal: this.#controller.signal })
  }

  disconnectedCallback () {
    this.#controller.abort()
  }

  #toggleApiKeyVisibility () {
    const apiKeyInput = this.shadowRoot.querySelector(`#${this.#lsKeyName}`)
    const iconEye = this.shadowRoot.querySelector('#icon-eye')

    if (apiKeyInput.getAttribute('type') === 'password') {
      apiKeyInput.setAttribute('type', 'text')
      iconEye.innerHTML = this.#iconEyeSlash
    } else {
      apiKeyInput.setAttribute('type', 'password')
      iconEye.innerHTML = this.#iconEye
    }
  }

  #save (evt) {
    evt.preventDefault()
    evt.stopPropagation()

    const apiKeyInput = this.shadowRoot.querySelector(`#${this.#lsKeyName}`)
    localStorage.setItem(this.#lsKeyName, apiKeyInput.value)

    const orgIDInput = this.shadowRoot.querySelector(`#${this.#lsOrgIDName}`)
    localStorage.setItem(this.#lsOrgIDName, orgIDInput.value)

    this.dispatchEvent(
      new CustomEvent('done', { composed: true, bubbles: true })
    )
  }

  #exportChatHistory (evt) {
    evt.preventDefault()
    evt.stopPropagation()

    if (!localStorage.length) {
      return
    }

    // Extract all chats form localStorage
    let chats = []
    for (let i = 0, len = localStorage.length; i < len; i++) {
      const key = localStorage.key(i)
      if (key.substring(0, 5) !== 'chat-') {
        continue
      }

      let chat = localStorage.getItem(key)
      try {
        chat = JSON.parse(chat)
      } catch (e) {
        continue
      }

      if (!chat.name) {
        chat.name = key.substring(5)
      }

      chat.key = key

      chats.push(chat)
    }

    // Sort according to creation time
    chats.sort((chatA, chatB) => { return chatB.createdAt - chatA.createdAt })

    // Convert JSON object to a JSON string
    const jsonString = JSON.stringify({ chats: chats }, null, 2)

    // Create a Blob object with the JSON string
    const jsonBlob = new Blob([jsonString], { type: 'application/json' })

    // Create an anchor element with a download attribute
    const downloadAnchor = document.createElement('a')
    downloadAnchor.download = 'chatty-gpt-chats.json'
    downloadAnchor.href = URL.createObjectURL(jsonBlob)
    downloadAnchor.style.display = 'none'

    // Add the anchor element to the DOM
    document.body.appendChild(downloadAnchor)

    // Trigger the download by simulating a click event
    downloadAnchor.click()

    // Remove the anchor element from the DOM
    document.body.removeChild(downloadAnchor)

    this.dispatchEvent(
      new CustomEvent('done', { composed: true, bubbles: true })
    )
  }

  #importChatHistory (evt) {
    evt.preventDefault()
    evt.stopPropagation()

    // Get the FileList object from the event
    const files = evt.target.files

    // Iterate over the files
    for (const file of files) {
      // Initialize a new FileReader instance
      const fileReader = new FileReader()

      // Add an event listener for when the file is loaded
      fileReader.addEventListener('load', (loadEvt) => {
        // Get the import as JSON from the event
        let importJSON = null
        try {
          importJSON = JSON.parse(loadEvt.target.result)
        } catch (e) {
          console.log(e)
          return
        }

        // Check that there's actually any data in the import
        if (!importJSON.chats || !importJSON.chats.length) {
          return
        }

        // Iterate over the chats
        for (let i = 0; i < importJSON.chats.length; i++) {
          try {
            let newChat = importJSON.chats[i]
            const key = newChat.key
            delete newChat.key

            // Check if the chat already exists
            const curChat = localStorage.getItem(key)
            if (!curChat) {
              localStorage.setItem(key, JSON.stringify(newChat))

            } else {
              // Parse the chat into a JSON object
              const cur = JSON.parse(curChat)

              // Check the chat messages.
              // If they're the same, but with more messages in the import, the local version is updated.
              // If not, the import is created as a new chat.

              let update = true
              for (let i = 0; i < cur.messages.length; i++) {
                if (!deepCompare(cur.messages[i], newChat.messages[i])) {
                  update = false
                  break
                }
              }

              if (update) {
                cur.messages = newChat.messages
                localStorage.setItem(key, JSON.stringify(cur))
              } else {
                const chatID = `chat-${randomString()}`
                localStorage.setItem(chatID, JSON.stringify(newChat))
              }
            }

          } catch (e) {
            console.log(e)
            continue
          }
        }

        this.dispatchEvent(
          new CustomEvent('done', { composed: true, bubbles: true })
        )
      }, { signal: this.#controller.signal })

      // Read the file as text
      fileReader.readAsText(file)
    }
  }
}

customElements.define('settings-view', SettingsView)
