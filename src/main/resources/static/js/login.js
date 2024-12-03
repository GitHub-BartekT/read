document.getElementById('loginBtn').addEventListener('click', function () {
    login();
});

document.getElementById('password').addEventListener('keydown', function (event) {
    if (event.key === 'Enter') {
        login();
    }
});

document.getElementById('forgotten-password').addEventListener('click', function () {
    forgottenPassword();
});

function forgottenPassword() {
    if (!checkEmail()) {
        return;
    }
    const email = document.getElementById('email').value;

    showError("Jeżeli w naszej bazie znajduje się Twój email, to na niego prześlemy link do zmiany hasła!");

    const url = '/api/auth/password/' + email;

    fetch(url, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        }
    });
}