function logout() {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');

    goToLoginPage();
}

document.getElementById('logout-button').addEventListener('click', logout);
