import { RedirectUrl } from "../Router.js";
import { getUserSessionData } from "../../utils/session.js";
import { callAPI } from "../../utils/api.js";
import PrintError from "../PrintError.js";
const API_BASE_URL_ADMIN = "/api/admin/";
const API_BASE_URL_USER = "/api/furniture/";

let page = document.querySelector("#page");
let furniturePage;
let furnitures;

const FurnitureListPage = async () => {
  furniturePage = `<h4 id="pageTitle">Liste de meubles</h4>
  <div class="form-group">
    <select name="types" id="type-select">
        <option value="0" selected>Choisir un type de meuble</option>
    </select>
    <input class="col-auto" type="number" id="price" placeholder="Prix max">
    <button class="btn btn-success" id="searchBtn" type="submit">Rechercher</button>
  </div>
  <div id="furnitureList"></div>`;
  page.innerHTML = furniturePage;

  

  const user = getUserSessionData();
  if(user && user.user.userType == "admin"){
    try {
      furnitures = await callAPI(API_BASE_URL_ADMIN + "allFurniture", "GET", user.token);
      onFurnitureList(furnitures);
    } catch (err) {
      console.error("FurnitureListPage::onFurnitureList", err);
      PrintError(err);
    }
  } else {
    let userId = -1;
    if(user != null){
      userId = user.user.id;
    }
    try {
      furnitures = await callAPI(API_BASE_URL_USER + "allFurniture", "GET", undefined);
      onFurnitureList(furnitures);
    } catch (err) {
      console.error("FurnitureListPage::onFurnitureList", err);
      PrintError(err);
    }
  }


  let typeSelect = document.getElementById("type-select");

  furnitures.forEach(furniture => {
      let id = "type-" + furniture.type.id;
      if(document.getElementById(id) === null) {
          typeSelect.innerHTML += `<option id="${id}" value="${furniture.type.id}">${furniture.type.name}</option>`;
      }
  });

  let searchBtn = document.querySelector('#searchBtn');
  searchBtn.addEventListener("click", onSubmitSearch);

};

const onFurnitureList = (data) => {
  if (!data) return;
  let furnitureList = ``;
  furnitureList += `
  <div id="tablefurnitures" class="table-responsive mt-3">
  <table class="table">
      <thead>
          <tr>
              <th>Description</th>
              <th>Type</th>
              <th>Etat</th>
          </tr>
      </thead>
      <tbody>`;

    const user = getUserSessionData();

    data.forEach((element) => {
      furnitureList += `<tr>
                  <td><a id="furniture${element.id}" href="" target="_blank">${element.description}</a></td>
                  <td>${element.type.name}</td>`
                  if(user && user.user.userType == "admin")
                  furnitureList += `<td>${element.condition}</td>`
                  furnitureList +=
                `</tr>`
              ;
    });

    furnitureList += `</tbody>
  </table>
  </div>`;

    document.getElementById("furnitureList").innerHTML = furnitureList;

  data.forEach((element) => {
    let furnitureElement = document.getElementById("furniture"+element.id);
    furnitureElement.addEventListener("click", (e) => {
      e.preventDefault();
      if(getUserSessionData() == null || getUserSessionData().user.userType != "admin"){
        RedirectUrl("/furniture",element,"?id="+element.id);
      }else{
        RedirectUrl("/admin/furniture",element,"?id="+element.id);
      }
    });
  });
};

const onSubmitSearch = async () => {
  let typeSearch = onTypeSearch(furnitures);
  let priceSearch = onPriceSearch(typeSearch);
  onFurnitureList(priceSearch);
};

const onTypeSearch = (data) => {
  let typeSelect = document.getElementById("type-select");
  let typeValue = typeSelect.value;
  if(typeValue == 0) {
      return data;
  }
  return data.filter(data => {
      return data.type.id == typeValue;
  });
};

const onPriceSearch = (data) => {
  let priceField = document.querySelector('#price');
  let priceValue = priceField.value;
  if(priceValue.length === 0) {
      return data;
  }
  return data.filter(data => {
      console.log(data.sellingPrice);
      return data.sellingPrice >= priceValue;
  });
};



export default FurnitureListPage;
