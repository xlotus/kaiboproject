#!bash

OLD_JAVA_HOME=$JAVA_HOME
JAVA_HOME=/Library/Java/JavaVirtualMachines/zulu-11.jdk/Contents/Home
/Users/xuewc/.gradle/wrapper/dists/gradle-7.3.3-bin/6a41zxkdtcxs8rphpq6y0069z/gradle-7.3.3/bin/gradle clean
/Users/xuewc/.gradle/wrapper/dists/gradle-7.3.3-bin/6a41zxkdtcxs8rphpq6y0069z/gradle-7.3.3/bin/gradle assembleRelease

JAVA_HOME=$OLD_JAVA_HOME

rm -f /Users/xuewc/works/xuewc/tv/SdkDemo/localRepo/CIBNSdk/*.aar
cp build/outputs/aar/*.aar /Users/xuewc/works/xuewc/tv/SdkDemo/localRepo/CIBNSdk/cibn.aar

