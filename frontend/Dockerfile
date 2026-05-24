# Constrói a imagem do frontend em duas fases para o servidor final não levar Node nem dependências de desenvolvimento, a primeira instala as dependências npm e compila a SPA com Vite, a segunda serve os ficheiros estáticos com nginx na porta 80 usando a config nginx.conf que faz proxy para a API.
FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

FROM nginx:1.27-alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
