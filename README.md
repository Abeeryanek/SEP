# Sep-Voyage

Fahrdienst-Webanwendung (Kunden fragen Fahrten an, Fahrer machen Angebote) mit Kartenansicht, Routenplanung, Echtzeit-Fahrtsimulation und Live-Chat. Entwickelt im Rahmen des Software-Engineering-Projekts (SEP) von **Gruppe E**.

## Inhaltsverzeichnis

- [Features](#features)
- [Tech-Stack](#tech-stack)
- [Voraussetzungen](#voraussetzungen)
- [Installation](#installation)
  - [Variante A: Docker (empfohlen)](#variante-a-docker-empfohlen)
  - [Variante B: Lokale Installation](#variante-b-lokale-installation)
- [Benutzung](#benutzung)
- [Hinweise zum Testen](#hinweise-zum-testen)
- [Projektstruktur](#projektstruktur)
- [Team](#team)

## Features

Die Entwicklung erfolgte in drei Zyklen.

**Zyklus I – Grundlagen**
- Registrierung und Login (Kunde oder Fahrer) mit Zwei-Faktor-Authentifizierung per E-Mail und einem Supercode als Alternative
- Benutzerprofile mit Suchfunktion
- Interaktive Karte (Zoom, Verschieben, Start, Ziel und Zwischenstopps setzen, Route anzeigen)
- Fahranfragen erstellen (Start und Ziel per aktueller Position, POI, Koordinaten oder Adresse; Wahl der Autoklasse), anzeigen und löschen

**Zyklus II – Fahrten**
- Geldkonto für Kunden (Guthaben aufladen, im Profil einsehen)
- Liste der verfügbaren Fahranfragen für Fahrer, sortierbar nach Entfernung, Distanz, Dauer, Preis und Kundenbewertung
- Fahrtplanung mit Distanz, Dauer und Preis
- Fahrangebote: Fahrer senden oder ziehen Angebote zurück, Kunden nehmen an oder lehnen ab
- Fahrtdurchführung als Echtzeit-Simulation mit Start/Pause/Fortsetzen, einstellbarer Geschwindigkeit, automatischer Abrechnung und gegenseitiger Bewertung (1 bis 5 Sterne)
- Fahrhistorie

**Zyklus III – Erweiterungen**
- Fahrten mit mehreren Zwischenstopps
- Grafische Statistiken für Fahrer (Einnahmen, Distanz, Fahrzeit, Durchschnittsbewertung; monatlich oder täglich)
- Live-Änderungen während der Fahrt (Ziel ändern, Zwischenstopps hinzufügen oder entfernen)
- Fahrer-Leaderboard
- Live-Chat während der Fahrsimulation (Nachrichten bearbeiten, löschen, als gelesen markieren)

## Tech-Stack

- **Backend:** Java 17, Spring Boot, Maven, JWT, WebSockets
- **Frontend:** Angular, Leaflet (Karten)
- **Datenbank:** MySQL 8
- **Deployment:** Docker, Docker Compose

## Voraussetzungen

**Für Docker:**
- [Docker Desktop](https://www.docker.com/products/docker-desktop/)

**Für die lokale Installation:**
- Java 17 (z. B. OpenJDK 17)
- Maven
- Node.js ab Version 18 und npm
- Angular CLI (`npm install -g @angular/cli`)
- MySQL 8

## Installation

### Variante A: Docker (empfohlen)

1. Folgende Image-Dateien herunterladen und in einem gemeinsamen Ordner speichern (z. B. `docker_images`). Dort muss auch die Datei `docker-compose.yml` liegen:
   - `sepdrive-gruppe-e-backend.tar`
   - `sepdrive-gruppe-e-frontend.tar`
   - `mysql_8.0.tar`
2. Docker Desktop starten und warten, bis der Dienst vollständig läuft.
3. Im Ordner ein Terminal öffnen und die Images importieren:
   ```bash
   docker load -i sepdrive-gruppe-e-backend.tar
   docker load -i sepdrive-gruppe-e-frontend.tar
   docker load -i mysql_8.0.tar
   ```
4. Container starten:
   ```bash
   docker compose up -d
   ```

Weitere Hinweise stehen in der `readme-docker.md`.

### Variante B: Lokale Installation

**1. Datenbank**

MySQL 8 installieren und eine Datenbank namens `sep_drive` anlegen. Die Zugangsdaten müssen mit denen in der `application.properties` übereinstimmen (Standard: Benutzer `root`, Passwort `sep1`).

**2. Backend** (Ordner `gruppe-e/backend`)

```bash
mvn clean install -DskipTests
mvn spring-boot:run
```

**3. Frontend** (Ordner `gruppe-e/frontend`)

```bash
npm install
npm install @angular/cli
npm install leaflet
npm start
```

Das Frontend ist anschließend unter <http://localhost:4200> erreichbar.

## Benutzung

### Registrierung und Login
1. Auf **Registrierung** klicken und alle Angaben ausfüllen.
2. Mit Benutzername und Passwort anmelden.
3. Der 2FA-Code kommt per E-Mail an die hinterlegte Adresse.
4. Nach erfolgreichem Login bestätigt man die Meldung mit **OK** und landet auf der Homepage.

### Profile und Suche
- Eigenes Profil: Profilbild-Icon oben rechts, dann **Profile**
- Profil suchen: Lupen-Icon anklicken und den Benutzernamen eingeben

### Karte
- Im linken Panel einen Button wählen (z. B. **Ziel hinzufügen**) und danach auf die Karte klicken.
- Start und Ziel sind Pflicht, jeweils nur einmal. Zwischenstopps sind optional.
- **Strecke anzeigen** berechnet die Route, **Karte zurücksetzen** entfernt alle Pins und die Route.

### Fahranfrage erstellen (Kunde)
1. Start und Ziel festlegen (aktuelle Position, POI, Koordinaten oder Adresse). Bei Adresse oder POI muss ein Eintrag aus der Vorschlagsliste gewählt werden.
2. Optional über **Zwischenstopps hinzufügen** weitere Wegpunkte ergänzen.
3. Autoklasse wählen.
4. Mit **Route anzeigen** werden Distanz, Dauer und Preis angezeigt.
5. **Fahranfrage erstellen** sendet die Anfrage ab.

Die aktive Fahrt erscheint unter **Meine Fahrt** auf dem Dashboard. Sie kann gelöscht oder abgeschlossen werden, solange sie noch nicht angenommen wurde.

### Fahrangebote
1. Der Fahrer öffnet **Verfügbare Fahranfragen** und klickt bei einer Anfrage auf **annehmen**. Erneutes Klicken (**zurücknehmen**) zieht das Angebot zurück.
2. Der Kunde öffnet **Offene Fahrangebote** und nimmt ein Angebot an oder lehnt es ab.

Ein Fahrer kann nur Anfragen annehmen, deren gewünschte Autoklasse zu seinem Fahrzeug passt.

### Fahrsimulation
Profilbild-Icon, dann **Ride Simulation**.

- Start, Pause und Fortsetzen, Geschwindigkeit per Schieberegler (Fahrtdauer 3 bis 30 Sekunden)
- Kunde kann live das Ziel ändern sowie Zwischenstopps hinzufügen oder per Doppelklick entfernen (nur noch nicht passierte)
- Nach Ankunft wird der Fahrpreis automatisch abgebucht bzw. gutgeschrieben, anschließend ist eine gegenseitige Bewertung (1 bis 5 Sterne) möglich
- Live-Chat mit Bearbeiten, Löschen und **mark as read**. Gelesene Nachrichten lassen sich nicht mehr ändern oder löschen.

### Weitere Funktionen
- **Mein Guthaben** (Kunde): Guthaben aufladen
- **Fahrhistorie**: alle abgeschlossenen Fahrten mit Details
- **Meine Statistiken** (Fahrer): Diagramme nach Monat oder Tag
- **Fahrleaderboard**: Ranking aller Fahrer

## Hinweise zum Testen

- Für Echtzeit-Funktionen (Angebote, Simulation, Chat) **zwei verschiedene Browser** verwenden: einen für den Kunden, einen für den Fahrer.
- Eher kurze Strecken testen.
- Bei Live-Änderungen Ziel oder Zwischenstopps nicht direkt an der aktuellen Fahrzeugposition setzen.
- Während einer laufenden Simulation die Seite nicht neu laden, da dies die Synchronisation stören kann.
- Die erste Fahranfrage gilt als aktuelle Fahrt und steht unter **Meine Fahrt**. Erst nach ihrem Abschluss erscheinen Historie und weitere Anfragen unter **Fahranfragen anzeigen**.

##