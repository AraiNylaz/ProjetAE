import { RedirectUrl } from "../Router.js";
import { getUserSessionData } from "../../utils/session.js";
import { callAPI, callAPIWithoutJSONResponse } from "../../utils/api.js";
import PrintError from "../PrintError.js"
const API_BASE_URL_USER = "/api/furniture/";
const API_BASE_URL_ADMIN = "/api/admin/";
const axios = require('axios');
const leaflet = require('leaflet');

let visitAdminPage = `
<h4 id="pageTitle">Liste de demandes de visites</h4>
<div class="col-12">
    <div class="form-group">
        <div class="row">
            <div class="col-6">
                <div id="infos"></div>
            </div>
            <div class="col-6">
                <div class="map" id="map"></div>
            </div>
        </div>
        <div class="row">
            <div class="col-12">
                <div id="furnitureList"></div>
            </div>
        </div>
    </div>
</div>
`;

const VisitAdminPage = async(f) => {
    let page = document.querySelector("#page");
    page.innerHTML = visitAdminPage;
    if(f === undefined || f === null){
        try {
            let queryString = window.location.search;
            let urlParams = new URLSearchParams(queryString);
            let id = urlParams.get("id");
            f = await callAPI(API_BASE_URL_ADMIN + "visit/" + id, "GET", user.token);
        } catch (err) {
            console.error("VisitAdminPage::VisitAdminPage", err);
            PrintError(err);
        }
    }

    onVisitInformation(f);
    onMap(f.address);
    let furnitureList = await callAPI(API_BASE_URL_ADMIN + "furnituresvisit/" + id, "GET", user.token);
    onFurnituresInformation(furnitureList);
};

const onVisitInformation = (data) => {
    let requestDate = new Date(data.requestDate);
    let visitInformation = `
    <div class="form-group">
        <div class="row">
            <div class="col-6">
                <label for="request_date">Date de la demande</label>
                <input type="text" class="form-control" id="request_date" placeholder=${requestDate.getDate() + "/" + requestDate.getMonth() + "/" + requestDate.getFullYear()} readonly>
            </div>
            <div class="col-6">
                <label for="status">Status de la demande</label>
                <input type="text" class="form-control" id="status" placeholder=${data.status} readonly>
            </div>
        </div>
        <div class="row">
            <div class="col-6">
                <label for="time_slot">Disponnibilitée</label>
                <input type="text" class="form-control" id="time_slot" placeholder=${data.timeSlot} readonly>
            </div>
            <div class="col-6">
                <label for="customer">Client</label>
                <input type="text" class="form-control" id="customer" placeholder=${data.customer.firstName} readonly>
            </div>
        </div>
        <div class="row">
            <div class="col-6">`;
    if(data.chosenDateTime !== undefined) {
        let chosenDateTime = new Date(data.chosenDateTime);
        visitInformation += `<label for="chosen_date_time">Date de la visite</label>
                             <input type="text" class="form-control" id="chosen_date_time" placeholder=${chosenDateTime.getDate() + "/" + chosenDateTime.getMonth() + "/" + chosenDateTime.getFullYear()} readonly>`;
    } else if(data.cancellationReason !== undefined) {
        visitInformation += `<label for="cancellation_reason">Raison de l'annulation</label>
                             <input type="text" class="form-control" id="cancellation_reason" placeholder=${data.cancellationReason} readonly>`;
    } else {
        visitInformation += `    <label for="chosen_date_time">Date de la visite</label>
                                 <input type="date" class="form-control" id="chosen_date_time" placeholder="Choisir une date">
                                 <button class="btn btn-success" id="chosen_date_time_btn">Demandes de visites</button></div>
                            <div class="col-6">
                                <label for="cancellation_reason">Raison de l'annulation</label>
                                <input type="text" class="form-control" id="cancellation_reason" placeholder="Raison d'annulation">
                                <button class="btn btn-danger" id="cancellation_reason_btn">Refuser la demande</button>
                            </div>`;
    }
    visitInformation += `</div>`;
    let infosDiv = document.querySelector("#infos");
    infosDiv.innerHTML = visitInformation;

    let cancelBtn = document.querySelector("#cancellation_reason_btn");
    if(cancelBtn !== null) {
        cancelBtn.addEventListener("click", onCancelBtn);
    }
    let chosenDateBtn = document.querySelector("#chosen_date_time_btn");
    if(chosenDateBtn !== null) {
        chosenDateBtn.addEventListener("click", onChosenDateBtn);
    }
};

