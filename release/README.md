## RELEASE

Steps:

1. ~~run `check-version.bat` update~~
2. change `pom.xml` version tag
3. change `me/n1ar4/clazz/obfuscator/Const` version
4. change `.github/workflows/build.yml` version
5. check `CHANGELOG.MD` file
6. check `README.md` api doc
7. actions build
8. ~~release~~
9. mvn clean deploy -DskipTests --file pom.xml
