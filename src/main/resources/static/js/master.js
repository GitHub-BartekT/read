const API_URL = 'http://localhost:8080/api';
const REFRESH_TOKEN_URL = API_URL + '/auth/refresh';

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

function fetchWithToken(url, options = {}) {
    const accessToken = localStorage.getItem('accessToken');

    if (!accessToken) {
        goToLoginPage();
        return Promise.reject('No access token available');
    }

    // Set the Authorization header with the access token
    options.headers = {
        ...options.headers, // Preserve any other headers
        'Authorization': `Bearer ${accessToken}`,
        'Content-Type': options.headers?.['Content-Type'] || 'application/json'
    };

    return fetch(url, options)
        .then(response => response.json().then(data => ({ status: response.status, data })))
        .then(({ status, data }) => {
            if (status === 401 && data.message.includes('JWT expired')) {
                // If the token is expired, attempt to refresh
                return refreshToken().then(newToken => {
                    if (newToken) {
                        // Retry the request with the new token
                        options.headers['Authorization'] = `Bearer ${newToken}`;
                        return fetch(url, options).then(response => response.json());
                    } else {
                        goToLoginPage();
                        return null;
                    }
                });
            } else if (status === 401) {
                // If the token is invalid, redirect to login
                goToLoginPage();
                return null;
            }
            return data; // Return the data for non-401 responses
        })
        .catch(error => {
            console.error('Error during fetch:', error);
            return null;
        });
}