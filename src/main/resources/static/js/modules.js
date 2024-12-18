getAllUserModules();

function setModulesButton(module, newModuleBtn) {
    const allButtons = document.querySelectorAll("#new-session-buttons .button");

    allButtons.forEach(button => {
        button.classList.remove("green-button");
        button.classList.add("blue-button");
    });

    fetchModuleDetails(module.id);
    newModuleBtn.classList.remove("blue-button");
    newModuleBtn.classList.add("green-button");
}

function getAllUserModules() {
    console.log('Fetching all modules...');
    const token = localStorage.getItem('accessToken');

    if (!token) {
        goToLoginPage();
    }

    fetch(API_URL_MODULE, {
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
                    newModuleBtn.classList.add("blue-button")
                    newModuleBtn.textContent = module.moduleName;
                    newModuleBtn.id = 'module-' + module.id;

                    // Add event listener to select the module
                    newModuleBtn.addEventListener('click', () => {
                        setModulesButton(module, newModuleBtn);
                    });

                    tempParent.appendChild(newModuleBtn);
                });
            }
        })
        .catch(error => {
            console.error('Error fetching modules:', error.message);
        });
}

function createNewModule() {
    console.log('Fetching all modules...');
    const token = localStorage.getItem('accessToken');

    if (!token) {
        goToLoginPage();
    }

    fetch(API_URL_MODULE, {
        method: 'POST',
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
                    newModuleBtn.classList.add("blue-button")
                    newModuleBtn.textContent = module.moduleName;
                    newModuleBtn.id = 'module-' + module.id;

                    // Add event listener to select the module
                    newModuleBtn.addEventListener('click', () => {
                        console.log('Clicked button:', newModuleBtn.id);
                        console.log('Before:', newModuleBtn.className);
                        clearLabels();
                        setModulesButton(module, newModuleBtn);
                        console.log('After:', newModuleBtn.className);
                    });

                    tempParent.appendChild(newModuleBtn);

                });

                resetSelectedModule();
                clearLabels();
                const showButton = document.getElementById('add-sentence-btn');
                showButton.disabled = true;
                showButton.classList.remove("green-button");
                showButton.classList.add("grey-button");

                const removeSentenceButton = document.getElementById('remove-sentence-btn');
                removeSentenceButton.disabled = true;
                removeSentenceButton.classList.remove("red-button");
                removeSentenceButton.classList.add("grey-button");

                clearObject('sentence-list');
                clearObject('sentence-select');
            }
        })
        .catch(error => {
            console.error('Error fetching modules:', error.message);
        });
}

