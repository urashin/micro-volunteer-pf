TARGET=micro-volunteer-pf


# build
gradle build -x test

# stop
#kill -9 `ps -elf | grep ${TARGET}-0.0.1-TEST | grep -v "grep" | cut -b14-20`
#kill -9 `ps -elf | grep ${TARGET}-0.0.1-SNAPSHOT | grep -v "grep" | cut -b14-20`

# run
java -jar ./build/libs/${TARGET}-0.0.1-SNAPSHOT.jar

