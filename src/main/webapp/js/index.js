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
        .then(r => r.json())
        .then(r => console.log(r))
}

const readButton = document.getElementById("db-read-button")
if(readButton) {
    readButton.addEventListener('click', readButtonClick)
}

function readButtonClick(e) {
    fetch(window.location.href, {
        method: "COPY"
    })
        .then((r) => r.json())
        .then((calls) => {
            const container = document.getElementById('table-container')
            const table = document.createElement('table')
            const thead = document.createElement('thead')
            const tbody = document.createElement('tbody')
            const initialRow = document.createElement('tr')
            initialRow.append(
                createTableCell('<b>Id</b>'),
                createTableCell('Name'),
                createTableCell('Phone'),
                createTableCell('Moment')
            )
            thead.appendChild(initialRow)

            for(let i = 0; i < calls.length; i++) {
                const call = calls[i]
                const tr = document.createElement('tr')
                tr.append(
                    createTableCell(call.id),
                    createTableCell(call.name),
                    createTableCell(call.phone),
                    createTableCell(new Date(call.moment).toDateString())
                )
                tbody.appendChild(tr);
            }

            table.append(thead, tbody)
            container.replaceChildren(table)
        })
}

function createTableCell(text) {
    const td = document.createElement('td')
    td.innerHTML = text
    return td;
}