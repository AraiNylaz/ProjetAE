import {callAPI} from "../utils/api.js";
import { getUserSessionData } from "../utils/session.js";
import PrintError from "./PrintError.js";
const API_BASE_URL = "/api/visit/";


let myVisitPage = `
<h4 id="pageTitle">Ma demande de visite</h4>
<div id="visitErrorNull"></div>
<div class="col-12">
    <div class="form-group">
        <div class="row">
            <div class="col-12">
                <div id="infos"></div>
            </div>
        </div>
        <div class="row">
            <div class="col-12">
                <div id="furnitureList"></div>
            </div>
        </div>
    </div>
</div>`;


const MyVisitPage = async (f) => {
    let page = document.querySelector("#page");
    page.innerHTML = myVisitPage;

    let queryString = window.location.search;
    let urlParams = new URLSearchParams(queryString);
    let id = urlParams.get("id");
    let user = getUserSessionData();
    if(f === undefined || f === null){
        try {
            f = await callAPI(API_BASE_URL + "visit/" + id, "GET", user.token);
        } catch (err) {
            console.error("MyVisitPage::getVisitRequest", err);
        }
    }
    if(f === undefined || f === null){
        document.getElementById("visitErrorNull").innerHTML = `<h6 style="color:red">La ressource n'existe pas ou vous n'y avez pas accès</h6>`;
    } else {
        onVisitInformation(f);
    }

};

const onVisitInformation = (data) => {
    let requestDate = new Date(data.requestDate);
    let visitInformation = `
    <div class="form-group">
        <div class="row">
            <div class="col-6">
                <label for="request_date">Date de la demande</label>
                <input type="text" class="form-control" id="request_date" placeholder="${requestDate.getDate() + "/" + (requestDate.getMonth()+1) + "/" + requestDate.getFullYear()}" readonly>
            </div>
            <div class="col-6">
                <label for="status">Statut de la demande</label>
                <input type="text" class="form-control" id="status" placeholder="${data.status}" readonly>
            </div>
        </div>
        <div class="row">
            <div class="col-6">
                <label for="time_slot">Plage horaire</label>
                <input type="text" class="form-control" id="time_slot" placeholder="${data.timeSlot}" readonly>
            </div>
            <div class="col-6">`;
    if(data.chosenDateTime !== undefined) {
        let chosenDateTime = new Date(data.chosenDateTime);
        visitInformation += `<label for="chosen_date_time">Date de la visite</label>
                             <input type="text" class="form-control" id="chosen_date_time" placeholder="${chosenDateTime.getDate() + "/" + (chosenDateTime.getMonth()+1) + "/" + chosenDateTime.getFullYear()}" readonly>`;
    } else if(data.cancellationReason !== undefined) {
        visitInformation += `<label for="cancellation_reason">Raison de l'annulation</label>
                             <input type="text" class="form-control" id="cancellation_reason" placeholder="${data.cancellationReason}" readonly>`;
    }
    visitInformation += `</div>`;
    let infosDiv = document.querySelector("#infos");
    infosDiv.innerHTML = visitInformation;
};

export default MyVisitPage;