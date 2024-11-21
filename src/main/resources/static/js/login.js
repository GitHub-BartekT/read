document.getElementById('loginBtn').addEventListener('click', function () {
    login();
});

document.getElementById('password').addEventListener('keydown', function (event) {
    if (event.key === 'Enter') {
        login();
    }
});