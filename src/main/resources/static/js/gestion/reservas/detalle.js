document.addEventListener('DOMContentLoaded', function() {

    
    //-------------------------------------------------------------------------------------------------
    // Elementos
    //-------------------------------------------------------------------------------------------------
    const fechaInicio = document.getElementById('fechaInicio');
    const fechaFin = document.getElementById('fechaFin');
    const fechaInicioInput = document.getElementById("fechaInicioInput");
    const fechaFinInput = document.getElementById("fechaFinInput");
    const alojamientoSelect = document.getElementById("alojamientoSelect");
    const clienteSelect = document.getElementById('clienteSelect');
    const totalInput = document.getElementById("totalInput");
    const hoy = new Date();
    hoy.setHours(0, 0, 0, 0);

    let fechasOcupadas = []; 

    //-------------------------------------------------------------------------------------------------
    // Buscadores Mejorados (Tom Select)
    //-------------------------------------------------------------------------------------------------
    
    if (clienteSelect) {
        new TomSelect(clienteSelect, {
            create: false,
            sortField: {
                field: "text",
                direction: "asc"
            },
            placeholder: "Escribe para buscar un cliente..."
        });
    }

    if (alojamientoSelect) {
        new TomSelect(alojamientoSelect, {
            create: false,
            sortField: {
                field: "text",
                direction: "asc"
            },
            placeholder: "Escribe para buscar un alojamiento..."
        });
    }

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

    fechaInicioInput.setAttribute('readonly', true);
    fechaFinInput.setAttribute('readonly', true);

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
            
            const fechaMinFin = new Date(fechaInicioSeleccionada.getTime() + 24 * 60 * 60 * 1000);

            let proximaFechaBloqueada = null;
            
            const fechasBloqueadasOrdenadas = fechasBloqueadas
                .map(d => d.getTime())
                .sort((a, b) => a - b);

            for (const fechaTimestamp of fechasBloqueadasOrdenadas) {
                if (fechaTimestamp > fechaInicioSeleccionada.getTime()) {
                    proximaFechaBloqueada = new Date(fechaTimestamp);
                    break;
                }
            }

            let fechasDeshabilitadasFin = [];
            if (proximaFechaBloqueada) {
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
                    disabledDates: fechasDeshabilitadasFin 
                }
            });

        } else {
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
                    
                    tdCell.classList.remove('fecha-ocupada'); 

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
            fechaInicioInput.removeAttribute('readonly');
            fechaFinInput.removeAttribute('readonly');

            await cargarFechasOcupadas(alojamientoId);
        } else {
            fechaInicioInput.setAttribute('readonly', true);
            fechaFinInput.setAttribute('readonly', true);
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
        fechaInicioInput.removeAttribute('readonly');
        fechaFinInput.removeAttribute('readonly');
        cargarFechasOcupadas(alojamientoSelect.value);
    }

});