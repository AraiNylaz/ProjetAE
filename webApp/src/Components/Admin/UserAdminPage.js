import { RedirectUrl } from "../Router.js";
import { getUserSessionData } from "../../utils/session.js";
import { callAPI } from "../../utils/api.js";
import PrintError from "../PrintError.js"
const API_BASE_URL_USER = "/api/furniture/";
const API_BASE_URL_ADMIN = "/api/admin/";

let userAdmin = `
<div class="col-12">
    <div class="form-group">
        <div class="row">
            <div class="col-6">
                <div class="form-group">
                    <label for="firstname">Prénom</label>
                    <div id="firstname"></div>
                </div>
            </div>
            <div class="col-6">
                <div class="form-group">
                    <label for="lastname">Nom</label>
                    <div id="lastname"></div>
                </div>
            </div>
        </div>
        <div class="row">
            <div class="col-6">
                <div class="form-group">
                    <label for="username_form">Pseudo</label>
                    <div id="username_form"></div>
                </div>
            </div>
            <div class="col-6">
                <div class="form-group">
                    <label for="email">E-mail</label>
                    <div id="email"></div>
                </div>
            </div>
        </div>
        <div class="row">
            <div class="col-6">
                <div class="form-group">
                    <label for="registration_date">Date d'inscription</label>
                    <div id="registration_date"></div>
                </div>
            </div>
            <div class="col-6">
                <div class="form-group">
                    <label for="user_type">Type d'utilisateur</label>
                    <div id="user_type"></div>
                </div>
            </div>
        </div>
        <div class="row">
            <div class="col-6">
                <div class="form-group">
                <label for="furniture_buyed">Meuble acheté par le client</label>
                <div id="furniture_buyed"></div>
            </div>
        </div>
        <div class="col-6">
            <div class="form-group">
                <label for="furniture_selled">Meuble vendu par le client</label>
                <div id="furniture_selled"></div>
            </div>
        </div>
    </div>
</div>`;

const UserAdmin = async(f) => {
    let page = document.querySelector("#page");
    page.innerHTML = userAdmin;

    let user = getUserSessionData();

    if(f == null){
        let queryString = window.location.search;
        let urlParams = new URLSearchParams(queryString);
        let id = urlParams.get("id");
        try {
            f = await callAPI(API_BASE_URL_ADMIN + "user/" + id , "GET", user.token);
        } catch (err) {
            console.error(err);
            console.log("call 1")
            PrintError(err);
        }
    }

    onUserInformation(f);

    let furnitureBuyByUser = await callAPI(API_BASE_URL_USER + "furniturebuyby/" + f.id , "GET", user.token);

    onFurnitureBuy(furnitureBuyByUser);

    let furnitureSellByUser = await callAPI(API_BASE_URL_USER + "sales/" + f.id , "GET", user.token);

    onFurnitureSell(furnitureSellByUser);
}

const onUserInformation = (data) => {
    let firstname = document.querySelector("#firstname");
    firstname.innerHTML = `<input class="form-control" id="firstname_input" type="text" placeholder=${data.firstName} readonly />`;
    let lastname = document.querySelector("#lastname");
    lastname.innerHTML = `<input class="form-control" id="lastname_input" type="text" placeholder=${data.lastName} readonly />`;
    let username = document.querySelector("#username_form");
    username.innerHTML = `<input class="form-control" id="username_form_input" type="text" placeholder=${data.username} readonly />`;
    let email = document.querySelector("#email");
    email.innerHTML = `<input class="form-control" id="email_input" type="text" placeholder=${data.email} readonly />`;
    let registrationDate = document.querySelector("#registration_date");
    let date = new Date(data.registrationDate);
    registrationDate.innerHTML = `<input class="form-control" id="registration_date_input" type="text" placeholder=${date.getDate() + "/" + (date.getMonth()+1) + "/" + date.getFullYear()} readonly />`;
    let userType = document.querySelector("#user_type");
    userType.innerHTML = `<input class="form-control" id="user_type_input" type="text" placeholder=${data.userType} readonly />`;
};

const onFurnitureBuy = (data) => {
    let furnitureBuyed = document.querySelector('#furniture_buyed');
    let display =
        `<div id="tablefurnitures" class="table-responsive mt-3">
            <table class="table">
                <thead>
                    <tr>
                        <th>Description</th>
                        <th>Type</th>
                        <th>Etat</th>
                    </tr>
                </thead>
                <tbody>`;
    data.forEach((element) => {
        display +=  `<tr>
                                    <td><a id="furnitureBuyed${element.id}" href="" target="_blank">${element.description}</a></td>
                                    <td>${element.type.name}</td>
                                    <td>${element.condition}</td>
                                </tr>`;
    });
    display += `</tbody></table></div>`;
    furnitureBuyed.innerHTML = display;

    data.forEach((element) => {
        let furnitureElement = document.getElementById("furnitureBuyed"+element.id);
        furnitureElement.addEventListener("click", (e) => {
            e.preventDefault();
            RedirectUrl("/admin/furniture",element,"?id="+element.id);
        });
    });
};

const onFurnitureSell = (data) => {
    let furnitureSelled = document.querySelector('#furniture_selled');
    let display =
        `<div id="tablefurnitures" class="table-responsive mt-3">
            <table class="table">
                <thead>
                    <tr>
                        <th>Description</th>
                        <th>Type</th>
                        <th>Etat</th>
                    </tr>
                </thead>
                <tbody>`;
    data.forEach((element) => {
        display +=  `<tr>
                                    <td><a id="furnitureSelled${element.id}" href="" target="_blank">${element.description}</a></td>
                                    <td>${element.type.name}</td>
                                    <td>${element.condition}</td>
                                </tr>`;
    });
    display += `</tbody></table></div>`;
    furnitureSelled.innerHTML = display;

    data.forEach((element) => {
        let furnitureElement = document.getElementById("furnitureSelled"+element.id);
        furnitureElement.addEventListener("click", (e) => {
            e.preventDefault();
            RedirectUrl("/admin/furniture",element,"?id="+element.id);
        });
    });
};

export default UserAdmin;