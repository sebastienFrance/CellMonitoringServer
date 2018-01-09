#!/bin/sh

export INSTALLDIR="/Users/sebastien/Documents/Java-Dev/CellMonitoringServer/GenerateLocalizationData/build/install/GenerateLocalizationData"
export CLASSPATH="$INSTALLDIR/lib/*:$INSTALLDIR/*"
export MAINCLASS="com.seb.networkGenerator.ImprovedNetworkGenerator"
export MEMORYOPTIONS="-Xmx4096m"

export installDirectory=$INSTALLDIR
export traceDirectory="$INSTALLDIR/trace"
mkdir -pv $traceDirectory

export OTHERSOPTIONS="-Dlog4j.configurationFile=File://$installDirectory/conf/LogConfiguration.xml -DgenerateLocalizationLogPath=$traceDirectory"

java $MEMORYOPTIONS  $OTHERSOPTIONS -classpath $CLASSPATH  $MAINCLASS  "$INSTALLDIR/conf/generateRandomData.property"
#java $MEMORYOPTIONS  $OTHERSOPTIONS -classpath $CLASSPATH  $MAINCLASS  "$INSTALLDIR/conf/generateRandomData.property" -add
