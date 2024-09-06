all: hackass.jar

clean:
	rm -r class hackass.jar hackass/grammar/*.{java,tokens,interp}

grammar/HackassParser.java:
	java -jar /usr/local/lib/antlr-complete.jar hackass/grammar/Hackass.g4 

hackass.jar: $(HACKASS_JAVA) grammar/HackassParser.java
	javac -sourcepath . -cp .:/usr/local/lib/antlr-complete.jar -d class -Xlint:deprecation hackass/*.java hackass/grammar/*.java
	cd class; jar cf ../hackass.jar hackass/*.class hackass/grammar/*.class
