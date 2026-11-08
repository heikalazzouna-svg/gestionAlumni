@echo off
echo ========================================================
echo Execution de la suite de tests Postman avec Newman
echo Projet: gestionAlumni
echo ========================================================
echo Assurez-vous que le backend est demarre sur http://localhost:8081 !
echo.
npx newman run gestionAlumni.postman_collection.json -e gestionAlumni_environment.json --reporters cli
pause
