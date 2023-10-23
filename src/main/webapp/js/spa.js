document.addEventListener('DOMContentLoaded', () => {
    const instances = M.Modal.init(document.getElementById("signup-modal"), {
        opacity: 0.6,
        inDuration: 200,
        outDuration: 200,
        onOpenStart: onModalOpens
    });

    const signInBtn = document.getElementById("auth-sign-in-btn")
    if(signInBtn) {
        signInBtn.addEventListener('click', authSignInClick)
    }
    else {
        console.error("#auth-sign-in-btn not found")
    }
})

function onModalOpens() {
    const {authLogin, authPassword, authMessage} = getAuthElements();
    authLogin.value = ""
    authPassword.value = ""
    authMessage.innerText = ""
}

function authSignInClick(e) {
    const {authLogin, authPassword, authMessage} = getAuthElements();

    if(authLogin.value.length === 0) {
        authMessage.innerText = "Login is required"
    }
    else if(authPassword.value.length === 0) {
        authMessage.innerText = "Password is required"
    }

    const context = window.location.pathname.split('/')[1]
    fetch(`/${context}/auth`, {
        method: 'POST',
        body: JSON.stringify({login: authLogin.value, password: authPassword.value}),
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(console.log)
}

function getAuthElements() {
    const authLogin = document.getElementById('auth-login')
    const authPassword = document.getElementById('auth-password')
    const authMessage = document.getElementById('auth-message')

    if(!authLogin) {
        throw '#auth-login not found'
    }
    if(!authPassword) {
        throw '#auth-password not found'
    }
    if(!authPassword) {
        throw '#auth-message not found'
    }

    return {authLogin, authPassword, authMessage}
}