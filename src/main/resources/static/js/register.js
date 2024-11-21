document.getElementById('registerBtn').addEventListener('click', function () {
    register();
});

document.getElementById('password').addEventListener('keydown', function (event) {
    if (event.key === 'Enter') {
        register();
    }
});

function register() {
    if (!checkForm()){
        return;
    }
    showError("Właśnie tworzymy Ci kont! Za chwilę zostaniesz automatycznie zalogowany!")
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;

    fetch('/api/auth/create/signup', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({email, password})
    })
        .then(response => response.json())
        .then(data => {
            if (data.statusCode !== 200) {
                showError(data.message);
            } else {
                login();
            }
        })
        .catch(error => showError('Wystąpił błąd podczas rejestracji.'));
}