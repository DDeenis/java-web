<%@ page contentType="text/html;charset=UTF-8" %>
<h2>WebSocket</h2>
<form onsubmit="sendMsg(event)">
    <strong><%=request.getSession().getAttribute("user")%></strong>
    <input name="user-message" id="user-message" type="text" value="Hello">
    <button class="btn blue lighten-2" type="submit">Send</button>
</form>
<ul class="collection" id="chat-container"></ul>

<script defer>
    initWebSocket()

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
        addMessage("Chat activated")
    }

    function onWsClose(e) {
        console.log('onWsClose', e)
    }

    function onWsMessage(e) {
        //console.log('onWsMessage', e)
        addMessage(e.data)
    }

    function onWsError(e) {
        console.log('onWsError', e)
    }

    function sendMsg(e) {
        e.preventDefault()
        window.websoket.send(document.getElementById('user-message').value)
    }

    function addMessage(msg) {
        const li = document.createElement('li')
        li.classList.add("collection-item")
        li.appendChild(document.createTextNode(msg))
        document.getElementById("chat-container").appendChild(li)
    }
</script>