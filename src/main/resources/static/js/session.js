const API_URL = 'http://ec2-3-64-60-246.eu-central-1.compute.amazonaws.com:8000/api/session';
const token = localStorage.getItem('accessToken');
const sessionId = new URLSearchParams(window.location.search).get('sessionId');
let currentSentenceIndex = 0;
let sentences = [];

// Fetch the sentences from the API
fetch(`${API_URL}/next-session?sessionId=${sessionId}`, {
    method: 'GET',
    headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
    }
})
    .then(response => response.json())
    .then(data => {
        sentences = data.data; // This will be the list of strings (sentences)
        showNextSentence(); // Display the first sentence
    })
    .catch(error => {
        console.error('Error fetching sentences:', error);
        document.getElementById('current-sentence').innerText = 'Failed to load session.';
    });

// Function to show the next sentence on click or keypress
document.addEventListener('click', showNextSentence);
document.addEventListener('keypress', showNextSentence);

function showNextSentence() {
    const sentenceElement = document.getElementById('current-sentence');

    if (currentSentenceIndex < sentences.length) {
        sentenceElement.innerText = sentences[currentSentenceIndex];
        currentSentenceIndex++;
        console.log("current sentence indes: " + currentSentenceIndex);
    } else if (currentSentenceIndex === sentences.length) {
        sentenceElement.innerHTML = `😀<br><span>Koniec sesji!</span>`;
        endSession(); // Call the function to end the session
        currentSentenceIndex++;
    } else {
        redirectToDashboard();
    }
}

function endSession() {
    fetch(`${API_URL}/end-session?sessionId=${sessionId}`, {
        method: 'PATCH',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        }
    })
        .then(() => {
            console.log('Session ended.');
        })
        .catch(error => {
            console.error('Error ending session:', error);
        });
}

function redirectToDashboard() {
    window.location.href = '/dashboard.html'; // Redirect to the dashboard
}
