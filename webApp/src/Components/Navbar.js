let navBar = document.querySelector("#navBar");
import {getUserSessionData} from "../utils/session.js";
import logoNavBar from "../images/logoNavBar.png"
// destructuring assignment
const Navbar = () => {
  let navbar;
  let user = getUserSessionData();    
  if (user && user.user.userType == "admin") {
    navbar = `<nav class="navbar navbar-expand-lg navbar-light bg-light mb-2" id="navBar">
  <a class="navbar-brand" href="/">
    <img src="${logoNavBar}" width="85" height="85" alt="">
  </a>
  <button
    class="navbar-toggler"
    type="button"
    data-toggle="collapse"
    data-target="#navbarNavAltMarkup"
    aria-controls="navbarNavAltMarkup"
    aria-expanded="false"
    aria-label="Toggle navigation"
  >
    <span class="navbar-toggler-icon"></span>
  </button>
  <div class="collapse navbar-collapse" id="navbarNavAltMarkup">
  <ul class="navbar-nav mr-auto">
    <span class="navbar-text">
      Sentier des artistes 1bis <br>
      4800 Verviers <br>
      Belgique
    </span>
  </ul>
  <ul class="navbar-nav ml-auto px-2 btn-group">
    <a class="nav-item nav-link btn btn-outline-warning" href="#" type="button" data-uri="/">Accueil</a>
    <a class="nav-item nav-link btn btn-outline-warning" href="#" type="button" data-uri="/search">Meubles en magasin</a>
    <a class="nav-item nav-link btn btn-outline-warning" href="#" type="button" data-uri="/visit">Introduire une demande de visite</a>
    <a class="nav-item nav-link btn btn-outline-warning" href="#" type="button" data-uri="/admin">Administration</a>
  </ul>
  <ul class="navbar-nav">
    <div class="text-center">
      <a class="alert alert-warning nav-item nav-link disabled" id="username" href="#">${user.user.username}</a>
      <a type="button" class="nav-item nav-link btn btn-outline-warning" data-uri="/logout" href"#">Se déconnecter</a>
    </div>
  </ul>
  </div>
  </nav>`;
  } else if(user) {
    navbar = `<nav class="navbar navbar-expand-lg navbar-light bg-light mb-2" id="navBar">
    <a class="navbar-brand" href="/">
    <img src="${logoNavBar}" width="85" height="85" alt="">
  </a>  <button
    class="navbar-toggler"
    type="button"
    data-toggle="collapse"
    data-target="#navbarNavAltMarkup"
    aria-controls="navbarNavAltMarkup"
    aria-expanded="false"
    aria-label="Toggle navigation"
  >
    <span class="navbar-toggler-icon"></span>
  </button>
  <div class="collapse navbar-collapse" id="navbarNavAltMarkup">
    <ul class="navbar-nav mr-auto">
    <span class="navbar-text">
      Sentier des artistes 1bis <br>
      4800 Verviers <br>
      Belgique
    </span>
  </ul>
  <ul class="navbar-nav ml-auto px-2 btn-group">
    <a class="nav-item nav-link btn btn-outline-warning" href="#" type="button" data-uri="/">Accueil</a>
    <a class="nav-item nav-link btn btn-outline-warning" href="#" type="button" data-uri="/search">Meubles en magasin</a>
    <a class="nav-item nav-link btn btn-outline-warning" href="#" type="button" data-uri="/visit">Introduire une demande de visite</a>
    <a class="nav-item nav-link btn btn-outline-warning" href="#" type="button" data-uri="/myfurnitures">Mes meubles</a>
    <a class="nav-item nav-link btn btn-outline-warning" href="#" type="button" data-uri="/myvisits">Mes demandes de visite</a>
  </ul>
  <ul class="navbar-nav">
    <div class="text-center">
      <a class="alert alert-warning nav-item nav-link disabled" id="username" href="#">${user.user.username}</a>
      <a type="button" class="nav-item nav-link btn btn-outline-warning" data-uri="/logout" href"#">Se déconnecter</a>
    </div>
  </ul>
  </div>
  </nav>`;
  } else {
    navbar = `<nav class="navbar navbar-expand-lg navbar-light bg-light mb-2" id="navBar">
    <a class="navbar-brand" href="/">
    <img src="${logoNavBar}" width="85" height="85" alt="">
  </a>  <button
    class="navbar-toggler"
    type="button"
    data-toggle="collapse"
    data-target="#navbarNavAltMarkup"
    aria-controls="navbarNavAltMarkup"
    aria-expanded="false"
    aria-label="Toggle navigation"
  >
    <span class="navbar-toggler-icon"></span>
  </button>
  <div class="collapse navbar-collapse" id="navbarNavAltMarkup">
  <ul class="navbar-nav mr-auto">
  <span class="navbar-text">
    Sentier des artistes 1bis <br>
    4800 Verviers <br>
    Belgique
  </span>
</ul>
<ul class="navbar-nav ml-auto px-2 btn-group">
  <a class="nav-item nav-link btn btn-outline-warning" href="#" type="button" data-uri="/">Accueil</a>
  <a class="nav-item nav-link btn btn-outline-warning" href="#" type="button" data-uri="/search">Meubles en magasin</a>
  <a type="button" class="nav-item nav-link btn btn-outline-warning" data-uri="/register" href"#">S'inscrire</a>
  <a type="button" class="nav-item nav-link btn btn-outline-warning" data-uri="/login" href"#">Se connecter</a>
</ul>
  </div>
  </nav>`
  }

  return (navBar.innerHTML = navbar);
};

export default Navbar;
