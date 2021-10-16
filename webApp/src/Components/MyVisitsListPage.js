import {callAPI} from "../utils/api.js";
import { getUserSessionData } from "../utils/session.js";
import PrintError from "./PrintError.js";
import { RedirectUrl } from "./Router.js";
const API_BASE_URL = "/api/visit/";


let myVisitsListPage = `
<h4 id="pageTitle">Mes demandes de visite</h4>
<div id="visitList"></div>
`;


const MyVisitsListPage = async () => {
  let page = document.querySelector("#page");
  page.innerHTML = myVisitsListPage;

  const user = getUserSessionData();

  try {
    const visitList = await callAPI(API_BASE_URL + "user/" + user.user.id, "GET", user.token);
    onVisitList(visitList);
  } catch (err) {
    console.error("MyVisitsListPage::onVisitRequests", err);
    PrintError(err);
  }
};

const onVisitList = (data) => {
    if (!data) return;
    let visitList = `
  <div id="tablevisits" class="table-responsive mt-3">
  <table class="table">
      <thead>
          <tr>
              <th>Plage horaire</th>
              <th>Date de la demande</th>
              <th>Etat</th>
          </tr>
      </thead>
      <tbody>`;

    data.forEach((element) => {
        let date = new Date(element.requestDate);
        visitList += `<tr>
                  <td><a id="visit${element.id}" href="" target="_blank">${element.timeSlot}</a></td>
                  <td>${date.getDate() + "/" + (date.getMonth()+1) + "/" + date.getFullYear()}</td>
                  <td>${element.status}</td>
               </tr>`;
    });

    visitList += `</tbody>
  </table>
  </div>`;


    let visitListDiv = document.querySelector("#visitList");
    visitListDiv.innerHTML = visitList;

    data.forEach((element) => {
        let visitElement = document.getElementById("visit"+element.id);
        visitElement.addEventListener("click", (e) => {
            e.preventDefault();
            RedirectUrl("/myvisit",element,"?id="+element.id);
        });
    });
};


export default MyVisitsListPage;