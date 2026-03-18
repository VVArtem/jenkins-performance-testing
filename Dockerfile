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
    curl

# Install lighthouse
RUN npm install -g lighthouse

# Install JMeter
RUN curl -L https://archive.apache.org/dist/jmeter/binaries/apache-jmeter-5.6.3.tgz > /tmp/jmeter.tgz && \
    tar -xf /tmp/jmeter.tgz -C /opt && \
    rm /tmp/jmeter.tgz

# Додаємо JMeter у PATH
ENV PATH="$PATH:/opt/apache-jmeter-5.6.3/bin"
ENV JAVA_HOME="/usr/lib/jvm/default-jvm"

WORKDIR /app