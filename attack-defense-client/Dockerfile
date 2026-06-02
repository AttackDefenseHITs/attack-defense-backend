FROM node:20.11.0-slim AS build

WORKDIR /app

COPY package*.json ./
RUN npm install --legacy-peer-deps

COPY . .
RUN npm run build

FROM node:20.11.0-slim
WORKDIR /app

COPY --from=build /app/build ./build
RUN npm install -g serve

EXPOSE 80
CMD ["serve", "-s", "build", "-l", "80"]
