document.addEventListener('DOMContentLoaded', function () {
    console.log("slidepanel.js loaded.");

    const slidePanelOverlay = document.createElement('div');
    slidePanelOverlay.className = 'slide-panel-overlay';
    document.body.appendChild(slidePanelOverlay);

    const slidePanel = document.createElement('div');
    slidePanel.className = 'slide-panel';
    slidePanel.id = 'bookingDetailPanel';
    slidePanel.innerHTML = `
        <div class="slide-panel-header">
            <h3 id="slidePanelTitle">Booking Details</h3>
            <button class="slide-panel-close-btn" id="slidePanelCloseBtn">&times;</button>
        </div>
        <div class="slide-panel-body" id="slidePanelBody">
            <p>Loading details...</p>
        </div>
    `;
    document.body.appendChild(slidePanel);

    const slidePanelTitle = document.getElementById('slidePanelTitle');
    const slidePanelBody = document.getElementById('slidePanelBody');
    const slidePanelCloseBtn = document.getElementById('slidePanelCloseBtn');

    function openSlidePanel() {
        console.log("Opening slide panel.");
        slidePanelOverlay.classList.add('active');
        slidePanel.classList.add('active');
        document.body.style.overflow = 'hidden'; // Prevent background scroll
    }

    function closeSlidePanel() {
        console.log("Closing slide panel.");
        slidePanelOverlay.classList.remove('active');
        slidePanel.classList.remove('active');
        document.body.style.overflow = ''; // Restore background scroll
        slidePanelBody.innerHTML = '<p>Loading details...</p>'; // Reset content
    }

    slidePanelCloseBtn.addEventListener('click', closeSlidePanel);
    slidePanelOverlay.addEventListener('click', closeSlidePanel); // Close on overlay click

    // This function will be called from main.js or directly from HTML event
    // (if you add data-booking-id to a clickable element in the table row)
    window.showBookingDetails = async function(bookingId) {
        console.log(`Workspaceing details for booking ID: ${bookingId}`);
        slidePanelBody.innerHTML = '<p>Loading details...</p>';
        openSlidePanel();

        try {
            // baseUrl is defined in main.html's inline script
            const response = await fetch(`${window.baseUrl}bookings/${bookingId}/details`);
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            const data = await response.json(); // Expecting JSON response

            if (data) {
                slidePanelTitle.textContent = `Details for: ${data.siteName || 'Booking'}`;
                // Password should ideally not be sent to client, even if masked.
                // If sent, ensure it's for display only and heavily masked or indicated as "Set".
                slidePanelBody.innerHTML = `
                    <div class="detail-item"><strong>Site Name:</strong> <span id="detailSiteName">${data.siteName || 'N/A'}</span></div>
                    <div class="detail-item"><strong>Site URL:</strong> <span><a href="${data.siteUrl || '#'}" target="_blank">${data.siteUrl || 'N/A'}</a></span></div>
                    <div class="detail-item"><strong>Login ID:</strong> <span id="detailLoginId">${data.loginId || 'N/A'}</span></div>
                    <div class="detail-item"><strong>Password:</strong> <span>******** (Protected)</span></div>
                    <div class="detail-item"><strong>Booking Time:</strong> <span id="detailBookingTime">${data.displayBookingTime || 'N/A'}</span></div>
                    <div class="detail-item"><strong>Status:</strong> <span id="detailStatus" class="${data.active ? 'status-active' : 'status-inactive'}">${data.active ? 'Active' : 'Inactive'}</span></div>
                    <div class="detail-item"><strong>Created By:</strong> <span id="detailCreatedBy">${data.createdByUsername || 'N/A'}</span></div>
                    `;
            } else {
                slidePanelBody.innerHTML = '<p style="color:red;">Could not load booking details.</p>';
            }
        } catch (error) {
            console.error('Error fetching booking details:', error);
            slidePanelBody.innerHTML = `<p style="color:red;">Error loading details: ${error.message}</p>`;
        }
    };
    console.log("Slide panel setup complete. showBookingDetails function is now global.");
});