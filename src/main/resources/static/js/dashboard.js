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
            tempParent.innerHTML = '';

            if (data.data.length === 0) {
                const createNewModuleBtn = document.createElement("button");
                createNewModuleBtn.className = "button yellow-button";
                createNewModuleBtn.textContent = "Stwórz nowy moduł";
                createNewModuleBtn.addEventListener("click", () => {
                    alert("Create new module functionality triggered!");
                });
                tempParent.appendChild(createNewModuleBtn);
            } else {

                data.data.forEach((userSession) => {
                    const newSessionBtn = document.createElement("button");
                    newSessionBtn.className = "button";
                    newSessionBtn.textContent = userSession.name;
                    newSessionBtn.id = 'session-' + userSession.id;

                    newSessionBtn.addEventListener('click', () => {
                        console.log(`Session ID: ${userSession.id} was clicked.`);

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
    window.location.href = `session.html?sessionId=${sessionId}`;
}