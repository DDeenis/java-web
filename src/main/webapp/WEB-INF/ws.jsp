<%@ page contentType="text/html;charset=UTF-8" %>
<h2>WebSocket</h2>
<form onsubmit="sendMsg(event)">
    <strong id="chat-user">Connecting...</strong>
    <input name="user-message" id="user-message" type="text" value="Hello">
    <button class="btn blue lighten-2" type="submit">Send</button>
</form>
<ul class="collection" id="chat-container"></ul>

<script defer>
    document.addEventListener('DOMContentLoaded', () => {
        const token = localStorage.getItem("token")
        if(token) {
            initWebSocket()
        }
        else {
            document.getElementById('chat-user').innerText = "Log in to use chat"
        }
    })

    function getAppContext() {
        return '/' + window.location.pathname.split('/')[1]
    }

    function initWebSocket() {
        const host = window.location.host + getAppContext()
        window.websoket = new WebSocket(`ws://${host}/chat`)

        window.websoket.onopen = onWsOpen;
        window.websoket.onclose = onWsClose;
        window.websoket.onmessage = onWsMessage;
        window.websoket.onerror = onWsError;
    }

    function onWsOpen(e) {
        //console.log('onWsOpen', e)
        const token = localStorage.getItem("token")
        window.websoket.send(JSON.stringify({
            command: 'auth',
            data: token
        }))
        addMessage("Chat activated")
    }

    function onWsClose(e) {
        console.log('onWsClose', e)
        addMessage('Chat deactivated')
    }

    function onWsMessage(e) {
        const msg = JSON.parse(e.data)
        switch (msg.status) {
            case 201: {
                const data = JSON.parse(msg.data)
                console.log(data)
                addMessage(`${data.user}: ${data.message}`)
            }
                break;

            case 202: {
                document.getElementById('chat-user').innerText = msg.data
            }
                break;

            case 401: {
                document.getElementById('chat-user').innerText = "Auth required"
            }
                break;

            case 403: {
                document.getElementById('chat-user').innerText = "Log in one more time"
            }

            default:
                break;
        }
        if(msg.status === 200) {

        }
        else {
            console.log(msg)
        }
    }

    function onWsError(e) {
        console.log('onWsError', e)
        addMessage('Chat deactivated')
    }

    function sendMsg(e) {
        e.preventDefault()
        window.websoket.send(JSON.stringify({
            command: 'chat',
            data: document.getElementById('user-message').value
        }))
        document.getElementById('user-message').value = ''
    }

    function addMessage(msg) {
        const li = document.createElement('li')
        li.classList.add("collection-item")
        li.appendChild(document.createTextNode(msg))
        document.getElementById("chat-container").appendChild(li)
    }
</script>