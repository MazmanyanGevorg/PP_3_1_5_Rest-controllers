const BASE_URL = 'http://localhost:8080/api/users';

const modalEdit = document.getElementById('edit-modal-window');
const modalDelete = document.getElementById('delete-modal-window');
const modalDeleteClose = document.getElementById('close_delete_modal');
const demoATbody = document.getElementById('demoA');
const adminLeftBtn = document.getElementById('adminLeftBtn');
const deleteUserForm = document.getElementById('delete-user-form');
const createNewUser = document.getElementById('createNewUser');


///////////////////// обновление общей таблицы /////////////////////
async function refreshTable() {
    await fetch(BASE_URL + '/')
        .then(response => response.json())
        .then(data => {
            fillTableWithData(data);
        })
        .catch(error => {
            console.error('Произошла ошибка:', error);
        });
}


///////////заполнение общей таблицы///////////////////////////////
function fillTableWithData(data) {

    // Очистка таблицы
    demoATbody.innerHTML = '';
    // Перебор каждого объекта пользователя в JSON
    data.forEach(user => {

        const row = demoATbody.insertRow();
        row.insertCell().append(document.createTextNode(user.id));
        row.insertCell().append(document.createTextNode(user.name));
        row.insertCell().append(document.createTextNode(user.surname));
        row.insertCell().append(document.createTextNode(user.age));
        row.insertCell().append(document.createTextNode(user.level));
        row.insertCell().append(document.createTextNode(user.points));
        const roles = user.roles.map(role => role.role.split('_')[1]);

        row.insertCell().append(document.createTextNode(roles.join(' ')));

        // кнопка edit
        const editButton = document.createElement('button');
        editButton.textContent = 'Edit';
        editButton.classList.add('btn');
        editButton.classList.add('btn-warning');
        editButton.setAttribute("onclick", "editButtonHandler(event)");
        editButton.setAttribute("data-userid", user.id);
        editButton.setAttribute("id", "idButtonEdit");
        row.insertCell().append(editButton);

        // кнопка delete
        const deleteButton = document.createElement('button');
        deleteButton.textContent = 'Delete';
        deleteButton.classList.add('btn');
        deleteButton.classList.add('btn-danger');
        deleteButton.setAttribute("onclick", "deleteButtonHandler(event)");
        deleteButton.setAttribute("data-userid", user.id);
        deleteButton.setAttribute("id", "idButtonDelete");
        row.insertCell().append(deleteButton);
    });
}


//обработчик кнопки submit-delete
deleteUserForm.addEventListener('submit', event => {
    console.log('Нажата кнопка delete формы удаления юзера');
    event.preventDefault();

    const userID = document.getElementById('deleteID').value;

    const URL = BASE_URL + '/' + userID;
    console.log('DELETE запрос на URL ', URL);
    // Отправляем DELETE запрос
    fetch(URL, {method: 'DELETE', credentials: 'include', headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(userID)})
        .then(response => {
            if (response.ok) {
                console.log('Запрос на удаление успешно выполнен');
                adminLeftBtn.click();
                modalDelete.style.display = 'none';
                return refreshTable();
            } else {
                // TODO: refactor
                console.log('Произошла ошибка при выполнении запроса удаления');
            }
        })
        .catch(error => {
            console.log('Ошибка при удалении пользователя', error);
        })

});

createNewUser.onsubmit = async (e) => {
    e.preventDefault();
    let formData = new FormData(createNewUser);
    const plainFormData = Object.fromEntries(formData.entries());
    plainFormData.roles = formData.getAll('roles');
    const json = JSON.stringify(plainFormData);

    try {
        let response = await fetch('http://localhost:8080/api/users/', {
            headers: {
                'Content-Type': 'application/json; charset=utf-8'
            },
            method: 'POST',
            body: json
        });

        if (response.ok) {

            createNewUser.reset(); // Очистка полей формы
            adminLeftBtn.click(); // Программное нажатие кнопки adminLeftBtn

            const row = demoATbody.insertRow();
            row.insertCell().append(plainFormData.name + "");
            row.insertCell().append(plainFormData.surname + "");
            row.insertCell().append(plainFormData.age + "");
            row.insertCell().append(plainFormData.level + "");
            row.insertCell().append(plainFormData.points + "");
            row.insertCell().append(plainFormData.password + "");
            row.insertCell().append(plainFormData.roles + "");

            return refreshTable();
        } else {
            console.error('Ошибка: ', response.statusText)
        }
    } catch (error) {
        console.log('Ошибка при создании нового пользователя:', error);
    }
};