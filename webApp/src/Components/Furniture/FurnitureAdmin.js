import { RedirectUrl } from "../Router.js";
import { getUserSessionData } from "../../utils/session.js";
import { callAPI, callAPIFormData, callAPIWithoutJSONResponse } from "../../utils/api.js";
import PrintError from "../PrintError.js"

const API_BASE_URL = "/api/furniture/";
const API_BASE_URL_ADMIN = "/api/admin/";
const IMAGES = "http://localhost:8080/images/";

let furniture;
let pictures;

let furniturePage = `
<h4 id="pageTitle">Furniture User</h4>
<div class="row">
    <div class="col-6">
      <div id="carousel"></div>
      <div id="uploadReturn"></div>
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
            <label for="purchasePrice">Prix d'achat</label>
            <div id="purchasePrice"></div>
        </div>
        </div>
        <div class="col-6">
          <div class="form-group">
              <label for="sellerEmail">Email du vendeur</label>
              <div id="sellerEmail"></div>
          </div>
        </div>
        <div class="col-6">
          <div class="form-group">
              <label for="requestDate">Date de la demande de visite</label>
              <div id="requestDate"></div>
          </div>
        </div>
        <div class="col-6">
          <div class="form-group">
              <label for="depositDate">Date de dépot en magasin</label>
              <div id="depositDate"></div>
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
              <label for="prix">Prix de vente</label>
              <div id="prix"></div>
          </div>
        </div>
        <div class="col-6">
          <div class="form-group">
              <label for="specialSalePrice">Prix de vente spécial</label>
              <div id="specialSalePrice"></div>
          </div>
        </div>
        <div class="col-6">
          <div class="form-group">
            <label for="sellingDate">Date de vente</label>
            <div id="sellingDate"></div>
          </div>
        </div>
        <div class="col-6">
          <div class="form-group">
              <label for="deliveryDate">Date de livraison</label>
              <div id="deliveryDate"></div>
          </div>
        </div>
        <div class="col-6">
          <div class="form-group">
              <label for="withdrawalDateToCustomer">Date de retrait par le client</label>
              <div id="withdrawalDateToCustomer"></div>
          </div>
        </div>
        <div class="col-6">
          <div class="form-group">
              <label for="buyerEmail">Email du client</label>
              <div id="buyerEmail"></div>
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
  
  <div class="input-group mb-3">
    <div class="input-group-prepend">
      <label class="input-group-text" for="inputGroupSelect01">Etat du meuble</label>
    </div>
    <div id="conditionsDiv"></div>

  </div>

  <button class="btn btn-success" id="btnSave" type="submit">Enregistrer</button>
  <p>
  <div id=option></div>

  <div id="toast"></div>
  </div>
</div>
`;

let isOption = `
<div class="alert alert-secondary" role="alert">
  Il y a une option sur ce meuble (<strong><span id="userOption"> </span></strong>).
</div>
<button class="btn btn-danger" id="btnOption" type="submit">Annuler l'option</button>
  `;

let noOption =`
<div class="alert alert-secondary alert-dismissible fade show" role="alert">
  Il n'y a aucune option sur ce meuble.
  <button type="button" class="close" data-dismiss="alert" aria-label="Close">
    <span aria-hidden="true">&times;</span>
  </button>
</div>`;

let codeBtnAddScrollingPicture = `<button class="btn btn-primary" id="btnScrollingPicture">Ajouter la photo sur l'accueil</button>`;
let codeBtnRemoveScrollingPicture = `<button class="btn btn-primary" id="btnScrollingPicture">Enlever la photo de l'accueil</button>`;
let codeBtnDeletePicture = `<button class="btn btn-danger" id="btnDeletePicture">Supprimer la photo</button>`;
let codebtnAddFavouritePicture = `<button class="btn btn-warning" id="btnAddFavouritePicture">Ajouter photo favorite</button>`;
let codeBtnAddVisibility = `<button type="button" class="btn btn-dark" id="btnVisibleForEveryone">Rendre la photo publique</button>`;
let codeBtnRemoveVisibility = `<button type="button" class="btn btn-dark" id="btnVisibleForEveryone">Rendre la photo privée</button>`;
let codeUploadPicture = `<div class="col-6"><form id="uploadForm"><input id="file" type="file"/><input type="submit" value="Upload"/></form></div>`
let codeReturnButton = `<div class="col-12"><button class="btn btn-secondary" id="btnReturn">Retour</button></div>`;

