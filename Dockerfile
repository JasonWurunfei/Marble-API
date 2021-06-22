FROM openjdk:8-alpine
COPY target/marble.jar /root/
WORKDIR /root
CMD ["java", "-jar", "marble.jar", "--spring.profiles.active=deploy"]