import {callAPI, callAPIFormData} from "../utils/api.js";
import PrintError from "./PrintError.js";

const API_BASE_URL = "/api/furniture/";
const IMAGES = "http://localhost:8080/images/";

let carouselPictures;

let homePage = `
<h4 id="pageTitle" class="text-center">Accueil</h4>
<div id="filter_carousel" class="float-right"></div>
<div id="carousel" class="col-12 float-left"></div>
`;


const HomePage = async () => {
  let page = document.querySelector("#page");
  page.innerHTML = homePage;
  try {
    carouselPictures = await callAPI(API_BASE_URL + "carousel-pictures", "GET");
    onFilterCarousel();
    onPicturesList(carouselPictures);
  } catch (err) {
    console.error("HomePage::onPicturesList", err);
    PrintError(err);
  }
};

const onFilterCarousel = () => {
  document.getElementById("filter_carousel").innerHTML = `
    <select name="types" id="type-select">
        <option value="0" selected>Choisir un type de meuble</option>
    </select>
  `;

  let typeSelect = document.getElementById("type-select");

  carouselPictures.forEach(picture => {
    let id = "type-" + picture.furniture.type.id;
    if(document.getElementById(id) === null) {
      typeSelect.innerHTML += `<option id="${id}" value="${picture.furniture.type.id}">${picture.furniture.type.name}</option>`;
    }
  });

  typeSelect.addEventListener("change", onChangeType);
};

const onChangeType = () => {
  let typeSelect = document.getElementById("type-select");
  let typeValue = typeSelect.value;
  if(typeValue == 0) {
    onPicturesList(carouselPictures);
    return;
  }
  let picturesOfType = carouselPictures.filter(data => {
    return data.furniture.type.id == typeValue;
  });
  onPicturesList(picturesOfType);
};

const onPicturesList = (data) => {
  let carousel = `
    <div id="carouselPictures" class="carousel slide" data-ride="carousel"><ol class="carousel-indicators carousel-dark">`;
  for (let i = 0; i < data.length; i++) {
    if(i== 0){
      carousel += `<li data-target="#carouselPictures" data-slide-to="${i}" class="active"></li>`
    }else{
      carousel += `<li data-target="#carouselPictures" data-slide-to="${i}"></li>`;
    }
  }
  carousel += `</ol> <div class="carousel-inner">`;

  let counter = 0;
  data.forEach(picture => {
    if(counter == 0){
      carousel += `<div class="carousel-item active">`;
    }else{
      carousel += `<div class="carousel-item">`;
    }
    carousel += `
    <div class="container d-flex justify-content-center">
        <img id="carouselHomePageBlur" src="${IMAGES}${picture.name}" alt="${counter}">
        <img id="carouselHomePage" src="${IMAGES}${picture.name}" alt="${counter}">
        <div class="carousel-caption">
            <p class="font-weight-bold carousel-dark">${picture.furniture.description}</p>
    </div></div></div>
    `;
    counter ++;
  });

  carousel += `
    <a class="carousel-control-prev carousel-dark" href="#carouselPictures" role="button" data-slide="prev">
      <span class="carousel-control-prev-icon" aria-hidden="true"></span>
      <span class="sr-only">Previous</span>
    </a>
    <a class="carousel-control-next carousel-dark" href="#carouselPictures" role="button" data-slide="next">
      <span class="carousel-control-next-icon" aria-hidden="true"></span>
      <span class="sr-only">Next</span>
    </a>
    </div>
    </div>
    `;

  document.querySelector("#carousel").innerHTML = carousel;
}

export default HomePage;