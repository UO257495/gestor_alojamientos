document.addEventListener('DOMContentLoaded', function() {

    const fechaInicio = document.getElementById('fechaInicio');
    const fechaFin = document.getElementById('fechaFin');
    const fechaInicioInput = document.getElementById("fechaInicioInput");
    const fechaFinInput = document.getElementById("fechaFinInput");
    const fechaInicioCard = document.getElementById('fechaInicioCard');
    const fechaFinCard = document.getElementById('fechaFinCard');
    const precioTotalCard = document.getElementById('precioTotalCard');
    const precioTotalInput = document.getElementById('precioTotalInput');
    const alojamientoIdInput = document.querySelector('input[name="alojamientoId"]');
    const hoy = new Date();
    hoy.setHours(0, 0, 0, 0);

    let fechasOcupadas = [];

    // Inicializar Tempus Dominus
    const tdFechaInicio = new tempusDominus.TempusDominus(fechaInicio, {
        localization: { locale: 'es' },
        restrictions: { minDate: hoy }
    });

    const tdFechaFin = new tempusDominus.TempusDominus(fechaFin, {
        localization: { locale: 'es' },
        restrictions: { minDate: hoy }
    });

    fechaInicioInput.disabled = false;
    fechaFinInput.disabled = false;

    async function cargarFechasOcupadas(alojamientoId) {
        if (!alojamientoId) return;
        try {
            const response = await fetch(`/gestion/reservas/gestion/alojamientos/${alojamientoId}/fechas-ocupadas`);
            if (!response.ok) return;
            const data = await response.json();
            fechasOcupadas = data.map(f => ({
                inicio: new Date(f.inicio),
                fin: new Date(f.fin)
            }));
            actualizarCalendarios();
        } catch (e) {
            console.error("Error cargando fechas ocupadas", e);
        }
    }

    function actualizarCalendarios() {
        const fechasBloqueadas = fechasOcupadas.flatMap(rango => {
            const lista = [];
            let fecha = new Date(rango.inicio);
            while (fecha <= rango.fin) {
                if (fecha >= hoy) lista.push(new Date(fecha));
                fecha.setDate(fecha.getDate() + 1);
            }
            return lista;
        });

        function esFechaOcupada(date) {
            return fechasBloqueadas.some(d =>
                d.getFullYear() === date.getFullYear() &&
                d.getMonth() === date.getMonth() &&
                d.getDate() === date.getDate()
            );
        }

        tdFechaInicio.updateOptions({ restrictions: { minDate: hoy, disabledDates: fechasBloqueadas } });

        if (fechaInicioInput.value) {
            const partes = fechaInicioInput.value.split('/');
            const fechaInicioSeleccionada = new Date(partes[2], partes[1]-1, partes[0]);
            const fechaMinFin = new Date(fechaInicioSeleccionada.getTime() + 24*60*60*1000);

            let fechasDeshabilitadasFin = [];
            let proximaFechaBloqueada = fechasBloqueadas.find(d => d > fechaInicioSeleccionada);
            if (proximaFechaBloqueada) {
                let fecha = new Date(proximaFechaBloqueada.getTime() + 24*60*60*1000);
                while (fecha.getFullYear() <= hoy.getFullYear() + 10) {
                    fechasDeshabilitadasFin.push(new Date(fecha));
                    fecha.setDate(fecha.getDate() + 1);
                }
            }

            tdFechaFin.updateOptions({ restrictions: { minDate: fechaMinFin, disabledDates: fechasDeshabilitadasFin } });
        } else {
            tdFechaFin.updateOptions({ restrictions: { minDate: hoy, disabledDates: fechasBloqueadas } });
        }

        // Marcar fechas ocupadas en rojo
        [tdFechaInicio, tdFechaFin].forEach(td => {
            td._calendar?.querySelectorAll('td').forEach(tdCell => {
                const dataDate = tdCell.getAttribute('data-date');
                if (dataDate) {
                    const d = new Date(dataDate);
                    tdCell.classList.remove('fecha-ocupada');
                    if (esFechaOcupada(d)) tdCell.classList.add('fecha-ocupada');
                }
            });
        });
    }

    function recalcularPrecio() {
        if (!fechaInicioInput.value || !fechaFinInput.value) {
            precioTotalCard.textContent = '0 €';
            precioTotalInput.value = 0;
            return;
        }

        const inicio = new Date(fechaInicioInput.value.split('/').reverse().join('-'));
        const fin = new Date(fechaFinInput.value.split('/').reverse().join('-'));
        if (fin < inicio) return;

        const dias = (fin - inicio)/(1000*60*60*24) + 1;
        const precioBase = [[${alojamiento.tarifaBase}]]; 
        const total = precioBase * dias;

        precioTotalCard.textContent = total.toFixed(2) + ' €';
        precioTotalInput.value = total.toFixed(2);
    }

    tdFechaInicio.subscribe('change.td', e => {
        if (e.date) {
            fechaInicioInput.value = e.date.toLocaleDateString('es-ES');
            fechaInicioCard.textContent = fechaInicioInput.value;
            recalcularPrecio();
            actualizarCalendarios();
        }
    });

    tdFechaFin.subscribe('change.td', e => {
        if (e.date) {
            fechaFinInput.value = e.date.toLocaleDateString('es-ES');
            fechaFinCard.textContent = fechaFinInput.value;
            recalcularPrecio();
        }
    });

    // Inicializar fechas ocupadas según alojamientoId
    if (alojamientoIdInput.value) {
        cargarFechasOcupadas(alojamientoIdInput.value);
    }

});
