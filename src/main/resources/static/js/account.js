function logout() {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');

    goToLoginPage();
}

document.getElementById('logout-button').addEventListener('click', logout);

document.getElementById('delete-account-btn').addEventListener('click', () => {
    const confirmDelete = confirm('Czy na pewno chcesz usunąć kont? Po potwierdzeniu zostaniesz wylogowany, a na twój email zostanie wysłana wiadomość z prośbą o potwierdzenie chęci usunięcia konta.');
    if (confirmDelete) {
        deleteAccount();
        logout();
    }
});

function deleteAccount() {
    const token = localStorage.getItem('accessToken');

    fetch(API_URL_DELETE, {
        method: 'DELETE',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        }
    })
        .then(response => response.json())
        .catch(error => {
            console.error('Error deleting module:', error.message);
        });
}