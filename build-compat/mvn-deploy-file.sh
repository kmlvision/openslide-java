#!/bin/sh

JAVA_TARGET=$1
OS_TARGET=$2
MVN_REPO_ID=$3
MVN_REPO_URL=$4

if [ $# -ne 4 ]; then
    echo "Usage: $0 java-target os-target maven-repo-id maven-repo-url"
    exit 1
fi

# deploy the Java library (bindings)
mvn deploy:deploy-file \
    -DpomFile=pom.xml \
    -Dfile="../packaging/${OS_TARGET}-java-${JAVA_TARGET}/openslide-java-${JAVA_TARGET}.jar" \
    -Dclassifier="${OS_TARGET}" \
    -DrepositoryId=${MVN_REPO_ID} \
    -Durl=${MVN_REPO_URL}

DEP_BUNDLE_FILE="../packaging/${OS_TARGET}-java-${JAVA_TARGET}/openslide-natives-${OS_TARGET}.jar"
if [ -f ${DEP_BUNDLE_FILE} ]; then
    # deploy the dependencies bundle
    mvn deploy:deploy-file \
        -DgroupId=org.openslide \
        -DartifactId=openslide-natives \
        -Dversion=3.4.1 \
        -Dfile=${DEP_BUNDLE_FILE} \
        -Dclassifier="${OS_TARGET}" \
        -DrepositoryId=${MVN_REPO_ID} \
        -Durl=${MVN_REPO_URL}
fi