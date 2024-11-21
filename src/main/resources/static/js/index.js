function showError(message) {
    const errorMessageElement = document.getElementById('errorMessage');
    errorMessageElement.textContent = message;
}

function checkForm() {
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
        showError('Nieprawidłowy format e-mail.');
        return false;
    }

    if (password.length < 3) {
        showError('Hasło musi mieć co najmniej 3 znaki.');
        return false;
    }
    showError('');
    return true;
}

function saveTokensInLocalStorage(accessToken, refreshToken){
    localStorage.setItem('accessToken', accessToken);
    localStorage.setItem('refreshToken', refreshToken);
    window.location.href = '/dashboard.html';
}


function login() {
    if (!checkForm()){
        return;
    }

    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;

    fetch('/api/auth/signin', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({email, password})
    })
        .then(response => {
            if (response.ok) {
                showError("Logowanie zakończone sukcesem!")
                return response.json();
            } else {
                return response.json().then(data => {
                    throw new Error(data.message || 'Wystąpił błąd podczas logowania.');
                });
            }
        }).then(data => {
        if (data.statusCode !== 200) {
            showError(data.body.message);
        } else {
            const accessToken = data.data.accessToken;
            const refreshToken = data.data.refreshToken;

            saveTokensInLocalStorage(accessToken , refreshToken);
        }
    })
        .catch(error => showError('Wystąpił błąd podczas logowania.'));
}