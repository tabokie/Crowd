build: crowd/*.java
	javac -Djava.ext.dirs=./lib crowd/*.java -d ./build -encoding UTF-8
monitor: build
	java -cp .;./build -Djava.ext.dirs=./lib crowd.Monitor
worker: build
	java -cp .;./build -Djava.ext.dirs=./lib crowd.Worker
test: build
	java -cp .;./build -Djava.ext.dirs=./lib crowd.Test
