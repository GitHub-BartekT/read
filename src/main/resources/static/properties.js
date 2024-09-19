const API_URL = 'http://localhost:8080/api';
const MODULE_API_URL = `${API_URL}/module`;
const PROPERTIES_API_URL = `${API_URL}/moduleProperties`;

fetch(`${API_URL}/allModules`)
    .then((response) => response.json())
    .then((modulesPropArr) => {
        modulesPropArr.map(s => {
            let table = document.getElementById('all-module-table');
            let row = table.insertRow(-1);
            newCellInRow(row, 0, s.id);
            newCellInRow(row, 1, s.module_Name);
            newCellInRow(row, 2, s.active);

            let tableButtons = document.getElementById('buttons');
            let rowButtons = tableButtons.insertRow(-1);
            let newCell = rowButtons.insertCell(0);
            const newButton = document.createElement("div");
            newButton.innerHTML = `<div>
                                        <style>
                                                .button-accept,
                                                .button-decline,
                                                 {
                                                    color: white;
                                                    border-radius: 4px;
                                                    text-shadow: 0 1px 1px rgba(0, 0, 0, 0.2);
                                                }
                                                .button-accept {
                                                    background: rgb(28, 184, 65);
                                                }
                                                .button-decline {
                                                    background: rgb(202, 60, 60);
                                                }
                                        </style>
                                    <button id=${s.id} class="pure-button" >${s.module_Name}</button>
                                </div>`;
            newCell.appendChild(newButton);
        });
    });

fetch(`${PROPERTIES_API_URL}`)
    .then((response) => response.json())
    .then((modulesPropArr) => {
        modulesPropArr.map(s => {
            let table = document.getElementById('module-properties-table');
            let row = table.insertRow(-1);

            newCellInRow(row, 0, s.id);

            newCellInRow(row, 1, s.sessionsPerDay);
            newCellInRow(row, 2, s.presentationsPerSession);
            newCellInRow(row, 3, s.newSentencesPerDay);
            newCellInRow(row, 4, s.actualDay);
            newCellInRow(row, 5, s.nextSession);
            let cells = row.cells;
            cells[0].id = `${s.id}0`;

            let newCell = row.insertCell(6);
            const newButton = document.createElement("div");
            let textChangeBtn = `changeBtn${s.id}`;
            newButton.innerHTML = ` <button id="${textChangeBtn}" class="pure-button pure-button-primary" >Change module</button>`;
            newCell.appendChild(newButton);


            let newChangeCell = row.insertCell(7);
            const newChangeButton = document.createElement("div");
            let textAcceptChangeBtn = `acceptChangesBtn${s.id}`;
            newChangeButton.innerHTML = `<button id="${textAcceptChangeBtn}" class="pure-button pure-button-disabled" >Accept</button>`;
            newChangeCell.appendChild(newChangeButton);
        });
    });

const changeModuleButtons = document.getElementById('main table');

const changeModuleButtonsPressed = e => {
    const isButton = e.target.nodeName === 'BUTTON';

    if(!isButton){ return}

    let clickBtnID = `${e.target.id}`;
    let rowID = clickBtnID.substring(9);

    let checkButton = clickBtnID.substring(0,9);
    let changeBtnID =       "changeBtn" + rowID;
    let acceptChangeBtnID = "acceptChangesBtn" + rowID;

    let propertiesTable = document.getElementById("module-properties-table");


    // Click "Change Module" button
    if (checkButton === "changeBtn" && document.getElementById(changeBtnID).innerText === "Change module"){
        let row = propertiesTable.rows[rowID];
        let cells = row.cells;
        changeBtnToDecline(changeBtnID);
        changeBtnToAccept(acceptChangeBtnID, true);
        for(let i = 1; i < 6; i++ ){
            let labelId = `${rowID}${i}`;
            cellCreateLabel( cells[i], cells[i].innerText, labelId);
        }
    }

    // Click "Decline" button
    else if (checkButton === "changeBtn" && document.getElementById(changeBtnID).innerText === "Decline"){
        changeBtnToPrimary(changeBtnID, "Change module");
        changeBtnToAccept(acceptChangeBtnID, false);
        let row = propertiesTable.rows[rowID];
        let cells = row.cells;

        for(let i = 1; i <6;i++){
            let actualCell = document.getElementById(`${rowID}${i}`);
            let cellText;
            if (actualCell.placeholder !== null) {
                cellText = actualCell.placeholder;
            }
            cellPutPlainText(cells[i], cellText);
        }
    }

    // Click "Accept" button
    else if(checkButton === "acceptCha"){

        let rowId = clickBtnID.substring(16);
        changeBtnID = "changeBtn" + rowId;
        acceptChangeBtnID = "acceptChangesBtn" + rowId;
        changeBtnToPrimary(changeBtnID, "Change module");
        changeBtnToAccept(acceptChangeBtnID, false);
        updateParameters(rowId);
    }
}

