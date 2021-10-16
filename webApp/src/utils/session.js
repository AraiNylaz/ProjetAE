const STORE_NAME = "user";

const THEME = "theme";

const getUserSessionData = () => {
  const retrievedUserSession = sessionStorage.getItem(STORE_NAME);
  if (retrievedUserSession) return JSON.parse(retrievedUserSession);
  const retrievedUser = localStorage.getItem(STORE_NAME);
  if (!retrievedUser) return;
  return JSON.parse(retrievedUser);
};

const updateUserInStorage = (user) => {
  let retrievedUser = sessionStorage.getItem(STORE_NAME);
  if (retrievedUser) {
    retrievedUser = JSON.parse(retrievedUser);
    retrievedUser.user = user;
    setUserSessionData(retrievedUser);
    return;
  }
  retrievedUser = localStorage.getItem(STORE_NAME);
  if (!retrievedUser) return;
  retrievedUser = JSON.parse(retrievedUser);
  retrievedUser.user = user;
    setUserLocalData(retrievedUser);
};

const getUserInSessionStorage = () => {
  const user = sessionStorage.getItem(STORE_NAME);
  return JSON.parse(user);
}

const getUserInLocalStorage = () => {
  const user = localStorage.getItem(STORE_NAME);
  return JSON.parse(user);
}

const setUserLocalData = (user) => {
  const storageValue = JSON.stringify(user);
  localStorage.setItem(STORE_NAME, storageValue);
};

const setUserSessionData = (user) => {
  const storageValue = JSON.stringify(user);
  sessionStorage.setItem(STORE_NAME, storageValue);
};

const getTheme = () => {
  const theme = localStorage.getItem(THEME);
  if (!theme) return;
  return JSON.parse(theme);
};

const setTheme = (theme) => {
  const storageValue = JSON.stringify(theme);
  localStorage.setItem(THEME, storageValue);
};

const removeSessionData = () => {
  localStorage.removeItem(STORE_NAME);
  localStorage.removeItem(THEME);
  sessionStorage.removeItem(STORE_NAME);
  sessionStorage.removeItem(THEME);
};

export {
  getUserInSessionStorage,
  getUserInLocalStorage,
  getUserSessionData,
  setUserSessionData,
  setUserLocalData,
  removeSessionData,
  updateUserInStorage,
  getTheme,
  setTheme,
};
