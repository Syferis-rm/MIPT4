# 📱 Android Skaičiuotuvas (Calculator App)

[![Android Platform](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-purple.svg)](https://kotlinlang.org)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-24%20(Android%207.0)-blue.svg)](https://developer.android.com/about/versions/nougat)
[![Target SDK](https://img.shields.io/badge/Target%20SDK-34%20(Android%2014)-brightgreen.svg)](https://developer.android.com/about/versions/14)
[![Architecture](https://img.shields.io/badge/Architecture-MVVM-orange.svg)](https://developer.android.com/topic/architecture)

Moderni, intuityvi ir funkcionali Android skaičiuotuvo programėlė, sukurta naudojant **Kotlin** ir modernią **Android Jetpack MVVM** architektūrą. Programėlė pasižymi Windows 10/11 stiliaus standartinio skaičiuotuvo naudotojo sąsaja, savarankiškai realizuotu matematiniu analizatoriumi (angl. *recursive descent parser*), atminties registrais bei atskira skaičiavimų istorijos sekimo sistema.

---

## 📑 Turinys

1. [Pagrindinės savybės ir funkcijos](#-pagrindinės-savybės-ir-funkcijos)
2. [Vartotojo vadovas](#-vartotojo-vadovas)
   - [Baziniai skaičiavimai ir operacijų eiliškumas](#baziniai-skaičiavimai-ir-operacijų-eiliškumas)
   - [Specialiosios operacijos](#specialiosios-operacijos)
   - [Atminties funkcijos (MC, MR, M+, M−, MS)](#atminties-funkcijos-mc-mr-m-m--ms)
   - [Skaičiavimų istorija](#skaičiavimų-istorija)
   - [Klaidų pranešimai ir prevencija](#klaidų-pranešimai-ir-prevencija)
3. [Techninė architektūra ir sprendimai](#-techninė-architektūra-ir-sprendimai)
   - [Matematinių išraiškų variklis (CalculatorEngine)](#matematinių-išraiškų-variklis-calculatorengine)
   - [MVVM architektūra ir duomenų srautai](#mvvm-architektūra-ir-duomenų-srautai)
4. [Projekto struktūra](#-projekto-struktūra)
5. [Reikalavimai ir diegimas](#-reikalavimai-ir-diegimas)
6. [Automatizuotas testavimas](#-automatizuotas-testavimas)

---

## 🚀 Pagrindinės savybės ir funkcijos

- **Matematinis tikslumas ir operacijų pirmumas**: daugyba ir dalyba vykdoma prieš sudėtį ir atimtį (pvz., `2 + 3 × 4 = 14`, o ne `20`).
- **Nėra išorinių bibliotekų skaičiavimams**: matematinis analizatorius parašytas nuo nulio (angl. *lexer & recursive descent parser*).
- **Atminties valdymas**: palaikomi standartiniai atminties registrai su dinaminiu mygtukų aktyvumu (mygtukai pasyvūs, kai atmintis tuščia).
- **Skaičiavimų istorija**: automatinis atliktų skaičiavimų registravimas, peržiūra atskirame lange bei istorijos išvalymas.
- **Šoninis meniu (Navigation Drawer)**: patogi navigacija tarp pagrindinio skaičiuotuvo ir istorijos lango.
- **Daugiakalbystė (i18n)**: pilna integracija lietuvių (`values-lt`) ir anglų (`values`) kalboms su lokalizuotais tekstais ir kablelio/taško palaikymu.
- **Prisitaikantis dizainas**: mygtukai su vizualiniu paspaudimo atsaku (angl. *ripple effects*), palaikoma ekrano rotacija ir prisitaikymas prie skirtingų ekrano dydžių.

---

## 📖 Vartotojo vadovas

### Mygtukų funkcijos

| Mygtukas | Pavadinimas | Aprašymas |
| :---: | :--- | :--- |
| **0 – 9** | Skaitmenys | Įveda skaičių reikšmes į dabartinį laukelį |
| **,** arba **.** | Kablelis / Taškas | Įveda trupmeninę dalį (neleidžia įvesti daugiau nei vieno kablelio viename skaičiuje) |
| **+** | Sudėtis | Sudeda du operandus |
| **−** | Atimtis | Atima dešinįjį operandą iš kairiojo |
| **×** | Daugyba | Daugina reikšmes |
| **÷** | Dalyba | Dalina kairįjį operandą iš dešiniojo |
| **=** | Lygu | Apskaičiuoja visos išraiškos rezultatą |
| **%** | Procentai | Apskaičiuoja procentinę vertę priklausomai nuo konteksto |
| **¹/x** | Trupmena | Apskaičiuoja atvirkštinę reikšmę ($1 / x$) |
| **x²** | Kvadratas | Pakelia dabartinį skaičių kvadratu ($x^2$) |
| **²√x** | Kvadratinė šaknis | Ištraukia kvadratinę šaknį ($\sqrt{x}$) |
| **⁺/₋** | Ženklo keitimas | Pakeičia skaičiaus ženklą (teigiamą į neigiamą ir atvirkščiai) |
| **CE** | Clear Entry | Išvalo tik dabartinę įvestį, palikdamas anksčiau įvestą išraišką |
| **C** | Clear All | Pilnai išvalo visą įvestą išraišką ir dabartinį skaičių |
| **⌫** | Backspace | Pašalina paskutinį įvestą skaitmenį |

---

### Baziniai skaičiavimai ir operacijų eiliškumas

1. Įveskite pirmąjį skaičių paspausdami skaitmenų mygtukus.
2. Pasirinkite norimą operatorių (`+`, `−`, `×`, `÷`). Viršutinėje eilutėje matysite formuojamą išraišką.
3. Jei norite pakeisti pasirinktą operatorių prieš įvesdami kitą skaičių, tiesiog paspauskite kitą operatoriaus mygtuką — programa automatiškai jį pakeis.
4. Įveskite kitą operandą ir paspauskite **`=`**.
5. Programa atliks veiksmus pagal matematikos taisykles:
   $$\text{Pavyzdys: } 5 + 2 \times 3 = 11$$
6. Gavus rezultatą, iš karto paspaudus operatorių (`+`, `−`, `×`, `÷`), skaičiavimas tęsiamas su gautu rezultatu.

---

### Specialiosios operacijos

- **Kvadratinė šaknis (`²√x`)**: įveskite skaičių (pvz., `16`) ir paspauskite `²√x` $\rightarrow$ ekrane rodoma `4`. Jei bandoma traukti šaknį iš neigiamo skaičiaus, pateikiamas pranešimas *„Neteisingi duomenys“* (*Invalid input*).
- **Kėlimas kvadratu (`x²`)**: įveskite skaičių (pvz., `9`) ir paspauskite `x²` $\rightarrow$ ekrane rodoma `81`.
- **Atvirkštinis skaičius (`¹/x`)**: įveskite skaičių (pvz., `4`) ir paspauskite `¹/x` $\rightarrow$ ekrane rodoma `0.25`. Jei skaičius yra `0`, pateikiamas pranešimas *„Negalima dalinti iš nulio“*.
- **Ženklo keitimas (`⁺/₋`)**: bet kada pakeičia įvedamo skaičiaus ar gauto rezultato poliariškumą.

---

### Atminties funkcijos (MC, MR, M+, M−, MS)

Programėlė palaiko vieną atminties registrą:

- **MS (Memory Store)**: išsaugo dabartinį ekrane rodomą skaičių į atmintį. Išsaugojus reikšmę, mygtukai `MC` ir `MR` tampa aktyvūs (pasidaro ryškūs).
- **MR (Memory Recall)**: atkuria atmintyje esančią reikšmę į dabartinį įvesties laukelį.
- **M+ (Memory Add)**: prideda dabartinę ekrano reikšmę prie atmintyje jau saugomo skaičiaus.
- **M− (Memory Subtract)**: atima dabartinę ekrano reikšmę iš atmintyje esančio skaičiaus.
- **MC (Memory Clear)**: anuliuoja atminties registrą (nustato į `0`). Mygtukai `MC` ir `MR` tampa pasyvūs (pilki).

---

### Skaičiavimų istorija

Kiekvieną kartą paspaudus **`=`**, atlikta išraiška ir rezultatas yra automatiškai įrašomi į istoriją.

1. Norėdami atidaryti istoriją, spustelėkite viršutiniame kairiajame kampe esantį **meniu mygtuką (☰)** (Navigation Drawer).
2. Šoniniame meniu pasirinkite punktą **„Istorija“ (History)**.
3. Atsivėrusiame lange pateikiamas visų atliktų skaičiavimų sąrašas (`Išraiška = Rezultatas`).
4. Norėdami išvalyti visą istoriją, paspauskite ekrano apačioje esantį mygtuką **„Išvalyti istoriją“ (Clear History)**.
5. Grįžti į skaičiuotuvą galima paspaudus viršutinį atgal mygtuką (**←**).

---

### Klaidų pranešimai ir prevencija

- **Dalyba iš nulio**: bandoma dalinti iš nulio $\rightarrow$ programėlė aiškiai informuoja:
  *LT:* `Negalima dalinti iš nulio` | *EN:* `Cannot divide by zero`
- **Šaknis iš neigiamo skaičiaus**: bandoma atlikti $\sqrt{-x}$ $\rightarrow$:
  *LT:* `Neteisingi duomenys` | *EN:* `Invalid input`
- **Sintaksės klaidos**: bandoma įvertinti neteisingą išraišką $\rightarrow$ rodoma `Klaida` / `Error`.
- **Daugkartiniai kableliai**: mygtukas `,` blokuoja bandymą įvesti antrą kablelį į tą patį skaičių.

---

## 🛠 Techninė architektūra ir sprendimai

Projektas sukurtas vadovaujantis oficialiomis Android architektūros gairėmis:

```
com.calculator.app
├── CalculatorEngine.kt      # Lekseris ir Recursive Descent Parser išraiškų skaičiavimui
├── CalculatorViewModel.kt   # UI būsenos valdymas, LiveData, verslo logika
├── MainActivity.kt          # Pagrindinis vartotojo sąsajos ekranas, mygtukų susiejimas
├── HistoryActivity.kt       # Skaičiavimų istorijos ekranas
├── HistoryAdapter.kt        # RecyclerView adapteris istorijos elementų atvaizdavimui
├── HistoryEntry.kt          # Istorijos įrašo duomenų modelis (data class)
└── HistoryRepository.kt     # Vienvietis (Singleton) istorijos duomenų šaltinis
```

### Matematinių išraiškų variklis (`CalculatorEngine`)

Skaičiavimų variklis nenaudoja jokių trečiųjų šalių bibliotekų. Jis naudoja formaliąją gramatiką:

```
expression = term (('+' | '-') term)*
term       = factor (('*' | '/') factor)*
factor     = ['+' | '-'] (NUMBER | '(' expression ')')
```

Veikimo etapai:
1. **Normalizavimas**: vartotojo sąsajos simboliai `×`, `÷`, `−` bei dešimtainis kablelis `,` pakeičiami standartiniais `*`, `/`, `-`, `.`.
2. **Tokenizavimas (Lexer)**: eilutė suskaidoma į atskirus žetonus (`Number`, `Plus`, `Minus`, `Multiply`, `Divide`, `LParen`, `RParen`).
3. **Parsinimas ir skaičiavimas (Parser)**: rekursyvaus nusileidimo metodu pagal operatorių pirmumą apskaičiuojama galutinė reikšmė.
4. **Formatavimas (`formatResult`)**: sveikieji skaičiai atvaizduojami be kablelio (pvz., `4.0` rodomas kaip `4`), o dešimtainiai skaičiai apvalinami iki 10 ženklų po kablelio be perteklinių nulinių pabaigoje.

### MVVM architektūra ir duomenų srautai

- **CalculatorViewModel**:
  - `displayText: LiveData<String>` – pagrindinė ekrano reikšmė.
  - `expressionText: LiveData<String>` – viršutinėje eilutėje rodoma pilna išraiška.
  - `memoryActive: LiveData<Boolean>` – nusako, ar atmintyje yra reikšmė (valdo mygtukų vizualinę būseną).
  - `historyList: LiveData<List<HistoryEntry>>` – reaguojanti istorijos duomenų seka.
- **ViewBinding**: visi sąsajos elementai pasiekiami per saugius tipus be `findViewById`.

---

## 💻 Reikalavimai ir diegimas

### Reikalavimai sistemai

- **Android Studio**: Jellyfish / Koala / Ladybug (arba naujesnė)
- **Android SDK**: Min SDK 24 (Android 7.0 Nougat), Target SDK 34 (Android 14)
- **JDK**: Java 17 arba Java 21

### Diegimas ir paleidimas

1. Klonuokite GitHub saugyklą:
   ```bash
   git clone https://github.com/Syferis-rm/mipt4.git
   cd mipt4
   ```

2. Atidarykite projektą **Android Studio** aplinkoje.

3. Paleiskite projektą emuliatoriuje arba prijungtame fiziniame įrenginyje paspaudę **Run** (`Shift + F10`).

4. Kompiliavimas per komandinę eilutę:
   - **Linux / macOS**:
     ```bash
     ./gradlew assembleDebug
     ```
   - **Windows**:
     ```powershell
     .\gradlew.bat assembleDebug
     ```
   Gautas APK failas bus sugeneruotas kataloge: `app/build/outputs/apk/debug/app-debug.apk`.

---

## 🧪 Automatizuotas testavimas

Projekte realizuoti automatizuoti vienetų testai (`CalculatorEngineTest.kt`), tikrinantys:

- Bazines operacijas ir operatorių eiliškumą (`2 + 3 * 4 = 14`, `(2 + 3) * 4 = 20`)
- Vartotojo sąsajos simbolių normalizavimą (`×`, `÷`, `−`)
- Dešimtainio kablelio `,` palaikymą (Lietuvių lokalė)
- Unarinį minusą (`-5`, `--5`, `-5 + 3`)
- Apsaugą nuo dalybos iš nulio (`ArithmeticException`)
- Netinkamos sintaksės klaidų apdorojimą (`ParseException`)
- Rezultatų formatavimo nepriklausomumą nuo sistemos lokalės

Testų paleidimas:
```bash
./gradlew test
```

---

## 📄 Licencija

Projektas skirtas mokymosi ir demonstraciniams tikslams (MIPT4).
Autorius: **Syferis-rm**
