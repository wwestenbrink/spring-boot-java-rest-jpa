FROM gcr.io/distroless/java:11
COPY build/libs/*.jar app.jar
CMD ["/app.jar"]