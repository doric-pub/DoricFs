#!/usr/bin/env bash
##############################################################################
##
##  Publish JS,Android,iOS
##
##############################################################################
CURRENT_DIR=$(cd $(dirname $0); pwd)
CURRENT_VERSION=$(cat $CURRENT_DIR/version)

echo "Current version is "$CURRENT_VERSION

git add version

git commit -m "Modify version file ${CURRENT_VERSION}"

## JS
npm version $CURRENT_VERSION --allow-same-version

## iOS
sed -i "" "s/\(version[ ]*= \)'[0-9 \.]*'/\1'$CURRENT_VERSION'/g" $CURRENT_DIR/DoricSQLite.podspec

echo "Commit changes"
git add .
git commit -m "Release v${CURRENT_VERSION}"

git tag ${CURRENT_VERSION}

git push 

git push --tags

echo "Publish JS"
cd $CURRENT_DIR && npm publish

echo "Publish Android"
cd $CURRENT_DIR/android && ./gradlew clean publish

echo "Publish iOS"
cd $CURRENT_DIR && pod trunk push DoricSQLite.podspec --allow-warnings
