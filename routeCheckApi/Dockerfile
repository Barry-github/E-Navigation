FROM java:8-jre-alpine

ENV TIME_ZONE Asia/Shanghai
RUN apk add --no-cache tzdata
RUN echo "${TIME_ZONE}" > /etc/timezone
RUN ln -snf /usr/share/zoneinfo/${TIME_ZONE} /etc/localtime


RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.ustc.edu.cn/' /etc/apk/repositories
ADD target/*.jar routeCheckApi.jar
RUN sh -c 'touch /routeCheckApi.jar'
RUN echo $(date) > /image_built_at
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/routeCheckApi.jar"]