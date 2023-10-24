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

    const spaTokenStatus = document.getElementById("spa-token-status")
    const spaTokenExp = document.getElementById("spa-exp-status")
    if(spaTokenStatus) {
        const jti = localStorage.getItem("jti");
        const exp = localStorage.getItem("exp");
        const expDate = new Date(`${exp} UTC`);
        const now = new Date()
        const isExpired = expDate.getTime() < now.getTime();
        spaTokenStatus.textContent = jti ? jti : 'Not set'
        spaTokenExp.textContent = isExpired ? 'Token expired' : `Token expires at ${exp.toString()}`
        if(jti) {
            fetch('tpl/spa-auth.html')
                .then(r => r.text())
                .then((html) => {
                    document.querySelector('auth-part').innerHTML = html
                })
        }
    }

    const logOutBtn = document.getElementById("spa-log-out")
    if(logOutBtn) {
        logOutBtn.addEventListener('click', logOutClick)
    }

    const getDataBtn = document.getElementById("spa-get-data")
    if(getDataBtn) {
        getDataBtn.addEventListener('click', getDataClick)
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
        .then(r => {
            if(!r.ok) {
                authMessage.textContent = "Authentication failed";
                return;
            }
            return r.json();
        })
        .then(token => {
            if(!token.jti || !token.exp) {
                authMessage.textContent = "Failed to generate token";
                return;
            }
            window.localStorage.setItem('jti', token.jti)
            window.localStorage.setItem('exp', token.exp)
            const context = window.location.pathname.split('/')[1]
            const targetPath = `/${context}/spa`
            if(window.location.pathname === targetPath) {
                window.location.reload()
            }
            else {
                window.location.replace(window.location.origin + targetPath)
            }
        })
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

function logOutClick() {
    localStorage.removeItem('jti')
    localStorage.removeItem('exp')
    window.location.reload()
}

function getDataClick() {

}