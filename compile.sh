find src -type f -name "*.class" -delete
find bin -type f -name "*.class" -delete
javac -d bin src/*.java