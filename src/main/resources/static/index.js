// src/main/resources/static/index.js
document.addEventListener('DOMContentLoaded', () => {
    fetchGames();
});

async function fetchGames() {
    try {
        const response = await fetch('/api/v1/games');
        const apiResponse = await response.json();
        
        const gameList = document.getElementById('game-list');
        apiResponse.data.forEach(game => {
            const gameCard = document.createElement('div');
            gameCard.className = 'game-card';
            gameCard.innerHTML = `
                <div class="card-body">
                    <h3>${game.title}</h3>
                    <p>📍 ${game.location}</p>
                    <p>🕒 ${new Date(game.gameDatetime).toLocaleString()}</p>
                    <button onclick="location.href='reservation.html?gameId=${game.id}'" class="btn-primary">예매하기</button>
                </div>
            `;
            gameList.appendChild(gameCard);
        });
    } catch (error) {
        console.error('경기 목록 로딩 실패:', error);
    }
}
