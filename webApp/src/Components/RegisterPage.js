import { RedirectUrl } from "./Router.js";
import Navbar from "./Navbar.js";
import { setUserSessionData } from "../utils/session.js";
import { callAPI } from "../utils/api.js";
import PrintError from "./PrintError.js";
const API_BASE_URL = "/api/auths/";

let registerPage = `<h4 id="pageTitle">S'inscrire</h4>
<form>
<div class="row">
  <div class="col-6">
    <div class="form-group">
      <label for="username">Pseudonyme</label>
      <input class="form-control" id="username" type="text" placeholder="Entrez votre nom pseudonyme" required="" />
    </div>
    <div class="form-group">
      <label for="firstname">Prénom</label>
      <input class="form-control" id="firstname" type="text" placeholder="Entrez votre prénom" required="" />
    </div>
    <div class="form-group">
      <label for="lastname">Nom</label>
      <input class="form-control" id="lastname" type="text" placeholder="Entrez votre nom de famille" required="" />
    </div>
    <div class="form-group">
      <label for="email">Adresse Electronique</label>
      <input class="form-control" id="email" type="text" placeholder="Entrez votre email" required="" />
    </div>
    <div class="form-group">
      <label for="password">Mot de passe</label>
      <input class="form-control" id="password" type="password" name="password" placeholder="Entrez votre mot de passe" required="" />
    </div>
    <div class="form-group">
    <label for="verifypassword">Vérification Mot de passe</label>
    <input class="form-control" id="verifypassword" type="password" name="verifypassword" placeholder="Entrez à nouveau votre mot de passe" required="" />
    </div>
  </div>
  <div class="col-6">
    <div class="form-group">
      <label for="street">Rue</label>
      <input class="form-control" id="street" type="text" placeholder="Entrez votre rue" required="" />
    </div>
    <div class="row">
      <div class="form-group">
        <label for="buildingnumber">Numéro</label>
        <input class="form-control" id="buildingnumber" type="text" placeholder="Entrez votre numéro" required="" />
      </div>
      <div class="form-group">
        <label for="unitnumber">Boite</label>
        <input class="form-control" id="unitnumber" type="text" placeholder="Entrez votre boite" />
      </div>
      <div class="form-group">
        <label for="postcode">Code postal</label>
        <input class="form-control" id="postcode" type="text" placeholder="Entrez votre code postal" required="" />
      </div>
    </div>
    <div class="row">
      <div class="form-group">
        <label for="commune">Commune</label>
        <input class="form-control" id="commune" type="text" placeholder="Entrez votre commune" required="" />
      </div>
      <div class="form-group">
        <label for="country">Pays</label>
        <input class="form-control" id="country" type="text" placeholder="Entrez votre pays" required="" />
      </div>
    </div>
    <button class="btn btn-primary" id="btn" type="submit">Confirmer mon inscription</button>
  </div>
</div>
<!-- Create an alert component with bootstrap that is not displayed by default-->
<div class="alert alert-danger mt-2 d-none" id="messageBoard"></div><span id="errorMessage"></span>
</form>`;

const RegisterPage = () => {
  let page = document.querySelector("#page");
  page.innerHTML = registerPage;
  let registerForm = document.querySelector("form");
  registerForm.addEventListener("submit", onRegister);
};

const onRegister = async (e) => {
  e.preventDefault();
  if(document.getElementById("password").value != document.getElementById("verifypassword").value){
    let error = {
      message: "Le mot de passe n'est pas égal à la vérification",
    };
    PrintError(error);
  } else{
    let address = {
      street: document.getElementById("street").value,
      buildingNumber: document.getElementById("buildingnumber").value,
      unitNumber: document.getElementById("unitnumber").value,
      postcode: document.getElementById("postcode").value,
      commune: document.getElementById("commune").value,
      country: document.getElementById("country").value,
  
    };
    let user = {
      username: document.getElementById("username").value,
      firstName: document.getElementById("firstname").value,
      lastName: document.getElementById("lastname").value,
      email: document.getElementById("email").value,
      password: document.getElementById("password").value,
      address: address,
    };
    try {
      const userRegistered = await callAPI(
        API_BASE_URL + "register",
        "POST",
        undefined,
        user
      );
      onUserRegistration(userRegistered);
    } catch (err) {
      console.error("RegisterPage::onRegister", err);
      PrintError(err);
    }
  }
};

const onUserRegistration = (userData) => {
  console.log("onUserRegistration", userData);
  const user = { ...userData, isAutenticated: true };
  setUserSessionData(user);
  // re-render the navbar for the authenticated user
  Navbar();
  RedirectUrl("/");
};

export default RegisterPage;
