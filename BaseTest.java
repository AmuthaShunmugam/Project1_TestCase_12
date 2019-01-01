package com.ibm.test;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.ibm.pages.AdminPage;
import com.ibm.pages.AdminPage1;
import com.ibm.pages.UserPage;
import com.ibm.utilities.ExcelUtil;
import com.ibm.utilities.PropertiesFileHandler;

public class BaseTest {
	WebDriver driver;
	WebDriverWait wait;
	PropertiesFileHandler propFIleHandler;
	HashMap<String, String> data;

	@BeforeSuite
	public void propertiesfile() throws IOException {
		String file = "./TestData/data.properties";
		PropertiesFileHandler propFileHandler = new PropertiesFileHandler();
		data = propFileHandler.getPropertiesAsMap(file);
	}

	@BeforeMethod
	public void BrowserInitialization() {
		System.setProperty("webdriver.chrome.driver", "./drivers/chromedriver.exe");
		driver = new ChromeDriver();
		wait = new WebDriverWait(driver, 60);
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
	}

	@Test
	public void ModifyBannerNameVerifyDatabase() throws InterruptedException, SQLException {
		
		String url = data.get("url");
		String userName = data.get("username");
		String password = data.get("password");
		String Bannername=data.get("Banner");
		String SuccessMessage=data.get("BannerSuccess");
		String ErrorMessage=data.get("ErrorMsg");
		driver.get(url);
		System.out.println("Modify a banner, Check for the success message, Check for the presence of modified record in admin table, Check for the presence of record in the database");
		AdminPage1 home = new AdminPage1(driver, wait);
		home.EnetrEmailAddress(userName);
		home.EnetrPassword(password);
		home.ClickonLoginButton();
		home.ClickonCatalogTabButton();
		home.ClickOnBanner();
		home.ClickOnBannerAction();
		home.ClickOnBannerEdit();
		home.EnterBannerName();
		home.ClickonTheSaveButton();
		//1.Validate the presence of modifies banner header
		WebElement Banner= driver.findElement(By.xpath("//table[@id='dataTableExample2']/tbody/tr[1]/td[2]"));
		String Banner1=Banner.getText();
		System.out.println("The new Banner added is :" +Banner1);
		Assert.assertEquals(Banner1,Bannername);
		//3.1.Validate the Success Message
		WebElement Msg=driver.findElement(By.xpath("//div[@class='alert alert-success alert-dismissible']"));
		String Success=Msg.getText().replace("×","").trim();
		System.out.println("The success message is :" +Success);
		Assert.assertEquals(Success,SuccessMessage);
		//3.2 Validate the modified record data 'ForTestChange' is the newly added reocrd
		Assert.assertEquals(Banner1, Bannername);
		//3.3 Validate the presence of modified record in db table
		Connection c = DriverManager.getConnection("jdbc:mysql://foodsonfinger.com:3306/foodsonfinger_atozgroceries",
				"foodsonfinger_atoz", "welcome@123");
		Statement s = c.createStatement();
		ResultSet rs = s.executeQuery("SELECT * from as_banner where name='ForTestChange'  ");
		   while (rs.next()) 
			{
				System.out.println("The coupon name added:" +rs.getString("name"));
				Assert.assertEquals(rs.getString("name"), Bannername);
			}
		   //2.Validate the error message
		   //Login with the user id alone and click on login button and validate the error message
		   System.out.println("To Check the error message");
		   driver.navigate().to("https://atozgroceries.com/admin");
		   home.EnetrEmailAddress(userName);
	   	   home.ClickonLoginButton();
		   WebElement Error=driver.findElement(By.xpath("//div[@class='alert alert-danger alert-dismissible']"));
		   String ErrMessage= Error.getText().replace("×","").trim();
		   System.out.println("The error message:" +ErrMessage);
           Assert.assertEquals(ErrMessage, ErrorMessage);
	}

		
}