const FurnitureAdmin = async(f) => {
  let page = document.querySelector("#page");
  page.innerHTML = furniturePage;

  let btnSave = document.querySelector("#btnSave");
  btnSave.addEventListener("click", onSave);

  const user = getUserSessionData();

  if(f == null){
    let queryString = window.location.search;
    let urlParams = new URLSearchParams(queryString);
    let id = urlParams.get("id");
    if(user && user.user.userType == "admin"){
      try {
        furniture = await callAPI(API_BASE_URL_ADMIN + id , "GET", user.token);
      } catch (err) {
        console.error("FurnitureUser::GetFurnitureByID", err);
        PrintError(err);
      }
    }else {
      try {
        furniture = await callAPI(API_BASE_URL + id , "GET", undefined);
      } catch (err) {
        console.error("FurnitureUser::GetFurnitureByID", err);
        PrintError(err);
      }
    }

  }else{
    furniture = f;
  }

  try {
    pictures = await callAPI(API_BASE_URL + furniture.id + "/all-pictures-furniture", "GET", user.token);
    if(pictures.length == 0){
      document.querySelector("#carousel").innerHTML = `<div class="alert alert-danger mt-2">Il n'y a pas de photos disponible pour ce meuble.</div>`
    }else{
      onPicturesList(pictures);
    }
  } catch (err) {
    console.error("AdminFurniture::onPicturesList", err);
    PrintError(err);
  }


  try {
    const types = await callAPI(API_BASE_URL + "allFurnitureTypes", "GET", undefined);
    onTypesList(types);
  } catch (err) {
    console.error("FurnitureAdmin::onTypesList", err);
    PrintError(err);
  }

  onFurniture();
  onCheckConditions();
  let optionCondition = document.querySelector("#"+furniture.condition);
  optionCondition.setAttribute("selected","");
  

  if(furniture.condition == "en_vente" || furniture.condition == "en_option"){
    onCheckOption();
  }
  document.querySelector("#uploadReturn").innerHTML = codeUploadPicture + codeReturnButton;
  let btnReturn = document.querySelector("#btnReturn");
  btnReturn.addEventListener("click", () => RedirectUrl("/search"));
  let btnAddPicture = document.querySelector("form");
  btnAddPicture.addEventListener("submit", onAddPicture);
}

