let sentences = [[${sessionSentences}]];
let currentSentenceIndex = 0;

document.addEventListener('click', showNextSentence);
document.addEventListener('keypress', showNextSentence);

function showNextSentence() {
    if (currentSentenceIndex < sentences.length) {
        document.getElementById('current-sentence').innerText = sentences[currentSentenceIndex];
        currentSentenceIndex++;
    } else {
        document.getElementById('current-sentence').innerHTML = `😀<br>
                    <a href="/api/dashboard">
                    <button class="pure-button button-xlarge">Back to main</button></a>`;
    }
}