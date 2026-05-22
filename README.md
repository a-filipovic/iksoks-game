# X-O variants — objedinjeni JavaFX projekat

Jedan Java projekat koji objedinjuje **dve varijante iks-oks igara** sa različitim pravilima, zajedničkim JavaFX menijem i istim tokom na kraju partije (*Play again* / *Back to menu*).

| Igra | Tabla | Posebnost | AI |
|------|-------|-----------|-----|
| **Four in Line 5×5** | 5×5 | Gravitacija (potez pada u koloni) | Minimax + alpha-beta, dubina 6 |
| **Inverse Tic-Tac-Toe** | 3×3 | Pobednik je onaj koji **ne** napravi tri u nizu | Minimax + alpha-beta (puna pretraga 3×3) |

---

## Opis projekta

### 1) Tic-Tac-Toe 5×5 sa gravitacijom (Four in Line)

Proširena verzija klasičnog iks-oks-a inspirisana **Connect Four** mehanikom:

- **Tabla:** 5×5  
- **Gravitacija:** klik na kolonu spušta figuru na najniže slobodno polje u toj koloni  
- **Cilj:** spojiti **4** simbola u nizu (horizontalno, vertikalno ili dijagonalno)  
- **Protivnik:** AI (Minimax) — u trenutnoj verziji UI-a igrač je uvek čovek protiv računara  

AI koristi **Minimax** sa **alpha-beta odsecanjem**, ograničenom dubinom pretrage i heurističkom evaluacijom kada dubina nije dovoljna za potpunu analizu.

### 2) Inverzni Tic-Tac-Toe (Inverse)

Klasična tabla 3×3, ali sa obrnutim ciljem:

- Ko **napravi tri u nizu — gubi**  
- Ako tabla popuni bez tri u nizu — **nereseno**  
- Igrač (P) prvi bira polje; računar (C) odgovara optimalnim potezom  

Na tabli 3×3 AI može da koristi **punu pretragu** (alpha-beta), jer je prostor stanja mali.

---

## Tok aplikacije

```
MainApp (JavaFX)
    └── MenuView — izbor igre
            ├── Four in Line 5×5  →  FourInLineView
            └── Inverse Tic-Tac-Toe  →  InverseTicTacToeView
                        │
                        └── kraj partije → EndGameDialog
                                ├── Play again  → nova partija (ista igra)
                                └── Back to menu  → MenuView
```

Tokom igre dostupno je i dugme **Back to menu** (bez čekanja kraja partije).

---

## Struktura projekta

Kod je organizovan po slojevima — **logika odvojena od prikaza** (minimum za održiv kod):

```
src/
├── app/
│   ├── MainApp.java          # ulazna tačka, navigacija između menija i igara
│   └── MenuView.java         # početni meni
├── common/
│   ├── EndGameDialog.java    # zajednički dijalog na kraju partije
│   └── GameShell.java        # interfejs: getRoot(), newGame()
├── games/
│   ├── fourinline/
│   │   ├── FourInLineLogic.java       # zajednička logika (tabla, pobeda, potezi)
│   │   ├── FourInLine5x5Logic.java   # 5×5 + Minimax + evaluacija
│   │   ├── FourInLineView.java       # JavaFX UI (Canvas, klikovi)
│   │   └── PlayerType.java
│   └── inverse/
│       ├── InverseTicTacToeLogic.java # pravila + AI
│       ├── InverseTicTacToeView.java  # JavaFX UI (dugmad 3×3)
│       └── InversePlayer.java
```

| Klasa (stari naziv → novi) | Uloga |
|----------------------------|--------|
| `GameLogic` → `FourInLineLogic` | Zajednička logika table, provera 4 u nizu, potezi sa gravitacijom |
| `GameLogic5x5` → `FourInLine5x5Logic` | 5×5 pravila + Minimax + heuristika |
| `FourInLine5x5` → `FourInLineView` | JavaFX prikaz (više nije zasebna `Application`) |
| `InverseTicTacToe` → `InverseTicTacToeLogic` + `InverseTicTacToeView` | Logika i UI razdvojeni |

---

## AI implementacija — Four in Line 5×5

### Algoritam

- **Minimax** (max = igrač, min = AI)  
- **Alpha-beta odsecanje** radi smanjenja broja posećenih čvorova  
- **Ograničena dubina:** `MAX_DEPTH = 6`  

### Zašto ograničena dubina?

| Faktor | Posledica |
|--------|-----------|
| 5×5 tabla + gravitacija | veliki broj mogućih stanja |
| Puna pretraga (full search) | sekunde po potezu ili više — neupotrebljivo u realnoj igri |

Zato se posle dubine `MAX_DEPTH` koristi **heuristička evaluacija** stanja.

### Heuristika

Kada pretraga ne može da ide do kraja, procenjuje se stanje na osnovu:

- potencijalnih linija (horizontalno, vertikalno, dijagonalno)  
- broja povezanih simbola po strani  

Isprobane varijante u razvoju:

- ponderisane funkcije — preciznije, ali sporije  
- **jednostavno brojanje potencijala** — brže i stabilnije (korišćeno u projektu)  