const onCancelBtn = async () => {
    try {
        let queryString = window.location.search;
        let urlParams = new URLSearchParams(queryString);
        let id = urlParams.get("id");
        let cancellationReason = document.querySelector("#cancellation_reason");
        let cancellationReasonValue = cancellationReason.value;
        console.log(cancellationReasonValue);
        let user = getUserSessionData();
        let status = document.querySelector("#status");
        let newStatus = await callAPI(API_BASE_URL_ADMIN + "visit/" + id, "PUT", user.token, {cancellationReason: cancellationReasonValue});
        status.setAttribute("placeholder", newStatus.status);
        cancellationReason.setAttribute("readonly", true);
        cancellationReason.removeAttribute("value");
        cancellationReason.setAttribute("placeholder", cancellationReasonValue);
        let btn = document.querySelector("#cancellation_reason_btn");
        btn.remove();

        let chosenDateTime = document.querySelector("#chosen_date_time");
        chosenDateTime.setAttribute("readonly", true);
        btn = document.querySelector("#chosen_date_time_btn");
        btn.remove();

    } catch (err) {
        console.error("VisitAdminPage::onCancelBtn", err);
        PrintError(err);
    }
};

const onChosenDateBtn = async () => {
    try {
        let queryString = window.location.search;
        let urlParams = new URLSearchParams(queryString);
        let id = urlParams.get("id");
        let chosenDateTime = document.querySelector("#chosen_date_time");
        let chosenDateTimeValue = chosenDateTime.value;
        let user = getUserSessionData();
        let status = document.querySelector("#status");
        let newStatus = await callAPI(API_BASE_URL_ADMIN + "visit/" + id, "PUT", user.token, {chosenDateTime: chosenDateTimeValue});
        status.setAttribute("placeholder", newStatus.status);
        chosenDateTime.setAttribute("readonly", true);
        chosenDateTime.removeAttribute("value");
        chosenDateTime.setAttribute("placeholder", chosenDateTimeValue);
        let btn = document.querySelector("#chosen_date_time_btn");
        btn.remove();

        let cancellationReason = document.querySelector("#cancellation_reason");
        cancellationReason.setAttribute("readonly", true);
        btn = document.querySelector("#cancellation_reason_btn");
        btn.remove();
    } catch (err) {
        console.error("VisitAdminPage::onChosenDateBtn", err);
        PrintError(err);
    }
};

const onMap = (data) => {
    let addressString = data.street + " " + data.buildingNumber + " ";
    if(data.unitNumber !== undefined) {
        addressString += data.unitNumber + " ";
    }
    addressString += data.postcode + " " + data.commune + " " + data.country;

    const params = {
        access_key: '1e6153ae46dfd9f72ecbe2d7ba7faaf8',
        query: addressString,
        limit: 1
    }

    getLatLngAndDisplayMap(params,data);
};

const getLatLngAndDisplayMap = (params,address) => {

    axios.get('http://api.positionstack.com/v1/forward', {params})
        .then(response => {
            if(response.data.data.length === 0){
                document.getElementById("map").innerHTML = `<h3 style="color:red">Adresse introuvable</h3>`;
            } else {
                const latitude = response.data.data[0].latitude;
                const longitude = response.data.data[0].longitude;
                if(latitude === undefined || longitude === undefined){
                    //Parfois la latitude et longitude sont buggées donc je rappelle l'API
                    getLatLngAndDisplayMap(params,address);
                } else {
                    let mymap = leaflet.map("map").setView(new leaflet.LatLng(latitude, longitude), 13);

                    leaflet.tileLayer('https://api.mapbox.com/styles/v1/{id}/tiles/{z}/{x}/{y}?access_token={accessToken}', {
                        attribution: 'Map data &copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors, Imagery © <a href="https://www.mapbox.com/">Mapbox</a>',
                        maxZoom: 18,
                        id: 'mapbox/streets-v11',
                        tileSize: 512,
                        zoomOffset: -1,
                        accessToken: 'pk.eyJ1IjoidmV2ZTE5MDQiLCJhIjoiY2tvMWZuZHBrMHAzZDJ4b2JwZGVybzBwOSJ9.uZvV6shk7iYy4fmJNspb1Q'
                    }).addTo(mymap);

                    leaflet.marker(new leaflet.LatLng(latitude, longitude)).addTo(mymap);
                }
            }

        }).catch(error => {
        console.log(error);
    });
}

const onFurnituresInformation = (resultList) => {
    let furnitureListDiv = document.querySelector('#furnitureList');
    let furnitureListDisplay =
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
    resultList.forEach((element) => {
        furnitureListDisplay +=  `<tr>
                                    <td><a id="furniture${element.id}" href="" target="_blank">${element.description}</a></td>
                                    <td>${element.type.name}</td>
                                    <td>${element.condition}</td>
                                </tr>`;
    });
    furnitureListDisplay += `</tbody></table></div>`;
    furnitureListDiv.innerHTML = furnitureListDisplay;

    resultList.forEach((element) => {
        let furnitureElement = document.getElementById("furniture"+element.id);
        furnitureElement.addEventListener("click", (e) => {
            e.preventDefault();
            RedirectUrl("/furnitureAdmin",element,"?id="+element.id);
        });
    });
};

export default VisitAdminPage;