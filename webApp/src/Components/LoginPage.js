/* In a template literal, the ` (backtick), \ (backslash), and $ (dollar sign) characters should be 
escaped using the escape character \ if they are to be included in their template value. 
By default, all escape sequences in a template literal are ignored.*/
import { getUserSessionData, setUserSessionData, setUserLocalData } from "../utils/session.js";
import { RedirectUrl } from "./Router.js";
import Navbar from "./Navbar.js";
import { callAPI } from "../utils/api.js";
import PrintError from "./PrintError.js";
const API_BASE_URL = "/api/auths/";

let loginPage = `<h4 id="pageTitle">Se connecter</h4>
<form>
<div class="form-group">
  <label for="username">Pseudonyme</label>
  <input class="form-control" id="username" type="text" placeholder="Entrez votre pseudonyme" required="" />
</div>
<div class="form-group">
  <label for="password">Mot de passe</label>
  <input class="form-control" id="password" type="password" name="password" placeholder="Entrez votre mot de passe" required=""  />
</div>
<div class="form-check">
  <input type="checkbox" class="form-check-input" id="rememberMe">
  <label class="form-check-label" for="rememberMe">Se souvenir de moi</label>
</div>
<button class="btn btn-primary" id="btn" type="submit">Connexion</button>
<!-- Create an alert component with bootstrap that is not displayed by default-->
<div class="alert alert-danger mt-2 d-none" id="messageBoard"></div>
</form>`;

const LoginPage = () => {  
  let page = document.querySelector("#page");
  page.innerHTML = loginPage;
  let loginForm = document.querySelector("form");
  const user = getUserSessionData();
  if (user) {
    // re-render the navbar for the authenticated user
    Navbar();
    RedirectUrl("/");
  } else loginForm.addEventListener("submit", onLogin);
};

const onLogin = async (e) => {
  e.preventDefault();
  let username = document.getElementById("username");
  let password = document.getElementById("password");
  let rememberMe = document.getElementById("rememberMe");
  
  let user = {
    username: username.value,
    password: password.value
  };

  try {
    const userLogged = await callAPI(
      API_BASE_URL + "login",
      "POST",
      undefined,
      user
    );
    onUserLogin(userLogged, rememberMe.checked);
  } catch (err) {
    console.error("LoginPage::onLogin", err);
    PrintError(err);
  }
};

const onUserLogin = (userData, rememberMe) => {
  const user = { ...userData, isAutenticated: true };
  if(rememberMe) {
    setUserLocalData(user);
  } else {
    setUserSessionData(user);
  }
  // re-render the navbar for the authenticated user
  Navbar();
  RedirectUrl("/");
};

export default LoginPage;
