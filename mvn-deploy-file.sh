#!/bin/sh

OPENSLIDE_VERSION=0.12.2
JAR_FILE=$1
MVN_REPO_ID=$2
MVN_REPO_URL=$3

if [ $# -ne 3 ]; then
    echo "Usage: $0 jar-file.jar maven-repo-id maven-repo-url"
    exit 1
fi

mvn deploy:deploy-file -DgroupId=org.openslide \
  -DartifactId=openslide \
  -Dversion=${OPENSLIDE_VERSION} \
  -Dpackaging=jar \
  -Dfile=${JAR_FILE} \
  -DrepositoryId=${MVN_REPO_ID} \
  -Durl=${MVN_REPO_URL}
