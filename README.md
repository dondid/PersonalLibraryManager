https://github.com/user-attachments/assets/0e22ca7e-5e90-48c1-9fd3-bf24a85b770f

# 📚 Biblioteca Mea Personală

Sistem avansat de gestionare a bibliotecii personale cu interfață grafică modernă, dezvoltat în Java Swing.

## ✨ Caracteristici

### 🎯 Funcționalități Principale
- **Gestionare completă a cărților** - Adăugare, editare, ștergere
- **Sistem de rating** - Evaluare cărți de la 1 la 5 stele
- **Categorii multiple** - Ficțiune, Non-ficțiune, Tehnică, Istorie, Știință, Biografii
- **Status de lectură** - De citit, În curs, Citită
- **Căutare avansată** - Filtrare după titlu, autor, categorie, status
- **Export de date** - Salvare bibliotecă în format text
- **Statistici detaliate** - Analize complete ale bibliotecii

### 🎨 Interfață Modernă
- **Design contemporary** cu culori plăcute și fonturi clare
- **Animații fluide** și efecte hover pe butoane
- **Splash screen animat** cu progress bar
- **Context menu** pentru acțiuni rapide
- **Iconiță personalizată** pentru aplicație
- **Layout responsive** adaptat la diferite rezoluții

### 📊 Statistici Avansate
- Total cărți în bibliotecă
- Distribuție pe statusuri (citite, în curs, de citit)
- Analiza pe categorii cu procente
- Rating mediu al cărților
- Top cărți cu rating maxim
- Total pagini citite

## 🚀 Instalare și Rulare

### Cerințe de Sistem
- **Java 17** sau versiune superioară
- **Sistem de operare:** Windows, macOS, Linux
- **Memorie RAM:** Minimum 512 MB
- **Spațiu pe disc:** 50 MB liberi

### Compilare și Rulare

#### Opțiunea 1: Compilare manuală
```bash
# Compilare
javac PersonalLibraryManager.java

# Rulare
java PersonalLibraryManager
```

#### Opțiunea 2: Folosind IDE
1. Deschide proiectul în **IntelliJ IDEA**, **Eclipse** sau **NetBeans**
2. Compilează și rulează clasa `PersonalLibraryManager`

#### Opțiunea 3: Creare JAR executabil
```bash
# Compilare
javac PersonalLibraryManager.java

# Creare JAR
jar cfe BibliotecaPersonala.jar PersonalLibraryManager *.class

# Rulare JAR
java -jar BibliotecaPersonala.jar
```

## 📖 Utilizare

### Primul Start
1. La pornirea aplicației, va apărea un **splash screen animat**
2. Se vor încărca automat **5 cărți de exemplu** pentru demonstrație
3. Interfața principală se va deschide maximizată

### Adăugarea unei Cărți
1. Click pe butonul **"➕ Adaugă Carte"**
2. Completează informațiile obligatorii:
    - **Titlu** (obligatoriu)
    - **Autor** (obligatoriu)
    - **Categorie** (dropdown)
    - **Status** (dropdown)
    - **Rating** (0-5 stele)
    - **Număr pagini**
    - **Note** (opțional)
3. Click **"💾 Salvează"**

### Căutare și Filtrare
- **Căutare text:** Tastează în câmpul de căutare pentru a găsi cărți după titlu sau autor
- **Filtru categorie:** Selectează o categorie specifică din dropdown
- **Filtru status:** Filtrează după statusul de lectură
- **Reset:** Butonul "🔄 Reset" șterge toate filtrele

### Acțiuni pe Cărți
- **Editare:** Selectează o carte și click "✏️ Editează" sau dublu-click
- **Ștergere:** Selectează o carte și click "🗑️ Șterge"
- **Detalii:** Click dreapta pe o carte → "📄 Detalii"
- **Context menu:** Click dreapta pentru meniu rapid

### Export Date
1. Click pe **"💾 Export"**
2. Alege locația pentru salvare
3. Fișierul `.txt` va conține toate informațiile bibliotecii

