document.addEventListener('DOMContentLoaded', function() {

    //-------------------------------------------------------------------------------------------------
    // Elementos
    //-------------------------------------------------------------------------------------------------
    const fechaInicio = document.getElementById('fechaInicio');
    const fechaFin = document.getElementById('fechaFin');
    const fechaInicioInput = document.getElementById("fechaInicioInput");
    const fechaFinInput = document.getElementById("fechaFinInput");
    const alojamientoSelect = document.getElementById("alojamientoSelect");
    const totalInput = document.getElementById("totalInput");
    const hoy = new Date();

    let fechasOcupadas = []; // lista de intervalos ocupados

    //-------------------------------------------------------------------------------------------------
    // Fechas Tempus Dominus
    //-------------------------------------------------------------------------------------------------
    
    const tdFechaInicio = new tempusDominus.TempusDominus(fechaInicio, {
        localization: {
            locale: 'es',
            format: 'dd/MM/yyyy',
            hourCycle: 'h23'
        },
        restrictions: { minDate: hoy },

    });

    const tdFechaFin = new tempusDominus.TempusDominus(fechaFin, {
        useCurrent: false,
        localization: {
            locale: 'es',
            format: 'dd/MM/yyyy',
            hourCycle: 'h23'
        },
        restrictions: { minDate: hoy },

    });

    // Desactivar inputs hasta seleccionar alojamiento
    fechaInicioInput.disabled = true;
    fechaFinInput.disabled = true;

    //-------------------------------------------------------------------------------------------------
    // Función para cargar fechas ocupadas del alojamiento
    //-------------------------------------------------------------------------------------------------
    async function cargarFechasOcupadas(alojamientoId) {
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

    //-------------------------------------------------------------------------------------------------
    // Función para actualizar calendarios con fechas ocupadas
    //-------------------------------------------------------------------------------------------------
    function actualizarCalendarios() {
        // Generar lista de todas las fechas ocupadas posteriores a hoy
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

        //-------------------------------
        // Actualizar fecha inicio
        //-------------------------------
        tdFechaInicio.updateOptions({
            restrictions: { minDate: hoy, disabledDates: fechasBloqueadas }
        });

        //-------------------------------
        // Actualizar fecha fin
        //-------------------------------
        let fechaMinFin = hoy;
        let fechasDisponiblesFin = [];

        if (fechaInicioInput.value) {
            const partes = fechaInicioInput.value.split('/');
            const fechaInicioSeleccionada = new Date(partes[2], partes[1]-1, partes[0]);
            fechaMinFin = new Date(fechaInicioSeleccionada.getTime() + 24*60*60*1000);

            // Calcular fechas consecutivas disponibles desde fechaMinFin
            let fecha = new Date(fechaMinFin);
            const limite = new Date(hoy.getFullYear() + 10, hoy.getMonth(), hoy.getDate()); // 10 años adelante

            while (!esFechaOcupada(fecha) && fecha <= limite) {
                fechasDisponiblesFin.push(new Date(fecha));
                fecha.setDate(fecha.getDate() + 1);
            }

            // Generar todas las fechas posteriores a la última consecutiva para bloquearlas
            const fechasPosterioresBloqueadas = [];
            if (fechasDisponiblesFin.length) {
                let f = new Date(fechasDisponiblesFin[fechasDisponiblesFin.length - 1].getTime() + 24*60*60*1000);
                const limitePosterior = new Date(hoy.getFullYear() + 10, hoy.getMonth(), hoy.getDate()); //10 años
                while (f <= limitePosterior) {
                    fechasPosterioresBloqueadas.push(new Date(f));
                    f.setDate(f.getDate() + 1);
                }
            }

            tdFechaFin.updateOptions({
                restrictions: {
                    minDate: fechaMinFin,
                    disabledDates: fechasBloqueadas.concat(fechasPosterioresBloqueadas)
                }
            });

            //-------------------------------
            // Función para marcar fechas ocupadas en rojo
            //-------------------------------
            function formatDate(d) {
                const y = d.getFullYear();
                const m = String(d.getMonth() + 1).padStart(2, '0');
                const day = String(d.getDate()).padStart(2, '0');
                return `${y}-${m}-${day}`;
            }

            function marcarFechasOcupadas(tdInstance) {
                tdInstance._calendar?.querySelectorAll('td').forEach(tdCell => {
                    const dataDate = tdCell.getAttribute('data-date');
                    if (!dataDate) return;

                    const cellDate = new Date(dataDate);
                    const cellStr = formatDate(cellDate);

                    const ocupada = fechasOcupadas.some(rango => {
                        let f = new Date(rango.inicio);
                        while (f <= rango.fin) {
                            if (formatDate(f) === cellStr) return true;
                            f.setDate(f.getDate() + 1);
                        }
                        return false;
                    });

                    if (ocupada) tdCell.classList.add('fecha-ocupada');
                });
            }

            // Suscribir el marcado de fechas ocupadas a eventos de renderizado
            [tdFechaInicio, tdFechaFin].forEach(td => {
                td.subscribe('update.td', () => marcarFechasOcupadas(td));
                marcarFechasOcupadas(td); // también marcar inmediatamente
            });
        }

        //-------------------------------
        // Marcar fechas ocupadas en rojo
        //-------------------------------
        [tdFechaInicio, tdFechaFin].forEach(td => {
            td._calendar?.querySelectorAll('td').forEach(tdCell => {
                const dataDate = tdCell.getAttribute('data-date');
                if (dataDate) {
                    const d = new Date(dataDate);
                    if (esFechaOcupada(d)) tdCell.classList.add('fecha-ocupada');
                }
            });
        });
    }



    //-------------------------------------------------------------------------------------------------
    // Calculo total precio reserva
    //-------------------------------------------------------------------------------------------------
    async function calcularTotal() {
        const alojamientoId = alojamientoSelect.value;
        const inicio = fechaInicioInput.value;
        const fin = fechaFinInput.value;

        if (!alojamientoId || !inicio || !fin) return;

        const response = await fetch(`/gestion/reservas/calcular-total?alojamientoId=${alojamientoId}&inicio=${inicio}&fin=${fin}`);
        if (response.ok) {
            const total = await response.json();
            totalInput.value = total.toFixed(2);
        }
    }


    //-------------------------------------------------------------------------------------------------
    // Eventos
    //-------------------------------------------------------------------------------------------------
    alojamientoSelect.addEventListener("change", async function() {
        const alojamientoId = alojamientoSelect.value;

        if (alojamientoId) {
            fechaInicioInput.disabled = false;
            fechaFinInput.disabled = false;
            await cargarFechasOcupadas(alojamientoId);
        } else {
            fechaInicioInput.disabled = true;
            fechaFinInput.disabled = true;
            fechaInicioInput.value = "";
            fechaFinInput.value = "";
            totalInput.value = "";
            fechasOcupadas = [];
            actualizarCalendarios();
        }

        calcularTotal();
    });

    fechaInicioInput.addEventListener("change", function() {
        calcularTotal();
        actualizarCalendarios();
    });

    fechaFinInput.addEventListener("change", calcularTotal);

});