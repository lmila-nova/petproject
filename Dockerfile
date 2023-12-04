FROM gradle:8.5-jdk11-alpine
USER root
COPY --chown=gradle:gradle . /
WORKDIR /