function initModals() {
    const elems = document.querySelectorAll('.modal');
    const instances = M.Modal.init(elems, {
        opacity: 0.6,
        inDuration: 200,
        outDuration: 200
    });
}

function initDatePickers() {
    const elems = document.querySelectorAll('.datepicker');
    const instances = M.Datepicker.init(elems, {
        autoClose: true,
        yearRange: [1920, new Date().getFullYear()]
    });
}

initModals()
initDatePickers()