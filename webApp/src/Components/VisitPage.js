import { RedirectUrl } from "./Router.js";
import Navbar from "./Navbar.js";
import { getUserSessionData } from "../utils/session.js";
import { callAPI, callAPIFormData} from "../utils/api.js";
import PrintError from "./PrintError.js"
import htmlDecode from "../utils/text.js";
const API_BASE_URL = "/api/visit/";
const API_BASE_URL_FURNITURE = "/api/furniture/";

let furnitureList = [];
let picturesList = [];
let fd = new FormData();

let addressForm = `
<div class="row">
    <h5 class="col text-center">Adresse du lieu</h5>
</div>
<div class="row">
    <small class="col-sm-8">(Veuillez entrer votre adresse complète)</small>
    <small class="col-sm-4 text-right">*champs obligatoires</small>
</div>
<div class="row">
    <div class="form-group col">
        <label for="street">Rue*</label>
        <input class="form-control" id="street" type="text" placeholder="Entrez votre rue" required="" />
    </div>
</div>
<div class="row">
    <div class="form-group col-sm-12 col-md-6 col-lg-4">
        <label for="buildingnumber">Numéro*</label>
        <input class="form-control" id="buildingnumber" type="text" placeholder="Entrez votre numéro" required="" />
    </div>
    <div class="form-group col-sm-12 col-md-6 col-lg-4">
        <label for="unitnumber">Boite</label>
        <input class="form-control" id="unitnumber" type="text" placeholder="Entrez votre boite" />
    </div>
    <div class="form-group col-sm-12 col-md-6 col-lg-4">
        <label for="postcode">Code postal*</label>
        <input class="form-control" id="postcode" type="text" placeholder="Entrez votre code postal" required="" />
    </div>
</div>
<div class="row">
    <div class="form-group col-sm-12 col-md-6">
        <label for="commune">Commune*</label>
        <input class="form-control" id="commune" type="text" placeholder="Entrez votre commune" required="" />
    </div>
    <div class="form-group col-sm-12 col-md-6">
        <label for="country">Pays*</label>
        <input class="form-control" id="country" type="text" placeholder="Entrez votre pays" required="" />
    </div>
</div>
`;

let visitPage = `
<h4 id="pageTitle">Ajouter Demande visite</h4>
<div class="row">
    <div class="col">
        <form id="visitRequestForm">
            <div id="admin" class="form-group"></div>
            <div id="addressForm"></div>
            <div class="form-group">
                <label for="timeslot">Plage horaire*</label>
                <textarea class="form-control" id="timeslot" rows="3"></textarea>
            </div>
            <button class="btn btn-primary col-6" id="btnVisitRequest" type="submit">Introduire ma demande</button>
        </form>
    </div>
    <div class="col">
        <form id="furnitureForm">
            <div class="row">
                <div class="form-group col">
                    <label for="furnituredescription">Description du meuble*</label>
                    <textarea class="form-control" id="furnituredescription" rows="6"></textarea>
                </div>
            </div>
            <div id="typesList" class="row"></div>
            <button class="btn btn-primary col-6" id="btnaddfurniture" type="submit">Ajouter le meuble</button>
        </form>
        <form id="pictureForm">
            <div class="row">
                <div class="form-group col">
                    <label for="file">Vous devez choisir au moins une photo de votre meuble*</label>
                    <input class="form-control-file" id="file" type="file" multiple />
                </div>
            </div>
            <button class="btn btn-primary col-6" id="btnaddpicture" type="submit">Ajouter la photo</button>
        </form>
    </div>
</div>
<div class="row">
    <div class="col">
        <p>Liste meubles :</p>
        <div id="furnitureListDiv"></div>
    </div>
    <div class="col">
        <p>Liste photos :</p>
        <div id="picturesListDiv"></div>
    </div>
</div>
`;

