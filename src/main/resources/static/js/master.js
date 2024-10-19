const API_BASE_URL = `${window.location.protocol}//${window.location.host}/api`;
const REFRESH_TOKEN_URL = API_BASE_URL + '/auth/refresh';
const API_URL_DELETE = API_BASE_URL + '/auth/delete';
const API_URL_SENTENCES = API_BASE_URL + '/sentence';
const API_URL_MODULE = API_BASE_URL + '/module';
const API_URL_SESSION = API_BASE_URL + '/session';

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
        .then(response => response.json()
            .then(data => ({ status: response.status, data })))
        .then(({ status, data }) => {
            if (status === 401 && data.message.includes('JWT expired')) {
                return refreshToken().then(newToken => {
                    if (newToken) {
                        options.headers['Authorization'] = `Bearer ${newToken}`;
                        return fetch(url, options).then(response => response.json());
                    } else {
                        goToLoginPage();
                        return null;
                    }
                });
            } else if (status === 401) {
                goToLoginPage();
                return null;
            }
            return data;
        })
        .catch(error => {
            console.error('Error during fetch:', error);
            return null;
        });
}