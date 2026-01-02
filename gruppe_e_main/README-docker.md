# SEP Drive – Schnellstart mit Docker

Sehr geehrter Herr Professor,

hier finden Sie eine kurze und einfache Anleitung, wie Sie mein gesamtes Projekt (Backend, Frontend, Datenbank) mit Docker starten können. Sie benötigen lediglich Docker & Docker Compose – weitere Installationen sind nicht nötig.

---

## Was ist enthalten?
- **Backend:** Spring Boot (Java), übernimmt die gesamte Logik und kommuniziert mit der Datenbank
- **Frontend:** Angular, stellt die Benutzeroberfläche bereit
- **MySQL:** Die Datenbank, in der alle Daten gespeichert werden

---

## Wie starten Sie das Projekt?

1. **Projekt klonen** (falls noch nicht geschehen):
   ```bash
   git clone <repo-url>
   cd <repo-ordner>
   ```

2. **Bauen & Starten:**
   Im Hauptverzeichnis (dort, wo die `docker-compose.yaml` liegt) folgenden Befehl ausführen:
   ```bash
   docker-compose up --build
   ```
   Der erste Start kann ein wenig dauern, da alle Komponenten (Backend, Frontend, Datenbank) gebaut werden.

   **Hinweis:**
   - Das Backend wird jetzt automatisch im Container gebaut (Multi-Stage-Build mit Maven). Sie müssen **kein** JAR mehr lokal bauen oder Maven/Java installieren!
   - Das Frontend wird ebenfalls komplett im Container gebaut. Es sind keine lokalen Node/npm/Angular-Installationen nötig.

3. **Zugriff:**
   - Das **Frontend** erreichen Sie unter: [http://localhost](http://localhost)
   - Das **Backend** läuft auf: [http://localhost:8080](http://localhost:8080)
   - Die **Datenbank** (MySQL) läuft intern im Docker-Netzwerk und ist normalerweise nicht direkt erforderlich.

---

## Wie stoppen Sie das Projekt?

Im selben Verzeichnis einfach:
```bash
docker-compose down
```

---

## Hinweise
- Die Datenbank speichert ihre Daten in einem Docker-Volume, sodass die Daten auch nach dem Stoppen erhalten bleiben.
- Möchten Sie alles komplett löschen (inklusive Datenbankdaten):
  ```bash
  docker-compose down -v
  ```
- Die Zugangsdaten für die Datenbank finden Sie in der `docker-compose.yaml` und werden als Umgebungsvariablen an das Backend übergeben.
- Nach Änderungen am Code bitte erneut `docker-compose up --build` ausführen.
- **MySQL im Container läuft auf Port 3306**. Falls auf Ihrem Rechner bereits ein MySQL-Server läuft, kann es zu einem Port-Konflikt kommen. In diesem Fall müssen Sie den lokalen MySQL-Dienst stoppen:
  1. Drücken Sie `Windows + R`, geben Sie `services.msc` ein und bestätigen Sie.
  2. Suchen Sie in der Liste nach `MySQL80` (oder ähnlich).
  3. Rechtsklick → `Beenden`.
  4. Jetzt ist der Port 3306 frei und Docker kann starten.
- **Hinweis:** Der Build-Prozess (insbesondere das Backend mit Maven) kann beim ersten Mal etwas länger dauern. Bitte haben Sie etwas Geduld – es wird funktionieren!

---

## Bei Problemen
- Bitte prüfen Sie, ob Docker läuft.
- Schauen Sie ins Terminal, ob Fehlermeldungen erscheinen.
- Falls Docker beim Bauen des Frontends mit einer Fehlermeldung wie "bundle initial exceeded maximum budget" abbricht, bitte das Budget in der Datei `gruppe-e/frontend/angular.json` unter `budgets` erhöhen (z.B. auf 3MB für `maximumError`).
- Bei weiteren Fragen stehe ich gerne zur Verfügung.

---

## Optional: Docker-Images direkt weitergeben

Falls Sie die Docker-Images nicht selbst bauen möchten (z.B. weil kein Build-Tool installiert ist), können die Images auch als Datei exportiert und importiert werden.

### Images exportieren (auf dem Entwickler-Rechner):
```bash
docker save -o sepdrive-gruppe-e-backend.tar sepdrive-gruppe-e-backend:latest
docker save -o sepdrive-gruppe-e-frontend.tar sepdrive-gruppe-e-frontend:latest
```

### Images importieren (auf dem Ziel-Rechner):
```bash
docker load -i sepdrive-gruppe-e-backend.tar
docker load -i sepdrive-gruppe-e-frontend.tar
```

Danach können Sie wie gewohnt mit `docker-compose up` starten. Die MySQL-Datenbank wird automatisch als Standard-Image geladen.

---

Vielen Dank für Ihr Interesse und viel Erfolg beim Testen! 