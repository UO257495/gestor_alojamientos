document.addEventListener('DOMContentLoaded', function() {

    //-------------------------------------------------------------------------------------------------
    // Fechas Tempus Dominus
    //-------------------------------------------------------------------------------------------------

    const fechaInicio = document.getElementById('fechaInicio');
    const fechaFin = document.getElementById('fechaFin');
    
    const tdFechaInicio = new tempusDominus.TempusDominus(fechaInicio, {
        localization: {
            locale: 'es',
            format: 'dd/MM/yyyy',
            hourCycle: 'h23'
        },
        display: {
            components: {
                useTwentyfourHour: true
            }
        }
    });

    const tdFechaFin = new tempusDominus.TempusDominus(fechaFin, {
        useCurrent: false,
        localization: {
            locale: 'es',
            format: 'dd/MM/yyyy',
            hourCycle: 'h23'
        },
        display: {
            components: {
                useTwentyfourHour: true
            }
        }
    });

    //-------------------------------------------------------------------------------------------------
    // Calculo total precio reserva
    //-------------------------------------------------------------------------------------------------

    const alojamientoSelect = document.getElementById("alojamientoSelect");
    const fechaInicioInput = document.getElementById("fechaInicioInput");
    const fechaFinInput = document.getElementById("fechaFinInput");
    const totalInput = document.getElementById("totalInput");

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

    alojamientoSelect.addEventListener("change", calcularTotal);
    fechaInicioInput.addEventListener("change", calcularTotal);
    fechaFinInput.addEventListener("change", calcularTotal);
});