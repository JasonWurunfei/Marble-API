@echo off

echo [1/4] Build the project
call mvn package

echo [2/4] Remove the old image
docker rmi %1

echo [3/4] Build the image
docker build -t %1 .

echo [4/4] Export the image
docker save %1 > %1.tar
