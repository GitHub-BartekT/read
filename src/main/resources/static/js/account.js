const API_URL_PASSWORD = API_BASE_URL + '/auth/users/password';

function logout() {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');

    goToLoginPage();
}

document.getElementById('logout-button').addEventListener('click', logout);

document.getElementById('acceptNewPassword').addEventListener('click', changePassword);

document.getElementById('newPassword2').addEventListener('keydown', function (event) {
    if (event.key === 'Enter') {
        changePassword();
    }
});

function changePassword() {
    if (!checkPasswords()) {
        return;
    }
    fetchNewPassword()
    setElementDisabled('newPassword1', true);
    setElementDisabled('newPassword2', true);
    setElementDisabled('acceptNewPassword', true);
    const button = document.getElementById("acceptNewPassword");
    button.classList.remove("green-button");
    button.classList.add("grey-button");
}

function fetchNewPassword() {
    const token = localStorage.getItem('accessToken');

    const newPassword = document.getElementById('newPassword1').value;

    fetch(`${API_URL_PASSWORD}`, {
        method: 'PATCH',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        },
        body: newPassword
    }) .then(response => {
        return response.json();
    })
        .then(apiResponse => {
            if (apiResponse.statusCode === 200) {
                showError("Hasło zostało zmienione!");
            } else {
                showError(apiResponse.message || "Wystąpił problem ze zmianą hasła.");
            }
        })
        .catch(error => {
            showError(error.message || 'Wystąpił błąd podczas zmiany hasła.');
        });
}

function checkPasswords() {
    const password1 = document.getElementById('newPassword1').value;
    const password2 = document.getElementById('newPassword2').value;

    if (!checkPasswordsLength(password1.length)) {
        return false;
    }

    if (password1 !== password2) {
        showError('Hasło muszą być jednakowe.');
        return false;
    }

    return true;
}

function checkPasswordsLength(length) {
    if (length < 3) {
        showError('Hasło musi mieć co najmniej 3 znaki.');
        return false;
    }
    return true;
}

function showError(message) {
    const errorMessageElement = document.getElementById('errorMessage');
    errorMessageElement.textContent = message;
}

document.getElementById('delete-account-btn').addEventListener('click', () => {
    const confirmDelete = confirm('Czy na pewno chcesz usunąć kont? Po potwierdzeniu zostaniesz wylogowany, a na twój email zostanie wysłana wiadomość z prośbą o potwierdzenie chęci usunięcia konta.');
    if (confirmDelete) {
        deleteAccount();
    }
});


document.getElementById('change-password').addEventListener('click', setNewPassword);

function setNewPassword() {
    setElementDisabled('newPassword1', false);
    setElementDisabled('newPassword2', false);
    setElementDisabled('acceptNewPassword', false);
    const button = document.getElementById("acceptNewPassword");
    button.classList.remove("grey-button");
    button.classList.add("green-button");
}


function setElementDisabled(id, param) {
    const input = document.getElementById(id);
    input.disabled = param;
}


function deleteAccount() {
    const token = localStorage.getItem('accessToken');
    fetch(API_URL_DELETE, {
        method: 'DELETE',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            if (response.ok) {
                console.log('Account deleted successfully');
                logout(); // Wywołanie logout dopiero po zakończeniu żądania
            } else {
                console.error('Error deleting account:', response.status);
            }
        })
        .catch(error => {
            console.error('Error deleting account:', error.message);
        });
}