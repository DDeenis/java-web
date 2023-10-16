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
//initDatePickers()

const createButton = document.getElementById("db-create-button")
if(createButton) {
    createButton.addEventListener('click', createButtonClick)
}

function createButtonClick(e) {
    fetch(window.location.href, {
        method: "PUT"
    })
        .then((r) => r.json())
        .then((r) => console.log(r))
}

const insertForm = document.getElementById("db-insert-form")
if(insertForm) {
    insertForm.addEventListener('submit', insertFormSubmit)
}

function insertFormSubmit(e) {
    e.preventDefault();
    const fields = insertForm.querySelectorAll("[name]")
    let body = {}
    fields.forEach(i => {
        body[i.name] = i.value
    })
    fetch(window.location.href, {
        method: "POST",
        body: JSON.stringify(body),
        headers: {
            "Content-Type": "application/json"
        }
    })
}