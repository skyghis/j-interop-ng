
```console
RELEASE_VERSION=3.2.0
NEXT_VERSION=3.3.0

cd j-interop-ng
JAVA_HOME=/usr/lib/jvm/adoptopenjdk-8-hotspot-amd64 mvn release:clean
nano CHANGELOG.md
git add CHANGELOG.md
git commit -m "Prepare version ${RELEASE_VERSION}."
JAVA_HOME=/usr/lib/jvm/adoptopenjdk-8-hotspot-amd64 mvn release:prepare -D 'pushChanges=false' -D "tag=${RELEASE_VERSION}" -D "releaseVersion=${RELEASE_VERSION}" -D "developmentVersion=${NEXT_VERSION}-SNAPSHOT"
git push --follow-tags 
git co "${RELEASE_VERSION}"
JAVA_HOME=/usr/lib/jvm/adoptopenjdk-8-hotspot-amd64 mvn clean install
JAVA_HOME=/usr/lib/jvm/adoptopenjdk-8-hotspot-amd64 mvn deploy -P release
```
