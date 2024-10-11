function goToLoginPage() {
    fetch('/', {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            if (response.ok) {
                return response.text();
            } else {
                throw new Error('Brak dostępu do login page.');
            }
        })
        .then(data => {
            document.body.innerHTML = data;
        })
        .catch(error => showError(error.message));

    console.log('Tu powinien być template: dashboard');
}