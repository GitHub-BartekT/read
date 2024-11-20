document.getElementById('loginBtn').addEventListener('click', function () {
    login();
});

document.getElementById('password').addEventListener('keydown', function (event) {
    if (event.key === 'Enter') {
        login();
    }
});

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

            localStorage.setItem('accessToken', accessToken);
            localStorage.setItem('refreshToken', refreshToken);
            window.location.href = '/dashboard.html';
        }
    })
        .catch(error => showError('Wystąpił błąd podczas logowania.'));
}