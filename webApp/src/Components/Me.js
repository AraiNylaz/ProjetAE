import {getUserSessionData,removeSessionData,updateUserInStorage} from "../utils/session.js";
import {callAPI} from "../utils/api.js";


const Me = async () => {
    let user = getUserSessionData();
    if(user) {
      try{
        user = await callAPI("/api/users/me", "GET", user.token);
        updateUserInStorage(user);
      } catch(err){
        removeSessionData();
      }
    }
}

export default Me;
