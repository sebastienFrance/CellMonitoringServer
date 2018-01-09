#!/bin/sh

if [ $# -lt 3 ]
  then
    echo "No arguments supplied"
    echo "Usage: $0 installDirectory confDirectory traceDirectory"
    exit 1
    #export installDirectory='..'
  else
    export installDirectory=$1
    export confDirectory=$2
    export traceDirectory=$3
    export webServer=$4
fi

export CLASSPATH="$installDirectory/*:$installDirectory/lib/*"
export MAINCLASS="com.seb.imonserver.main.Main"
export PROCESSNAME="-Dname=iMonserver"
export MEMORYOPTIONS="-Xmx1024m"
export OTHERSOPTIONS="-Dlog4j.configurationFile=File://$confDirectory/LogConfiguration.xml -DiMonserverLogPath=$traceDirectory"

export JETTYOPTIONS="-Xbootclasspath/p:$installDirectory/lib/alpn-boot-8.1.3.v20150130.jar"

mkdir -pv $traceDirectory

nohup java $PROCESSNAME $OTHERSOPTIONS $MEMORYOPTIONS $JETTYOPTIONS -classpath $CLASSPATH $MAINCLASS  < /dev/null >> $traceDirectory/std.out 2>> $traceDirectory/std.err &

exit 0
