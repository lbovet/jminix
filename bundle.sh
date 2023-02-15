#/bin/sh
mvn clean install
mvn repository:bundle-create
cd target/
unzip -d bundle jminix-*-bundle.jar
rm jminix-*-bundle.jar
cd bundle
for i in *.jar; do gpg --detach-sig -a --sign $i; done
for i in *.xml; do gpg --detach-sig -a --sign $i; done
zip ../jminix-bundle.jar *
