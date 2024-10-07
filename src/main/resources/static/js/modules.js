const API_URL = 'http://localhost:8080/api/session';

getAllUserSessions();

function getAllUserSessions() {
    console.log('Wywołanie funkcji getAllUserSessions');
    const token = localStorage.getItem('accessToken');

    if (!token) {
        goToLoginPage();
    }

    fetch(API_URL, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        }
    })
        .then(response => response.json())
        .then(data => {
            let tempParent = document.getElementById("new-session-buttons");
            tempParent.innerHTML = ''; // Clear any existing buttons

            // Add the "Create New Module" button first
            const createNewModuleBtn = document.createElement("button");
            createNewModuleBtn.className = "button yellow-button";
            createNewModuleBtn.textContent = "Stwórz nowy moduł";
            createNewModuleBtn.addEventListener("click", () => {
                alert("Create new module functionality triggered!");
            });
            tempParent.appendChild(createNewModuleBtn);

            // Check if there are sessions to load
            if (data.data.length === 0) {
                console.log("No sessions available.");
            } else {
                // Loop through the sessions and create buttons
                data.data.forEach((userSession) => {
                    const newSessionBtn = document.createElement("button");
                    newSessionBtn.className = "button";
                    newSessionBtn.textContent = userSession.name;
                    newSessionBtn.id = 'session-' + userSession.id;

                    // Add event listeners to each button if needed
                    newSessionBtn.addEventListener('click', () => {
                        console.log(`Session ID: ${userSession.id} was clicked.`);
                        redirectToSessionPage(userSession.id);
                    });

                    tempParent.appendChild(newSessionBtn);
                });
            }
        })
        .catch(error => {
            console.error('Error fetching sessions:', error.message);
        });
}

function redirectToSessionPage(sessionId) {
    // Redirect to the session.html page with the sessionId in the URL
    window.location.href = `session.html?sessionId=${sessionId}`;
}

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

function showError(message) {
    const errorMessageElement = document.getElementById('errorMessage');
    errorMessageElement.textContent = message;
}