const onTypesList = (typesList) => {
  let typesListPage = `
  <div class="input-group mb-3">
    <select class="custom-select" id="typesList">`;
    typesList.forEach(type => {
      if(furniture.type.id == type.id)
        typesListPage += `<option id="${type.id}" selected="selected" value="${type.id}">${type.name}</option>`;
      else
        typesListPage += `<option id="${type.id}" value="${type.id}">${type.name}</option>`;
    });
  
  typesListPage += 
    `</select>
  </div>`;

document.getElementById("type").innerHTML = typesListPage;
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

    <div id="btnModifyPicture"></div>
    
    
    `;
    document.querySelector("#carousel").innerHTML = carousel;
    onModifyPicture();
    $("#carouselPictures").on("slid.bs.carousel",onModifyPicture);
}





const onFurniture = async() => {
  let prix = document.querySelector("#prix");
  prix.innerHTML = `<input class="form-control" id="inputSellingPrice" type="number" placeholder=${furniture.sellingPrice} readonly />`;

  let specialSalePrice = document.querySelector("#specialSalePrice");
  specialSalePrice.innerHTML = `<input class="form-control" id="inputSpecialSalePrice" type="number" placeholder=${furniture.specialSalePrice} readonly />`;

  let furnitureDescription = document.querySelector("#furnitureDescription");
  furnitureDescription.innerHTML = `<textarea class="form-control" id="furnituredescription" rows="6" >${furniture.description}</textarea>`;
  let purchasePrice = document.querySelector("#purchasePrice");
  purchasePrice.innerHTML = `<input class="form-control" id="inputPurchasePrice" type="number" placeholder=${furniture.purchasePrice} readonly />`;

  let withdrawalDateFromCustomer = document.querySelector("#withdrawalDateFromCustomer");
  if(furniture.withdrawalDateFromCustomer){
    let date = new Date(furniture.withdrawalDateFromCustomer);
    withdrawalDateFromCustomer.innerHTML = `<input class="form-control" id="inputWithdrawalDateFromCustomer" type="date" value="${date.getFullYear() + "-"+ ("0" + (date.getMonth() + 1)).slice(-2) + "-" + ("0" + date.getDate()).slice(-2)}" readonly/>`;
  }
  else
    withdrawalDateFromCustomer.innerHTML = `<input class="form-control" id="inputWithdrawalDateFromCustomer" type="date" readonly/>`;

  let deliveryDate = document.querySelector("#deliveryDate");
  if(furniture.deliveryDate) {
    let date = new Date(furniture.deliveryDate);
    deliveryDate.innerHTML = `<input class="form-control" id="inputDeliveryDate" type="date" value="${date.getFullYear() + "-"+ ("0" + (date.getMonth() + 1)).slice(-2) + "-" + ("0" + date.getDate()).slice(-2)}" readonly/>`;
  }
  else
    deliveryDate.innerHTML = `<input class="form-control" id="inputDeliveryDate" type="date" readonly/>`;

  let withdrawalDateToCustomer = document.querySelector("#withdrawalDateToCustomer");
  if(furniture.withdrawalDateToCustomer){
    let date = new Date(furniture.withdrawalDateToCustomer)
    withdrawalDateToCustomer.innerHTML = `<input class="form-control" id="inputWithdrawalDateToCustomer" type="date" value="${date.getFullYear() + "-"+ ("0" + (date.getMonth() + 1)).slice(-2) + "-" + ("0" + date.getDate()).slice(-2)}" readonly/>`;
  }
  else
    withdrawalDateToCustomer.innerHTML = `<input class="form-control" id="inputWithdrawalDateToCustomer" type="date" readonly/>`;

  let buyerEmail = document.querySelector("#buyerEmail");
  if(furniture.buyer && furniture.buyer.email){
    let buyer;
    let user = getUserSessionData();
    try {
      buyer = await callAPI(API_BASE_URL_ADMIN + "user/" + furniture.buyer.id , "GET", user.token);
    } catch (err) {
      console.error(err);
      PrintError(err);
  }
  buyerEmail.innerHTML = `<button class="form-control" id="inputBuyerEmail" type="button" class="btn btn-secondary">${furniture.buyer.email}</button>`
  document.querySelector("#inputBuyerEmail").addEventListener("click",()=> {
    RedirectUrl("/userAdmin",buyer,"?id="+buyer.id);
  })
  }
  else if(furniture.unregisteredBuyerEmail)
    buyerEmail.innerHTML = `<input class="form-control" id="inputBuyerEmail" type="email" value="${furniture.unregisteredBuyerEmail}" readonly/>`;
  else
    buyerEmail.innerHTML = `<input class="form-control" id="inputBuyerEmail" type="email" readonly/>`;

  let requestDate = document.querySelector("#requestDate");
  if(furniture.visitRequest && furniture.visitRequest.requestDate){
    let date = new Date(furniture.visitRequest.requestDate);
    requestDate.innerHTML = `<input class="form-control" id="inputRequestDate" type="date" value="${date.getFullYear() + "-"+ ("0" + (date.getMonth() + 1)).slice(-2) + "-" + ("0" + date.getDate()).slice(-2)}" readonly/>`;
  }else
    requestDate.innerHTML = `<input class="form-control" id="inputRequestDate" type="date" readonly/>`;
  let sellerEmail = document.querySelector("#sellerEmail");
  if(furniture.visitRequest && furniture.visitRequest.customer.email){
    sellerEmail.innerHTML = `<input class="form-control" id="inputSellerEmail" type="text" value="${furniture.visitRequest.customer.email}" readonly/>`;
  }else
    sellerEmail.innerHTML = `<input class="form-control" id="inputSellerEmail" type="text" readonly/>`;
    
  let depositDate = document.querySelector("#depositDate");
  if(furniture.depositDate){
    let date = new Date(furniture.depositDate);
    depositDate.innerHTML = `<input class="form-control" id="inputDepositDate" type="date" value="${date.getFullYear() + "-"+ ("0" + (date.getMonth() + 1)).slice(-2) + "-" + ("0" + date.getDate()).slice(-2)}" readonly/>`;
  }else
    depositDate.innerHTML = `<input class="form-control" id="inputDepositDate" type="date" readonly/>`;

  let sellingDate = document.querySelector("#sellingDate");
  if(furniture.sellingDate){
    let date = new Date(furniture.sellingDate);
    sellingDate.innerHTML = `<input class="form-control" id="inputSellingDate" type="date" value="${date.getFullYear() + "-"+ ("0" + (date.getMonth() + 1)).slice(-2) + "-" + ("0" + date.getDate()).slice(-2)}" readonly/>`;
  }else
  sellingDate.innerHTML = `<input class="form-control" id="inputSellingDate" type="date" readonly/>`;


}

const onSale = () => {
  let price = document.querySelector("#prix");
  price.innerHTML = `<input class="form-control" id="inputSellingPrice" type="number" value=${furniture.sellingPrice} />`;

  /*let specialSalePrice = document.querySelector("#specialSalePrice");
  specialSalePrice.innerHTML = `<input class="form-control" id="inputSpecialSalePrice" type="number" value=${furniture.specialSalePrice} />`;*/
}

const onPurchase = () => {
  let purchasePrice = document.querySelector("#purchasePrice");
  purchasePrice.innerHTML = `<input class="form-control" id="inputPurchasePrice" type="number" value=${furniture.purchasePrice} />`;

  let withdrawalDateFromCustomer = document.querySelector("#withdrawalDateFromCustomer");
  if(furniture.withdrawalDateFromCustomer) {
    let date = new Date(furniture.withdrawalDateFromCustomer);
    withdrawalDateFromCustomer.innerHTML = `<input class="form-control" id="inputWithdrawalDateFromCustomer" type="date" value="${date.getFullYear() + "-"+ ("0" + (date.getMonth() + 1)).slice(-2) + "-" + ("0" + date.getDate()).slice(-2)}"/>`;
  }
  else
    withdrawalDateFromCustomer.innerHTML = `<input class="form-control" id="inputWithdrawalDateFromCustomer" type="date"/>`;
}

const onSold = () => {
  let deliveryDate = document.querySelector("#deliveryDate");
  if(furniture.deliveryDate){
    let date = new Date(furniture.deliveryDate);
    deliveryDate.innerHTML = `<input class="form-control" id="inputDeliveryDate" type="date" value="${date.getFullYear() + "-"+ ("0" + (date.getMonth() + 1)).slice(-2) + "-" + ("0" + date.getDate()).slice(-2)}"/>`;
  }
  else
    deliveryDate.innerHTML = `<input class="form-control" id="inputDeliveryDate" type="date" />`;

  let withdrawalDateToCustomer = document.querySelector("#withdrawalDateToCustomer");
  if(furniture.withdrawalDateToCustomer){
    let date = new Date(furniture.withdrawalDateToCustomer)
    withdrawalDateToCustomer.innerHTML = `<input class="form-control" id="inputWithdrawalDateToCustomer" type="date" value="${date.getFullYear() + "-"+ ("0" + (date.getMonth() + 1)).slice(-2) + "-" + ("0" + date.getDate()).slice(-2)}" />`;
  }
  else
    withdrawalDateToCustomer.innerHTML = `<input class="form-control" id="inputWithdrawalDateToCustomer" type="date"/>`;
  let specialSalePrice = document.querySelector("#specialSalePrice");
  specialSalePrice.innerHTML = `<input class="form-control" id="inputSpecialSalePrice" type="number" value=${furniture.specialSalePrice} />`;
}

const onBuyerEmail = () => {
  let buyerEmail = document.querySelector("#buyerEmail");
  if(furniture.buyer && furniture.buyer.email)
    buyerEmail.innerHTML = `<button class="form-control" id="inputBuyerEmail" type="button" class="btn btn-secondary">${furniture.buyer.email}</button>`
  else if(furniture.unregisteredBuyerEmail)
    buyerEmail.innerHTML = `<input class="form-control" id="inputBuyerEmail" type="email" value="${furniture.unregisteredBuyerEmail}"/>`;
  else
    buyerEmail.innerHTML = `<input class="form-control" id="inputBuyerEmail" type="email"/>`;
}

const onSave = async() => {
    let condition = document.querySelector("#conditions");
    condition = condition.value;

    let type = null;
    if(furniture.type.id != document.getElementById("typesList").value){
      type = document.getElementById("typesList").value;
      furniture.type.id = type;
    }
    let sellingPrice = null;
    let specialSalePrice = null;
    let purchasePrice = null;
    let withdrawalDateFromCustomer = null;
    let deliveryDate = null;
    let withdrawalDateToCustomer = null;
    let buyerEmail = null;
    let description = null;
    if(furniture.description != document.getElementById("furnituredescription").value){
      description = document.getElementById("furnituredescription").value;
      furniture.description = description;
    }

    if(condition == "vendu"){
      if(furniture.deliveryDate != document.querySelector("#inputDeliveryDate").value){
        deliveryDate = document.querySelector("#inputDeliveryDate").value;
        furniture.deliveryDate = deliveryDate;
      }
      if(furniture.withdrawalDateToCustomer != document.querySelector("#inputWithdrawalDateToCustomer").value){
        withdrawalDateToCustomer = document.querySelector("#inputWithdrawalDateToCustomer").value;
        furniture.withdrawalDateToCustomer = withdrawalDateToCustomer;
      }
      let optionDocument = document.querySelector("#option");
      optionDocument.innerHTML = "";
    }
    if(condition == "en_vente"){
      if(furniture.sellingPrice != document.querySelector("#inputSellingPrice").value){
        sellingPrice = document.querySelector("#inputSellingPrice").value;
        furniture.sellingPrice = sellingPrice;
      }

      document.querySelector("#inputWithdrawalDateToCustomer").value = null;
      document.querySelector("#inputBuyerEmail").value = null;
    } else if(condition == "achete"){
      if(furniture.purchasePrice != document.querySelector("#inputPurchasePrice").value){
        purchasePrice = document.querySelector("#inputPurchasePrice").value;
        furniture.purchasePrice = purchasePrice;
      }
      if(furniture.withdrawalDateFromCustomer != document.querySelector("#inputWithdrawalDateFromCustomer").value){
        withdrawalDateFromCustomer = document.querySelector("#inputWithdrawalDateFromCustomer").value;
        furniture.withdrawalDateFromCustomer = withdrawalDateFromCustomer;
      }
    } else if (condition == "vendu" || condition == "reserve" || 
    condition == "livre" || condition == "emporte_par_client") {
      if(furniture.unregisteredBuyerEmail != document.querySelector("#inputBuyerEmail").value){
        buyerEmail = document.querySelector("#inputBuyerEmail").value;
        furniture.buyerEmail = buyerEmail;
      } else if(furniture.buyer && furniture.buyer.email){
        buyerEmail = document.querySelector("#inputBuyerEmail").value;
        furniture.buyer.email = buyerEmail;
      }
      if(condition == "vendu"){
        if(furniture.specialSalePrice != document.querySelector("#inputSpecialSalePrice").value){
          specialSalePrice = document.querySelector("#inputSpecialSalePrice").value;
          furniture.specialSalePrice = specialSalePrice;
        }
      }
    }
    let checkConditions = true;
    if(furniture.condition == condition){
      condition = null;
      checkConditions = false;
    }
    furniture.condition = condition;
    let struct = {
      condition: condition,
      type: type,
      purchasePrice: purchasePrice,
      sellingPrice: sellingPrice,
      specialSalePrice: specialSalePrice,
      withdrawalDateFromCustomer: withdrawalDateFromCustomer,
      deliveryDate: deliveryDate,
      withdrawalDateToCustomer: withdrawalDateToCustomer,
      buyerEmail: buyerEmail,
      description: description
    }
    

    const user = getUserSessionData();
    try {
      await callAPIWithoutJSONResponse(API_BASE_URL + furniture.id, "PUT", user.token, struct);
      document.getElementById("toast").innerHTML = `</br><h5 style="color:green">Le meuble a bien été modifié.</h5>`;
      if(checkConditions){
        onCheckConditions();
      }
      if(condition == "retire_de_vente"){
        pictures.forEach(pct => {
            pct.scrollingPicture = false; 
        });
      }
      onModifyPicture();
    } catch (err) {
      console.error("FurnitureAdmin::Change condition", err);
      PrintError(err);
    }
}


const onModifyPicture = () => {
    let btnModifyPicture = document.querySelector("#btnModifyPicture");
    let codeModifyPicture = `<div class="row"><div class="btn-group" role="group" aria-label="Basic example">`;
    let p = document.querySelector(".carousel-item.active img").src;
    let pictureName = p.substring(p.lastIndexOf('/')+1);
    let picture;
    pictures.forEach(pct => {
      if(pct.name == pictureName){
        picture = pct; 
        return;
      }
    });

    if(picture.visibleForEveryone){
      codeModifyPicture += codeBtnRemoveVisibility;
    }else{
      codeModifyPicture += codeBtnAddVisibility;
    }
    if(picture.scrollingPicture){
      codeModifyPicture += codeBtnRemoveScrollingPicture;
    }else{
      codeModifyPicture += codeBtnAddScrollingPicture;
    }
    if(furniture.favouritePicture != picture.id){
      codeModifyPicture += codebtnAddFavouritePicture;
      codeModifyPicture += codeBtnDeletePicture;
    }
    codeModifyPicture += `</div>`;

    btnModifyPicture.innerHTML = codeModifyPicture;

    let btnScrollingPicture = document.querySelector("#btnScrollingPicture");
    btnScrollingPicture.addEventListener("click", onAddScrollingPicture);
    let btnDeletePicture = document.querySelector("#btnDeletePicture");
    if(btnDeletePicture != null){
      btnDeletePicture.addEventListener("click", onDeletePicture);
    }
    let btnAddFavouritePicture = document.querySelector("#btnAddFavouritePicture");
    if(btnAddFavouritePicture != null){
      btnAddFavouritePicture.addEventListener("click", onAddFavouritePicture);
    }
    let btnVisibleForEveryone = document.querySelector("#btnVisibleForEveryone");
    btnVisibleForEveryone.addEventListener("click",onVisibleForEveryone);

   
    codeModifyPicture += `</div></div>`

}

const onAddPicture = async (e) => {
  e.preventDefault();
  let file = document.getElementById("file").files[0];
  let fd = new FormData();
  fd.append("file",file);
  fd.append("furnitureID", furniture.id);


  const user = getUserSessionData();
  try {
    const response = await callAPIFormData(
      API_BASE_URL_ADMIN + "picture",
      "POST",
      user.token,
      fd
    );
  } catch (err) {
    console.error("FurnitureAdmin::onAddPicture", err);
    PrintError(err);
  }

  try {
    pictures = await callAPI(API_BASE_URL + furniture.id + "/all-pictures-furniture", "GET", user.token);
    if(pictures.length == 0){
      document.querySelector("#carousel").innerHTML = `<div class="alert alert-danger mt-2">Il n'y a pas de photos disponible pour ce meuble.</div>`
    }else{
      onPicturesList(pictures);
    }
  } catch (err) {
    console.error("AdminFurniture::onPicturesList", err);
    PrintError(err);
  }
}

