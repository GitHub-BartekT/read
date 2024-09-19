const API_URL = 'http://localhost:8080/api';
const MODULE_API_URL = `${API_URL}/module`;
const PROPERTIES_API_URL = `${API_URL}/moduleProperties`;

const queryString = window.location.search;
console.log(queryString);
const urlParams = new URLSearchParams(queryString);
const moduleId = urlParams.get('module');

const formNextSession = {module: moduleId};
const mySession = new Array();
let meter = 0;

fetch(`${MODULE_API_URL}?${new URLSearchParams(formNextSession)}`)
    .then((response) => response.json())
    .then((modulesArr) => {
        const list = modulesArr.map(s =>
            mySession.push(s.sentence));
        nextSentence();
    });

document.addEventListener("keypress", nextSentence);
document.addEventListener("click", nextSentence);

function nextSentence() {
    if (meter < mySession.length) {
        document.getElementById("current sentence").innerText = mySession.at(meter);
    } else if (meter === mySession.length){
        updateParameters(moduleId);
    } else {
        backToMain("current sentence");
    }
    meter++;
}

function updateParameters(moduleId){
    const formId = {id: moduleId};
    fetch(`${PROPERTIES_API_URL}/session?${new URLSearchParams(formId)}`,
        {method: 'PUT',
            headers: {
                'Accept': 'application/json',
                'Content-Type': `application/json`
            },}
    ).then(response => response.json())
        .then(s => {
       backToMain("current sentence");
            });
}

function backToMain(elementId){
    document.getElementById(elementId).innerHTML = `😀<br>
    <a href="index.html">
    <button class="pure-button button-xlarge" value="go">Back to main</button></a>`;
}