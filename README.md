# Prayer Time

A lightweight Java application that calculates daily prayer times **locally** (using accurate astronomical calculations) and outputs the result in **JSON format**.

Designed to be:
-  **API-free:** No external dependencies on prayer time APIs.
-  **Offline-capable:** Works without internet after initial location fix.
-  **Accurate:** Uses astronomical algorithms via the Adhan library.
-  **Integration-friendly:** Optimized for status bars like Waybar, Polybar, or custom scripts.

---

## ✨ Features

- **Local Calculation:** Computes times on your CPU, ensuring privacy and speed.
- **Auto Location:** Detects location automatically via IP and caches it locally.
- **Configurable:** Supports manual offsets and adjustments.
- **JSON Output:** Prints a single-line JSON, perfect for custom modules.

---

## 🛠 Prerequisites

* **Java JDK 21**
* **Maven 3.9.12** or newer

Check your versions:
```bash
java --version
mvn --version
```
---

 Installation

Clone the repository and build the project using Maven:
```bash

git clone https://github.com/othman-su57/Prayer-Time.git
cd Prayer-Time

mvn clean package
```
After a successful build, the JAR file will be generated in: target/PrayerTime-1.0-SNAPSHOT.jar

## Usage

Run the application using:
```bash

java -jar target/PrayerTime-1.0-SNAPSHOT.jar
```
## Output

The application prints a single-line JSON object to standard output (stdout).

---
##Example output:

```json
{
  "text": "Dhuhr 00:27",
  "tooltip": "Fajr: 05:16\nDhuhr: 12:08\nAsr: 15:09\nMaghrib: 17:32\nIsha: 18:51"
}
```
---
##This format is suitable for:

    Waybar custom modules (return-type": "json")

    Polybar scripts

    Shell scripts or cron jobs

    Any tool expecting JSON input
---
## Location Handling

    Detection: Location is detected automatically on the first run using IP-based geolocation.

    Caching: The detected latitude and longitude are cached locally in /tmp/prayerTime.json.

    Offline Mode: Subsequent runs read from the cache and do not require a network connection.
---
## Calculation Details

    Engine: Uses astronomical calculations via the Adhan Java Library.

    Logic: Prayer times are calculated locally based on the cached coordinates and date.

    Adjustments: Supports manual prayer time offsets and calculation method configuration (configurable in the source code).
---
## Integration Example (Waybar)

Add the following to your Waybar configuration file:

```json
"custom/prayer": {
    "exec": "java -jar /path/to/PrayerTime-1.0-SNAPSHOT.jar",
    "interval": 60,
    "return-type": "json"
}
```
---
## Notes

    Platform: Designed primarily for Linux environments.

    Runtime: Requires Java runtime environment (JRE) installed to execute.

    Type: This is a CLI utility, not a full GUI application.

---
## License

GPL 3
