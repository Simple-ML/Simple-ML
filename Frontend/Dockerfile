FROM node:17-alpine as builder

WORKDIR /tmp/

COPY package.json /tmp/
RUN npm install

COPY . /tmp/
RUN npm run build


FROM node:17-alpine as runner

WORKDIR /app/

RUN npm install -g serve@12.0.0

COPY --from=builder \
    /tmp/build/ \
    /app/

EXPOSE 4200

CMD ["serve", "-l", "4200"]
