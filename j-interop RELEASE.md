
# Deploy SNAPSHOT

JAVA_HOME=/usr/lib/jvm/java-8-oracle mvn clean deploy

# Deploy release

mvn versions:set -DnewVersion=3.0.0
mvn versions:set-scm-tag -DnewTag=3.0.0
mvn versions:set-scm-tag -f j-interop/pom.xml -DnewTag=3.0.0
mvn versions:set-scm-tag -f j-interopdeps/pom.xml -DnewTag=3.0.0
# Update CHANGELOG / README
git add CHANGELOG.md pom.xml **/pom.xml
git commit -m "Version 3.0.0."
git tag "3.0.0"
JAVA_HOME=/usr/lib/jvm/java-8-oracle mvn clean deploy -P release

# Remove poms <scm><tag>
mvn versions:set -DnewVersion=3.1.0-SNAPSHOT
git add pom.xml **/pom.xml
git commit -m "Set version to 3.1.0-SNAPSHOT."

