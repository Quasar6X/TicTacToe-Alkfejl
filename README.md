![](tictactoe-desktop/src/main/resources/hu/alkfejl/img/logo.png)
# ❌ Amőba ⭕

## A projektről 📃
___
Ezt a projektet az alkalmazásfejlesztés I. kurzusra készítettem.
A projektben megtalálható kép forrásokat magam készítettem, a betűtípust pedig
köszönöm Jacob Fischer-nek **[link](https://pizzadude.dk/site/)** a weboldalára. A 
betűtípus licensze megtalálható a betűtípus mellett **[itt](tictactoe-desktop/src/main/resources/hu/alkfejl/font/Font%20License.txt)**,
illetve **[itt](tictactoe-web/src/main/webapp/font/Font%20License.txt)** is.<br/>
>* A webes modul használatához **[Tomcat 9.0.45-re](https://tomcat.apache.org/download-90.cgi)** van szükség.
>* A projekt **JDK 11-en** készült.
>* A desktop modul **JavaFX 11.0.2-t** használ.
>* A projektben **log4j-t** is használtam több helyen, ez a modulok gyökérkönyvtárában hoz létre minden futtatás után egy
új logot timestamp-pel a fájl nevén.

## A játék menete 🎮
___
A játékos a körében lerakja valamelyik szabad mezőre a jelét. Az egyes játékos az **X**, a kettes
játékos a **O**. A játéknak három féle képpen lehet vége:
>1. Valamelyik játékos elég jelet rak le. Ez 3x3-as táblán 3, 4x4-es táblán 4, 5x5-ös vagy nagyobb
táblán 5.
>2. A tábla megtelik, ekkor a játék döntetlen.
>3. Lejár a játékra megszabott időkorlát. _(kivétel web PVP)_.
___
Ha az egy körre megszabott idő letelik a játék automatikusan átpasszolja a kört a másik playernek _(kivétel web PVP)_.<br/>
Ha bármelyik óra _**"00:00:00"-ra**_ van állítva akkor az az időzítő nem lesz figyelembe véve.

## TicTacToe-core 🌌
___
A core modul tartalmazza a Model réteget valamint a DAO-kat. A connection packagben lévő
_*ConnectionManagerClient*_ és _*ConnectionManagerServer*_ Connection poolingot valósítanak meg.
A kliens oldali ezt **dbcp2-vel** teszi a szerveroldali pedi a **tomcat-jdbc-t** használja.
Ez utóbbi verziója megegyezik a szerver verziójával ezért szükséges a 9.0.45 _(valószínűleg bármelyik 9-es Tomcat-tel működik)._<br/>
Ez a modul tartalmaz unit testeket is _(Junit)_, de sajnos a nem volt időm a DAO-kat lemockolni _Mockito-val_,
így az élő adatbázisba írnak. 🤦‍♂

## TicTacToe-desktop 💻
___
A desktop modul egy asztali alkalmazást tartalmaz JavaFX keret rendszerben megvalósítva.
_**Az alkalmazás tulajdonságai:**_
>* A play gombbal lehet új játékot kezdeni. Ez a először a player hozzáadás és törlés oldalra visz, ahonnan az alsó play gombbal lehet
kiválasztani a játék paramétereit. Az első a tábla mérete, a második és harmadik beállítás a két játékos, a negyedik és őtődik pedig a játék
időzítői, melyek formátuma hh:mm:ss _(pl. 12:23:59)_. A játék ezután játszható a _**[Játék menete](#a-játék-menete-)**_ leírtja alapján. 
>* A játékot meg lehet szakítani idő előtt a File menüből, itt kiválasztható, hogy mentsük-e a visszajátszást.
>* A help menüben a help almenüben egy egyszerű segítség szöveg ugrik fel. Az about almenüben pedig a github linkem található.
>* A főképernyőről elérhető **Match History** az összes lejátszott meccset kilistázza. Ezek a matchek törölhetők, és vissza is játszhatók!
A replay gomb megnyomásával elindul a visszajátszás ahol a játék minden lépést 1 másodperces időközönként tesz meg.

## TicTacToe-web 🌍
___
A webes alkalmazás a Google GSON csomagját használja a válasz objekutom JSON-ná alakításához. A PVP játék befejeződik, ha valamelyik
játékos böngészője tálcára kerül vagy átnavigál egy másik tabra. Ez egy **FEATURE** nem egy **BUG**, azért történik így mert az utolsó
biztosan megfigyelhető böngésző esemény a "visibilitychange", ezért ha ez bekövetkezik akkor lebontom mind a kettő player munkamenetét.
A webes alkalmazás csak pár tekintetben tér el az asztalitól, ezért inkább a különbségeket sorolnám fel.
>* Először be kell jelentkezni, nem a játék indításakor lehet a játékost kiválasztani.
>* Nincs meccs visszajátszás funkció.
>* A Match History csak a bejelentkezett felhasználó meccseit listázza.
>* PvP módban nincsenek időzítők, az órák szinkronizációja bonyolult lenne.
___
Az órák futtatása Player Vs Ai módban **[moment.js](https://momentjs.com/)** segítségével történik.