function updateParameters(rowId){
    const paramUpdateProperties = {
        sessionsPerDay: document.getElementById(`${rowId}1`).value,
        presentationsPerSession:  document.getElementById(`${rowId}2`).value,
        newSentencesPerDay: document.getElementById(`${rowId}3`).value,
        actualDay: document.getElementById(`${rowId}4`).value,
        nextSession: document.getElementById(`${rowId}5`).value
    };
    const formId = {id: document.getElementById(`${rowId}0`).innerText};
    fetch(`${PROPERTIES_API_URL}?${new URLSearchParams(formId)}`,
        {   method: 'PUT',
            headers: {
                'Accept': 'application/json',
                'Content-Type': `application/json`
            },
            body: JSON.stringify(paramUpdateProperties)}
    ).then(response => response.json())
        .then(s => {
            let propertiesTable = document.getElementById("module-properties-table");
            let row = propertiesTable.rows[rowId];
            let cells = row.cells;
            cellPutPlainText(cells[0], s.id);
            cellPutPlainText(cells[1], s.sessionsPerDay);
            cellPutPlainText(cells[2], s.presentationsPerSession);
            cellPutPlainText(cells[3], s.newSentencesPerDay);
            cellPutPlainText(cells[4], s.actualDay);
            cellPutPlainText(cells[5], s.nextSession);
        });
}

changeModuleButtons.addEventListener("click",changeModuleButtonsPressed);

function cellCreateLabel(parent, text, id){
    const form = document.createElement("label");
    parent.innerText = "";
    form.innerHTML =    `<input id="${id}" size=3
                            class="pure-input-rounded pure-u-1" value="${text}" placeholder="${text}"/>
                            `;
    parent.appendChild(form);
}

function cellPutPlainText(parent, text){
    parent.innerText = text;
}

function changeBtnToDecline(btnId){
    document.getElementById(btnId).className = "";
    document.getElementById(btnId).style.color = "white";
    document.getElementById(btnId).classList.add("pure-button", "button-decline");
    document.getElementById(btnId).innerText = "Decline";
}

function changeBtnToAccept(btnId, enable){
    document.getElementById(btnId).className = "";
    if (enable === true){
        document.getElementById(btnId).style.color = "white";
        document.getElementById(btnId).classList.add("pure-button", "button-accept");
    } else {
        document.getElementById(btnId).style.color = "grey";
        document.getElementById(btnId).classList.add("pure-button", "pure-button-disabled");
    }
}

function changeBtnToPrimary(btnId, text){
    document.getElementById(btnId).className = "";
    document.getElementById(btnId).style.color = "white";
    document.getElementById(btnId).classList.add("pure-button", "pure-button-primary");
    document.getElementById(btnId).innerText = text;
}

//Add new sentence
document.getElementById('Add sentence btn').addEventListener('click',addSentence);

function addSentence(){
    const paramAddSentence = {
        sentence: document.getElementById('new sentence').value,
        module: document.getElementById('moduleNumber').innerHTML
    };

    fetch(`${MODULE_API_URL}`,
        {   method: 'Post',
            headers: {
                'Accept': 'application/json',
                'Content-Type': `application/json`
            },
            body: JSON.stringify(paramAddSentence)}
    )
        .then(processOkResponse)
        .then(addSentenceToList);
}

function addSentenceToList(){
    let newSentenceText = document.getElementById('new sentence').value;
    document.getElementById('list').innerHTML += `<li>${newSentenceText}</li>`
}

const moduleButtons = document.getElementById('buttons');
const moduleButtonsPressed = e => {
    const isButton = e.target.nodeName === 'BUTTON';

    if(!isButton){ return}

    const formObj = {id: e.target.id};

    const formNextSession = {module: e.target.id};

    let button = document.getElementById(e.target.id).innerText;
    let text = button.innerText;

    document.getElementById('moduleNumber').innerText = e.target.id;

    fetch(`${MODULE_API_URL}?${new URLSearchParams(formNextSession)}`)
        .then((response) => response.json())
        .then((modulesArr) => {
            const list = modulesArr.map(s =>
                `<li>${s.sentence}</li>`)
                .join('\n');
            document.getElementById('next session').innerHTML = list;
        });

    document.getElementById("moduleName").innerText = text;
    fetch(`${MODULE_API_URL}?${new URLSearchParams(formObj)}`)
        .then((response) => response.json())
        .then((modulesArr) => {
            const list = modulesArr.map(s =>
                `<li>${s.sentence}</li>`)
                .join('\n');
            document.getElementById('list').innerHTML = list;
        });
}

moduleButtons.addEventListener("click", moduleButtonsPressed);

function newCellInRow(row, int, text){
    let newCell = row.insertCell(int);
    let newText = document.createTextNode(text);
    newCell.appendChild(newText);
}

function processOkResponse(response = {}) {
    if (response.ok) {
        return response.json();
    }
    throw new Error(`Status not 200 (${response.status})`);
}