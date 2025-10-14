
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

});