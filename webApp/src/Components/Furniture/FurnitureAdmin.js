import { RedirectUrl } from "../Router.js";
import Navbar from "../Navbar.js";
import { getUserSessionData } from "../../utils/session.js";
import { callAPIWithoutJSONResponse } from "../../utils/api.js";
import PrintError from "../PrintError.js"
import img1 from "./1.jpg";
import img2 from "./2.jpg";
const API_BASE_URL = "/api/furniture/";
const IMAGES = "../../../../images";
let option;

let optionTaken = false;

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
  
  <div class="input-group mb-3">
    <div class="input-group-prepend">
      <label class="input-group-text" for="inputGroupSelect01">Etat du meuble</label>
    </div>
    <select class="custom-select" id="conditions">
      <option id="en_restauration" value="en_restauration">En restauration</option>
      <option id="en_magasin" value="en_magasin">En magasin</option>
      <option id="en_vente" value="en_vente">En vente</option>
      <option id="retire_de_vente" value="retire_de_vente">Retiré de la vente</option>
    </select>
  </div>

  <button class="btn btn-success" id="btnSave" type="submit">Enregistrer</button>


</div>
`;

const FurnitureAdmin = (furniture) => {
  let page = document.querySelector("#page");
  page.innerHTML = furniturePage;

  



  let optionCondition = document.querySelector("#"+furniture.condition);
  optionCondition.setAttribute("selected","");

  let btnSave = document.querySelector("#btnSave");
  btnSave.addEventListener("click", async () => {
    let conditionChoice = document.querySelector("#conditions");
    conditionChoice = conditionChoice.value;
    console.log(conditionChoice);
    //CALL API
    let user = getUserSessionData();
  
    // reste à rajouter un url en plus? + voir cmment passer plusieurs params

    let condition = {
      condition: conditionChoice
    };
  
    await callAPIWithoutJSONResponse(API_BASE_URL + furniture.id, "PUT", user.token, condition);
  });

  let onSaleCondition = document.querySelector("#conditions");
  console.log(onSaleCondition);
  onSaleCondition.addEventListener("select",(e)=>{
    //Pouvoir modifier son prix
    //TODO

    console.log("click sur en vente");

  })
  



  //Question => Mettre l'id dans l'url
  /*try {
    const furniture = await callAPI(API_BASE_URL + id , "GET",undefined);
    onFurniture(furniture);
  } catch (err) {
    console.error("FurnitureUser::onFurniture", err);
    PrintError(err);
  }*/

  
  onFurniture(furniture);



}

const onFurniture = (data) => {

  if (!data) return;
  let type = document.querySelector("#type");
  type.innerHTML = `<input class="form-control" id="type" type="text" placeholder=${data.type.name} readonly />`;
  let prix = document.querySelector("#prix");
  prix.innerHTML = `<input class="form-control" id="prix" type="text" placeholder=${data.sellingPrice} readonly />`;
  let furnitureDescription = document.querySelector("#furnitureDescription");
  furnitureDescription.innerHTML = `<textarea class="form-control" id="furnituredescription" rows="6" readonly>${data.description}</textarea>`;
  
}

const onSave = async(data) => {
  if(!data)return;  
  let conditionChoice = document.querySelector("#conditions");
  conditionChoice = conditionChoice.value;
  console.log(conditionChoice);
  //CALL API
  let user = getUserSessionData();

  // reste à rajouter un url en plus? + voir cmment passer plusieurs params

  await callAPIWithoutJSONResponse(API_BASE_URL + data.id, "PUT", user.token, conditionChoice);

}


export default FurnitureAdmin;



