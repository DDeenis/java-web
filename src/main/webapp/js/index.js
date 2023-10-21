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

const readAllButton = document.getElementById("db-read-all-button")
if(readButton) {
    readAllButton.addEventListener('click', readButtonClick)
}

function readButtonClick(e) {
    const includeDeleted = e.target.dataset.includeDeleted === 'true'
    const url = new URL(window.location.href)
    includeDeleted && url.searchParams.set('includeDeleted', 'true')
    fetch(url.toString(), {
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
                createTableCell('Moment'),
                createTableCell('Call Moment'),
                createTableCell('Delete Moment'),
            )
            thead.appendChild(initialRow)

            for(let i = 0; i < calls.length; i++) {
                const call = calls[i]
                const tr = document.createElement('tr')
                const callDate = call.callMoment ? new Date(`${call.callMoment} UTC`) : null;
                const deleteDate = call.deleteMoment ? new Date(`${call.deleteMoment} UTC`) : null;
                tr.append(
                    createTableCell(call.id),
                    createTableCell(call.name),
                    createTableCell(call.phone),
                    createTableCell(new Date(call.moment).toDateString()),
                    createTableCell(
                        callDate ?
                            formatMoment(callDate) :
                            `<button class="waves-effect waves-light blue lighten-2 btn" data-id="${call.id}" onclick="callClickPatch(event)">Call</button>`
                    ),
                    createTableCell(
                        deleteDate ?
                            `<p>${formatMoment(deleteDate)}</p><button class="waves-effect waves-light green lighten-2 btn" data-id="${call.id}" onclick="restoreClick(event)">Restore</button>` :
                            `<button class="waves-effect waves-light red lighten-2 btn" data-id="${call.id}" onclick="deleteClick(event)">Delete</button>`
                    ),
                )
                tbody.appendChild(tr);
            }

            table.append(thead, tbody)
            container.replaceChildren(table)
        })
}

function formatMoment(date) {
    return `${date.toDateString()} ${date.toLocaleTimeString()}`
}

function createTableCell(text) {
    const td = document.createElement('td')
    td.innerHTML = text
    return td;
}

function callClick(e) {
    const id = e.target.dataset.id
    if(!id) return;

    if(confirm("Are you sure you want to call?")) {
        fetch(window.location.href, {
            method: 'LINK',
            body: JSON.stringify({ id }),
            headers: {
                "Content-Type": "application/json"
            }
        })
            .then(r => r.json())
            .then(r => {
                if(typeof r === "string") {
                    console.error(r);
                    return;
                }

                e.target.outerHTML = formatMoment(new Date(`${r.timestamp} UTC`));
            })
    }
}

function callClickPatch(e) {
    const id = e.target.dataset.id
    if(!id) return;

    if(confirm("Are you sure you want to call?")) {
        fetch(`${window.location.href}?call-id=${id}`, {
            method: 'PATCH',
        })
            .then(r => r.json())
            .then(r => {
                if(typeof r === 'string') {
                    console.error(r);
                    return;
                }

                e.target.outerHTML = formatMoment(new Date(`${r.callMoment} UTC`));
            })
    }
}

function deleteClick(e) {
    const id = e.target.dataset.id
    if(!id) return;

    if(confirm("Are you sure you want to delete call?")) {
        fetch(`${window.location.href}?call-id=${id}`, {
            method: 'DELETE',
        })
            .then(r => {
                if(r.status === 204) {
                    e.target.closest('tr').remove()
                    return;
                }

                r.json().then(console.error)
            })
    }
}

function restoreClick(e) {
    const id = e.target.dataset.id
    if(!id) return;

    if(confirm("Are you sure you want to restore call?")) {
        fetch(`${window.location.href}?call-id=${id}`, {
            method: 'RESTORE',
        })
            .then(r => {
                if(r.status === 204) {
                    readAllButton.dispatchEvent(new Event('click'))
                    return;
                }

                r.json().then(console.error)
            })
    }
}