**Zaključak:** jednostavna heuristika + ograničena dubina = najbolji kompromis brzine i “pametnosti”.

### Flag `testFullSearch`

U `FourInLine5x5Logic` postoji `testFullSearch`:

- `true` — puna pretraga (za eksperimente / merenje performansi)  
- `false` — normalan režim sa dubinom i heuristikom  

Za realnu igru ostaje `false`; full search je praktično beskoristan zbog performansi.

### Performanse (orientaciono)

Mereno pomoću `System.nanoTime()` i `Runtime` (memorija) u razvojnoj verziji:

| Režim | Performanse |
|-------|-------------|
| Full search | veoma sporo (sekunde) |
| Depth-limited + heuristika | tipično **&lt; 100 ms** po AI potezu |

**Tradeoff:** optimalnost vs brzina — u ovom projektu **brzina pobeđuje**.

---

## AI implementacija — Inverse Tic-Tac-Toe

- **Minimax** sa **alpha-beta** odsecanjem  
- Tabla 3×3 → dovoljno malo stanja za **efikasnu punu pretragu** bez posebne heuristike  
- Vrednosti listova: pobeda/ poraz / nereseno u skladu sa inverznim pravilima (tri u nizu = gubitak za onoga ko je odigrao potez)  

---

## Zahtevi

- **JDK** (npr. 26) — `C:\Program Files\Java\jdk-26.0.1`  
- **JavaFX SDK** (LTS 21 preporučeno) — `C:\Program Files\javafx-sdk-21.0.2`  
- Eclipse **nije obavezan** — dovoljan su JDK + JavaFX + terminal  

Putanje su podešene u `.classpath` (JavaFX JAR-ovi iz `javafx-sdk-21.0.2/lib`).

---

## Pokretanje

### Ulazna klasa

`app.MainApp`

### PowerShell (copy/paste)

```powershell
$PROJ="b:\Sve\TudjiProjekti\Aca\Game"
$SRC="$PROJ\src"
$OUT="$PROJ\bin"
$JFX="C:\Program Files\javafx-sdk-21.0.2\lib"
$JDK="C:\Program Files\Java\jdk-26.0.1\bin"

New-Item -ItemType Directory -Force $OUT | Out-Null
Get-ChildItem -Recurse -Filter *.java $SRC | ForEach-Object { $_.FullName } | Set-Content -Encoding ASCII "$PROJ\sources.txt"

& "$JDK\javac.exe" -encoding UTF-8 --release 26 --module-path "$JFX" --add-modules javafx.controls,javafx.graphics -d "$OUT" "@$PROJ\sources.txt"

& "$JDK\java.exe" --module-path "$JFX" --add-modules javafx.controls,javafx.graphics -cp "$OUT" app.MainApp
```

**Napomena:** `--module-path` i `--add-modules` moraju biti i pri **kompilaciji** i pri **pokretanju**.

### Eclipse (opciono)

1. Import projekta (folder `Game`)  
2. Proveri da je JRE **JavaSE-26** i da `.classpath` pokazuje na JavaFX 21  
3. Run → `app.MainApp`  

---

## Poznati problemi (JavaFX / grafika)

Ako se pojavi greška:

`Error initializing QuantumRenderer: no suitable pipeline found`

to **nije greška u kodu igre**, već u inicijalizaciji JavaFX grafičkog pipeline-a na sistemu. Česti uzroci na Windows-u:

- **Parsec Virtual Display Adapter** (virtualni ekran) — probaj *Disable device* u Device Manager-u + restart  
- zastareli ili konfliktni GPU drajveri — update NVIDIA/Intel/AMD drajvera  

Ako JavaFX 26 ne radi, projekat je podešen na **JavaFX 21 LTS** (`javafx-sdk-21.0.2`).

---

## Moguća poboljšanja

### Four in Line 5×5

- adaptivna dubina pretrage (različit `depth` po fazi igre)  
- bolja evaluaciona funkcija (prepoznavanje “skoro pobede”)  
- nivoi težine AI-a  
- optimizacije: transposition table, keširanje stanja  
- režim **dva igrača** lokalno (trenutno: čovek vs AI)  

### Inverse

- vizuelni hint za “opasna” polja  
- nivoi težine (slučajni potez / ograničena dubina)  

### Aplikacija

- čuvanje statistike partija  
- Maven/Gradle build (jedna komanda `run` bez ručnog `javac`)  

Bez ovih koraka projekat ostaje solidan **studentski / seminarski** nivo sa jasnom podelom logike i UI-a.

---

## Literatura

- Russell & Norvig — *Artificial Intelligence: A Modern Approach*  
- [Minimax algorithm (alpha-beta pruning)](https://en.wikipedia.org/wiki/Alpha%E2%80%93beta_pruning) — Wikipedia  
- Materijali sa predavanja (kurs veštačke inteligencije / algoritmi)  

---

## Autor / kontekst

Projekat je nastao spajanjem dve odvojene varijante iks-oks igara u **jedan** JavaFX projekat sa zajedničkim menijem, umesto dva nezavisna foldera/projekta spojena na kraju.