### Statistici
Click pe **"📊 Statistici"** pentru a vedea:
- Statistici generale
- Distribuția pe categorii
- Top cărți cu rating maxim

## 🎨 Personalizare

### Culori Tema
Aplicația folosește o paletă de culori moderne:
- **Primar:** `#2980B9` (albastru)
- **Succes:** `#27AE60` (verde)
- **Atenție:** `#F39C12` (portocaliu)
- **Pericol:** `#E74C3C` (roșu)
- **Fundal:** `#F8F9FA` (gri deschis)

### Fonturi
- **Titluri:** Segoe UI Bold, 18-24px
- **Text normal:** Segoe UI Regular, 14px
- **Emoji:** Segoe UI Emoji pentru suport complet

## 🔧 Dezvoltare

### Structura Proiectului
```
PersonalLibraryManager.java
├── PersonalLibraryManager (clasa principală)
├── Book (model pentru cărți)
├── BookDialog (dialog adăugare/editare)
├── BookDetailsDialog (afișare detalii)
├── StatisticsDialog (statistici avansate)
└── SplashScreen (ecran de pornire)
```

### Extensii Posibile
- **Bază de date:** Integrare cu SQLite sau MySQL
- **Import cărți:** Din CSV, Excel sau API-uri externe
- **Backup automat:** Salvare periodică automată
- **Tema întunecată:** Comutator pentru modul întunecat
- **Sincronizare cloud:** Google Drive, Dropbox
- **Grafice avansate:** Statistici cu JFreeChart
- **Export PDF:** Rapoarte în format PDF

## 🐛 Depanare

### Probleme Comune

#### Aplicația nu pornește
- Verifică versiunea Java: `java -version`
- Asigură-te că ai Java 17+

#### Emoji-urile nu se afișează
- Actualizează fontul Segoe UI Emoji
- Pe Linux: instalează `fonts-noto-color-emoji`

#### Interfața pare neclară
- Verifică scalarea DPI din sistemul de operare
- Aplica JVM flags: `-Dsun.java2d.uiScale=1.25`

### Log-uri
Mesajele de eroare sunt afișate în consolă. Pentru debugging detaliat:
```bash
java -Djava.util.logging.level=ALL PersonalLibraryManager
```

## 🤝 Contribuții

Contribuțiile sunt binevenite! Pentru a contribui:

1. **Fork-uiește** repository-ul
2. **Creează** o ramură pentru feature-ul tău (`git checkout -b feature/NovaFunctionalitate`)
3. **Commit-uiește** schimbările (`git commit -am 'Adaugă nova functionalitate'`)
4. **Push-uiește** pe ramură (`git push origin feature/NovaFunctionalitate`)
5. **Creează** un Pull Request

### Ghid pentru Dezvoltatori
- Respectă stilul de cod existent
- Adaugă comentarii pentru funcții complexe
- Testează pe multiple sisteme de operare
- Documentează modificările în README

## 📝 Istoric Versiuni

### v2.0 (Actuală)
- ✨ Interfață grafică complet redesignată
- 🚀 Splash screen animat cu progress bar
- 📊 Statistici avansate cu analize detaliate
- 🎨 Butoane moderne cu efecte hover
- 💾 Export îmbunătățit al datelor
- 🔍 Căutare și filtrare avansată

### v1.0
- 📚 Funcționalitate de bază pentru gestionarea cărților
- ✏️ Adăugare, editare, ștergere cărți
- 📋 Tabel simplu pentru afișare

## 📞 Contact

Pentru întrebări, sugestii sau raportarea bug-urilor:

- **Email:** ionutdanieldodoc@gmail.com
- **Issues:** Folosește sistemul de issues din GitHub
- **Documentație:** Wiki-ul proiectului

## 🏆 Recunoașteri

Mulțumiri speciale pentru:
- **Oracle** pentru Java Swing framework
- **Segoe UI** font family pentru tipografie excelentă
- Comunitatea **Java** pentru inspirație și suport

---

**Dezvoltat cu ❤️ pentru iubitorii de cărți!**

© 2025 Biblioteca Mea Personală. Toate drepturile rezervate.