const VisitPage = async () => {

    let page = document.querySelector("#page");
    page.innerHTML  = visitPage;

    document.getElementById("addressForm").innerHTML = addressForm;

    let visitRequestForm = document.getElementById("visitRequestForm");
    visitRequestForm.addEventListener("submit", onVisitRequest);
    let furnitureForm = document.getElementById("furnitureForm");
    furnitureForm.addEventListener("submit", onFurniture);

    const user = getUserSessionData();
    if(user.user.userType === "admin") {
        let userDiv = document.getElementById("admin");
        userDiv.innerHTML = `<input class="form-control" id="email" type="text" placeholder="Entrez l'email de l'utilisateur" required="" />
                             <input class="form-check-inline" type="checkbox" id="home_visit" name="home_visit"><label class="form-check-label" for="home_visit">Visite chez le client</label>`;
        document.getElementById("home_visit").addEventListener("change", onChangeCheckbox);
    } else {
        try {
            const address = await callAPI(API_BASE_URL + "address/" + user.user.id, "GET", undefined);
            onAddress(address);
        } catch (err) {
            console.error("VisitPage::onAddress", err);
            PrintError(err);
        }
    }

    try {
        const types = await callAPI(API_BASE_URL_FURNITURE + "allFurnitureTypes", "GET", undefined);
        onTypesList(types);
    } catch (err) {
        console.error("VisitPage::onTypesList", err);
        PrintError(err);
    }
    let pictureForm = document.getElementById("pictureForm");
    pictureForm.addEventListener("submit", onPicture);
}

const onChangeCheckbox = async () => {
    if(document.getElementById("home_visit").checked) {
        document.getElementById("addressForm").innerHTML = "";
    } else {
        document.getElementById("addressForm").innerHTML = addressForm;
    }
};

const onAddress = (address) => {
    document.getElementById("street").value = htmlDecode(address.street);
    document.getElementById("buildingnumber").value = address.buildingNumber;
    if(address.unitNumber != undefined)
        document.getElementById("unitnumber").value = address.unitNumber;
    document.getElementById("postcode").value = address.postcode;
    document.getElementById("commune").value = htmlDecode(address.commune);
    document.getElementById("country").value = htmlDecode(address.country);
}

const onTypesList = (typesList) => {
    let typesListPage = `
    <div class="form-group col">
    <label for="furnituretype">Sélectionner type de meuble*</label>
    <select class="form-control" id="furnituretype">`;
      typesList.forEach(type => {
        typesListPage += `<option id="${type.id}" value="${type.id}">${type.name}</option>`;
      });
    
    typesListPage += `
    </select>
      </div>`;
  
  document.getElementById("typesList").innerHTML = typesListPage;
}
  
const onVisitRequest = async (e) => {
    e.preventDefault();
    const user = getUserSessionData();
    let address;
    if(user.user.userType === "admin"){
        let email = document.getElementById("email").value;
        console.log(email);
        console.log(JSON.stringify(email));
        fd.append("email", email);
        fd.append("home_visit", JSON.stringify(document.getElementById("home_visit").checked));
        if(!document.getElementById("home_visit").checked) {
            let unitNumber = null;
            if(document.getElementById("unitnumber").value != ""){
                unitNumber = document.getElementById("unitnumber").value;
            }

            address = {
                street: document.getElementById("street").value,
                buildingNumber: document.getElementById("buildingnumber").value,
                unitNumber: unitNumber,
                postcode: document.getElementById("postcode").value,
                commune: document.getElementById("commune").value,
                country: document.getElementById("country").value,
            };
        }
    } else {
        let unitNumber = null;
        if(document.getElementById("unitnumber").value != ""){
            unitNumber = document.getElementById("unitnumber").value;
        }

        address = {
            street: document.getElementById("street").value,
            buildingNumber: document.getElementById("buildingnumber").value,
            unitNumber: unitNumber,
            postcode: document.getElementById("postcode").value,
            commune: document.getElementById("commune").value,
            country: document.getElementById("country").value,
        };
    }

    let visitRequest = {
        address: address,
        timeSlot: document.getElementById("timeslot").value,
        furnitureList: furnitureList
    };

    console.log(visitRequest);
    fd.append("json", JSON.stringify(visitRequest));

    for(let i=0; i<visitRequest.furnitureList.length; i++){
        for(let j=0; j<visitRequest.furnitureList[i].picturesList.length; j++){
            fd.append("file"+i,visitRequest.furnitureList[i].picturesList[j]);
        }
    }
    try {
        if(user.user.userType === "admin") {
            const visitRequested = await callAPIFormData(
                API_BASE_URL + "addforother",
                "POST",
                user.token,
                fd
            );
        } else {
            const visitRequested = await callAPIFormData(
                API_BASE_URL + "add",
                "POST",
                user.token,
                fd
            );
        }
        onVisitRequestAdded();
      } catch (err) {
        console.error("VisitPage::onVisitRequestAdded", err);
        PrintError(err);
      }
      fd = new FormData();
}

