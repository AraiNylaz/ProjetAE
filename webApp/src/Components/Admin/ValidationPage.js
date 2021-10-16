import { getUserSessionData } from "../../utils/session.js";
import { callAPI,callAPIWithoutJSONResponse } from "../../utils/api.js";
import PrintError from "../PrintError.js";
const API_BASE_URL = "/api/admin/";
const axios = require('axios');
const leaflet = require('leaflet');
let validationPage;
let page = document.querySelector("#page");

const ValidationPage = async () => {
    validationPage = `<h4 id="pageTitle">Validation</h4>`;  
    page.innerHTML  = validationPage;
    const user = getUserSessionData();

    try {
        const users = await callAPI(API_BASE_URL + "validate", "GET", user.token);
        onUnvalidatedUsers(users);
    } catch (err) {
        console.error("ValidationPage::onUnvalidatedUsers", err);
        PrintError(err);
    }
};

const onUnvalidatedUsers = (users) => {

    users.forEach((user) => {
        let address = user.address;
        let date = new Date(user.registrationDate);
        let unitNumber = "";
        if(address.unitNumber != undefined)
            unitNumber = address.unitNumber;
        validationPage += `
        <div id="user${user.id}">
            <div class="row">
                <div class="col-3">
                    <p>Pseudo : ${user.username}</p>
                    <p>Nom : ${user.lastName}</p>
                    <p>Prénom : ${user.firstName}</p>
                    <p>Email : ${user.email}</p>
                    <p>Date d'inscription : ${date.getDate() + "/" + (date.getMonth()+1) + "/" + date.getFullYear()}</p>
                    <select id="selectUserType${user.id}" class="form-select" aria-label="Default select example">
                        <option selected>Chosir type utilisateur</option>
                        <option value="client">Client</option>
                        <option value="antiquaire">Antiquaire</option>
                        <option value="admin">Administrateur</option>
                    </select>
                    <button class="btn btn-primary" id="buttonValidation${user.id}">Valider</button>
                    <div id="messageBoard${user.id}"></div>
                </div>
                <div class="col-3">
                    <div id="address${address.id}" name="${address.street} ${address.buildingNumber} ${address.unitNumber} ${address.postcode} ${address.commune} ${address.country}"></div>
                    <p>Rue : ${address.street}</p>
                    <p>Numéro : ${address.buildingNumber}</p>
                    <p>Boite : ${address.unitNumber}</p>
                    <p>Code postal : ${address.postcode}</p>
                    <p>Commune : ${address.commune}</p>
                    <p>Pays : ${address.country}</p>
                </div>
                <div class="col-6">
                    <div class="map" id="mapid${address.id}"></div>
                </div>
            </div>
        </div>
        <hr/>`;
    });
    
    page.innerHTML = validationPage;
    users.forEach((user) => {
        let address = user.address;
        //Manière abjecte de récupérer l'adresse mais pas le choix car j'ai pas réussi à unescape le javascript
        let addressString = document.getElementById("address"+address.id).getAttribute("name");
        const params = {
            key: 'SZOA4k5qE7c0bWx4YkeuejhMjpdjwOMm',
            location: addressString,
            maxResults: 1
        }

        getLatLngAndDisplayMap(params,address);

        let buttonValidation = document.getElementById("buttonValidation"+user.id);
        buttonValidation.addEventListener("click", async () => {

            let selectUserType = document.getElementById("selectUserType"+user.id).value;
            if(selectUserType == "Chosir type utilisateur"){
                document.getElementById("messageBoard"+user.id).innerHTML = `
                <h6 style="color:red">Vous devez choisir sélectionner un type utilisateur</h6>`;
            } else {
                let unvalidatedUser = {
                    id: user.id,
                    type: selectUserType
                };
                user = getUserSessionData();

                try {
                    await callAPIWithoutJSONResponse(
                      API_BASE_URL + "validate",
                      "PUT",
                      user.token,
                      unvalidatedUser
                    );
                    onValidate(unvalidatedUser.id);
                  } catch (err) {
                    console.error("ValidationPage::onValidate", err);
                    PrintError(err);
                  }
            }
        });
    });

}

const getLatLngAndDisplayMap = (params,address) => {
    axios.get('http://www.mapquestapi.com/geocoding/v1/address',{params})
    .then(response => {
        const latitude = response.data.results[0].locations[0].latLng.lat;
        const longitude = response.data.results[0].locations[0].latLng.lng;
        if(latitude == undefined || longitude == undefined){
            //Parfois la latitude et longitude sont buggées donc je rappelle l'API
            getLatLngAndDisplayMap(params,address);
        } else {
            let mymap = leaflet.map('mapid'+address.id).setView(new leaflet.LatLng(latitude, longitude), 13);

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
    }).catch(error => {
      console.log(error);
    });
}

const onValidate = (userId) => {
    document.getElementById("user"+userId).innerHTML = `<h3 style="color:green">L'utilisateur a bien été validé</h3>`;
}

export default ValidationPage;