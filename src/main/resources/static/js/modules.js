const API_URL = 'http://localhost:8080/api/module';

getAllUserModules();

function getAllUserModules() {
    console.log('Fetching all modules...');
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

            if (data.data.length !== 0) {
                // Loop through the modules and create buttons
                data.data.forEach((module) => {
                    const newModuleBtn = document.createElement("button");
                    newModuleBtn.className = "button";
                    newModuleBtn.classList.add("left-button", "blue-button")
                    newModuleBtn.textContent = module.moduleName;
                    newModuleBtn.id = 'module-' + module.id;

                    // Add event listener to select the module
                    newModuleBtn.addEventListener('click', () => {
                        fetchModuleDetails(module.id);
                    });

                    tempParent.appendChild(newModuleBtn);
                });
            }
        })
        .catch(error => {
            console.error('Error fetching modules:', error.message);
        });
}

// Fetch the selected module's details
function fetchModuleDetails(moduleId) {
    const token = localStorage.getItem('accessToken');

    fetch(`${API_URL}/${moduleId}`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        }
    })
        .then(response => response.json())
        .then(data => {
            const module = data.data;
            selectModule(module.id, module);
        })
        .catch(error => {
            console.error('Error fetching module details:', error.message);
        });
}

function selectModule(moduleId, module) {
    const selectedModuleBtn = document.getElementById('selected-module');
    selectedModuleBtn.classList.remove('yellow-button');
    selectedModuleBtn.classList.add('blue-button');
    selectedModuleBtn.textContent = `Moduł: ${module.moduleName}`;

    // Store the selected module ID for future use (like deletion)
    selectedModuleBtn.setAttribute('data-module-id', moduleId);

    document.getElementById('module-name').value = module.moduleName;
    document.getElementById('sessions-per-day').value = module.sessionsPerDay;
    document.getElementById('presentations-per-session').value = module.presentationsPerSession;
    document.getElementById('new-sentences-per-day').value = module.newSentencesPerDay;
    document.getElementById('actual-module-day').value = module.actualDay;
    document.getElementById('next-session').value = module.nextSession;
}

// Handle the delete module button
document.getElementById('delete-module-button').addEventListener('click', () => {
    const selectedModuleBtn = document.getElementById('selected-module');

    if (!selectedModuleBtn || !selectedModuleBtn.hasAttribute('data-module-id')) {
        alert('Nie wybrano modułu.');
        return;
    }

    const moduleId = selectedModuleBtn.getAttribute('data-module-id');

    const confirmDelete = confirm('Czy na pewno chcesz usunąć ten moduł?');
    if (confirmDelete) {
        deleteModule(moduleId);
    }
});

function deleteModule(moduleId) {
    const token = localStorage.getItem('accessToken');

    fetch(`${API_URL}?moduleId=${moduleId}`, {
        method: 'DELETE',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        }
    })
        .then(response => response.json())
        .then(data => {
            console.log('Module deleted:', data.message);
            getAllUserModules(); // Refresh the module list and reset the selected module
            resetSelectedModule();
        })
        .catch(error => {
            console.error('Error deleting module:', error.message);
        });
}

function resetSelectedModule() {
    const selectedModuleBtn = document.getElementById('selected-module');
    selectedModuleBtn.classList.remove('blue-button');
    selectedModuleBtn.classList.add('yellow-button');
    selectedModuleBtn.textContent = 'Nie wybrano żadnego modułu';
    selectedModuleBtn.removeAttribute('data-module-id');
}
document.getElementById("change-module").addEventListener("click", function() {
    const changeModuleButton = this;
    const acceptButton = document.getElementById("accept-changes");

    changeButtons(changeModuleButton, acceptButton);
});

document.getElementById("accept-changes").addEventListener("click", function() {
    const changeModuleButton = document.getElementById("change-module");
    const acceptButton = this;  // Here, this refers to "accept-changes"

    changeButtons(changeModuleButton, acceptButton);
});

function changeButtons(changeModuleButton, acceptButton) {
    if(document.getElementById("selected-module").classList.contains("yellow-button")){
    } else if (changeModuleButton.classList.contains("red-button") || acceptButton.classList.contains("green-button")) {
        // Przywracanie do początkowego stanu (żółty i szary przycisk)
        changeModuleButton.classList.remove("red-button");
        changeModuleButton.classList.add("yellow-button");
        changeModuleButton.textContent = "zmień";

        acceptButton.disabled = true;
        acceptButton.classList.remove("green-button");
        acceptButton.classList.add("grey-button");
    } else {
        // Zmiana przycisków (czerwony i zielony)
        changeModuleButton.classList.remove("yellow-button");
        changeModuleButton.classList.add("red-button");
        changeModuleButton.textContent = "odrzuć zmiany";

        acceptButton.disabled = false;
        acceptButton.classList.remove("grey-button");
        acceptButton.classList.add("green-button");
    }
}