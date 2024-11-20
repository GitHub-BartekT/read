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