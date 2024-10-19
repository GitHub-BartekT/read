getAllSentences();

function getAllSentences(moduleId) {
    fetchWithToken(API_URL_SESSION, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    })
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
            console.error('Error fetching sentences:', error.message);
        });
}

function redirectToSessionPage(sessionId) {
    // Redirect to the session.html page with the sessionId in the URL
    window.location.href = `session.html?sessionId=${sessionId}`;
}