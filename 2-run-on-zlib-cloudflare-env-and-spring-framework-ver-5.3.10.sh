#!/bin/bash
set -Eeuo pipefail

mvn -Dspring-framework.version=5.3.10 clean package
docker build -t spring-framework-issue-27429:1.0.2 -f dockerfile-zlib-cloudflare.Dockerfile .
docker run -it spring-framework-issue-27429:1.0.2
