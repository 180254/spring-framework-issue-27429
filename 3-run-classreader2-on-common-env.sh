#!/bin/bash
set -Eeuo pipefail

mvn -Dstart-class=com.example.demo.ClassReader2 clean package
docker build -t spring-framework-issue-27429:1.0.3 -f dockerfile-common-env.Dockerfile .
docker run -it spring-framework-issue-27429:1.0.3
