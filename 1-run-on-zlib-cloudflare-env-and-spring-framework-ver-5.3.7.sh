#!/bin/bash
set -Eeuo pipefail

mvn -Dspring-framework.version=5.3.7 clean package
docker build -t spring-framework-issue-27429:1.0.1 -f dockerfile-zlib-cloudflare-env.Dockerfile .
docker run -it spring-framework-issue-27429:1.0.1
