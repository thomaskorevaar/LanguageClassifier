# LanguageClassifier
Deze classifier maakt gebruik van een mapper & reducer, gemaakt in Java voor gebruik met Hadoop
# Uitvoeren
de `.JAR` file uit de `build` folder is de nieuwste build. Deze is uit te voeren met het commando:

`hadoop jar *PATH-TO-JAR*/LanguageClassifier-1.jar LanguageClassifier.WordCount.WordCount *PATH-TO-INPUT* *PATH-TO-OUTPUT*`

de gebruikte teksten zijn te vinden in de `testfiles` folder.

Na het uitvoeren zou dit de output moeten zijn:
- ![Alt text](screenshots/output.png?raw=true "Hadoop output")

En zijn deze bestanden in de `output` folder geplaats
- ![Alt text](screenshots/output_folder_content.png?raw=true "Folder content")
# Zelf een JAR bouwen
Als je zelf een `.JAR` wil genereren, maak dan gebruik van het commando `maven install` vanuit _Eclipse_ of _IntelliJ_

Het `.JAR` bestand in deze repo is gegenereerd vanuit _Eclipse_
# Testfiles
`dutch.txt` bestaat uit NRC artikelen uit 2012

`english.txt` bestaat uit de engelstalige versie van Alice in Wonderland

`test.txt` is het testbestand waar een mix van nederlandse en engelse zinnen in staan

`test.sh` is een BASH-script die gebruikt is tijdens het testen, deze kun je overnemen (wel de paden bewerken)

Alles behalve `test.sh` zet je in je `input` folder
# Precisie
De resultaten van de classifier zijn niet fijloos; hij maakt zo hier en daar nog een fout waar hij nederlands voor engels aan ziet en vice versa. 

- 193 regels in totaal
- 74 Nederlandse regels
- 117 Engelse regels
- 2 websites (deze ziet hij aan voor engels)

- 5 maal Nederlands voor Engels aangezien
- 5 maal Engels voor Nederlands aangezien

Daarmee kun je berekenen:
- `93,25%` correct is voor het Nederlands
- `95,79%` correct voor het Engels
- ![Alt text](screenshots/output_last_lines.png?raw=true "Last lines from output file")