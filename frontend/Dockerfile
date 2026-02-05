FROM node:18-alpine AS build
WORKDIR /app
COPY produit-app/package*.json ./
RUN npm install
COPY produit-app/ .
RUN npm run build --prod

FROM nginx:alpine
COPY --from=build /app/dist/produit-app/browser /usr/share/nginx/html/
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]