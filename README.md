# 🔤 Language Processors

![Version](https://img.shields.io/badge/version-1.0.0-blue)
![Status](https://img.shields.io/badge/status-maintained-brightgreen)
![Java](https://img.shields.io/badge/Java-17-orange)
![ANTLR](https://img.shields.io/badge/ANTLR-4-red)
![License](https://img.shields.io/badge/license-MIT-lightgrey)

A comprehensive collection of **language processing tools** built from scratch — covering the full compilation pipeline from lexical analysis to JVM bytecode generation. This project demonstrates core compiler construction concepts through three progressively complex modules.

---

## 📋 Table of Contents

- [The Problem It Solves](#-the-problem-it-solves)
- [Tech Stack](#-tech-stack)
- [Key Features](#-key-features)
- [Project Structure](#-project-structure)
- [Modules](#-modules)
- [Setup Guide](#-setup-guide)
- [Usage](#-usage)
- [Contributors](#-contributors)
- [Future Roadmap](#-future-roadmap)

---

## 🎯 The Problem It Solves

Understanding how programming languages work "under the hood" is essential for every software engineer, yet most compiler courses remain purely theoretical. This toolkit bridges that gap by providing **fully functional, production-quality implementations** of:

- **Finite automata** for pattern matching and string validation
- **Domain-Specific Languages (DSLs)** with custom grammars and semantic actions
- **A complete compiler** that translates a high-level language into executable JVM bytecode

Each module builds upon the previous one, creating a cohesive learning path through the world of language processors.

---

## 🛠️ Tech Stack

| Technology | Purpose |
|:--|:--|
| ☕ **Java 17** | Core implementation language |
| 🔧 **ANTLR 4** | Parser generator for lexer/parser grammars |
| 📦 **Jasmin** | JVM bytecode assembler |
| 🏗️ **Maven** | Build automation and dependency management |

---

## ✨ Key Features

- **DFA Simulator** — Interactive automaton that validates strings against configurable regular expressions and generates accepted languages
- **Custom DSL Parser** — Full ANTLR grammar for a treasure map definition language with semantic actions
- **Interactive Game Engine** — A playable treasure hunt game driven entirely by DSL-parsed map files
- **MiniBasic Compiler** — Compiles a BASIC dialect with variables, control flow, functions, arrays, and I/O into JVM bytecode
- **Symbol Table & Type Inference** — Automatic type detection (int, string, boolean, array) during compilation
- **Jasmin Code Generation** — Produces valid `.j` assembly files that can be assembled into runnable `.class` files
- **23 Example Programs** — Comprehensive test suite covering all language features (loops, recursion, GCD, string operations, error handling)

---

## 📁 Project Structure

```
language-processors/
│
├── 📄 README.md
├── 📄 .gitignore
│
├── 📂 pl1-automaton-simulator/          # Module 1: DFA Engine
│   ├── pom.xml
│   └── src/main/java/
│       ├── DeterministicFiniteAutomaton.java
│       ├── StateMachine.java
│       └── AutomatonMain.java
│
├── 📂 pl2-treasure-map-dsl/            # Module 2: DSL & Game
│   ├── grammar/
│   │   ├── MapLexer.g4                 # Treasure map lexer
│   │   ├── MapParser.g4                # Treasure map parser
│   │   ├── MiniBasicLexer.g4           # MiniBasic lexer (v1)
│   │   └── MiniBasicParser.g4          # MiniBasic parser (v1)
│   ├── src/
│   │   ├── GridCell.java               # Map grid cell model
│   │   ├── TreasureMap.java            # Map data structure
│   │   ├── TreasureHuntGame.java       # Game logic engine
│   │   ├── MapParserListener.java      # DSL → Map builder
│   │   ├── MapAnalyzer.java            # Map file analyzer
│   │   ├── MiniBAnalyzer.java          # MiniBasic analyzer
│   │   ├── MiniBTreePrinter.java       # Parse tree visualizer
│   │   └── TreasureHuntMain.java       # Game entry point
│   └── examples/
│       └── treasure_island.txt         # Sample map definition
│
├── 📂 pl3-minibasic-compiler/          # Module 3: Full Compiler
│   ├── grammar/
│   │   ├── MiniBasicLexer.g4           # Extended lexer (v2)
│   │   └── MiniBasicParser.g4          # Extended parser (v2)
│   ├── src/
│   │   ├── CompilerMain.java           # Compiler entry point
│   │   ├── JasminCodeGenerator.java    # AST → Jasmin bytecode
│   │   ├── Symbol.java                 # Symbol table entry
│   │   └── SymbolTable.java            # Variable scope manager
│   ├── examples/                       # 23 MiniBasic test programs
│   │   ├── for_simple.bas
│   │   ├── gcd_euclid.bas
│   │   ├── functions.bas
│   │   └── ...
│   └── jasmin-exercises/               # Standalone Jasmin exercises
│       ├── HolaMundo.j
│       ├── Sumar.j
│       └── ...
│
└── 📂 docs/
    └── project-reports/                # Academic documentation
```

---

## 📦 Modules

### Module 1: DFA Simulator (`pl1-automaton-simulator`)

A **Deterministic Finite Automaton** engine that supports two pre-configured regular expressions:

| Regex | Pattern | Description |
|:------|:--------|:------------|
| RE1 | `(b\|c)*a(b\|c)*` | Accepts strings containing exactly one `a` |
| RE2 | `a+(a\|b\|c)*` | Accepts strings starting with one or more `a`s |

**Capabilities:** String validation • Accepted language generation (bounded by count and length)

### Module 2: Treasure Map DSL (`pl2-treasure-map-dsl`)

A custom **Domain-Specific Language** for defining treasure maps, paired with an interactive treasure hunt game:

```
"Lost Island Map"
"The Titanic" esta enterrado en 2,3
"The Titanic" te da 100 puntos
en la casilla 1,1 hay una zona prohibida que reduce 50 puntos
en la casilla 2,2 hay un tesoro que da 20 puntos
en la casilla 1,2 hay una isla localizada
fin
```

**Game features:** Three difficulty levels • Ship discovery • Island bonuses • Forbidden zones • Score tracking

### Module 3: MiniBasic Compiler (`pl3-minibasic-compiler`)

A complete **source-to-bytecode compiler** for a BASIC-inspired language that targets the JVM via Jasmin:

```basic
LET x = 10
FOR i = 1 TO x
    IF i > 5 THEN
        PRINT "Big: " + i
    ELSE
        PRINT "Small: " + i
    END
NEXT
```

**Language features:** Variables with type inference • IF/ELSE • WHILE/FOR/REPEAT loops • CONTINUE/EXIT • Subroutines & Functions • String concatenation • Array literals • Built-in functions (VAL, LEN, ISNAN) • User INPUT

---

## 🚀 Setup Guide

### Prerequisites

- **Java 17+** — [Download JDK](https://adoptium.net/)
- **ANTLR 4** — [Installation Guide](https://www.antlr.org/)
- **Jasmin** (for Module 3) — Included in `pl3-minibasic-compiler/jasmin-exercises/`

### Building Module 1 (Maven)

```bash
cd pl1-automaton-simulator
mvn clean compile
mvn exec:java -Dexec.mainClass="AutomatonMain"
```

### Building Modules 2 & 3 (ANTLR)

```bash
# Generate parser from grammar (example for Module 3)
cd pl3-minibasic-compiler
antlr4 grammar/MiniBasicLexer.g4 grammar/MiniBasicParser.g4 -visitor -o src/

# Compile and run
javac -cp .:antlr-4.x-complete.jar src/*.java
java -cp .:antlr-4.x-complete.jar CompilerMain examples/for_simple.bas
```

### Running the Treasure Hunt Game

```bash
cd pl2-treasure-map-dsl
# After ANTLR generation and compilation:
java -cp .:antlr-4.x-complete.jar TreasureHuntMain examples/treasure_island.txt
```

---

## 💡 Usage

### Validate a string with the DFA

```
Select a regular expression (1/2):
1. RE1: (b|c)*a(b|c)*
2. RE2: a+(a|b|c)*
> 1
Select an option (1/2):
1. Validate a string.
> 1
Enter a string of characters:
> bac
Is the string 'bac' valid? true
```

### Compile a MiniBasic program

```bash
java CompilerMain examples/gcd_euclid.bas
# Output: Symbol table + Jasmin code written to output.j

# Assemble and run:
java -jar jasmin.jar output.j
java MiniBProgram
```

---

## 👥 Contributors

| Name | Role |
|:-----|:-----|
| **Andrea Pascual Aguilera** | Developer |
| **Sergio Alonso Zarcero** | Developer |
| **Asier Alamo** | Developer |

---

## 🔮 Future Roadmap

- **🧪 Jasmin Output Verification Pipeline** — Automated end-to-end testing that compiles `.bas` files, assembles the Jasmin output, executes the resulting bytecode, and validates stdout against expected outputs
- **📊 AST Visualization** — Interactive graphical representation of the Abstract Syntax Tree using D3.js or Graphviz, enabling step-by-step parsing visualization in the browser
- **🔧 Language Server Protocol (LSP)** — Implement an LSP server for MiniBasic to provide syntax highlighting, error diagnostics, and autocomplete in VS Code and other editors

---

## 🏷️ Suggested GitHub Topics

`compiler` · `antlr4` · `finite-automaton` · `bytecode` · `language-processing`

---

<p align="center">
Built with ❤️ as part of the <strong>Language Processors</strong> university course.
</p>