const onAddScrollingPicture = async() => {

  const user = getUserSessionData();

  let picture = document.querySelector(".carousel-item.active img").src;
  let pictureName = picture.substring(picture.lastIndexOf('/')+1);
  pictures.forEach(pct => {
    if(pct.name == pictureName){
      picture = pct; 
      return;
    }
  });

  try {
    await callAPIWithoutJSONResponse(API_BASE_URL + picture.id + "/scrolling-picture", "PUT", user.token);
    document.getElementById("toast").innerHTML = `</br><h5 style="color:green">L'attribut photo défilante de l'image a bien été modifié</h5>`;
    pictures.forEach(pct => {
      if(pct.name == pictureName){
        pct.scrollingPicture = !pct.scrollingPicture; 
        return;
      }
    });
    onModifyPicture();
  } catch (err) {
    console.error("FurnitureAdmin::Change scrolling picture", err);
    PrintError(err);
  }

  
}

const onAddFavouritePicture = async() => {
  const user = getUserSessionData();

  let picture = document.querySelector(".carousel-item.active img").src;
  let pictureName = picture.substring(picture.lastIndexOf('/')+1);
  pictures.forEach(pct => {
    if(pct.name == pictureName){
      picture = pct; 
      return;
    }
  });
  try {
    await callAPIWithoutJSONResponse(API_BASE_URL + picture.id + "/favourite-picture", "PUT", user.token);
    document.getElementById("toast").innerHTML = `</br><h5 style="color:green">La photo a bien été désignée favorite</h5>`;
    pictures.forEach(pct => {
      if(pct.name == pictureName){
        furniture.favouritePicture = pct.id;
        return;
      }
    });
    onModifyPicture();
  } catch (err) {
    console.error("FurnitureAdmin::Change favourite picture", err);
    PrintError(err);
  }
}

