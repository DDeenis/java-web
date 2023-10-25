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
        const tokenBase64 = localStorage.getItem("token")
        const token = JSON.parse(atob(tokenBase64))
        const expDate = new Date(`${token.exp} UTC`);
        const now = new Date()
        const isExpired = expDate.getTime() < now.getTime();
        spaTokenStatus.textContent = token.jti ? token.jti : 'Not set'
        spaTokenExp.textContent = isExpired ? 'Token expired' : `Token expires at ${expDate.toDateString()} ${expDate.toLocaleTimeString()}`
        if(token.jti) {
            fetch(getAppContext() + '/tpl/spa-auth.html')
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

    const getData1Btn = document.getElementById("spa-get-data1")
    const getData2Btn = document.getElementById("spa-get-data2")
    const getDataNotFound = document.getElementById("spa-get-notfound")
    if(localStorage.getItem('token') === undefined) {
        getData1Btn?.remove()
        getData2Btn?.remove()
        getDataNotFound?.remove()
    }
    if(getData1Btn) {
        getData1Btn.addEventListener('click', getDataClick("protected-data1.html", 'data-1'))
    }
    if(getData2Btn) {
        getData2Btn.addEventListener('click', getDataClick("protected-data2.html", 'data-2'))
    }
    if(getDataNotFound) {
        getDataNotFound.addEventListener('click', getDataClick("data-something.html", 'data-notfound'))
    }
})

function getAppContext() {
    return '/' + window.location.pathname.split('/')[1]
}

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
            return r.text();
        })
        .then(base64 => {
            const encoded = atob(base64)
            const token = JSON.parse(encoded)
            if(!token.jti || !token.exp) {
                authMessage.textContent = "Failed to generate token";
                return;
            }
            window.localStorage.setItem('token', base64)
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
    localStorage.removeItem('token')
    window.location.reload()
}

function getDataClick(templateName, elemId) {
    return () => {
        const token = localStorage.getItem('token')
        if(!token) return;
        fetch(`${getAppContext()}/tpl/${templateName}`)
            .then(r => {
                if(r.status === 404) {
                    throw 'Template not found';
                }

                return r.text();
            })
            .then(html => {
                const elem = document.getElementById(elemId)
                if(elem) {
                    elem.innerHTML = html
                }
            })
            .catch(err => {
                console.error(err)
                const elem = document.getElementById(elemId)
                if(elem) {
                    elem.innerHTML = `<span style='color: red; font-weight: bold;'>Template '${templateName}' not found</span>`
                }
            })
    }
}