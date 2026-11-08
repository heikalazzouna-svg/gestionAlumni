@echo off
chcp 65001 > nul
title SOUTENANCE & DÉMO DES TESTS - gestionAlumni (ENICarthage)

:MENU
cls
echo ===============================================================================
echo       🎓 PLATEFORME gestionAlumni - DÉMONSTRATION EN DIRECT DES TESTS
echo                    Module : Qualité & Test Logiciel
echo                      Enseignant : M. Faouzi Jaidi
echo ===============================================================================
echo.
echo  --- ÉTAPE PRÉALABLE (À lancer avant l'arrivée du professeur) ---
echo   [1] Démarrer le Backend Spring Boot (Port 8081)
echo   [2] Démarrer le Frontend Angular (Port 4200)
echo.
echo  --- DÉMONSTRATION EN DIRECT PAR ÉTUDIANT ---
echo   [3] 👤 ÉTUDIANT 1 : Tests Unitaires JUnit 5, Mockito & JaCoCo
echo   [4] 👤 ÉTUDIANT 2 : Analyse Statique SpotBugs & Contrats d'API Postman
echo   [5] 👤 ÉTUDIANT 3 : Performance JMeter, Selenium E2E & Lighthouse
echo.
echo  --- DOCUMENTS & RAPPORTS ---
echo   [6] 📄 Ouvrir le Guide PDF de la Soutenance (Discours & Rôles)
echo   [7] 📑 Ouvrir le Rapport Officiel de Test (Markdown)
echo   [8] 📊 Ouvrir le Dashboard de Simulation TestRail (Figure 2.1 & 7.1)
echo.
echo   [0] Quitter
echo ===============================================================================
set /p choice="Choisissez une option [1-8] ou 0 : "

if "%choice%"=="1" goto START_BACKEND
if "%choice%"=="2" goto START_FRONTEND
if "%choice%"=="3" goto DEMO_ETUDIANT_1
if "%choice%"=="4" goto DEMO_ETUDIANT_2
if "%choice%"=="5" goto DEMO_ETUDIANT_3
if "%choice%"=="6" goto OPEN_PDF
if "%choice%"=="7" goto OPEN_RAPPORT
if "%choice%"=="8" goto OPEN_TESTRAIL
if "%choice%"=="0" exit
goto MENU

:START_BACKEND
cls
echo [1/2] Démarrage du Backend Spring Boot sur le port 8081...
echo Une nouvelle fenêtre va s'ouvrir. Ne la fermez pas !
start "Spring Boot Backend (Port 8081)" cmd /k "cd /d C:\Users\LENOVO\Desktop\spring\gestionAlumni && set JAVA_HOME=C:\Users\LENOVO\Desktop\jdk-17.0.12&& set PATH=C:\Users\LENOVO\Desktop\jdk-17.0.12\bin;%%PATH%%&& mvnw.cmd spring-boot:run"
echo.
echo Backend lancé dans une fenêtre séparée.
pause
goto MENU

:START_FRONTEND
cls
echo [2/2] Démarrage du Frontend Angular sur le port 4200...
echo Une nouvelle fenêtre va s'ouvrir. Ne la fermez pas !
start "Angular Frontend (Port 4200)" cmd /k "npx --yes serve -s C:\Users\LENOVO\Desktop\spring\ProjetWeb\dist\demo-web\browser -l 4200"
echo.
echo Frontend lancé dans une fenêtre séparée.
pause
goto MENU

:DEMO_ETUDIANT_1
cls
echo ===============================================================================
echo   👤 ÉTUDIANT 1 : Tests Unitaires & Intégration (JUnit 5, Mockito, MockMvc)
echo ===============================================================================
echo.
echo Commande exécutée : mvnw.cmd test
echo.
cd /d C:\Users\LENOVO\Desktop\spring\gestionAlumni
set JAVA_HOME=C:\Users\LENOVO\Desktop\jdk-17.0.12
set PATH=C:\Users\LENOVO\Desktop\jdk-17.0.12\bin;%PATH%
call mvnw.cmd test
echo.
echo Ouverture du rapport JaCoCo dans votre navigateur...
if exist "target\site\jacoco\index.html" (
    start target\site\jacoco\index.html
)
echo.
pause
goto MENU

:DEMO_ETUDIANT_2
cls
echo ===============================================================================
echo   👤 ÉTUDIANT 2 : Analyse Statique SpotBugs & Contrats d'API Postman
echo ===============================================================================
echo.
echo [1/2] Exécution de SpotBugs (42 classes)...
cd /d C:\Users\LENOVO\Desktop\spring\gestionAlumni
set JAVA_HOME=C:\Users\LENOVO\Desktop\jdk-17.0.12
set PATH=C:\Users\LENOVO\Desktop\jdk-17.0.12\bin;%PATH%
call mvnw.cmd spotbugs:spotbugs
echo.
echo [2/2] Exécution des 13 requêtes Postman via Newman...
cd /d C:\Users\LENOVO\Desktop\spring\gestionAlumni\tests\postman
call npx newman run gestionAlumni.postman_collection.json -e gestionAlumni_environment.json
echo.
pause
goto MENU

:DEMO_ETUDIANT_3
cls
echo ===============================================================================
echo   👤 ÉTUDIANT 3 : Performance (BNF01), Selenium E2E & Lighthouse
echo ===============================================================================
echo.
echo [1/3] Test de Montée en Charge Autocannon (Exigence BNF01 sur l'Annuaire)...
call npx autocannon -c 10 -d 5 http://localhost:8081/api/alumni/directory
echo.
echo [2/3] Exécution des 8 tests Selenium E2E (Chrome Headless)...
cd /d C:\Users\LENOVO\Desktop\spring\gestionAlumni
call pytest tests\selenium\test_interface_angular.py -v
echo.
echo [3/3] Ouverture des rapports Google Lighthouse et TestRail...
start "" "C:\Users\LENOVO\Desktop\spring\gestionAlumni\preuves_rapport\05_interface_angular\lighthouse_accueil_desktop.report.html"
start "" "C:\Users\LENOVO\Desktop\spring\gestionAlumni\tests\testrail\testrail_dashboard_simulation.html"
echo.
pause
goto MENU

:OPEN_PDF
cls
echo Ouverture du Guide PDF de Soutenance...
start "" "C:\Users\LENOVO\Desktop\GUIDE_SOUTENANCE_ET_DEMO_3_PERSONNES.pdf"
pause
goto MENU

:OPEN_RAPPORT
cls
echo Ouverture du Rapport Complet de Test...
start "" "C:\Users\LENOVO\Desktop\spring\gestionAlumni\RAPPORT_DE_TEST_GESTION_ALUMNI.md"
pause
goto MENU

:OPEN_TESTRAIL
cls
echo Ouverture de la simulation TestRail Cloud...
start "" "C:\Users\LENOVO\Desktop\spring\gestionAlumni\tests\testrail\testrail_dashboard_simulation.html"
pause
goto MENU
