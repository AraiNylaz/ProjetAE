package be.vinci.pae.functional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import io.github.bonigarcia.wdm.WebDriverManager;

class AuthenticationTestNotJenkins {

  private WebDriver webDriver;
  private String baseUrl;

  @BeforeAll
  public static void setUpChromeDriver() {
    WebDriverManager.chromedriver().setup();
  }

  @BeforeEach
  public void setUpWebDriver() {
    ChromeOptions options = new ChromeOptions();
    options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1200",
        "--ignore-certificate-errors", "--disable-extensions", "--no-sandbox",
        "--disable-dev-shm-usage");
    webDriver = new ChromeDriver(options);
    baseUrl = "http://localhost/login";
  }

  @AfterEach
  public void quitWebDriver() {
    if (webDriver != null) {
      webDriver.quit();
    }
  }

  @Test
  public void authenticateUserGoodUsernameAndGoodPassword() {
    webDriver.get(baseUrl);
    // On récupère les champs du formulaire de connexion
    final WebElement usernameField = webDriver.findElement(By.id("login"));
    final WebElement passwordField = webDriver.findElement(By.id("password"));
    final WebElement submitButton = webDriver.findElement(By.id("btn"));

    // On remplit le formulaire avec les éléments souhaités
    usernameField.sendKeys("test");
    passwordField.sendKeys("test");
    submitButton.click();

    // Va nous faire attendre 5 secondes
    final WebDriverWait waiter = new WebDriverWait(webDriver, 5);
    // On récupère la réponse attendue
    final WebElement solutionElement =
        waiter.until(ExpectedConditions.presenceOfElementLocated(By.id("username")));
    final String solution = solutionElement.getText();
    // On vérifie que c'est correct
    assertAll(() -> assertTrue(solution.equals("test")));
  }

  @Test
  public void authenticateUserEmptyUsernameAndEmptyPassword() {
    webDriver.get(baseUrl);
    final WebElement usernameField = webDriver.findElement(By.id("login"));
    final WebElement passwordField = webDriver.findElement(By.id("password"));
    final WebElement submitButton = webDriver.findElement(By.id("btn"));

    // Va servir a enlever les attributs required du form
    JavascriptExecutor executor = (JavascriptExecutor) webDriver;
    executor.executeScript("window.document.getElementById(\"login\").required = false;");
    executor.executeScript("window.document.getElementById(\"password\").required = false;");

    usernameField.sendKeys("");
    passwordField.sendKeys("");
    submitButton.click();

    final WebDriverWait waiter = new WebDriverWait(webDriver, 5);
    final WebElement solutionElement =
        waiter.until(ExpectedConditions.presenceOfElementLocated(By.id("errorBoard")));

    final String solution = solutionElement.getText();
    assertAll(() -> assertTrue(solution.equals("Veuillez remplir les champs")));
  }

  @Test
  public void authenticateUserGoodPseudoAndBadPassword() {
    webDriver.get(baseUrl);
    final WebElement usernameField = webDriver.findElement(By.id("login"));
    final WebElement passwordField = webDriver.findElement(By.id("password"));
    final WebElement submitButton = webDriver.findElement(By.id("btn"));

    usernameField.sendKeys("test");
    passwordField.sendKeys("badPassword");
    submitButton.click();

    final WebDriverWait waiter = new WebDriverWait(webDriver, 5);
    final WebElement solutionElement =
        waiter.until(ExpectedConditions.presenceOfElementLocated(By.id("errorBoard")));
    final String solution = solutionElement.getText();
    assertAll(() -> assertTrue(solution.equals("Pseudo ou mot de passe incorrect")));
  }

}
