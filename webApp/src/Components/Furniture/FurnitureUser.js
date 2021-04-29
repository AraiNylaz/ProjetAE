import { RedirectUrl } from "../Router.js";
import { getUserSessionData } from "../../utils/session.js";
import {callAPI, callAPIWithoutJSONResponse} from "../../utils/api.js";
import PrintError from "../PrintError.js"
import img1 from "./1.jpg";
import img2 from "./2.jpg";
const API_BASE_URL = "/api/furniture/";

let furniture;

let furniturePage = `
<h4 id="pageTitle">Furniture User</h4>
<div class="row">
  <div class="col-6">
  <div id="carouselExampleIndicators" class="carousel slide" data-ride="carousel">
  <ol class="carousel-indicators">
    <li data-target="#carouselExampleIndicators" data-slide-to="0" class="active"></li>
    <li data-target="#carouselExampleIndicators" data-slide-to="1"></li>
    <li data-target="#carouselExampleIndicators" data-slide-to="2"></li>
  </ol>
  <div class="carousel-inner">
    <div class="carousel-item active">
      <img src="${img1}" class="d-block w-100" alt="1">
    </div>
    <div class="carousel-item">
      <img src="${img2}" class="d-block w-100" alt="2">
    </div>
    <div class="carousel-item">
      <img src="${img2}" class="d-block w-100" alt="3">
    </div>
  </div>
  <a class="carousel-control-prev" href="#carouselExampleIndicators" role="button" data-slide="prev">
    <span class="carousel-control-prev-icon" aria-hidden="true"></span>
    <span class="sr-only">Previous</span>
  </a>
  <a class="carousel-control-next" href="#carouselExampleIndicators" role="button" data-slide="next">
    <span class="carousel-control-next-icon" aria-hidden="true"></span>
    <span class="sr-only">Next</span>
  </a>
  </div>
  </br>
  <button class="btn btn-secondary" id="btnReturn">Retour</button>
</div>

<div class="col-6">
  <div class="form-group">
    <div class="row">
      <div class="col-6">
          <div class="form-group">
            <label for="type">Type de meuble</label>
            <div id="type"></div>
          </div>
      </div>
      <div class="col-6">
        <div class="form-group">
            <label for="prix">Prix de vente</label>
            <div id="prix"></div>
        </div>
      </div>
    </div>
  </div>

  <div class="row">
    <div class="col-12">
      <div class="form-group">
        <label for="furnituredescription">Description du meuble</label>
        <div id="furnitureDescription"></div>
      </div>
    </div>
  </div>
  
  <div id=option></div>

</div>
`;

let validateOption = `<p>Mettre une option sur ce meuble</p>
<div class="row">
  <div class="col-12">
    <div class="row">
      Délai de l'option (max. 5 jours cumulés) :
      <div class="col-2">
          <select class="custom-select custom-select-sm" id="dureeOption">
            <option value="1">1</option>
            <option value="2">2</option>
            <option value="3">3</option>
            <option value="4">4</option>
            <option value="5">5</option>
          </select> 
      </div>    
      <button class="btn btn-success" id="btnOption" type="submit">Valider</button>
    </div>
  </div>
</div>`;

let cancelOption = `
<div class="row">
  <div class="col-12">
      <p>Vous avez une option sur ce meuble.</p>
      <p>Il vous reste <strong><span id="daysOptionLeft"> </span></strong> jours.
      <button class="btn btn-danger" id="btnOption" type="submit">Annuler l'option</button>
      </p>
  </div>
</div>`;


const FurnitureUser = async (data) => {
  let page = document.querySelector("#page");
  page.innerHTML = furniturePage;

  let btnReturn = document.querySelector("#btnReturn");
  btnReturn.addEventListener("click", () => RedirectUrl("/search"));

  if(data == null){
    let queryString = window.location.search;
    let urlParams = new URLSearchParams(queryString);
    let id = urlParams.get("id");
    try {
      furniture = await callAPI(API_BASE_URL + id , "GET",undefined);
    } catch (err) {
      console.error("FurnitureUser::GetFurnitureByID", err);
      PrintError(err);
    }
  }else{
    furniture = data;
  }
  onFurniture();
}

const onFurniture = () => {

  let type = document.querySelector("#type");
  type.innerHTML = `<input class="form-control" id="type" type="text" placeholder=${furniture.type.name} readonly />`;
  let prix = document.querySelector("#prix");
  prix.innerHTML = `<input class="form-control" id="prix" type="text" placeholder=${furniture.sellingPrice} readonly />`;
  let furnitureDescription = document.querySelector("#furnitureDescription");
  furnitureDescription.innerHTML = `<textarea class="form-control" id="furnituredescription" rows="6" readonly>${furniture.description}</textarea>`;

  onCheckOption();
  
}

const onCheckOption = async () => {

  if (!furniture) return;
  let optionDocument = document.querySelector("#option");
  let option = await callAPI(API_BASE_URL + furniture.id + "/option", "GET");
  let user = getUserSessionData();
  console.log(user)
  if(option.status != undefined && option.status == "en_cours") {
    if(option.buyer.id == user.user.id) {
      //Afficher bouton annuler
      optionDocument.innerHTML = cancelOption;
      document.querySelector("#daysOptionLeft").innerHTML = option.duration;
      let btn = document.querySelector("#btnOption")
      btn.addEventListener("click", onClickCancelOption);
    } else {
      //Afficher qu'il y a une option
      optionDocument.innerHTML = "Il y a déjà une option sur ce meuble";
    }
  } else {
    if(user != undefined) {
      //Afficher form prendre option
      optionDocument.innerHTML = validateOption;
      let btn = document.querySelector("#btnOption")
      btn.addEventListener("click", onClickOption);
    }
  }
}


const onClickOption = async (e) => {
  e.preventDefault();
  let user = getUserSessionData();
  let optionDocument = document.querySelector("#option");
  try {
    let optionChoice = document.getElementById("dureeOption");
    optionChoice = optionChoice.value;
    let apiOption = await callAPIWithoutJSONResponse(API_BASE_URL + furniture.id + "/option/", "POST", user.token, {duration: optionChoice});
    optionDocument.innerHTML = cancelOption;
    let btn = document.querySelector("#btnOption")
    btn.addEventListener("click", onClickCancelOption);
  } catch (e) {
    console.log("erreur");
    console.log(e);
    PrintError(e);
    //Erreur
  }
}

const onClickCancelOption = async (e) => {
  e.preventDefault();
  let user = getUserSessionData();
  let optionDocument = document.querySelector("#option");
  try {
    let apiOption = await callAPIWithoutJSONResponse(API_BASE_URL + furniture.id + "/option", "PUT", user.token);
    optionDocument.innerHTML = validateOption;
    let btn = document.querySelector("#btnOption")
    btn.addEventListener("click", onClickOption);
  } catch (e) {
    console.log("erreur");
    console.log(e);
    PrintError(e);
    //Erreur
  }
}


export default FurnitureUser;



