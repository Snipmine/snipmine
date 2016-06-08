#!/bin/bash
find $1 -name pom.xml -exec nice -n 5 java -Dmaven.home=/opt/maven -jar snipmine-1.0.jar {} \;
