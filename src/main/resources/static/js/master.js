const API_URL = 'http://localhost:8080';
const REFRESH_TOKEN_URL = API_URL + '/api/auth/refresh';

function goToLoginPage() {
    window.location.href = '/';
}

function refreshToken() {
    const refreshToken = localStorage.getItem('refreshToken');

    if (!refreshToken) {
        goToLoginPage();
        return Promise.resolve(null);
    }

    return fetch(REFRESH_TOKEN_URL, {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${refreshToken}`,
            'Content-Type': 'application/json'
        }
    })
        .then(response => response.json())
        .then(data => {
            if (data.statusCode === 200 && data.data) {
                localStorage.setItem('accessToken', data.data.accessToken);
                localStorage.setItem('refreshToken', data.data.refreshToken);
                return data.data.accessToken;
            } else {
                goToLoginPage();
                return null;
            }
        })
        .catch(error => {
            console.error('Error refreshing token:', error);
            goToLoginPage();
            return null;
        });
}

function fetchWithToken(url, options) {
    const token = localStorage.getItem('accessToken');
    if (!token) {
        goToLoginPage();
        return;
    }

    options.headers = {
        'Authorization': `Bearer ${token}`,
    };

    return fetch(url, options)
        .then(response => response.json().then(data => ({status: response.status, data})))
        .then(({status, data}) => {
            if (status === 401 && data.message.includes('JWT expired')) {
                return refreshToken().then(newToken => {
                    if (newToken) {
                        options.headers['Authorization'] = `Bearer ${newToken}`;
                        return fetch(url, options)
                            .then(response => response.json());
                    } else {
                        goToLoginPage();
                    }
                });
            }
            return data;
        })
        .catch(error => {
            console.error('Error:', error);
        });
}