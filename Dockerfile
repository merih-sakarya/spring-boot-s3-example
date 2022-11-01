##### BUILD Stage #####
FROM maven:3.9.9-amazoncorretto-21-debian AS build
# Add current directory to /src in the image
ADD . /src
# Set the working directory to /src
WORKDIR /src
# Build the application
RUN mvn -B clean package -DskipTests

##### RUNTIME Stage #####
# Use a smaller base image and exclude source code for security
FROM openjdk:21-slim
# Copy the built jar file from the build stage
COPY --from=build /src/target/*.jar /app/

# Argument for specifying the Java profile (optional, no default)
ARG ARG_JAVA_PROFILE
# Ensure the profile is set, otherwise fail
RUN if [ -z "${ARG_JAVA_PROFILE}" ]; then echo "Error: ARG_JAVA_PROFILE is not set" && exit 1; fi
# Set the profile as an environment variable
ENV JAVA_PROFILE=${ARG_JAVA_PROFILE}

# Run the application using the sh shell
ENTRYPOINT ["sh", "-c", "java -jar /app/*.jar --spring.profiles.active=${JAVA_PROFILE}"]
