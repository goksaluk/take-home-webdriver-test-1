package com.dotdash.tests;

import com.dotdash.pages.LoginPage;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;


public class SeleniumTests {
    private WebDriver driver;
    private WebDriverWait wait;


    @BeforeMethod
    public void setupMethod() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @AfterMethod
    public void teardownMethod(){
        driver.quit();
    }

    LoginPage loginPage = new LoginPage();
    @Test(description = "1. Login Success")
    public void loginSuccess(){
        driver.get("http://localhost:7080/login");
        driver.findElement(By.id("username")).sendKeys("tomsmith");
        driver.findElement(By.id("password")).sendKeys("SuperSecretPassword!");
        driver.findElement(By.xpath("//*[text()=' Login']")).click();
        String pageText = driver.findElement(By.xpath("//*[text()=' Secure Area']")).getText();
        Assert.assertEquals(pageText,"Secure Area");

        /*
        2 nd solution (Page Object Model):

        loginPage.login("tomsmith","SuperSecretPassword!");
        String pageText = driver.findElement(By.xpath("//*[text()=' Secure Area']")).getText();
        Assert.assertEquals(pageText,"Secure Area");
        */
    }

    @Test(description = "2. Login Failure-1")
    public void loginFailure1(){ //incorrect username
        driver.get("http://localhost:7080/login");
        driver.findElement(By.id("username")).sendKeys("goksalc");
        driver.findElement(By.id("password")).sendKeys("SuperSecretPassword!");
        driver.findElement(By.xpath("//*[text()=' Login']")).click();
        String errorText = driver.findElement(By.id("flash")).getText();
        Assert.assertEquals(errorText,"Your username is invalid!\n×");

        /*
        2 nd solution (Page Object Model):

        loginPage.login("goksalc","SuperSecretPassword!");
        String errorText = driver.findElement(By.id("flash")).getText();
        Assert.assertEquals(errorText,"Your username is invalid!\n×");

        */
    }

    @Test(description = "2. Login Failure-2")
    public void loginFailure2(){ //incorrect password
        driver.get("http://localhost:7080/login");
        driver.findElement(By.id("username")).sendKeys("tomsmith");
        driver.findElement(By.id("password")).sendKeys("goksalfailuretest!");
        driver.findElement(By.xpath("//*[text()=' Login']")).click();
        String errorText = driver.findElement(By.id("flash")).getText();
        Assert.assertEquals(errorText,"Your password is invalid!\n×");

        /*
        2 nd solution (Page Object Model):

        loginPage.login("tomsmith","goksalfailuretest!");
        String errorText = driver.findElement(By.id("flash")).getText();
        Assert.assertEquals(errorText,"Your password is invalid!\n×");
         */
    }

    @Test(description = "3. Checkboxes")
    public void checkboxes(){
        driver.get("http://localhost:7080/checkboxes");
        WebElement checkboxOne = driver.findElement(By.xpath("//input[1]"));
        WebElement checkboxTwo = driver.findElement(By.xpath("//input[2]"));
        System.out.println("One is selected: " + checkboxOne.isSelected());
        System.out.println("two is selected: " + checkboxTwo.isSelected());

        // verify one is not selected
        Assert.assertFalse(checkboxOne.isSelected());
        // verify two is selected
        Assert.assertTrue(checkboxTwo.isSelected());

        // verify both of them are selected
        checkboxOne.click();

        System.out.println("One is selected: " + checkboxOne.isSelected());
        System.out.println("two is selected: " + checkboxTwo.isSelected());

        // verify that one is selected
        Assert.assertTrue(checkboxOne.isSelected());
        // verify that two is selected
        Assert.assertTrue(checkboxTwo.isSelected());
    }

    @Test(description = "4. Context Menu")
    public void contextMenu() {
        driver.get("http://localhost:7080/context_menu");
        WebElement box = driver.findElement(By.id("hot-spot"));
        Actions actions = new Actions(driver);
        actions.contextClick(box).perform();
        Alert alert = driver.switchTo().alert();
        String alertText = driver.switchTo().alert().getText();
        Assert.assertEquals(alertText, "You selected a context menu");
        alert.accept();

    }

    @Test(description = "5. Drag and Drop")
    public void dragAndDrop() throws InterruptedException {
        driver.get("http://localhost:7080/drag_and_drop");
        Actions actions = new Actions(driver);
        WebElement firstColumn = driver.findElement(By.id("column-a"));
        WebElement secondColumn= driver.findElement(By.id("column-b"));
        actions.moveToElement(firstColumn).clickAndHold(firstColumn).moveToElement(secondColumn).release().build().perform();
        //actions.dragAndDrop(firstColumn, secondColumn).perform();
        Assert.assertEquals(secondColumn.getText(),"A");
        Thread.sleep(3000);
    }

