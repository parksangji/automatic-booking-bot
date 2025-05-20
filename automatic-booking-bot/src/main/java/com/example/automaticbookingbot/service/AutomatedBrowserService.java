package com.example.automaticbookingbot.service;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class AutomatedBrowserService {
    private static final Logger log = LoggerFactory.getLogger(AutomatedBrowserService.class);

    public boolean loginToSite(String siteUrl, String loginId, String encryptedPassword) {
        log.info("Attempting to login to site: {} with user ID: {}", siteUrl, loginId);

        String decryptedPassword = encryptedPassword;

        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        // options.addArguments("--headless"); // UI 없이 백그라운드 실행
        options.addArguments("--disable-gpu"); // GPU 가속 비활성화 (headless 시 권장)
        options.addArguments("--window-size=1920,1080"); // 창 크기 설정
        options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36");

        WebDriver driver = null;
        try {
            driver = new ChromeDriver(options);
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10)); // 암묵적 대기

            driver.get(siteUrl);
            log.info("Navigated to {}", siteUrl);

            // --- !!! 중요: 아래 요소 선택자는 실제 대상 사이트의 HTML 구조에 맞게 수정해야 합니다 !!! ---
            WebElement usernameField = driver.findElement(By.id("username")); // 예시 ID
            WebElement passwordField = driver.findElement(By.id("password")); // 예시 ID
            WebElement loginButton = driver.findElement(By.id("loginButton")); // 예시 ID
            // 또는 By.name("username"), By.xpath("//input[@type='submit']"), By.cssSelector(".login-form #user") 등 사용

            usernameField.sendKeys(loginId);
            passwordField.sendKeys(decryptedPassword);
            log.info("Username and password entered.");

            loginButton.click();
            log.info("Login button clicked.");

            // 로그인 후 특정 요소가 나타날 때까지 대기 (예시: 사용자 프로필 요소)
            // WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            // wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("userProfile"))); // 예시 ID

            log.info("Login attempt to {} finished. Current URL: {}", siteUrl, driver.getCurrentUrl());
            return true;

        } catch (NoSuchElementException e) {
            log.error("Could not find login elements on {}: {}", siteUrl, e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("An error occurred during automated login to {}: {}", siteUrl, e.getMessage(), e);
            return false;
        } finally {
            if (driver != null) {
                driver.quit();
                log.info("WebDriver session for {} quit.", siteUrl);
            }
        }
    }
}
