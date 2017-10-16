FROM java:8-jdk-alpine

ENV LANG C.UTF-8
ENV TZ Asia/Tokyo

RUN mkdir /home/work
RUN apk add --update bash unzip

COPY ./run.sh /home/work
COPY ./play-catch-up-1.0-SNAPSHOT.zip /home/work

RUN addgroup -g 1001 executer\
    && adduser -u 1001 -D -G executer executer

USER executer

WORKDIR /home/work
RUN unzip play-catch-up-1.0-SNAPSHOT.zip -d /home/executer/\
    && cp -i run.sh /home/executer/\
    && chmod 755 /home/executer/run.sh\
    && chmod 755 /home/executer/play-catch-up-1.0-SNAPSHOT/bin/

WORKDIR /home/executer
CMD ["ash", "./run.sh"]
