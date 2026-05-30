const novaConsultaButton = document.getElementById('NovaCon');
const modalContainer = document.getElementById('cons-container');
const closeButton = document.getElementById('close');
novaConsultaButton.addEventListener('click', () => {
    modalContainer.classList.add('open');
});

closeButton.addEventListener('click', () => {
    modalContainer.classList.remove('open');
});