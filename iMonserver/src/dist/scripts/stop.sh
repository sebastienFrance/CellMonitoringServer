#!/bin/sh

jps -v | grep "iMonserver" | awk '{print $1}' | xargs kill

