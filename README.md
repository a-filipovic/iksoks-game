🎮 Tic-Tac-Toe 5x5 with Gravity (Iks-oks sa gravitacijom)
📌 Opis projekta

Ovaj projekat predstavlja proširenu verziju klasične igre Tic-Tac-Toe:

Tabla: 5x5
Mehanika: gravitacija (potezi se igraju kao u Connect Four — figura pada na najnižu slobodnu poziciju u koloni)
Režimi igre:
Igra za dva igrača
Igra protiv AI protivnika

AI koristi Minimax algoritam sa alpha-beta odsecanjem za donošenje odluka.

🧱 Struktura projekta

Kod je organizovan tako da razdvaja logiku igre od prikaza:

GameLogic
Osnovna klasa sa zajedničkom logikom igre.
GameLogic5x5
Implementacija pravila za 5x5 tablu + Minimax algoritam + evaluacija stanja.
FourInLine5x5
JavaFX UI sloj (renderovanje table, klikovi, prikaz poteza).

👉 Ova podela nije luksuz — ovo je minimum ako želiš da kod ostane održiv.

🧠 AI implementacija

AI koristi:

Minimax algoritam
Alpha-beta odsecanje za optimizaciju
Ograničenu dubinu pretrage:
MAX_DEPTH = 6

Zašto ograničenje?

Brutalna realnost:

5x5 tabla → eksplozija broja stanja
Full search → neupotrebljiv (sekunde po potezu ili više)

Postoji i:

testFullSearch flag
→ koristi se za testiranje
→ praktično beskoristan za realnu igru zbog performansi
⚖️ Funkcija evaluacije

Pošto se ne može pretražiti celo stablo:

AI koristi heuristiku koja procenjuje stanje na osnovu:

potencijalnih linija (horizontalno, vertikalno, dijagonalno)
broja povezanih simbola

Isprobane varijante:

❌ ponderisane funkcije (preciznije ali sporije)
✅ jednostavno brojanje potencijala (brže i stabilnije)

Zaključak:
Jednostavno > pametno ali sporo

🚀 Performanse

Testirano pomoću:

System.nanoTime() → vreme izvršavanja
Runtime.getRuntime() → memorija

Rezultati:

Režim	Performanse
Full search	❌ veoma sporo (sekunde)
Depth-limited	✅ < 100 ms po potezu

👉 Ovo je ključni tradeoff:
optimalnost vs brzina → brzina pobeđuje

🧪 Zaključci
Full Minimax na 5x5 → neizvodljiv
Ograničena dubina + dobra heuristika → najbolji kompromis
AI igra dovoljno pametno da bude izazovan, ali ne ubija performanse
🔧 Moguća poboljšanja

Ako želiš da ovo bude ozbiljniji projekat, ovde su realni sledeći koraci:

adaptivna dubina pretrage (ne koristi isti depth u svim situacijama)
bolja evaluaciona funkcija (prepoznavanje skoro-pobedničkih stanja)
različiti nivoi težine AI-a
optimizacija Minimax-a (transposition table, caching)

Ako ovo ne uradiš — projekat ostaje studentski nivo, ništa više.

📚 Literatura
Russell & Norvig – Artificial Intelligence: A Modern Approach
Minimax algorithm (alpha-beta pruning) – Wikipedia
Materijali sa predavanja
