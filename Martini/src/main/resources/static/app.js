// app.js – front‑end logic for consultation scheduling
document.addEventListener('DOMContentLoaded', function () {
    // ----- Constants -----
    const HORARIOS = ['09:00','10:00','11:00','12:00','13:00','14:00','15:00','16:00','17:00'];
    const API_CONSULTAS = '/api/consultas';
    const API_PACIENTES = '/api/pacientes'; // assumed existent for patient search

    // ----- Init FullCalendar -----
    const calendarEl = document.getElementById('calendar');
    const calendar = new FullCalendar.Calendar(calendarEl, {
        firstDay: 1,
        weekends: false,
        hiddenDays: [0,6],
        buttonText: { today: 'Hoje', week: 'Semana', day: 'Dia' },

        initialView: 'timeGridWeek',
        slotDuration: '01:00:00',
        businessHours: {
            daysOfWeek: [1,2,3,4,5], // Monday‑Friday
            startTime: '09:00',
            endTime: '18:00'
        },
        headerToolbar: {
            left: 'prev,next today',
            center: 'title',
            right: ''
        },
        events: fetchEvents,
        eventClick: onEventClick,
        locale: 'pt-br'
    });
    calendar.render();

    // ----- Helper: fetch events from backend -----
    function fetchEvents(info, successCallback, failureCallback) {
        fetch(API_CONSULTAS)
            .then(r => r.json())
            .then(data => successCallback(data))
            .catch(err => {
                console.error('Erro ao carregar consultas', err);
                if (failureCallback) failureCallback(err);
            });
    }

    // ----- Modal handling -----
    const modal = document.getElementById('modal-consulta');
    const btnAbrir = document.getElementById('btn-abrir-consulta');
    const btnFechar = document.getElementById('modal-close');
    const btnCancelar = document.getElementById('btn-cancelar');
    const btnSalvar = document.getElementById('btn-salvar');
    const btnExcluir = document.getElementById('btn-excluir-consulta');
    const form = document.getElementById('form-consulta');

    // Populate horario selects
    function populateHorarioSelects() {
        const selectInicio = document.getElementById('select-horario-inicio');
        const selectFim = document.getElementById('select-horario-fim');
        selectInicio.innerHTML = '';
        selectFim.innerHTML = '';
        HORARIOS.forEach(h => {
            const optI = document.createElement('option');
            optI.value = h; optI.textContent = h; selectInicio.appendChild(optI);
            const optF = document.createElement('option');
            optF.value = h; optF.textContent = h; selectFim.appendChild(optF);
        });
    }

    // Open modal for new consultation
    btnAbrir.addEventListener('click', () => {
        clearForm();
        populateHorarioSelects();
        document.getElementById('modal-title').textContent = 'Nova Consulta';
        btnExcluir.style.display = 'none';
        modal.style.display = 'block';
    });

    // Close modal
    function closeModal(){ modal.style.display = 'none'; }
    btnFechar.addEventListener('click', closeModal);
    btnCancelar.addEventListener('click', closeModal);

    // Clear form fields
    function clearForm(){
        form.reset();
        document.getElementById('consulta-id').value = '';
    }

    // Validate before submit
    function validateForm(){
        const pacienteId = document.getElementById('paciente-id').value;
        const data = document.getElementById('data-consulta').value;
        const inicio = document.getElementById('select-horario-inicio').value;
        const fim = document.getElementById('select-horario-fim').value;
        const valor = document.getElementById('valor-consulta').value;
        if(!pacienteId) return 'Selecione um paciente.';
        if(!data) return 'Informe a data da consulta.';
        if(!inicio || !fim) return 'Informe o horário inicial e final.';
        if(inicio >= fim) return 'O horário final deve ser maior que o horário inicial.';
        if(!valor) return 'Informe o valor da consulta.';
        return null; // ok
    }

    // Save (create or update)
    btnSalvar.addEventListener('click', () => {
        const err = validateForm();
        if(err){ toastError(err); return; }
        const payload = {
            clienteId: document.getElementById('paciente-id').value,
            data: document.getElementById('data-consulta').value,
            horario: document.getElementById('select-horario-inicio').value,
            horarioFim: document.getElementById('select-horario-fim').value,
            valor: parseFloat(document.getElementById('valor-consulta').value)
        };
        const id = document.getElementById('consulta-id').value;
        const method = id ? 'PUT' : 'POST';
        const url = id ? `${API_CONSULTAS}/${id}` : API_CONSULTAS;
        fetch(url, {
            method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        })
        .then(r => r.json().then(d => ({status: r.status, body: d})))
        .then(res => {
            if(res.status >= 200 && res.status < 300){
                toastSuccess(res.body.mensagem || 'Operação concluída');
                calendar.refetchEvents();
                closeModal();
            } else {
                toastError(res.body.erro || 'Erro ao salvar consulta');
            }
        })
        .catch(e => { console.error(e); toastError('Falha de comunicação'); });
    });

    // Delete consultation
    btnExcluir.addEventListener('click', () => {
        const id = document.getElementById('consulta-id').value;
        if(!id) return;
        if(!confirm('Confirma exclusão da consulta?')) return;
        fetch(`${API_CONSULTAS}/${id}`, { method: 'DELETE' })
            .then(r => r.json().then(d => ({status: r.status, body: d})))
            .then(res => {
                if(res.status === 200){
                    toastSuccess(res.body.mensagem || 'Consulta excluída');
                    calendar.refetchEvents();
                    closeModal();
                } else {
                    toastError(res.body.erro || 'Erro ao excluir');
                }
            })
            .catch(e => { console.error(e); toastError('Falha de comunicação'); });
    });

    // Click on calendar event – load details
    function onEventClick(info) {
        const id = info.event.id;
        fetch(`${API_CONSULTAS}/${id}`)
            .then(r => r.json())
            .then(c => {
                populateHorarioSelects();
                document.getElementById('modal-title').textContent = 'Editar Consulta';
                document.getElementById('consulta-id').value = c.idConsulta;
                document.getElementById('paciente-id').value = c.cliente.idCliente;
                document.getElementById('paciente-nome').value = c.cliente.nome;
                document.getElementById('data-consulta').value = c.data;
                document.getElementById('select-horario-inicio').value = c.horario;
                document.getElementById('select-horario-fim').value = c.horarioFim;
                document.getElementById('valor-consulta').value = c.valor;
                btnExcluir.style.display = 'inline-block';
                modal.style.display = 'block';
            })
            .catch(e => { console.error(e); toastError('Erro ao carregar consulta'); });
    }

    // ----- Patient autocomplete (basic) -----
    const pacienteInput = document.getElementById('paciente-nome');
    const pacienteIdInput = document.getElementById('paciente-id');
    pacienteInput.addEventListener('input', function(){
        const query = this.value.trim();
        if(query.length < 2) return;
        fetch(`${API_PACIENTES}?search=${encodeURIComponent(query)}`)
            .then(r => r.json())
            .then(list => {
                // simple suggestion list – replace with your UI component
                const datalist = document.getElementById('pacientes-datalist');
                datalist.innerHTML = '';
                list.forEach(p => {
                    const opt = document.createElement('option');
                    opt.value = p.nome;
                    opt.dataset.id = p.idCliente;
                    datalist.appendChild(opt);
                });
            });
    });
    // when a suggestion is chosen, set hidden id
    pacienteInput.addEventListener('change', function(){
        const dl = document.getElementById('pacientes-datalist');
        const opt = Array.from(dl.options).find(o => o.value === this.value);
        if(opt) pacienteIdInput.value = opt.dataset.id;
    });

    // ----- Toastify helpers (assumes Toastify lib is loaded) -----
    function toastSuccess(msg){ Toastify({text: msg, backgroundColor: "#28a745", duration: 3000}).showToast(); }
    function toastError(msg){ Toastify({text: msg, backgroundColor: "#dc3545", duration: 4000}).showToast(); }
});
