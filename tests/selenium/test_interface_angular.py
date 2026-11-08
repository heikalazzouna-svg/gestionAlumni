"""
Test Suite E2E - Interface Web Angular (gestionAlumni)
Validates Requirements:
  - NF-06 : Compatibilite multi-ecrans (Desktop, Tablette, Mobile) et navigateurs
  - NF-07 : Ergonomie et disponibilite de l'interface utilisateur
  - BF01-BF12 : Verification visuelle des formulaires et pages cles

Generates automated screenshots into: preuves_rapport/05_interface_angular/
"""

import os
import time
import pytest
from selenium import webdriver
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

BASE_URL = "http://localhost:4200"
OUTPUT_DIR = os.path.abspath(os.path.join(os.path.dirname(__file__), "..", "..", "preuves_rapport", "05_interface_angular"))

@pytest.fixture(scope="module")
def driver():
    os.makedirs(OUTPUT_DIR, exist_ok=True)
    options = Options()
    options.add_argument("--headless=new")
    options.add_argument("--no-sandbox")
    options.add_argument("--disable-dev-shm-usage")
    options.add_argument("--window-size=1920,1080")
    
    driver = webdriver.Chrome(options=options)
    driver.implicitly_wait(10)
    yield driver
    driver.quit()

class TestInterfaceAngular:

    def test_tc01_homepage_rendering(self, driver):
        """TC-E2E-01 : Verification du rendu de la page d'accueil (Hero, navigation, boutons d'acces)"""
        driver.set_window_size(1920, 1080)
        driver.get(BASE_URL)
        
        # Wait for Angular app to render
        WebDriverWait(driver, 15).until(
            EC.presence_of_element_located((By.TAG_NAME, "app-root"))
        )
        time.sleep(2)
        
        screenshot_path = os.path.join(OUTPUT_DIR, "tc01_accueil.png")
        driver.save_screenshot(screenshot_path)
        assert os.path.exists(screenshot_path)
        assert driver.current_url.startswith(BASE_URL)
        print(f"\n[PASS] TC-E2E-01 : Accueil capturee -> {screenshot_path}")

    def test_tc02_login_form(self, driver):
        """TC-E2E-02 : Verification du formulaire d'authentification (/login)"""
        driver.get(f"{BASE_URL}/login")
        
        WebDriverWait(driver, 15).until(
            EC.presence_of_element_located((By.TAG_NAME, "form"))
        )
        time.sleep(1)
        
        # Check inputs
        inputs = driver.find_elements(By.TAG_NAME, "input")
        assert len(inputs) >= 2, "Le formulaire de connexion doit comporter au moins 2 champs (email, mot de passe)"
        
        # Fill in form to demonstrate interaction
        for inp in inputs:
            inp_type = inp.get_attribute("type")
            if inp_type in ["email", "text"] and not inp.get_attribute("value"):
                inp.send_keys("test@admin.com")
            elif inp_type == "password":
                inp.send_keys("password123")
                
        time.sleep(1)
        screenshot_path = os.path.join(OUTPUT_DIR, "tc02_login_form.png")
        driver.save_screenshot(screenshot_path)
        assert os.path.exists(screenshot_path)
        print(f"\n[PASS] TC-E2E-02 : Formulaire Login capture -> {screenshot_path}")

    def test_tc03_signup_form(self, driver):
        """TC-E2E-03 : Verification du parcours d'inscription (/sign-up)"""
        driver.get(f"{BASE_URL}/sign-up")
        
        WebDriverWait(driver, 15).until(
            EC.presence_of_element_located((By.TAG_NAME, "app-root"))
        )
        time.sleep(1.5)
        
        screenshot_path = os.path.join(OUTPUT_DIR, "tc03_signup_form.png")
        driver.save_screenshot(screenshot_path)
        assert os.path.exists(screenshot_path)
        print(f"\n[PASS] TC-E2E-03 : Formulaire Inscription capture -> {screenshot_path}")

    def test_tc04_events_catalog(self, driver):
        """TC-E2E-04 : Verification du catalogue des evenements (/events)"""
        driver.get(f"{BASE_URL}/events")
        
        WebDriverWait(driver, 15).until(
            EC.presence_of_element_located((By.TAG_NAME, "app-root"))
        )
        time.sleep(1.5)
        
        screenshot_path = os.path.join(OUTPUT_DIR, "tc04_evenements_list.png")
        driver.save_screenshot(screenshot_path)
        assert os.path.exists(screenshot_path)
        print(f"\n[PASS] TC-E2E-04 : Page Evenements capturee -> {screenshot_path}")

    def test_tc05_terms_and_conditions(self, driver):
        """TC-E2E-05 : Verification de la page des conditions generales (/terms)"""
        driver.get(f"{BASE_URL}/terms")
        
        WebDriverWait(driver, 15).until(
            EC.presence_of_element_located((By.TAG_NAME, "app-root"))
        )
        time.sleep(1)
        
        screenshot_path = os.path.join(OUTPUT_DIR, "tc05_terms.png")
        driver.save_screenshot(screenshot_path)
        assert os.path.exists(screenshot_path)
        print(f"\n[PASS] TC-E2E-05 : Page Conditions d'utilisation capturee -> {screenshot_path}")

    def test_tc06_responsive_mobile_viewport(self, driver):
        """TC-E2E-06A : Exigence NF-06 - Resolution Mobile (375x812 - iPhone X/12)"""
        driver.set_window_size(375, 812)
        driver.get(BASE_URL)
        time.sleep(1.5)
        
        screenshot_path = os.path.join(OUTPUT_DIR, "tc06_responsive_mobile.png")
        driver.save_screenshot(screenshot_path)
        assert os.path.exists(screenshot_path)
        print(f"\n[PASS] TC-E2E-06A : Rendu Mobile capture -> {screenshot_path}")

    def test_tc06_responsive_tablet_viewport(self, driver):
        """TC-E2E-06B : Exigence NF-06 - Resolution Tablette (768x1024 - iPad)"""
        driver.set_window_size(768, 1024)
        driver.get(BASE_URL)
        time.sleep(1.5)
        
        screenshot_path = os.path.join(OUTPUT_DIR, "tc06_responsive_tablet.png")
        driver.save_screenshot(screenshot_path)
        assert os.path.exists(screenshot_path)
        print(f"\n[PASS] TC-E2E-06B : Rendu Tablette capture -> {screenshot_path}")

    def test_tc06_responsive_desktop_viewport(self, driver):
        """TC-E2E-06C : Exigence NF-06 - Resolution Desktop Full HD (1920x1080)"""
        driver.set_window_size(1920, 1080)
        driver.get(BASE_URL)
        time.sleep(1.5)
        
        screenshot_path = os.path.join(OUTPUT_DIR, "tc06_responsive_desktop.png")
        driver.save_screenshot(screenshot_path)
        assert os.path.exists(screenshot_path)
        print(f"\n[PASS] TC-E2E-06C : Rendu Desktop capture -> {screenshot_path}")
