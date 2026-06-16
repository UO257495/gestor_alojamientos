
document.addEventListener('DOMContentLoaded', function() {

    //-------------------------------------------------------------------------------------------------
    // Fechas Tempus Dominus
    //-------------------------------------------------------------------------------------------------

    const contenedorInicio = document.getElementById('fechaInicio');
    const contenedorFin = document.getElementById('fechaFin');
    
    const opcionesComunes = {
        localization: {
            locale: 'es',
            format: 'dd/MM/yyyy' 
        },
        display: {
            components: {
                calendar: true,
                date: true,
                month: true,
                year: true,
                clock: false, 
            }
        }
    };

    const tdFechaInicio = new tempusDominus.TempusDominus(contenedorInicio, opcionesComunes);
    
    const tdFechaFin = new tempusDominus.TempusDominus(contenedorFin, {
        ...opcionesComunes,
        useCurrent: false 
    });

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

    document.querySelectorAll('#fechaInicioInput, #fechaFinInput').forEach(input => {
        input.addEventListener('blur', function() {
            if(this.value) {
                let partes = this.value.split('/');
                if (partes.length === 3) {
                    let d = partes[0].padStart(2, '0');
                    let m = partes[1].padStart(2, '0');
                    let y = partes[2];
                    if (y.length === 2) y = '20' + y; 
                    this.value = `${d}/${m}/${y}`;
                }
            }
        });
    });

});