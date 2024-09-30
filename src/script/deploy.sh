#! /bin/sh
#
# This script deploys the artifacts for jfachwert to https://oss.sonatype.org/
# see https://docs.sonatype.org/display/Repository/Sonatype+OSS+Maven+Repository+Usage+Guide#SonatypeOSSMavenRepositoryUsageGuide-7b.StageExistingArtifacts
#
# (c)reated: 12-Mar-2017 by ob@jfachwert.de
#

# set up some constants
URL=https://oss.sonatype.org/service/local/staging/deploy/maven2/
VERSION=6.0.1
options="gpg:sign-and-deploy-file -Durl=$URL -DrepositoryId=sonatype-nexus-staging"

# passphrase is needed for signing
echo "passphrase for GPG: "
stty_orig=`stty -g`
stty -echo
read passphrase
stty $stty_orig

options="gpg:sign-and-deploy-file -Durl=$URL -DrepositoryId=sonatype-nexus-staging -Dgpg.passphrase=$passphrase"

deploy_pom_for() {
	module=$1
	echo deploying $module...
	mvn -N $options -DpomFile=target/$module-$VERSION.pom -Dfile=target/$module-$VERSION.pom
    echo
}

deploy_jar_for() {
    subdir=$1
	module=$2
	pushd $subdir
	echo deploying $module in $subdir...
    mvn $options -DpomFile=target/$module-$VERSION.pom -Dfile=target/$module-$VERSION.jar
    mvn $options -DpomFile=target/$module-$VERSION.pom -Dfile=target/$module-$VERSION-sources.jar -Dclassifier=sources
    mvn $options -DpomFile=target/$module-$VERSION.pom -Dfile=target/$module-$VERSION-javadoc.jar -Dclassifier=javadoc
    popd
    echo
}

# start deployment
deploy_pom_for jfachwert-parent
deploy_jar_for core core
deploy_jar_for domains/bank bank
deploy_jar_for domains/formular formular
deploy_jar_for domains/math math
deploy_jar_for domains/med med
deploy_jar_for domains/money money
deploy_jar_for domains/net net
deploy_jar_for domains/post post
deploy_jar_for domains/rechnung rechnung
deploy_jar_for domains/steuer steuer
deploy_jar_for domains/zeit zeit
deploy_jar_for jfachwert jfachwert
deploy_jar_for test test
