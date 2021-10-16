import { RedirectUrl } from "../Router.js";
import { getUserSessionData } from "../../utils/session.js";
import { callAPI } from "../../utils/api.js";
import PrintError from "../PrintError.js";
const API_BASE_URL = "/api/furniture/";

let furnitureBuyByUser, furnitureSellByUser;


let furniturePage = `<h4 id="pageTitle">Mes meubles</h4>
<div class="form-group">
<select name="types" id="type-select">
    <option value="0" selected>Choisir un type de meuble</option>
</select>
<input class="col-auto" type="number" id="price" placeholder="Prix max">
<button class="btn btn-success" id="searchBtn" type="submit">Rechercher</button>
</div>
<div class="row">
    <div class="col-6">
        <div class="form-group">
            <label for="furniture_buyed">Meuble acheté</label>
            <div id="furniture_buyed"></div>
        </div>
    </div>
    <div class="col-6">
        <div class="form-group">
            <label for="furniture_selled">Meuble vendu</label>
            <div id="furniture_selled"></div>
        </div>
    </div>
</div>`;

const UserFurnitureListPage = async () => {
    let page = document.querySelector("#page");
    page.innerHTML = furniturePage;

    const user = getUserSessionData();
    furnitureBuyByUser = await callAPI(API_BASE_URL + "furniturebuyby/" + user.user.id , "GET", user.token);
    onFurnitureBuy(furnitureBuyByUser);
    furnitureSellByUser = await callAPI(API_BASE_URL + "furnituresellby/" + user.user.id , "GET", user.token);
    onFurnitureSell(furnitureSellByUser);

    let typeSelect = document.getElementById("type-select");

    furnitureBuyByUser.forEach(furniture => {
        let id = "type-" + furniture.type.id;
        if(document.getElementById(id) === null) {
            typeSelect.innerHTML += `<option id="${id}" value="${furniture.type.id}">${furniture.type.name}</option>`;
        }
    });

    furnitureSellByUser.forEach(furniture => {
        let id = "type-" + furniture.type.id;
        if(document.getElementById(id) === null) {
            typeSelect.innerHTML += `<option id="${id}" value="${furniture.type.id}">${furniture.type.name}</option>`;
        }
    });

    let searchBtn = document.querySelector('#searchBtn');
    searchBtn.addEventListener("click", onSubmitSearch);
};

const onSubmitSearch = async () => {
    let typeSearchSell = onTypeSearch(furnitureSellByUser);
    let typeSearchBuy = onTypeSearch(furnitureBuyByUser);
    let priceSearchSell = onPriceSearch(typeSearchSell);
    let priceSearchBuy = onPriceSearch(typeSearchBuy);

    onFurnitureBuy(priceSearchBuy);
    onFurnitureSell(priceSearchSell);
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

const onFurnitureBuy = (data) => {
  let furnitureBuyed = document.querySelector('#furniture_buyed');
  let display =
      `<div id="tablefurnitures" class="table-responsive mt-3">
          <table class="table">
              <thead>
                  <tr>
                      <th>Description</th>
                      <th>Type</th>
                  </tr>
              </thead>
              <tbody>`;
  data.forEach((element) => {
      display +=  `<tr>
                                  <td><a id="furnitureBuyed${element.id}" href="" target="_blank">${element.description}</a></td>
                                  <td>${element.type.name}</td>
                              </tr>`;
  });
  display += `</tbody></table></div>`;
  furnitureBuyed.innerHTML = display;

  data.forEach((element) => {
      let furnitureElement = document.getElementById("furnitureBuyed"+element.id);
      furnitureElement.addEventListener("click", (e) => {
          e.preventDefault();
          
          RedirectUrl("/myfurniture",element,"?id="+element.id); 
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
                  </tr>
              </thead>
              <tbody>`;
  data.forEach((element) => {
      display +=  `<tr>
                                  <td><a id="furnitureSelled${element.id}" href="" target="_blank">${element.description}</a></td>
                                  <td>${element.type.name}</td>
                              </tr>`;
  });
  display += `</tbody></table></div>`;
  furnitureSelled.innerHTML = display;

  data.forEach((element) => {
      let furnitureElement = document.getElementById("furnitureSelled"+element.id);
      furnitureElement.addEventListener("click", (e) => {
          e.preventDefault();
          RedirectUrl("/myfurniture",element,"?id="+element.id);
      });
  });
};


export default UserFurnitureListPage;
