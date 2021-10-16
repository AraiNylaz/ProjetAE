const PrintError = (err) => {
  if (!err) return;
  let page = document.querySelector("#page");
 
  if(document.getElementById("errorBoard") == null){
    const errorDiv = document.createElement("div");
    errorDiv.className = "alert alert-danger mt-2";
    errorDiv.id = "errorBoard";
    errorDiv.innerText = err.message;  
    page.appendChild(errorDiv);
  }else{
    document.querySelector("#errorBoard").innerText = err.message;
  }
  
};

export default PrintError;
