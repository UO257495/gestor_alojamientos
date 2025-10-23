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
    hoy.setHours(0, 0, 0, 0);

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
            while (fecha < rango.fin) {
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

        if (fechaInicioInput.value) {
            const partes = fechaInicioInput.value.split('/');
            const fechaInicioSeleccionada = new Date(partes[2], partes[1] - 1, partes[0]);
            
            // 1. La fecha mínima de check-out es 1 día después del check-in
            const fechaMinFin = new Date(fechaInicioSeleccionada.getTime() + 24 * 60 * 60 * 1000);

            // 2. Encontrar la *próxima* fecha de check-in bloqueada *después* de nuestro check-in
            let proximaFechaBloqueada = null;
            
            // Ordenamos las fechas bloqueadas para encontrar la más cercana
            const fechasBloqueadasOrdenadas = fechasBloqueadas
                .map(d => d.getTime())
                .sort((a, b) => a - b);

            for (const fechaTimestamp of fechasBloqueadasOrdenadas) {
                // (Ignoramos el setTime(0,0,0,0) por simplicidad, JS lo maneja)
                if (fechaTimestamp > fechaInicioSeleccionada.getTime()) {
                    proximaFechaBloqueada = new Date(fechaTimestamp);
                    break;
                }
            }

            // 3. Deshabilitar todas las fechas *posteriores* a esa próxima reserva
            // El usuario SÍ puede hacer check-out el mismo día que otro hace check-in
            let fechasDeshabilitadasFin = [];
            if (proximaFechaBloqueada) {
                // Empezamos a deshabilitar el día *siguiente* al inicio de la próxima reserva
                let fecha = new Date(proximaFechaBloqueada.getTime() + 24 * 60 * 60 * 1000);
                const limite = new Date(hoy.getFullYear() + 10, hoy.getMonth(), hoy.getDate());

                while (fecha <= limite) {
                    fechasDeshabilitadasFin.push(new Date(fecha));
                    fecha.setDate(fecha.getDate() + 1);
                }
            }
            
            tdFechaFin.updateOptions({
                restrictions: {
                    minDate: fechaMinFin,
                    disabledDates: fechasDeshabilitadasFin // Solo deshabilitamos las fechas futuras
                }
            });

        } else {
            // Si no hay fecha de inicio, deshabilitamos todo lo que ya está bloqueado
             tdFechaFin.updateOptions({
                restrictions: {
                    minDate: hoy,
                    disabledDates: fechasBloqueadas
                }
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
                    
                    // 1. Limpiamos la clase primero
                    tdCell.classList.remove('fecha-ocupada'); 

                    // 2. Si está ocupada, la volvemos a añadir
                    if (esFechaOcupada(d)) {
                        tdCell.classList.add('fecha-ocupada');
                    }
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


    if (alojamientoSelect.value) {
        fechaInicioInput.disabled = false;
        fechaFinInput.disabled = false;
        cargarFechasOcupadas(alojamientoSelect.value);
    }

});