const onFurniture = (e) => {
    e.preventDefault();

    let type = {
        id:document.getElementById("furnituretype").value,
    };
    let furniture = {
        description: document.getElementById("furnituredescription").value,
        type: type,
        picturesList: picturesList,
    };
    furnitureList.push(furniture);
    document.getElementById("furnitureForm").reset();
    picturesList = [];
    document.getElementById("picturesListDiv").innerHTML = ``;
    onDeleteFurniture();
    console.log(furniture);
}

const onDeleteFurniture = () => {
    let furnitureListHtml = ``;
    let index = 0;
    furnitureList.forEach(element => {
        furnitureListHtml += `
        <div id="furniture${index}">
            <div class="row">
                <div class="col-6">
                    <p>${element.description}</p>
                </div>
                <div class="col-6">
                    <button class="btn btn-danger" id="btnDeleteFurniture${index}">Supprimer meuble</button>
                </div>
            </div>
        </div>`;
        index++;    
    });
    document.getElementById("furnitureListDiv").innerHTML = furnitureListHtml;
    index = 0;
    furnitureList.forEach(() => {
        let buttonDeleteFurniture = document.getElementById("btnDeleteFurniture"+index);
        buttonDeleteFurniture.addEventListener("click", (e) => {
            const id = e.currentTarget.id.substring(18);
            furnitureList.splice(id,1);
            onDeleteFurniture();
        });
        index++;
    });
}

const onPicture = (e) => {
    e.preventDefault();
    let files = document.getElementById("file").files;
    for(let i=0; i<files.length; i++){
        picturesList.push(files[i]);
    }
    onDeletePicture();
    document.getElementById("pictureForm").reset();
}

const onDeletePicture = () => {
    let picturesListHtml = ``;
    let index = 0;
    picturesList.forEach(() => {
        picturesListHtml += `
            <div class="row">
                <div class="col-6">
                    <img id="picture${index}" style="width: 100px;height:100px"/>
                </div>
                <div class="col-6">
                    <button class="btn btn-danger" id="btnDeletePicture${index}">Supprimer photo</button>
                </div>
            </div>`;
        index++;
    });
    document.getElementById("picturesListDiv").innerHTML = picturesListHtml;
    index = 0;
    picturesList.forEach((element) => {
        loadFile(element,index);
        let buttonDeletePicture = document.getElementById("btnDeletePicture"+index);
        buttonDeletePicture.addEventListener("click", (e) => {
            const id = e.currentTarget.id.substring(16);
            picturesList.splice(id,1);
            onDeletePicture();
        });
        index++;
    });
};

const loadFile = (file,index) => {
    var output = document.getElementById("picture"+index);
    output.src = URL.createObjectURL(file);
    output.onload = function() {
      URL.revokeObjectURL(output.src) // free memory
    }
  };

const onVisitRequestAdded = () => {
    Navbar();
    RedirectUrl("/");
}

export default VisitPage;