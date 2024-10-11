const API_URL = 'http://localhost:8080/api/session';

getAllUserSessions();

function getAllUserSessions() {
    console.log('Wywołanie funkcji getAllUserSessions')
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

            if (data.data.length === 0) {
                // If the list is empty, display "Create New Module" button
                const createNewModuleBtn = document.createElement("button");
                createNewModuleBtn.className = "button yellow-button";
                createNewModuleBtn.textContent = "Stwórz nowy moduł";
                createNewModuleBtn.addEventListener("click", () => {
                    alert("Create new module functionality triggered!");
                });
                tempParent.appendChild(createNewModuleBtn);
            } else {
                // Loop through the sessions and create buttons
                data.data.forEach((userSession) => {
                    const newSessionBtn = document.createElement("button");
                    newSessionBtn.className = "button";
                    newSessionBtn.textContent = userSession.name;
                    newSessionBtn.id = 'session-' + userSession.id;

                    // You can also add event listeners to each button if needed
                    newSessionBtn.addEventListener('click', () => {
                        console.log(`Session ID: ${userSession.id} was clicked.`);
                        // Add further action like redirecting or loading session details
                    });
                    newSessionBtn.addEventListener('click', () => {
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

function createStartSessionButtons() {
    fetch(`${API_URL}`)
        .then((response) => response.json())
        .then((modulesPropArr) => {
            modulesPropArr.map((s) => {
                if (s.active) {
                    let tempParent = document.getElementById("new session buttons");
                    const newSessionBtn = document.createElement("button");
                    newSessionBtn.innerHTML = `<a href="session.html?module=${s.id}">
<button id="start${s.id}" class="pure-button button-newSession" >${s.id}. ${s.module_Name}</button></a><br>`;
                    tempParent.appendChild(newSessionBtn);
                }
            });
        });
}