#!/bin/sh

mkdir -p ../packaging/osx-java-default/libs
cd ../packaging/osx-java-default
cp /usr/local/lib/openslide-java/openslide.jar ./openslide-java-default.jar

B=${BUNDLE:=0}
if [ "$B" -eq 1 ]; then
    echo "Bundling for OSX..."
    cp /usr/local/lib/openslide-java/libopenslide-jni.jnilib ./libs

    macpack ./libs/libopenslide-jni.jnilib -d .
    zip -d ./openslide-java-default.jar resources/openslide.properties
    jar cf ./openslide-natives-osx.jar -C ./libs .
fi
echo "Done building for OSX."