## Statische Abhängigkeiten in Java-Klassen als Neo4j-Datenbank

Dieses Projekt liest die `import` Anweisungen aus Java-Klassen aus und erzeugt daraus eine Graphen Datenbank. In dieser können Abhängigkeiten mit der Abfragesprache Cypher abgefragt und analysiert werden. 

Voraussetung ist eine laufende Neo4j-Datenbank im Server-Modus. 

Der Import kann über `gradle` gestartet werden: 

    gradle run -Pargs=<sourceDir>


