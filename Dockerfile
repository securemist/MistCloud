FROM openjdk:8-jdk-alpine

ENV FILE_DIR "/storage"

COPY ./target/mist-cloud.jar /

EXPOSE 8888

CMD ["java", "-jar", "mist-cloud.jar", "-DFILE_BASH_PATH=$FILE_DIR"]
