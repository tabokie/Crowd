#  -Xlint:unchecked

build: crowd/*.java
	javac -Djava.ext.dirs=.;./lib crowd/*.java crowd/concurrent/*.java crowd/ui/*.java -d ./build -encoding UTF-8
app: build
	java -cp .;./build -Djava.ext.dirs=.;./lib crowd.$(target)
monitor: build
	java -cp .;./build -Djava.ext.dirs=.;./lib crowd.Monitor
worker: build
	java -cp .;./build -Djava.ext.dirs=.;./lib crowd.Worker
test: build
	java -cp .;./build -Djava.ext.dirs=.;./lib crowd.Test $(P)

.PHONY : clean
clean : 
	-rm --recursive ./build/crowd
