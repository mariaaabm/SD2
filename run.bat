@echo off
echo A arrancar SportFlow...

start "Backend" cmd /k "cd backend && mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=demo"

timeout /t 20 /nobreak >nul

start "Frontend" cmd /k "cd frontend && npm install && npm run dev"

timeout /t 5 /nobreak >nul

start http://localhost:5173

echo.
echo Backend:  http://localhost:8080
echo Frontend: http://localhost:5173
echo Login:    admin@store.test / password
