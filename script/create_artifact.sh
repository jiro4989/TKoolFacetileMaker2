#!/bin/bash

ENTRYPOINT_SCRIPT_EXT=${ENTRYPOINT_SCRIPT_EXT:-''}

set -eux

test -n "${OS_NAME}"
test -n "${APP_NAME}"
test -n "${JAVAFX_VERSION}"
test -n "${ARCHIVE_CMD}"
test -n "${ARTIFACT_EXT}"
test -n "${ENTRYPOINT_SCRIPT_EXT}"
test -n "${VERSION}"

jmods_dir="jmods/${OS_NAME}"
jmods_download_url="https://download2.gluonhq.com/openjfx/${JAVAFX_VERSION}/openjfx-${JAVAFX_VERSION}_${OS_NAME}-x64_bin-jmods.zip"

# JavaFX用のjmodsをダウンロード
mkdir -p "${jmods_dir}"
curl -o "${jmods_dir}/jmods.zip" -sSL "${jmods_download_url}"
(
  cd "${jmods_dir}"
  unzip jmods.zip
)

ORG_GRADLE_PROJECT_CI_JMODS_DIR="${jmods_dir}/javafx-jmods-${JAVAFX_VERSION}" ./gradlew jlink

# アーティファクトを作成
artifact_name="${APP_NAME}_${OS_NAME}"
mkdir "${artifact_name}"
cp -r .github/dist/* LICENSE build/libs/"${APP_NAME}"-*.jar jre "${artifact_name}/"

# MacのBSD sedの -i オプションの振る舞いが違うのを吸収するために
# 一時ファイルにリダイレクトしてmvで上書きする。
entrypoint_script="${artifact_name}/${APP_NAME}${ENTRYPOINT_SCRIPT_EXT}"
tmp_script="${entrypoint_script}.tmp"
mkdir dist
sed "s/{tag}/${VERSION}/g" \
  "${entrypoint_script}" \
  > "${tmp_script}"
mv "${tmp_script}" "${entrypoint_script}"

# 配布用ディレクトリに移動
artifact="${artifact_name}${ARTIFACT_EXT}"
${ARCHIVE_CMD} "${artifact}" "${artifact_name}"
mv "${artifact}" dist/

ls dist/*
