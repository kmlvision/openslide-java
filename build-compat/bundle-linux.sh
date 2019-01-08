#!/bin/sh

COMPILE_TARGET=${JAVA_COMPILE_TARGET}
ARTIFACTS_DIR="/artifacts"
JARFILE_NAME="openslide-natives-linux"

mkdir -p $ARTIFACTS_DIR/libs
cd $ARTIFACTS_DIR
echo $PWD
cp -v /usr/local/lib/openslide-java/openslide.jar ./

B=${BUNDLE:=0}
if [ "$B" -eq 1 ]; then
    echo "Bundling/Patching OpenSlide Java natives for Linux"
    cp /usr/local/lib/libopenslide.so.0 ./
    cp /usr/local/lib/openslide-java/libopenslide-jni.so ./
    # set the rpath in the libraries
    cp ./libopenslide.so.0 ./libs/
    patchelf --set-rpath '$ORIGIN' ./libs/libopenslide.so.0
    cp ./libopenslide-jni.so ./libs/
    patchelf --set-rpath '$ORIGIN' ./libs/libopenslide-jni.so
    # Copy over all (non-standard) dependencies, also setting the rpath
    for l in $(ldd ./libopenslide.so.0 | egrep "(/usr/lib/)|(prefix64)|(libpng)" | cut -d ' ' -f 3 | egrep -v "lib(X|x|GL|gl|drm)" | tr '\n' ' '); do
        cp $l ./libs/
        fname=`basename $l`
        echo $fname
        patchelf --set-rpath '$ORIGIN' ./libs/$fname
    done
    # remove unnecessary libraries
    rm ./libs/libstdc++*
    rm ./libs/libfontconfig*
    rm ./libs/libfreetype*
    rm ./libs/libicu*
    # delete the hard-coded properties file
    zip -d ./openslide.jar resources/openslide.properties
    # bundle native jar file
    jar cf "./${JARFILE_NAME}.jar" -C ./libs .
fi

# rename the openslide jar file
mv -v ./openslide.jar ./openslide-java-${COMPILE_TARGET}.jar
echo "Done. Find libraries openslide*.jar in $ARTIFACTS_DIR."