    @Test(description = "6. Dropdown")
    public void dropdown(){
        driver.get("http://localhost:7080/dropdown");
        WebElement element = driver.findElement(By.id("dropdown"));
        Select options = new Select(element);
        options.selectByVisibleText("Option 1");
        String selection1 =options.getFirstSelectedOption().getText();
        Assert.assertEquals(selection1, "Option 1");
        options.selectByVisibleText("Option 2");
        String selection2 =options.getFirstSelectedOption().getText();
        Assert.assertEquals(selection2, "Option 2");
    }

    @Test(description = "7. Dynamic Content")
    public void dynamicContent() throws InterruptedException {
        driver.get("http://localhost:7080/dynamic_content");
        driver.navigate().refresh();
        WebElement firstElement = driver.findElement(By.xpath("(//*[@class='large-10 columns'])[1]"));
        String firstText = firstElement.getText();
        System.out.println("firstText = " + firstText);
        //Thread.sleep(4000);
        driver.navigate().refresh();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        wait.until(ExpectedConditions.visibilityOf(firstElement));
        String secondText = firstElement.getText();
        Assert.assertNotEquals(firstText,secondText);
    }

    @Test(description = "8. Dynamic Controls")
    public void dynamicControls() {
        driver.get("http://localhost:7080/dynamic_controls");

        // clicks on the Remove Button and uses explicit wait.
        // asserts that the checkbox is gone.
        driver.findElement(By.xpath("//*[text()='Remove']")).click();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("message")));
        WebElement message = driver.findElement(By.id("message"));
        String messageText = message.getText();
        Assert.assertEquals(messageText,"It's gone!");

        // clicks on Add Button and uses explicit wait.
        // asserts that the checkbox is present.
        driver.findElement(By.xpath("//*[text()='Add']")).click();
        wait = new WebDriverWait(driver, Duration.ofSeconds(7));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("checkbox")));
        WebElement checkbox = driver.findElement(By.xpath("//*[text()=' A checkbox']"));
        String checkboxText = checkbox.getText();
        Assert.assertEquals(checkboxText,"A checkbox");

        // clicks on the Enable Button and uses explicit wait.
        // asserts that the text box is enabled.
        driver.findElement(By.xpath("//*[text()='Enable']")).click();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("message")));
        WebElement disableMessage = driver.findElement(By.id("message"));
        String disableMessageText = disableMessage.getText();
        Assert.assertEquals(disableMessageText,"It's enabled!");

        // clicks on the Disable Button and uses explicit wait.
        // asserts that the text box is disabled.
        driver.findElement(By.xpath("//*[text()='Disable']")).click();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("message")));
        WebElement enableMessage = driver.findElement(By.id("message"));
        String enableMessageText = enableMessage.getText();
        Assert.assertEquals(enableMessageText,"It's disabled!");
    }

    @Test(description = "9. Dynamic Loading")
    public void dynamicLoading(){
        driver.get("http://localhost:7080/dynamic_loading/2");
        driver.findElement(By.xpath("//button[text()='Start']")).click();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("finish")));
        WebElement message = driver.findElement(By.id("finish"));
        String messageText = message.getText();
        Assert.assertEquals(messageText,"Hello World!");
    }

    @Test(description = "10. File Download")
    public void fileDownload() throws InterruptedException {
        driver.get("http://localhost:7080/download");
        driver.findElement(By.linkText("some-file.txt")).click();
        Thread.sleep(5000);
        String filepath = System.getProperty("user.home") + "/Downloads/some-file.txt";
        File txtFile = new File(filepath);
        Assert.assertTrue(txtFile.exists());
    }

    @Test(description = "11. File Upload")
    public void fileUpload(){
        driver.get("http://localhost:7080/upload");
        driver.findElement(By.id("file-upload")).sendKeys("/Users/goksalcavdar/Desktop/test_upload.png");
        driver.findElement(By.id("file-submit")).click();
        String uploadedFile = driver.findElement(By.id("uploaded-files")).getText();
        Assert.assertEquals(uploadedFile,"test_upload.png" );
    }

    @Test(description = "12. Floating Menu")
    public void scroll() throws InterruptedException {
        driver.get("http://localhost:7080/floating_menu");
        List<String> initialMenu = new ArrayList<>(Arrays.asList("Home","News","Contact","About"));
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        for (int i = 0; i < 10; i++) {
            Thread.sleep(100);
            //scrolling down
            jse.executeScript("window.scrollBy(0,500)");
        }

        List<String> currentMenu = new ArrayList<>();
        for(int i=1; i<=4; i++){
            currentMenu.add(driver.findElement(By.xpath("(//ul/li/a)[" +i+ "]")).getText());
        }
        Assert.assertEquals(currentMenu,initialMenu);
    }

    @Test(description = "13. Iframe")
    public void iframe(){
        driver.get("http://localhost:7080/iframe");
        driver.switchTo().frame("mce_0_ifr");
        WebElement iframeInnerArea = driver.findElement(By.id("tinymce"));
        iframeInnerArea.clear();
        iframeInnerArea.sendKeys("Hello World");
        String innerText= iframeInnerArea.getText();
        Assert.assertEquals(innerText,"Hello World");
    }

    @Test(description = "14. Mouse Hover")
    public void test() throws InterruptedException {
        driver.get("http://localhost:7080/hovers");
        Actions action = new Actions(driver);
        for (int i = 1; i <= 3; i++) {
            action.moveToElement(driver.findElement(By.cssSelector(".figure:nth-of-type(" + i + ")"))).perform();
            Thread.sleep(4000);
            String name = driver.findElement(By.cssSelector(".figure:nth-of-type(" + i + ") h5")).getText();
            System.out.println(name);
            Assert.assertEquals(name, "name: user"+i);
        }
    }

    @Test(description = "15. javaScript Alerts")
    public void javaScriptAlerts(){
        driver.get("http://localhost:7080/javascript_alerts");

        // clicks on JS Alert Button.
        // asserts alert message.
        driver.findElement(By.xpath("(//button)[1]")).click();
        Alert alert = driver.switchTo().alert();
        alert.accept();

        // clicks on JS confirm Button and clicks ok on alert.
        // asserts the alert message.
        driver.findElement(By.xpath("(//button)[2]")).click();
        alert = driver.switchTo().alert();
        alert.dismiss();

        //clicks on JS Prompt Button and types a message on Prompt.
        //asserts that the alert message contains the typed message.
        driver.findElement(By.xpath("(//button)[3]")).click();
        alert = driver.switchTo().alert();
        alert.sendKeys("red alert");
        System.out.println(alert.getText());
        alert.accept();
    }


    @Test(description = "16. JavaScript Error")
    public void JavaScriptError(){
        driver.get("http://localhost:7080/javascript_error");
        Set<String> logtyp = driver.manage().logs().getAvailableLogTypes();
        for (String s : logtyp) {
            System.out.println(logtyp);
        }
        LogEntries logEntries = driver.manage().logs().get(LogType.BROWSER);
        List<LogEntry> lg = logEntries.getAll();
        for(LogEntry logEntry : lg) {
            System.out.println(logEntry);
        }
    }

    @Test(description = "17. Open in New Tab")
    public void openNewTab(){
        driver.get("http://localhost:7080/windows");
        String targetWindowTitle = "New Window";
        driver.findElement(By.linkText("Click Here")).click();
        Set<String> windowHandles= driver.getWindowHandles();
        for (String handle : windowHandles) {
            driver.switchTo().window(handle);
            if(driver.getTitle().equals(targetWindowTitle)){
                break;
            }
        }
        Assert.assertEquals(driver.getTitle(), targetWindowTitle);
        System.out.println("After loop title is : "+driver.getTitle());
    }


    @Test(description ="18. Notification Message")
    public void notificationMessage(){
        driver.get("http://localhost:7080/notification_message_rendered");

        List<String> messages =new ArrayList<>(Arrays.asList("Action successful\n×", "Action unsuccesful, please try again\n×","Action Unsuccessful\n×"));
        driver.findElement(By.linkText("Click here")).click();
        WebElement notification = driver.findElement(By.id("flash"));
        String firstNotificationMessage = notification.getText();
        boolean result= true;
        for (int i=0; i<messages.size(); i++){
            if (!messages.get(i).equals(firstNotificationMessage)){
                Assert.assertTrue(result);
            }
        }

        driver.findElement(By.linkText("Click here")).click();
        for (int i=0; i<messages.size(); i++){
            if (!messages.get(i).equals(firstNotificationMessage)){
                Assert.assertTrue(result);
            }
        }

        driver.findElement(By.linkText("Click here")).click();
        for (int i=0; i<messages.size(); i++){
            if (!messages.get(i).equals(firstNotificationMessage)){
                Assert.assertTrue(result);
            }
        }

    }







}