const onVisibleForEveryone = async() => {
  const user = getUserSessionData();

  let picture = document.querySelector(".carousel-item.active img").src;
  let pictureName = picture.substring(picture.lastIndexOf('/')+1);
  pictures.forEach(pct => {
    if(pct.name == pictureName){
      picture = pct; 
      return;
    }
  });

  try {
    await callAPIWithoutJSONResponse(API_BASE_URL + picture.id + "/visible", "PUT", user.token);
    document.getElementById("toast").innerHTML = `</br><h5 style="color:green">La photo a bien été modifiée.</h5>`;
    pictures.forEach(pct => {
      if(pct.name == pictureName){
        pct.visibleForEveryone = !pct.visibleForEveryone; 
        return;
      }
    });
    onModifyPicture();
  } catch (err) {
    console.error("FurnitureAdmin::Change visible for everyone", err);
    PrintError(err);
  }
  
  
}


const onDeletePicture = async() => {
  const user = getUserSessionData();

  let picture = document.querySelector(".carousel-item.active img").src;
  let pictureName = picture.substring(picture.lastIndexOf('/')+1);
  pictures.forEach(pct => {
    if(pct.name == pictureName){
      picture = pct; 
      return;
    }
  });

  try {
    await callAPIWithoutJSONResponse(API_BASE_URL + picture.id + "/picture", "DELETE", user.token);
    document.getElementById("toast").innerHTML = `</br><h5 style="color:green">La photo a bien été supprimée</h5>`;
  } catch (err) {
    console.error("AdminFurniture::Delete picture", err);
    PrintError(err);
  }

  try {
    pictures = await callAPI(API_BASE_URL + furniture.id + "/all-pictures-furniture", "GET", user.token);
    if(pictures.length == 0){
      document.querySelector("#carousel").innerHTML = `<div class="alert alert-danger mt-2">Il n'y a pas de photos disponible pour ce meuble.</div>`
    }else{
      onPicturesList(pictures);
    }
  } catch (err) {
    console.error("AdminFurniture::onPicturesList", err);
    PrintError(err);
  }
}




