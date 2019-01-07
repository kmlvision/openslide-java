OpenSlide Java
==============

This is a Java binding to [OpenSlide](http://openslide.org/).

Build requirements
------------------

- JDK
- Apache Ant
- OpenSlide >= 3.4.0

Building on Linux or Mac OS X
-----------------------------

Install
1. `autoconf`
2. `autoconf-archive`
3. `automake`

```
# set JAVA_HOME and JDK_HOME to the respective paths

autoreconf -i
./configure
make
make install
```

Building in Docker
------------------

Build the latest version against the default Java 1.6 target with the latest OpenSlide binaries:
```bash
docker build -t local/openslide-java:6-git .
```

Retrieve the built jar file into the current directory:
```bash
docker run --rm -v $PWD:/artifacts local/openslide-java:6-git /bin/bash -c "cp /opt/openslide-java/openslide*.jar /artifacts/"
```


Compatibility builds for Java 8 and Java 11 are located in `/build-compat/java-*`:
```bash
docker build -t local/openslide-java:8-git -f build-compat/java-8/Dockerfile .
# then
docker run --rm -v $PWD:/artifacts local/openslide-java:8-git /bin/bash -c "cp /opt/openslide-java/openslide*.jar /artifacts/"
```

```bash
docker build -t local/openslide-java:11-git -f build-compat/java-11/Dockerfile .
# then
docker run --rm -v $PWD:/artifacts local/openslide-java:11-git /bin/bash -c "cp /opt/openslide-java/openslide*.jar /artifacts/"
```

#### Deploying to a Maven repository
Prerequisites:
1. `mvn` command installed
2. Access to `$MAVEN_REPO_URL` is configured in your local `.m2/settings.xml`

Set the ENV and deploy:
```bash
mvn deploy:deploy-file -DgroupId=org.openslide \
  -DartifactId=openslide \
  -Dversion=${OPENSLIDE_VERSION} \
  -Dpackaging=pom \
  -Dfile=${JAR_FILE} \
  -DrepositoryId=${MVN_REPO_ID} \
  -Durl=${MVN_REPO_URL}
```


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
