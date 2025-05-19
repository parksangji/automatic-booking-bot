document.addEventListener('DOMContentLoaded', function() {
    // ... (이전 JS 코드의 상단 변수 선언 및 함수 정의는 대부분 유지) ...
    console.log("main.js loaded and DOMContentLoaded.");

    // --- 전역 변수로 전달된 Thymeleaf 값 사용 ---
    // window.csrfToken, window.csrfHeaderName, window.baseUrl,
    // window.flashOpenModalFor, window.flashModalBookingId,
    // window.flashModalBookingData, window.flashBindingResultForBookingData 는 HTML에서 선언됨

    // --- DOM 요소 참조 ---
    const modal = document.getElementById('bookingModal');
    const modalTitle = document.getElementById('modalTitle');
    const modalBody = document.getElementById('bookingModalBody');
    const addBookingBtn = document.getElementById('addBookingBtn');
    const closeModalBtn = document.getElementById('closeModalBtn');
    const bookingsTable = document.getElementById('bookingsTable');

    // Alert 메시지 요소 참조
    const toastSuccessAlert = document.getElementById('toastSuccessAlert');
    const toastErrorAlert = document.getElementById('toastErrorAlert');

    // --- 함수 선언 ---
    function openModal() { /* 이전과 동일 */ if(modal) modal.style.display = 'flex'; console.log("Modal opened.");}
    function closeModal() { /* 이전과 동일 */ if(modal) modal.style.display = 'none'; if(modalBody) modalBody.innerHTML = '<p>Loading form...</p>'; if (window.flashOpenModalFor && window.history.replaceState) { const cleanUrl = window.location.protocol + "//" + window.location.host + window.location.pathname; window.history.replaceState({path:cleanUrl}, '', cleanUrl); } console.log("Modal closed.");}
    async function fetchAndInjectForm(url, title) { /* 이전과 동일 */ console.log(`Workspaceing form from: ${url} with title: ${title}`); if (!modalBody || !modalTitle) { console.error("Modal body or title element not found in fetchAndInjectForm!"); return; } try { const response = await fetch(url, { method: 'GET', headers: { 'Accept': 'text/html' } }); if (!response.ok) { throw new Error(`HTTP error! status: ${response.status} - ${response.statusText}`); } const formHtml = await response.text(); modalTitle.textContent = title; modalBody.innerHTML = formHtml; const loadedForm = modalBody.querySelector('form'); if (loadedForm && window.csrfToken && window.csrfHeaderName) { let csrfInput = loadedForm.querySelector('input[name="' + window.csrfHeaderName + '"]'); if (!csrfInput) { csrfInput = document.createElement('input'); csrfInput.type = 'hidden'; csrfInput.name = window.csrfHeaderName; loadedForm.prepend(csrfInput); } csrfInput.value = window.csrfToken; } openModal(); } catch (error) { console.error('Error fetching and injecting form:', error); modalBody.innerHTML = `<p style="color:red;">Could not load the form content. ${error.message}</p>`; openModal(); } }
    function populateFormFields(form, data) { /* 이전과 동일 */ if (!form || !data) return; console.log("Populating form with data:", data); if(form.elements['siteName']) form.elements['siteName'].value = data.siteName || ''; if(form.elements['siteUrl']) form.elements['siteUrl'].value = data.siteUrl || ''; if(form.elements['loginId']) form.elements['loginId'].value = data.loginId || ''; if(form.elements['bookingTime'] && data.bookingTime) { try { let isoDateTime = ''; if (typeof data.bookingTime === 'string') { isoDateTime = data.bookingTime.substring(0, 16); } else if (data.bookingTime.year) { const dt = data.bookingTime; isoDateTime = `${dt.year}-${('0'+dt.monthValue).slice(-2)}-${('0'+dt.dayOfMonth).slice(-2)}T${('0'+dt.hour).slice(-2)}:${('0'+dt.minute).slice(-2)}`;} form.elements['bookingTime'].value = isoDateTime; } catch(e) { console.error("Error formatting bookingTime for form:", e, data.bookingTime); form.elements['bookingTime'].value = ''; } } if(form.elements['active']) form.elements['active'].value = String(data.active); if(form.elements['id'] && data.id) form.elements['id'].value = data.id;}
    function displayValidationErrorsInModal(errors) { /* 이전과 동일 */ if (!errors || errors.length === 0) return; console.log("Displaying validation errors in modal:", errors); document.querySelectorAll('#modalBookingForm .modal-error').forEach(el => el.textContent = ''); errors.forEach(function(error){ let fieldIdInModal = 'modal' + error.field.charAt(0).toUpperCase() + error.field.slice(1); let errorElement = document.querySelector(`#${fieldIdInModal} ~ .modal-error, input[name='${error.field}'] ~ .modal-error`); if(errorElement) { errorElement.textContent = error.message; } else { console.warn(`No error placeholder found for field: ${error.field}`); } });}

    // --- Alert 메시지 처리 함수 ---
    function showToastAlert(alertElement) {
        if (alertElement && alertElement.textContent.trim() !== '') {
            console.log("Showing toast alert:", alertElement.id);
            alertElement.classList.add('show');
            setTimeout(() => {
                alertElement.classList.remove('show');
                alertElement.classList.add('slide-out'); // 슬라이드 아웃 시작
                // 애니메이션 시간 후 완전히 숨김 (CSS transition 시간과 일치시켜야 함)
                setTimeout(() => {
                    alertElement.style.display = 'none'; // 필요시 display none
                    alertElement.classList.remove('slide-out'); // 다음 사용을 위해 클래스 제거
                }, 500); // CSS transition 시간 (0.5s)
            }, 5000); // 5초 후 사라짐
        }
    }

    // --- 이벤트 리스너 바인딩 ---
    if (addBookingBtn) { /* 이전과 동일 */ addBookingBtn.addEventListener('click', function() { fetchAndInjectForm(window.baseUrl + 'bookings/add-form', 'Add New Booking'); }); } else { console.warn("Add Booking Button not found."); }
    if (closeModalBtn) { /* 이전과 동일 */ closeModalBtn.addEventListener('click', closeModal); } else { console.warn("Close Modal Button not found."); }
    if (bookingsTable) { /* 이전과 동일 */ bookingsTable.addEventListener('click', function(event) { const targetButton = event.target.closest('.editBookingBtn'); if (targetButton) { const bookingId = targetButton.dataset.bookingId; if (bookingId) { fetchAndInjectForm(window.baseUrl + `bookings/${bookingId}/edit-form`, 'Edit Booking'); } } }); } else { console.warn("Bookings Table not found."); }
    window.addEventListener('click', function(event) { /* 이전과 동일 */ if (event.target === modal) { closeModal(); } });

    // --- 페이지 로드 시 FlashAttribute 처리 및 Alert 표시 ---
    if (window.flashOpenModalFor) { // 모달 자동 열기 로직
        console.log(`Flash attribute 'openModalFor' detected: ${window.flashOpenModalFor}`);
        let fetchUrl;
        let modalViewTitle;
        if (window.flashOpenModalFor === 'add') {
            fetchUrl = window.baseUrl + 'bookings/add-form';
            modalViewTitle = 'Add New Booking (Check Errors)';
        } else if (window.flashOpenModalFor === 'edit' && window.flashModalBookingId) {
            fetchUrl = window.baseUrl + `bookings/${window.flashModalBookingId}/edit-form`;
            modalViewTitle = 'Edit Booking (Check Errors)';
        }
        if (fetchUrl) {
            fetchAndInjectForm(fetchUrl, modalViewTitle).then(() => {
                if (window.flashModalBookingData) {
                    populateFormFields(document.getElementById('modalBookingForm'), window.flashModalBookingData);
                }
                if (window.flashBindingResultForBookingData) {
                    displayValidationErrorsInModal(window.flashBindingResultForBookingData);
                }
            });
        }
    }

    // 페이지 로드 시 토스트 알림 표시
    if (toastSuccessAlert) showToastAlert(toastSuccessAlert);
    if (toastErrorAlert) showToastAlert(toastErrorAlert);

    console.log("main.js DOMContentLoaded setup complete.");
});