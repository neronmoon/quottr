FROM "clojure:lein-2.7.1-alpine" as builder
WORKDIR /app
COPY . .
RUN lein do clean, cljsbuild once min

FROM nginx
COPY ./.docker/quottr.conf /etc/nginx/conf.d/default.conf
COPY --from=builder /app/resources/public /app

