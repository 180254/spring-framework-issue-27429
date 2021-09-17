# Build zlib-clouflare (https://github.com/cloudflare/zlib).
FROM adoptopenjdk/openjdk11:ubuntu-slim as zlib_cloudflare_builder

RUN apt-get update
RUN apt-get install -y build-essential git

# Commit tree: https://github.com/cloudflare/zlib/tree/959b4ea305821e753385e873ec4edfaa9a5d49b7
# Commit date: Mon Mar 8 15:53:32 2021 +0000
ENV CLOUDFLARE_ZLIB_COMMIT_SHA1="959b4ea305821e753385e873ec4edfaa9a5d49b7"

RUN set -eux; \
    mkdir -p /app/zlib-cloudflare-git; \
    mkdir -p /app/zlib-cloudflare; \
    cd /app/zlib-cloudflare-git; \
    # https://stackoverflow.com/a/3489576
    git init; \
    git remote add origin "https://github.com/cloudflare/zlib.git"; \
    git fetch origin "${CLOUDFLARE_ZLIB_COMMIT_SHA1}"; \
    git reset --hard FETCH_HEAD; \
    ./configure --prefix=/app/zlib-cloudflare; \
    make -j 4; \
    make test; \
    make install;

# Build app.
FROM adoptopenjdk/openjdk11:ubuntu-slim

# Use zlib-cloudflare.
RUN mkdir -p /app/zlib-cloudflare
COPY --from=zlib_cloudflare_builder /app/zlib-cloudflare /app/zlib-cloudflare
ENV LD_LIBRARY_PATH /app/zlib-cloudflare/lib:$LD_LIBRARY_PATH

WORKDIR /app
COPY target/demo-0.0.1-SNAPSHOT.jar /app
CMD ["java", "-jar", "/app/demo-0.0.1-SNAPSHOT.jar"]
