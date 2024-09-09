HACKASS_JAVA = $(shell ls hackass/*.java)

all: hackass.jar

clean:
	rm -rf class hackass.jar build

build/hackass/grammar/HackassParser.java: hackass/grammar/Hackass.g4
	java -jar /usr/local/lib/antlr-complete.jar -o build hackass/grammar/Hackass.g4 

hackass.jar: $(HACKASS_JAVA) build/hackass/grammar/HackassParser.java
	javac -sourcepath . -cp .:/usr/local/lib/antlr-complete.jar -d class -Xlint:deprecation hackass/*.java build/hackass/grammar/*.java
	cd class; jar cf ../hackass.jar hackass/*.class hackass/grammar/*.class
