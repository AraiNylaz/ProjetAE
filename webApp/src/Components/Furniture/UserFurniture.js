import { RedirectUrl } from "../Router.js";
import { getUserSessionData } from "../../utils/session.js";
import {callAPI} from "../../utils/api.js";
import PrintError from "../PrintError.js"
const API_BASE_URL = "/api/furniture/";
const IMAGES = "http://localhost:8080/images/";

let furniture;

let furniturePage = `
<h4 id="pageTitle">Furniture User</h4>
<div class="row">
    <div id="carousel" class="col-6">
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
            <div id="prix"><label for="type">Prix</label></div>
        </div>
      </div>
      <div class="col-6">
          <div class="form-group">
              <label for="condition">Etat</label>
              <div id="condition"></div>
          </div>
      </div>
      <div class="col-6">
        <div class="form-group">
          <label for="dateOfSale">Date de vente</label>
          <div id="dateOfSale"></div>
        </div>
      </div>
      <div class="col-6">
          <div class="form-group">
              <label for="withdrawalDateFromCustomer">Date de retrait</label>
              <div id="withdrawalDateFromCustomer"></div>
          </div>
        </div>
        <div class="col-6">
          <div class="form-group">
              <label for="deliveryDate">Date de livraison</label>
              <div id="deliveryDate"></div>
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
  

</div>
`;



const UserFurniture = async (data) => {
  let page = document.querySelector("#page");
  page.innerHTML = furniturePage;
  const user = getUserSessionData();



  if(data == null){
    let queryString = window.location.search;
    let urlParams = new URLSearchParams(queryString);
    let id = urlParams.get("id");
    try {
      furniture = await callAPI(API_BASE_URL +  "personal/" + id , "GET",user.token);
    } catch (err) {
      console.error("UserFurniture::GetFurnitureByID", err);
      PrintError(err);
    }
  }else{
    furniture = data;
  }

  try {
    const pictures = await callAPI(API_BASE_URL + furniture.id + "/public-pictures-furniture", "GET", user.token);
    if(pictures.length == 0){
      document.querySelector("#carousel").innerHTML = `<div class="alert alert-danger mt-2">Il n'y a pas de photos disponible pour ce meuble.</div>`
    }else{
      onPicturesList(pictures);

    }
  } catch (err) {
    console.error("UserFurniture::onPicturesList", err);
    PrintError(err);
  }

  onFurniture();
}

const onFurniture = () => {
  let type = document.querySelector("#type");
  if(furniture) {
    type.innerHTML = `<input class="form-control" id="type" type="text" placeholder=${furniture.type.name} readonly />`;

    let prix = document.querySelector("#prix");
    if (furniture.purchasePrice) {
      prix.innerHTML = `<label for="prix">Prix d'achat</label> <input class="form-control" id="prix" type="text" placeholder=${furniture.purchasePrice} readonly />`;
    } else
      prix.innerHTML = `<label for="prix">Prix de vente</label> <input class="form-control" id="prix" type="text" placeholder=${furniture.sellingPrice} readonly />`;
    let furnitureDescription = document.querySelector("#furnitureDescription");
    furnitureDescription.innerHTML = `<textarea class="form-control" id="furnituredescription" rows="6" readonly>${furniture.description}</textarea>`;

    let deliveryDate = document.querySelector("#deliveryDate");
    if (furniture.deliveryDate) {
      let date = new Date(furniture.deliveryDate);
      deliveryDate.innerHTML = `<input class="form-control" id="inputDeliveryDate" type="date" value="${date.getDate() + "/" + (date.getMonth()+1) + "/" + date.getFullYear()}" readonly/>`;
    } else
      deliveryDate.innerHTML = `<input class="form-control" id="inputDeliveryDate" type="date" readonly/>`;

    let withdrawalDateFromCustomer = document.querySelector("#withdrawalDateFromCustomer");
    if (furniture.withdrawalDateFromCustomer) {
      let date = new Date(furniture.withdrawalDateFromCustomer);
      withdrawalDateFromCustomer.innerHTML = `<input class="form-control" id="inputWithdrawalDateFromCustomer" type="date" value="${date.getDate() + "/" + (date.getMonth()+1) + "/" + date.getFullYear()}" readonly/>`;
    }
    else
      withdrawalDateFromCustomer.innerHTML = `<input class="form-control" id="inputWithdrawalDateFromCustomer" type="date" readonly/>`;
  let dateOfSale = document.querySelector("#dateOfSale");
  if(furniture.sellingDate){
    let date = new Date(furniture.sellingDate);
    dateOfSale.innerHTML = `<input class="form-control" id="dateOfSale" type="date" value="${date.getDate() + "/" + (date.getMonth()+1) + "/" + date.getFullYear()}" readonly/>`;
  }else
    dateOfSale.innerHTML = `<input class="form-control" id="dateOfSale" type="date" readonly/>`;
  let condition = document.querySelector("#condition");
  condition.innerHTML = `<input class="form-control" id="condition" type="text" placeholder=${furniture.condition} readonly />`;
  }
  let btnReturn = document.querySelector("#btnReturn");
  btnReturn.addEventListener("click", () => RedirectUrl("/myfurnitures"));
}


const onPicturesList = (picturesList) => {
  let carousel = `
  <div id="carouselPictures" class="carousel slide" data-ride="carousel"><ol class="carousel-indicators">`;
  for (let i = 0; i < picturesList.length; i++) {
    if(i== 0){
      carousel += `<li data-target="#carouselPictures" data-slide-to="${i}" class="active"></li>`
    }else{
      carousel += `<li data-target="#carouselPictures" data-slide-to="${i}"></li>`;
    }
  }
  carousel += `</ol> <div class="carousel-inner">`;

  let counter = 0;
  picturesList.forEach(picture => {
    if(counter == 0){
      carousel += `<div class="carousel-item active"> 
      <img id="carouselFurnitureAdmin" src="${IMAGES}${picture.name}" class="d-block w-100" alt="${counter}">
      </div>`;
    }else{
      carousel += `<div class="carousel-item"> 
      <img id="carouselFurnitureAdmin" src="${IMAGES}${picture.name}" class="d-block w-100" alt="${counter}">
      </div>`;
    }
      counter ++;
  });

carousel += 
  `
  <a class="carousel-control-prev" href="#carouselPictures" role="button" data-slide="prev">
    <span class="carousel-control-prev-icon" aria-hidden="true"></span>
    <span class="sr-only">Previous</span>
  </a>
  <a class="carousel-control-next" href="#carouselPictures" role="button" data-slide="next">
    <span class="carousel-control-next-icon" aria-hidden="true"></span>
    <span class="sr-only">Next</span>
  </a>
  </div>
  </div>
  <button class="btn btn-secondary" id="btnReturn">Retour</button>
  `;
  document.querySelector("#carousel").innerHTML = carousel;
  onModifyPicture();
}

const onModifyPicture = () => {
  let btnReturn = document.querySelector("#btnReturn");
  btnReturn.addEventListener("click", () => RedirectUrl("/search"));
}







export default UserFurniture;



