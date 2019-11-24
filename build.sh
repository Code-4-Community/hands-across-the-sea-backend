#!bin/bash

echo "building this shit"

mvn clean package -pl \!persist

cd persist/

mvn package 

cd ../service/

mvn exec:java
