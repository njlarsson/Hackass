HACKASS_JAVA = $(shell ls hackass/*.java)

all: hackass.jar

clean:
	rm -r class hackass.jar hackass/grammar/*.{java,tokens,interp}

hackass/grammar/HackassParser.java: hackass/grammar/Hackass.g4
	java -jar /usr/local/lib/antlr-complete.jar hackass/grammar/Hackass.g4 

hackass.jar: $(HACKASS_JAVA) hackass/grammar/HackassParser.java
	javac -sourcepath . -cp .:/usr/local/lib/antlr-complete.jar -d class -Xlint:deprecation hackass/*.java hackass/grammar/*.java
	cd class; jar cf ../hackass.jar hackass/*.class hackass/grammar/*.class
