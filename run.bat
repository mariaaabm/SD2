@echo off
:: Arranca SportFlow em Windows numa única janela CMD, abre o backend Spring Boot com perfil dev numa janela separada, espera 20 segundos para o backend ficar pronto, lança o frontend Vite noutra janela depois de instalar dependências, e abre o browser na página inicial.
echo A arrancar SportFlow...

start "Backend" cmd /k "cd backend && mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev"

timeout /t 20 /nobreak >nul

start "Frontend" cmd /k "cd frontend && npm install && npm run dev"

timeout /t 5 /nobreak >nul

start http://localhost:5173

echo.
echo Backend:  http://localhost:8080
echo Frontend: http://localhost:5173
echo Login:    admin@store.test / password
