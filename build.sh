#!bash

OLD_JAVA_HOME=$JAVA_HOME
JAVA_HOME=/Library/Java/JavaVirtualMachines/zulu-11.jdk/Contents/Home
/Users/xuewc/.gradle/wrapper/dists/gradle-7.3.3-bin/6a41zxkdtcxs8rphpq6y0069z/gradle-7.3.3/bin/gradle clean
/Users/xuewc/.gradle/wrapper/dists/gradle-7.3.3-bin/6a41zxkdtcxs8rphpq6y0069z/gradle-7.3.3/bin/gradle assembleRelease
#/Users/xuewc/.gradle/wrapper/dists/gradle-7.3.3-bin/6a41zxkdtcxs8rphpq6y0069z/gradle-7.3.3/bin/gradle :App:dependencies > tree.txt

JAVA_HOME=$OLD_JAVA_HOME

############################################################################
############# build channels ###############################################

rm -rf channels/outputs
#java -jar channels/walle-cli-all.jar batch -f channels/channel  app/build/outputs/apk/release/app-release.apk channels/outputs

# Read build configure
CFG_FILE_NAME="gradle.properties"
BUILD_VERSION=`cat $CFG_FILE_NAME | grep VERSION_NAME | awk -F= '{print $2}'`
BUILD_CODE=`cat $CFG_FILE_NAME | grep VERSION_CODE | awk -F= '{print $2}'`

echo $BUILD_VERSION

for channel in $(cat "channels/channel")
do
echo $channel
java -jar channels/walle-cli-all.jar put -c $channel app/build/outputs/apk/release/app-release.apk channels/outputs/Kaibo_${BUILD_VERSION}_$channel.apk
done

echo 'build channels done'
