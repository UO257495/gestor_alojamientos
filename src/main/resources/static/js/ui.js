//-----------------------------------------------------------------------------
// Muestra un popup que pide una confirmación de una acción
//-----------------------------------------------------------------------------
function mostrarConfirmacionModal(titulo, contenido, callbackSi, callbackNo) {

    if (document.getElementById('confirmacionModal') == null) {
        let div = document.createElement('div');
        div.innerHTML = '<div class="modal fade" id="confirmacionModal" tabindex="-1">' +
                '<div class="modal-dialog modal-dialog-centered">' +
                    '<div class="modal-content">' +
                        '<div class="modal-header">' +
                            '<h1 class="modal-title fs-5">Modal title</h1>' +
                            '<button type="button" class="btn-close" data-bs-dismiss="modal"></button>' +
                        '</div>' +
                        '<div class="modal-body">' +
                            '...' +
                        '</div>' +
                        '<div class="modal-footer">' +
                            '<button type="button" id="btnSi" class="btn btn-success">Sí</button>' +
                            '<button type="button" id="btnNo" class="btn btn-danger">No</button>' +
                        '</div>' +
                    '</div>' +
                '</div>' +
            '</div>';
        document.body.appendChild(div);
    }

    const modal = bootstrap.Modal.getOrCreateInstance('#confirmacionModal');
    document.querySelector('#confirmacionModal .modal-title').innerHTML = titulo;
    document.querySelector('#confirmacionModal .modal-body').innerHTML = contenido;

    const btnSi = document.querySelector('#confirmacionModal #btnSi');
    btnSi.replaceWith(btnSi.cloneNode(true));  // Clona el nodo eliminando todas las suscripciones a eventos anteriores
    document.querySelector('#confirmacionModal #btnSi').addEventListener('click', (e) => {
        if (callbackSi !== undefined) callbackSi();
        modal.hide();
    });

    const btnNo = document.querySelector('#confirmacionModal #btnNo');
    btnNo.replaceWith(btnNo.cloneNode(true));  // Clona el nodo eliminando todas las suscripciones a eventos anteriores
    document.querySelector('#confirmacionModal #btnNo').addEventListener('click', (e) => {
        if (callbackNo !== undefined) callbackNo();
        modal.hide();
    });

    modal.show();
}


export {
    mostrarConfirmacionModal   
}