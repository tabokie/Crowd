all: 
	javac -Djava.ext.dirs=./lib crowd/*.java -d ./build -encoding UTF-8
	java -cp .;./build -Djava.ext.dirs=./lib crowd.Main
test:
	javac -Djava.ext.dirs=./lib crowd/*.java -d ./build -encoding UTF-8
	java -cp .;./build -Djava.ext.dirs=./lib crowd.Test