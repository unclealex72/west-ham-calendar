web: target/universal/stage/bin/west-ham-calendar -Dhttp.port=${PORT} -Dsilhouette.ssl=${USE_SSL} -Dsilhouette.google.clientId=${GOOGLE_CLIENT_ID} -Dsilhouette.google.clientSecret=${GOOGLE_SECRET} -Dsecret=${SECRET} -Dvalid-users.users=${VALID_USERS} -Dplay.evolutions.db.default.autoApply=true -Dslick.dbs.default.driver="slick.driver.PostgresDriver$" -Dslick.dbs.default.db.driver=org.postgresql.Driver -Dslick.dbs.default.db.url=${DATABASE_URL} -Dslick.dbs.default.db.user=${DATABASE_USER} -Dslick.dbs.default.db.password=${DATABASE_PASSWORD} -Dlogger.root=INFO -Dlogger.application=INFO slick.dbs.default.db.connectionTestQuery="select 1"
