# Draughts

Repozitorij vsebuje igro Dama, dodana je tudi JAR datoteka, ki pa ne deluje pravilno, če zraven ni mape icons v kateri so ikone, ki jih igra potrebuje! 

Dama je miselna igra na šahovnici med dvema igralcema. Figure se gibljejo diagonalno. Angleški naziv za igro je draughts. Poznamo različne vrste dame a ta je sestavljena po pravilih, ki so značilni za klasično damo. 
## Osnovna pravila so:
+ Obstajata dva premika žetona. En se premakne po diagonali za 1 mesto naprej, drugi premik pa je, ko preskoči nasprotnikov žeton in ga s tem "poje".
+ Igralec lahko v isti potezi z enim žetonom poje več nasprotnikovih z zaporednim preskakovanjem po plošči.
+ Navadna figura (mož) se lahko premika samo diagonalno naprej (proti nasprotnikovi strani plošče)
+ Ko navadna figura doseže nasprotnikov rob plošče, se spremeni v kralja
+ Kralj se lahko premika v vse štiri smeri a ponovno zgolj za eno polje ali 2 če vmes poje nasprotnikov žeton
+ Zmaga igralec, ki poje vse nasprotnikove žetone ali pa ko se nasprotnik ne more več premikati

# Posamezen opis classov

## Draughts.java
Class vsebuje osnovne metode, ki začnejo, končajo igro, zamenjajo igralca ali igro resetirajo. 

## GUI.java
Class vsebuje podatke o meni vrstici na vrhu, iz katere lahko igro resetiramo ali pa preskočimo potezo igralca

## Main.java
V classu main se zgodi vsa logika igre in pa izris vse grafike. Notri so dodana vsa pravila, katera sproti preverja in tako igralca prisili, da uporabljata legalne poteze. Prav tako vsakič preverja, če je kateri igralec izgubil in tako nakoncu izriše zmagovalni okvirček. Prav tako je pozoren na to, če je slučajno igra neodločena (vsak igralec ima samo še eno figuro in ta je kralj)

## Token.java
Vsak igralec ima večje število žetonov in vsak izmed teh predstavlja en class token. Tukaj metode spreminjajo stolpec in vrsto žetona in pa ali je le ta kralj ali samo mož.

## Player.java
Vsak igralec predstavlja svoj class player in sicer v vsakemu so shranjeni vsi žetoni in podatki o njegovih pozicijah in ali so le ti kralji ali samo navadni žetoni. Prav tako imamo pri tem clasu metode, ki ugotovijo če je žeton njegov in pa seveda metode, ki premaknejo, izbrišejo žeton.

## Field.java
Class field predstavlja vsako polje na igralni plošči, ta vsebuje vrsto in pa stolpec. S tem classom nato deluje celoten program in sicer vsi žetoni so na nekem polju in vsa pravila pregledujejo različna polja med seboj.

**Končen izgled igre:**
![steam_profile](/Draughts.png)
