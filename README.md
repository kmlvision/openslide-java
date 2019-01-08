OpenSlide Java
==============

This is a Java binding to [OpenSlide](http://openslide.org/).

Build requirements
------------------

- JDK
- Apache Ant
- [OpenSlide >= 3.4.0](https://github.com/kmlvision/openslide)

Building on Linux or Mac OS X
-----------------------------

Install
1. `autoconf`
2. `autoconf-archive`
3. `automake`

> Optional: set the `<property name="target" value="11"/>` value in `build.xml` to the Java compile target version. 

```
# set JAVA_HOME and JDK_HOME to the respective paths, then run
autoreconf -i && ./configure && make && make install
```

This will install the `openslide.jar` and `libopenslide-jni.{ dylib | so }` to `/usr/local/lib/openslide-java/`,

> **Caution**: this build contains a hard-coded path to the OpenSlide library! 
> The bindings expect the OpenSlide lib to be installed on the target system.


Building OSX native lib
-----------------------
**NB**: Following the invaluable addition to packaging OpenSlide Java by [quath](https://github.com/qupath/qupath/blob/master/maven/Notes%20on%20packaging%20OpenSlide.md).

Build the library

```bash
# install python 3 and macpack
brew install python3
pip3 install macpack

# create build dirs
mkdir -p packaging/osx-java-default/libs
cd packaging/osx-java-default
cp /usr/local/lib/openslide-java/openslide.jar ./openslide-java-default.jar
cp /usr/local/lib/openslide-java/libopenslide-jni.jnilib ./libs

macpack ./libs/libopenslide-jni.jnilib -d .
zip -d ./openslide-java-default.jar resources/openslide.properties
jar cf ./openslide-natives-osx.jar -C ./libs .
```

TODO: we need to load the openslide-jni from the jar when using from maven repository.

### Deploying bindings and natives to a Maven repository
Prerequisites:
1. `mvn` command installed
2. Access to `$MAVEN_REPO_URL` and the `$MVN_REPO_ID` is configured in your local `.m2/settings.xml`
3. Deploy using the helper script in the `build-compat` folder

```bash
cd build-compat
sh ./mvn-deploy-file.sh default osx MVN_REPO_ID MVN_REPO_ID
```


Building portable Java 8/11 in Docker (for Linux)
-------------------------------------------------

**NB**: Following the invaluable addition to packaging OpenSlide Java by [quath](https://github.com/qupath/qupath/blob/master/maven/Notes%20on%20packaging%20OpenSlide.md).

Build the latest version against the default Java 1.6 target with the latest OpenSlide binaries:
```bash
docker build -t local/openslide-java:6-git --build-arg ENV_JAVA_COMPILE_TARGET=6 -f build-compat/Dockerfile .
```
You need to explicitly enable the patching / dependency bundling step using `--build-arg BUNDLE=1` to build the dependencies as JAR and patch the openslide.jar file.

```bash
docker run --rm -v $PWD/packaging/linux-java-6:/exchange local/openslide-java:6-git /bin/bash -c "cp /artifacts/openslide*.jar /exchange"
```


Compatibility builds for Java 8 and Java 11 are located in `/build-compat/java-*`:
```bash
docker build -t local/openslide-java:8-git --build-arg ENV_JAVA_COMPILE_TARGET=8 -f build-compat/Dockerfile .
# then
docker run --rm -v $PWD/packaging/linux-java-8:/exchange local/openslide-java:8-git /bin/bash -c "cp /artifacts/openslide*.jar /exchange"
```

```bash
docker build -t local/openslide-java:11-git --build-arg ENV_JAVA_COMPILE_TARGET=11 -f build-compat/Dockerfile .
# then
docker run --rm -v $PWD/packaging/linux-java-11:/exchange local/openslide-java:11-git /bin/bash -c "cp /artifacts/openslide*.jar /exchange"
```

### Deploying dependencies and natives to a Maven repository
Prerequisites:
1. `mvn` command installed
2. Access to `$MAVEN_REPO_URL` and the `$MVN_REPO_ID` is configured in your local `.m2/settings.xml`
3. Deploy using the helper script in the `build-compat` folder

```bash
cd build-compat
# USAGE: sh ./mvn-deploy-file.sh JAVA_TARGET OS_TARGET MVN_REPO_ID MVN_REPO_ID
# e.g.
sh ./mvn-deploy-file.sh 11 linux MVN_REPO_ID MVN_REPO_ID
```

TODO: we need to load the openslide-jni from the jar when using from maven repository.

Cross-compiling for Windows with MinGW-w64
------------------------------------------

```
PKG_CONFIG=pkg-config \
	PKG_CONFIG_PATH=/path/to/cross/compiled/openslide/lib/pkgconfig \
	./configure --host=i686-w64-mingw32 --build=$(build-aux/config.guess)
make
make install
```

For a 64-bit JRE, substitute `--host=x86_64-w64-mingw32`.

Building on Windows
-------------------

Ensure that the path to the openslide-java source tree does not contain
whitespace.

Install Cygwin, selecting these additional packages:

- `make`
- `pkg-config`
- `mingw64-i686-gcc-core` and/or `mingw64-x86_64-gcc-core`

(Cygwin is only needed for the build environment; the resulting binaries
do not require Cygwin.)

Also install a JDK and Apache Ant.

Then:

```
./configure --prefix=/path/to/install/dir \
	--host=i686-w64-mingw32 --build=$(build-aux/config.guess) \
	PKG_CONFIG_PATH="/path/to/openslide/lib/pkgconfig" \
	JAVA_HOME="$(cygpath c:/Program\ Files/Java/jdk*)" \
	ANT_HOME="/path/to/ant/directory"
make
make install
```

For a 64-bit JRE, substitute `--host=x86_64-w64-mingw32`.
