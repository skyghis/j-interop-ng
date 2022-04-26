
```console
RELEASE_VERSION=3.4.0
NEXT_VERSION=3.5.0

cd j-interop-ng
git checkout jcifs-ng

JAVA_HOME=/usr/lib/jvm/temurin-8-jdk-amd64 mvn release:clean
nano CHANGELOG.md
git add CHANGELOG.md
git commit -m "Prepare version ${RELEASE_VERSION}."
JAVA_HOME=/usr/lib/jvm/temurin-8-jdk-amd64 mvn --batch-mode release:prepare -D 'pushChanges=false' -D "tag=${RELEASE_VERSION}" -D "releaseVersion=${RELEASE_VERSION}" -D "developmentVersion=${NEXT_VERSION}-SNAPSHOT"
git push --follow-tags

git checkout "${RELEASE_VERSION}"
JAVA_HOME=/usr/lib/jvm/temurin-8-jdk-amd64 mvn clean install
JAVA_HOME=/usr/lib/jvm/temurin-8-jdk-amd64 mvn deploy -P release

git checkout jcifs-ng
```

Go to [sonatype nexus](https://oss.sonatype.org/#nexus-search;quick~j-interop-ng) to check deployed version.
