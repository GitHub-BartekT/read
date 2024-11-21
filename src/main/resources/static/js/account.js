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