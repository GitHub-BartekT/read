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
                    newModuleBtn.textContent = module.moduleName;
                    newModuleBtn.id = 'module-' + module.id;

                    // Add event listener to select the module
                    newModuleBtn.addEventListener('click', () => {
                        selectModule(module.id, module.moduleName);
                    });

                    tempParent.appendChild(newModuleBtn);
                });
            }

            resetSelectedModuleButton();
        })
        .catch(error => {
            console.error('Error fetching modules:', error.message);
        });
}

function resetSelectedModuleButton() {
    const selectedModuleBtn = document.querySelector('.right-column .button');
    selectedModuleBtn.classList.remove('button');
    selectedModuleBtn.classList.add('yellow-button');
    selectedModuleBtn.textContent = 'Nie wybrano żadnego modułu';
    selectedModuleBtn.removeAttribute('data-module-id'); // Reset the module ID
}

function selectModule(moduleId, moduleName) {
    const selectedModuleBtn = document.querySelector('.right-column .yellow-button');
    selectedModuleBtn.classList.remove('yellow-button');
    selectedModuleBtn.classList.add('button');
    selectedModuleBtn.textContent = moduleName;

    // Store the selected module ID for future use (like deletion)
    selectedModuleBtn.setAttribute('data-module-id', moduleId);
}

document.getElementById('delete-module-button').addEventListener('click', () => {
    const selectedModuleBtn = document.querySelector('.right-column .button');

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
            getAllUserModules(); // Refresh the module list
        })
        .catch(error => {
            console.error('Error deleting module:', error.message);
        });
}

function showError(message) {
    const errorMessageElement = document.getElementById('errorMessage');
    errorMessageElement.textContent = message;
}
