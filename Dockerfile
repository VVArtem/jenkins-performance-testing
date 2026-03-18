FROM alpine:3.19

RUN apk add --no-cache \
    openjdk17 \
    maven \
    nodejs \
    npm \
    chromium \
    nss \
    freetype \
    harfbuzz \
    ca-certificates \
    ttf-freefont \
    curl \
    libc6-compat \
    gcompat

RUN npm install -g lighthouse puppeteer csv-parse

ENV CHROME_BIN=/usr/bin/chromium-browser \
    PUPPETEER_SKIP_CHROMIUM_DOWNLOAD=true \
    JAVA_HOME="/usr/lib/jvm/default-jvm" \
    _JAVA_OPTIONS="-Djava.net.preferIPv4Stack=true"

RUN curl -L https://archive.apache.org/dist/jmeter/binaries/apache-jmeter-5.6.3.tgz > /tmp/jmeter.tgz && \
    tar -xf /tmp/jmeter.tgz -C /opt && \
    rm /tmp/jmeter.tgz

ENV PATH="$PATH:/opt/apache-jmeter-5.6.3/bin"

WORKDIR /app