// Fetch the selected module's details
function fetchModuleDetails(moduleId) {
    const token = localStorage.getItem('accessToken');

    fetch(`${API_URL_MODULE}/${moduleId}`, {
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

function fetchPutModuleDetails(moduleId) {
    const token = localStorage.getItem('accessToken');
    const paramUpdateProperties = {
        id: moduleId,
        moduleName: document.getElementById(`module-name`).value,
        sessionsPerDay: document.getElementById(`sessions-per-day`).value,
        presentationsPerSession: document.getElementById(`presentations-per-session`).value,
        newSentencesPerDay: document.getElementById(`new-sentences-per-day`).value,
        actualDay: document.getElementById(`actual-module-day`).value,
        nextSession: document.getElementById('next-session').value
    };

    fetch(`${API_URL_MODULE}`, {
        method: 'PUT',
        headers: {
            'Accept': 'application/json',
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(paramUpdateProperties)
    })
        .then(response => response.json())
        .then(data => {
            console.log('Response data:', data);
            if (data && data.data) {
                const module = data.data;
                selectModule(module.id, module);
            } else {
                console.error('No data found in the response:', data);
            }
        })
        .catch(error => {
            console.error('Error fetching module details:', error.message);
        });
}

function selectModule(moduleId, module) {
    const showSentencesBtn = document.getElementById('show-sentences')
    showSentencesBtn.disabled = false;
    showSentencesBtn.classList.remove('grey-button');
    showSentencesBtn.classList.add('yellow-button');
    setInputDisabled("add-sentence", false);
    setInputDisabled("sentence-select", false);

    const selectedModuleBtn = document.getElementById('selected-module');
    selectedModuleBtn.classList.remove('yellow-button');
    selectedModuleBtn.classList.add('blue-button');
    selectedModuleBtn.textContent = `Moduł: ${module.moduleName}`;

    // Store the selected module ID for future use (like deletion)
    selectedModuleBtn.setAttribute('data-module-id', moduleId);

    fillInElementByText('module-name', module.moduleName);
    fillInElementByText('sessions-per-day', module.sessionsPerDay);
    fillInElementByText('presentations-per-session', module.presentationsPerSession);
    fillInElementByText('new-sentences-per-day', module.newSentencesPerDay);
    fillInElementByText('actual-module-day', module.actualDay);
    fillInElementByText('next-session', module.nextSession);

    const showButton = document.getElementById('add-sentence-btn');
    showButton.disabled = true;
    showButton.classList.remove("green-button");
    showButton.classList.add("grey-button");

    const removeSentenceButton = document.getElementById('remove-sentence-btn');
    removeSentenceButton.disabled = true;
    removeSentenceButton.classList.remove("red-button");
    removeSentenceButton.classList.add("grey-button");

    fillInElementByText('add-sentence', "");
    setInputDisabled("add-sentence", true);
    setInputDisabled("sentence-select", true);
    clearObject('sentence-list');
    clearObject('sentence-select');
}

function fillInElementByText(id, text) {
    document.getElementById(id).value = text;
}

function setAllModuleInputDisabled(param) {
    setInputDisabled('module-name', param);
    setInputDisabled('sessions-per-day', param);
    setInputDisabled('presentations-per-session', param);
    setInputDisabled('new-sentences-per-day', param);
    setInputDisabled('actual-module-day', param);
    setInputDisabled('next-session', param);
}

function setInputDisabled(id, param) {
    const input = document.getElementById(id);
    input.disabled = param;
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

    fetch(`${API_URL_MODULE}?moduleId=${moduleId}`, {
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
            clearLabels();
            const showButton = document.getElementById('add-sentence-btn');
            showButton.disabled = true;
            showButton.classList.remove("green-button");
            showButton.classList.add("grey-button");

            const removeSentenceButton = document.getElementById('remove-sentence-btn');
            removeSentenceButton.disabled = true;
            removeSentenceButton.classList.remove("red-button");
            removeSentenceButton.classList.add("grey-button");

            clearObject('sentence-list');
            clearObject('sentence-select');

        })
        .catch(error => {
            console.error('Error deleting module:', error.message);
        });
}

function resetSelectedModule() {
    const showSentencesBtn = document.getElementById('show-sentences')
    showSentencesBtn.disabled = true;
    showSentencesBtn.classList.remove('yellow-button');
    showSentencesBtn.classList.add('grey-button');
    setInputDisabled("add-sentence", true);
    setInputDisabled("sentence-select", true);

    const selectedModuleBtn = document.getElementById('selected-module');
    selectedModuleBtn.classList.remove('blue-button');
    selectedModuleBtn.classList.add('yellow-button');
    selectedModuleBtn.textContent = 'Nie wybrano żadnego modułu';
    selectedModuleBtn.removeAttribute('data-module-id');
}

function clearLabels() {
    fillInElementByText('module-name', "");
    fillInElementByText('sessions-per-day', "");
    fillInElementByText('presentations-per-session', "");
    fillInElementByText('new-sentences-per-day', "");
    fillInElementByText('actual-module-day', "");
    fillInElementByText('next-session', "");
    fillInElementByText('add-sentence', "");
}

document.getElementById("new-module-button").addEventListener("click", function () {
    createNewModule();
    resetSelectedModule();
    clearLabels();
});

document.getElementById("change-module").addEventListener("click", function () {
    const changeModuleButton = this;
    const acceptButton = document.getElementById("accept-changes");
    changeButtons(changeModuleButton, acceptButton);
});

document.getElementById("accept-changes").addEventListener("click", function () {
    const changeModuleButton = document.getElementById("change-module");
    const acceptButton = this;

    fetchPutModuleDetails(getModuleId());
    let id = 'module-' + getModuleId();
    document.getElementById(id).textContent = document.getElementById(`module-name`).value;
    setAllModuleInputDisabled(true);
    changeButtons(changeModuleButton, acceptButton);
});

function changeButtons(changeModuleButton, acceptButton) {
    if (document.getElementById("selected-module").classList.contains("yellow-button")) {
    } else if (changeModuleButton.classList.contains("red-button") || acceptButton.classList.contains("green-button")) {

        changeModuleButton.classList.remove("red-button");
        changeModuleButton.classList.add("yellow-button");
        changeModuleButton.textContent = "zmień";

        fetchModuleDetails(getModuleId());
        setAllModuleInputDisabled(true);

        acceptButton.disabled = true;
        acceptButton.classList.remove("green-button");
        acceptButton.classList.add("grey-button");
    } else {
        changeModuleButton.classList.remove("yellow-button");
        changeModuleButton.classList.add("red-button");
        changeModuleButton.textContent = "odrzuć zmiany";
        setAllModuleInputDisabled(false);

        acceptButton.disabled = false;
        acceptButton.classList.remove("grey-button");
        acceptButton.classList.add("green-button");
    }
}

function getModuleId() {
    const selectedModuleBtn = document.getElementById('selected-module');
    return moduleId = selectedModuleBtn.getAttribute('data-module-id');
}

document.getElementById("show-sentences").addEventListener("click", function () {
    getAllSentences(getModuleId());

    const showButton = document.getElementById('add-sentence-btn');
    showButton.disabled = false;
    showButton.classList.remove("grey-button");
    showButton.classList.add("green-button");
    setInputDisabled("add-sentence", false);
    setInputDisabled("sentence-select", false);


    const removeSentenceButton = document.getElementById('remove-sentence-btn');
    removeSentenceButton.disabled = false;
    removeSentenceButton.classList.remove("grey-button");
    removeSentenceButton.classList.add("red-button");
});

function getAllSentences(moduleId) {
    const token = localStorage.getItem('accessToken');

    if (!token) {
        goToLoginPage();
    }

    fetch(`${API_URL_SENTENCES}/${moduleId}`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        }
    })
        .then(response => response.json())
        .then(data => {
            const sentenceListElement = document.getElementById('sentence-list');
            const removeSentenceSelect = document.getElementById('sentence-select');
            clearObject('sentence-select');

            if (data && data.data && data.data.length > 0) {
                sentenceListElement.innerHTML = data.data.map(sentence =>
                    `<li>${sentence}</li>`
                ).join('\n');  // Insert sentences as list items into the <ol>

                // Populate the <select> with options
                data.data.forEach((sentence, index) => {
                    const option = document.createElement('option');
                    option.value = index;
                    option.textContent = `${index + 1}. ${sentence}`;
                    removeSentenceSelect.appendChild(option);
                });

                // Enable the select and button if sentences exist
                removeSentenceSelect.disabled = false;
                document.getElementById('remove-sentence-btn').disabled = false;
            } else {
                sentenceListElement.innerHTML = `<li>Brak słów do wyświetlenia</li>`;
                removeSentenceSelect.disabled = true;
                document.getElementById('sentence-select-btn').disabled = true;
            }
        })
        .catch(error => {
            console.error('Error fetching modules:', error.message);
        });
}

function fetchPostModuleDetails(moduleId) {
    const token = localStorage.getItem('accessToken');
    const sentence = document.getElementById(`add-sentence`).value;
    const sentenceListElement = document.getElementById('sentence-list');

    if (sentenceListElement.children.length > 0 && sentenceListElement.children[0].textContent === "Brak słów do wyświetlenia") {
        sentenceListElement.innerHTML = '';
    }

    fetch(`${API_URL_SENTENCES}?moduleId=${moduleId}&sentence=${sentence}`, {
        method: 'Post',
        headers: {
            'Accept': 'application/json',
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        }
    })
        .then(response => response.json())
        .then(data => {
            const sentenceListElement = document.getElementById('sentence-list');
            const removeSentenceSelect = document.getElementById('sentence-select');
            clearObject('sentence-select');

            if (data && data.data && data.data.length > 0) {
                sentenceListElement.innerHTML = data.data.map(sentence =>
                    `<li>${sentence}</li>`
                ).join('\n');  // Insert sentences as list items into the <ol>

                // Populate the <select> with options
                data.data.forEach((sentence, index) => {
                    const option = document.createElement('option');
                    option.value = index;
                    option.textContent = `${index + 1}. ${sentence}`;
                    removeSentenceSelect.appendChild(option);
                });

                // Enable the select and button if sentences exist
                removeSentenceSelect.disabled = false;
                document.getElementById('remove-sentence-btn').disabled = false;
            } else {
                sentenceListElement.innerHTML = `<li>Brak słów do wyświetlenia</li>`;
                removeSentenceSelect.disabled = true;
                document.getElementById('sentence-select-btn').disabled = true;
            }
        })
        .catch(error => {
            console.error('Error fetching module details:', error.message);
        });
}

function clearObject(objectIdd) {
    const documentObject = document.getElementById(objectIdd);
    documentObject.innerHTML = '';  // This clears all the contents of the <ol>
}

document.getElementById("add-sentence-btn").addEventListener("click", function () {
    fetchPostModuleDetails(getModuleId());
    fillInElementByText('add-sentence', "");
});

document.getElementById('remove-sentence-btn').addEventListener('click', function() {
    fetchDeleteSentence('sentence-select');
});

function fetchDeleteSentence(sentence) {
    const selectedSentenceIndex = document.getElementById(sentence).value;
    const adjustedIndex = Number(selectedSentenceIndex) + 1;
    console.log("selected object: " + adjustedIndex);
    const moduleId = getModuleId();

    if (!selectedSentenceIndex) {
        alert('Nie wybrano żadnego zdania.');
        return;
    }

    const token = localStorage.getItem('accessToken');

    fetch(`${API_URL_SENTENCES}?moduleId=${moduleId}&ordinalNumber=${adjustedIndex}`, {
        method: 'DELETE',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        }
    })
        .then(response => response.json())
        .then(data => {
            console.log('Response:', data);

            if (data.statusCode === 201) {
                getAllSentences(moduleId);
            } else {
                console.error('Nie udało się usunąć zdania:', data.message);
            }
        })
        .catch(error => {
            console.error('Error while deleting sentence:', error.message);
        });
}