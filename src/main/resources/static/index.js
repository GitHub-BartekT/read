const API_URL = 'http://localhost:8080/api/allModules';

createStartSessionButtons();

function createStartSessionButtons(){
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