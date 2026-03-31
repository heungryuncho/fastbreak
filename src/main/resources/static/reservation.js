// src/main/resources/static/reservation.js
const urlParams = new URLSearchParams(window.location.search);
const gameId = urlParams.get('gameId');
let stompClient = null;

document.addEventListener('DOMContentLoaded', () => {
    loadSeats();
    connectWebSocket();
});

// 1. 초기 좌석 목록 불러오기
async function loadSeats() {
    const response = await fetch(`/api/v1/games/${gameId}/seats`);
    const result = await response.json();
    renderSeats(result.data);
}

// 2. 좌석 렌더링 및 클릭 이벤트
function renderSeats(seats) {
    const grid = document.getElementById('seat-grid');
    grid.innerHTML = '';
    seats.forEach(seat => {
        const seatEl = document.createElement('div');
        seatEl.id = `seat-${seat.id}`;
        seatEl.className = `seat ${seat.status.toLowerCase()}`;
        seatEl.innerText = seat.seatNumber;

        if (seat.status === 'AVAILABLE') {
            seatEl.onclick = () => tryReserve(seat.id);
        }
        grid.appendChild(seatEl);
    });
}

// 3. 웹소켓 연결 (STOMP)
function connectWebSocket() {
    const socket = new SockJS('/ws-reservation');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, (frame) => {
        console.log('연결 성공: ' + frame);
        stompClient.subscribe(`/topic/games/${gameId}/seats`, (message) => {
            const updatedSeat = JSON.parse(message.body);
            updateSeatUI(updatedSeat);
        });
    });
}

// 4. 실시간 UI 업데이트
function updateSeatUI(seat) {
    const seatEl = document.getElementById(`seat-${seat.id}`);
    if (seatEl) {
        seatEl.className = `seat ${seat.status.toLowerCase()}`;
        if (seat.status === 'AVAILABLE') {
            seatEl.onclick = () => tryReserve(seat.id);
        } else {
            seatEl.onclick = null;
        }
    }
}

// 5. 예매 시도 (API 호출)
async function tryReserve(seatId) {
    const token = localStorage.getItem('token');

    const response = await fetch('/api/v1/reservations', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({ seatId: seatId })
    });

    if (response.ok) {
        const result = await response.json(); // 예약 ID를 받아옴
        const reservationId = result.data.id;

        alert('좌석이 선점되었습니다! 5분 이내에 결제해 주세요.');

        // 결제 버튼 노출 및 클릭 이벤트 연결
        const payBtn = document.getElementById('pay-button');
        payBtn.style.display = 'block';
        payBtn.onclick = () => tryPay(reservationId);
    }
}

async function tryPay(reservationId) {
    const token = localStorage.getItem('token');
    const response = await fetch(`/api/v1/reservations/${reservationId}/pay`, {
        method: 'PATCH',
        headers: { 'Authorization': `Bearer ${token}` }
    });

    if (response.ok) {
        alert('결제가 완료되었습니다! 즐거운 관람 되세요.');
        location.reload(); // 상태 반영을 위해 새로고침
    }
}

