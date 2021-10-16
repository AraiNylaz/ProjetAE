import { getUserSessionData } from "../../utils/session.js";
import { callAPI,callAPIWithoutJSONResponse } from "../../utils/api.js";
import PrintError from "../PrintError.js";
const API_BASE_URL = "/api/furniture/";
const API_BASE_URL_ADMIN = "/api/admin/";

let typesListArray = [];

let furnitureTypePage = `
    <h4 id="pageTitle">Ajouter ou supprimer un type de meuble</h4>
    <div id="typesList"></div>
    <div id="typeAdded"></div>
    <form>
        <div class="row">
            <div class="col-6">
                <div class="form-group">
                    <label for="newType">Nouveau type de meuble</label>
                    <input class="form-control" id="newType" type="text" placeholder="Entrez un type" />
                </div>
            </div>
            <div class="col-6">
                <button class="btn btn-success" id="buttonAdd" type="submit">Ajouter</button>
            </div>
        </div>
    </form>`;

let page = document.querySelector("#page");

const FurnitureTypePage = async () => {

    page.innerHTML = furnitureTypePage;

    let addTypeForm = document.querySelector("form");
    addTypeForm.addEventListener("submit", onAddType);

    try {
        const types = await callAPI(API_BASE_URL + "allFurnitureTypes", "GET", undefined);
        onTypesList(types);
    } catch (err) {
        console.error("FurnitureTypePage::onTypesList", err);
        PrintError(err);
    }
}

const onTypesList = (data) => {
    typesListArray = data;
    let typesList = `
    <h3>Types existants</h3>
    <div class="row">`;

    data.forEach(type => {
        typesList += `
            <div class="col-4">
                <div id="type${type.id}">
                    <div class="row"> 
                        <div class="col-6">
                            <h5>${type.name}</h5>
                        </div>
                        <div class="col-6">
                            <button class="btn btn-danger" id="buttonDelete${type.id}">Supprimer</button>
                        </div>
                    </div>
                </div> 
            </br>
            </div>`
    });

    typesList += `</div>`;
    document.getElementById("typesList").innerHTML = typesList;

    const user = getUserSessionData();

    data.forEach(type => {
        let buttonDelete = document.getElementById("buttonDelete"+type.id);
        buttonDelete.addEventListener("click", async () => {
            try {
                await callAPIWithoutJSONResponse(
                  API_BASE_URL_ADMIN + "type/" + type.id,
                  "DELETE",
                  user.token
                );
                onTypeDeleted(type.id);
            } catch (err) {
                console.error("FurnitureTypePage::onTypeDeleted", err);
                document.getElementById("type"+type.id).innerHTML = `<h5 style="color:red">Impossible de supprimer le type car il est utilisé pour un meuble</h5>`;
            }
        });
    });
}

const onTypeDeleted = (id) => {
    document.getElementById("type"+id).innerHTML = `<h5 style="color:green">Le type a bien été supprimé</h5>`;
}

const onAddType = async (e) => {
    e.preventDefault();
    let type ={
        type: document.getElementById("newType").value
    };

    const user = getUserSessionData();

    try {
        const typeIdAdded = await callAPI(
          API_BASE_URL_ADMIN + "type",
          "POST",
          user.token,
          type
        );
        onTypeAdded(typeIdAdded,type.type);
      } catch (err) {
        console.error("FurnitureTypePage::onAddType", err);
        PrintError(err);
      }
}

const onTypeAdded = (typeId,type) => {
    let newType = {
        id: typeId,
        name: type
    };
    typesListArray.push(newType);
    onTypesList(typesListArray);
    document.querySelector("form").reset();
    document.getElementById("typeAdded").innerHTML = `<h5 style="color:green">Le type a bien été ajouté</h5>`;
}

export default FurnitureTypePage;