  document.addEventListener('DOMContentLoaded', () => { 
        
    //-------------------------------------------------------------------------------------------------
    // Creación de mapa y marcador 
    //-------------------------------------------------------------------------------------------------

    const map = L.map('map').setView([43.35, -5.85], 8); // Centro de Asturias por defecto

    L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
        maxZoom: 19,
        attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
    }).addTo(map);

    // Marcador
    let marker = L.marker([43.35, -5.85], { draggable: true }).addTo(map);

    // Actualizar inputs al mover marcador
    marker.on('moveend', () => {
        const pos = marker.getLatLng();
        document.getElementById('latitud').value = pos.lat.toFixed(6);
        document.getElementById('longitud').value = pos.lng.toFixed(6);
    });

    // --- Geocodificación al escribir dirección ---
    const direccionInput = document.getElementById('direccion');
    direccionInput.addEventListener('blur', async () => {
        const address = direccionInput.value.trim();
        if (!address) return;

        const url = `https://nominatim.openstreetmap.org/search?format=json&q=${encodeURIComponent(address)}`;
        try {
            const response = await fetch(url);
            const data = await response.json();
            if (data && data.length > 0) {
                const lat = parseFloat(data[0].lat);
                const lon = parseFloat(data[0].lon);

                // Actualizar inputs
                document.getElementById('latitud').value = lat.toFixed(6);
                document.getElementById('longitud').value = lon.toFixed(6);

                // Mover marcador y centrar mapa
                marker.setLatLng([lat, lon]);
                map.setView([lat, lon], 15);
            }
        } catch (err) {
            console.error("Error geocodificando la dirección:", err);
        }
    });

    // --- Previsualización de imagen ---
    const fotoInput = document.getElementById('foto');
    const preview = document.getElementById('preview');
    fotoInput.addEventListener('change', (event) => {
        const file = event.target.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = e => {
                preview.src = e.target.result;
                preview.style.display = 'block';
            }
            reader.readAsDataURL(file);
        } else {
            preview.src = '';
            preview.style.display = 'none';
        }
    });

    // --- Formateo de tarifa base ---
    const tarifaInput = document.getElementById('tarifaBaseInput');

    tarifaInput.addEventListener('input', () => {
        tarifaInput.value = tarifaInput.value.replace(/[^0-9.]/g, '');
        const parts = tarifaInput.value.split('.');
        if (parts.length > 2) {
            tarifaInput.value = parts[0] + '.' + parts[1];
        }
    });

    tarifaInput.addEventListener('blur', () => {
        if (tarifaInput.value !== '' && !isNaN(tarifaInput.value)) {
            tarifaInput.value = parseFloat(tarifaInput.value).toFixed(2);
        }
    });

    tarifaInput.addEventListener('paste', e => {
        const paste = (e.clipboardData || window.clipboardData).getData('text');
        if (!/^\d*\.?\d*$/.test(paste)) e.preventDefault();
    });

    // --- Formateo de capacidad ---
    const capacidadInput = document.getElementById('capacidadInput');
    capacidadInput.addEventListener('input', () => {
        capacidadInput.value = capacidadInput.value.replace(/[^0-9]/g, '');
    });
    capacidadInput.addEventListener('paste', e => {
        const paste = (e.clipboardData || window.clipboardData).getData('text');
        if (!/^\d+$/.test(paste)) e.preventDefault();
    });
    
 });