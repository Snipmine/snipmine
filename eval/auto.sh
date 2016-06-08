#!/bin/bash
cat datasplit_b.txt | while read r; do 
  java -jar snipcloud-server.jar &
  scpid=$!
  sleep 10
  find "repos/$r" -name pom.xml -exec mvn dependency:build-classpath -f {} \;
  timeout 3600 ./snipmine.sh "repos/$r"
  echo $r >> progress.txt
  rm -rf ~/.m2/repository
  sleep 10
  kill $scpid
  sleep 10
  kill -9 $scpid
done > evala.txt 2>&1
