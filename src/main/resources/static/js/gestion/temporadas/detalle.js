
document.addEventListener('DOMContentLoaded', function() {

    //-------------------------------------------------------------------------------------------------
    // Fechas Tempus Dominus
    //-------------------------------------------------------------------------------------------------

    const contenedorInicio = document.getElementById('fechaInicio');
    const contenedorFin = document.getElementById('fechaFin');
    
    // 1. Configuración base estricta para que a Spring le guste
    const opcionesComunes = {
        localization: {
            locale: 'es',
            format: 'dd/MM/yyyy' // Fuerza siempre Día/Mes/Año completo
        },
        display: {
            components: {
                calendar: true,
                date: true,
                month: true,
                year: true,
                clock: false, // Fuera el reloj para no enviar horas a LocalDate
            }
        }
    };

    // 2. Inicializamos
    const tdFechaInicio = new tempusDominus.TempusDominus(contenedorInicio, opcionesComunes);
    
    const tdFechaFin = new tempusDominus.TempusDominus(contenedorFin, {
        ...opcionesComunes,
        useCurrent: false 
    });

    // 3. Sincronizar calendarios (Evitar que Fecha Fin sea anterior a Fecha Inicio)
    contenedorInicio.addEventListener('change.td', (e) => {
        if (e.detail.date) {
            tdFechaFin.updateOptions({
                restrictions: {
                    minDate: e.detail.date
                }
            });
        }
    });

    contenedorFin.addEventListener('change.td', (e) => {
        if (e.detail.date) {
            tdFechaInicio.updateOptions({
                restrictions: {
                    maxDate: e.detail.date
                }
            });
        }
    });

    // 4. PARCHE DE SEGURIDAD PARA ESCRITURA MANUAL
    // Si el usuario escribe a mano "13/3/26" y le da a Guardar, esto lo corrige a "13/03/2026"
    document.querySelectorAll('#fechaInicioInput, #fechaFinInput').forEach(input => {
        input.addEventListener('blur', function() {
            if(this.value) {
                let partes = this.value.split('/');
                if (partes.length === 3) {
                    let d = partes[0].padStart(2, '0');
                    let m = partes[1].padStart(2, '0');
                    let y = partes[2];
                    if (y.length === 2) y = '20' + y; // Convierte 26 en 2026
                    this.value = `${d}/${m}/${y}`;
                }
            }
        });
    });

});