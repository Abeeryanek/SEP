# Frontend Dockerfile
# 1. Build Stage
FROM node:18-alpine AS build
WORKDIR /app
COPY . .
RUN npm install && npm run build --prod

# 2. Production Stage
FROM nginx:alpine
COPY --from=build /app/dist/frontend/browser /usr/share/nginx/html
COPY --from=build /app/node_modules/leaflet/dist/images/ /usr/share/nginx/html/assets/leaflet/images/
COPY --from=build /app/node_modules/leaflet/dist/leaflet.css /usr/share/nginx/html/assets/leaflet/leaflet.css
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