const onCheckOption = async() => {

  let option = await callAPI(API_BASE_URL + furniture.id + "/option", "GET");
  let optionDocument = document.querySelector("#option");
  if(option.status != undefined && option.status == "en_cours") {
    optionDocument.innerHTML = isOption;
    let userOption = document.querySelector("#userOption");
    userOption.innerHTML = option.buyer.email;
    let btn = document.querySelector("#btnOption")
    btn.addEventListener("click", onClickCancelOption);
  }else{
    optionDocument.innerHTML = noOption;
  }


}

const onClickCancelOption = async (e) => {
  e.preventDefault();
  let optionDocument = document.querySelector("#option");
  try {
    let user = getUserSessionData();
    await callAPIWithoutJSONResponse(API_BASE_URL_ADMIN + furniture.id + "/cancelOption", "PUT",user.token);
    optionDocument.innerHTML = noOption;
    try {
      furniture = await callAPI(API_BASE_URL + furniture.id , "GET",undefined);
    } catch (err) {
      console.error("FurnitureAdmin::GetFurnitureByID", err);
      PrintError(err);
    }
    let optionCondition = document.querySelector("#"+furniture.condition);
    optionCondition.setAttribute("selected","");
  } catch (e) {
    PrintError(e);
  }
}



const onCheckConditions = () =>{
  let propose = `<option id="propose" value="propose">Proposé</option>`;
  let ne_convient_pas = `<option id="ne_convient_pas" value="ne_convient_pas">Ne convient pas</option>`
  let achete = `<option id="achete" value="achete">Acheté</option>`;
  let emporte_par_patron = `<option id="emporte_par_patron" value="emporte_par_patron">Emporté par le patron</option>`
  let en_restauration = `<option id="en_restauration" value="en_restauration">En restauration</option>`;
  let en_magasin = `<option id="en_magasin" value="en_magasin">En magasin</option>`;
  let en_vente = `<option id="en_vente" value="en_vente">En vente</option>`;
  let retire_de_vente = `<option id="retire_de_vente" value="retire_de_vente">Retiré de la vente</option>`;
  let en_option = `<option id="en_option" value="en_option">En option</option>`;
  let vendu = `<option id="vendu" value="vendu">Vendu</option>`;
  let reserve = `<option id="reserve" value="reserve">Réservé</option>`;
  let livre = `<option id="livre" value="livre">Livré</option>`
  let emporte_par_client = `<option id="emporte_par_client" value="emporte_par_client">Emporté par le client</option>`;

  let ensemble = `<select class="custom-select" id="conditions">`

  switch (furniture.condition) {
    case "propose":
      ensemble += propose;
      ensemble += ne_convient_pas;
      ensemble += achete;
      break;
    case "ne_convient_pas":
        ensemble += ne_convient_pas;
        ensemble += propose;
      break;
    case "achete":
      ensemble += achete;
      ensemble += emporte_par_patron;
      ensemble += vendu;
      ensemble += propose;
      
      break;
    case "emporte_par_patron":
      ensemble += emporte_par_patron;
      ensemble += en_restauration;
      ensemble += en_magasin;
      ensemble += vendu;
      ensemble += achete;
      break;
    case "en_restauration":
      ensemble += en_restauration;
      ensemble += en_magasin;
      ensemble += vendu;
      ensemble += emporte_par_patron;
      break;
    case "en_magasin":
      ensemble += en_magasin;
      ensemble += en_vente;
      ensemble += emporte_par_patron;
      ensemble += vendu;
      ensemble += en_restauration;
      break;
    case "en_vente":
      ensemble += en_vente;
      ensemble += retire_de_vente;
      ensemble += vendu;
      ensemble += en_magasin;
      break;
    case "retire_de_vente":
      ensemble += retire_de_vente;
      ensemble += en_vente;
      ensemble += vendu;
      break;
    case "en_option":
      ensemble += en_option;
      ensemble += vendu;
      ensemble += en_vente;
      break;
    case "vendu":
      ensemble += vendu;
      ensemble += reserve;
      ensemble += emporte_par_client;
      ensemble += livre;
      ensemble += en_vente;
      ensemble += en_option;
      ensemble += achete;
      ensemble += emporte_par_patron;
      ensemble += en_magasin;
      ensemble += en_restauration;
      ensemble += retire_de_vente;
      break;
    case "reserve":
      ensemble += reserve;
      ensemble += en_vente;
      ensemble += emporte_par_client;
      ensemble += vendu;
      break;
    case "livre":
      ensemble += livre;
      ensemble += vendu;
      break;
    case "emporte_par_client":
      ensemble += emporte_par_client;
      ensemble += vendu;
      ensemble += reserve;
      break;
  }

  ensemble += `</select>` 
  let conditionsDiv = document.querySelector("#conditionsDiv");
  conditionsDiv.innerHTML = ensemble;
  let conditions = document.querySelector("#conditions");
  if(conditions.value == "vendu"){
    onSold();
  }
  if(conditions.value == "en_vente"){
    onSale();
  } else if (conditions.value == "achete") {
    onPurchase();
  } else if (conditions.value == "vendu" || conditions.value == "reserve" || 
  conditions.value == "livre" || conditions.value == "emporte_par_client") {
    onBuyerEmail();
  }

  conditions.addEventListener("change",(e)=>{
    onFurniture();
    if(conditions.value == "vendu"){
      onSold();
    }
    if(conditions.value == "en_vente"){
      onSale();
    } else if (conditions.value == "achete") {
      onPurchase();
    } else if (conditions.value == "vendu" || conditions.value == "reserve" || 
    conditions.value == "livre" || conditions.value == "emporte_par_client") {
      onBuyerEmail();
    }
  });
}

export default FurnitureAdmin;