# Dust Tracking App 📱🌫️  

Gerçek-zamanlı **toz yoğunluğu takibi**, **Firebase bulut kaydı** ve **GPT-4o-mini tabanlı Türkçe tavsiye** sunan açık-kaynak Android uygulaması.  
ESP32 + toz sensörü donanımından Bluetooth Low Energy (BLE) ile veri çeker; telefonu internet bağlantısı olsa da olmasa da canlı ölçüm gösterir; verileri Firestore’a kaydeder ve son ölçümlerden hareketle hava iyileştirme önerileri üretir.

---

## Özellikler 🚀  

| Mobil Özellik | Açıklama |
|---------------|----------|
|🔒 **E-posta/Şifre Girişi** | Firebase Auth ile kullanıcı bazlı oturum |
|📡 **BLE Tarama & Bağlanma** | “Dust Sensor” cihazını 5 sn içinde bulur |
|📈 **Canlı Ölçüm Kartı** | 3 s’de bir sensör verisi güncellenir |
|🗄️ **Bulut Kayıt** | Her ölçüm `users/{uid}/dustLogs` koleksiyonuna yazılır |
|📚 **Kayıtlar Ekranı** | Sıralama / filtre (son 1 saat, bugün, vb.) + sayfa başına 60 öğe |
|🤖 **AI Tavsiye** | Son 1 saatin en yüksek 20 değeri ➜ GPT-4o-mini ➜ ≤150 kelime Türkçe öneri |
|🌙 **Karanlık / Aydınlık Tema** | Material 3 kapsamında otomatik geçiş |

> **Donanım (özet)**  
> ESP32-DevKit C, Sharp GP2Y1010AU0F toz sensörü, 5 cm fan + 3 D baskı muhafaza.  
> Firmware: `firmware/dust_sensor_ble.ino` – BLE Notify karakteristiğiyle μg/m³ değerini yayınlar.

---

## Mimari (MVVM)

Donanım → BLE

ESP32 + toz sensörü

Her 3 sn’de bir ölçüm yapar, Bluetooth Low Energy Notify ile telefonu bilgilendirir.

Uygulama Katmanı (Android)

BleViewModel: Sensörden gelen veriyi alır, ekrana ve buluta iletir.

LogsViewModel: Firestore’daki eski ölçümleri listeler, filtreler.

AdviceViewModel: Son 1 saatin verisini OpenAI’ye gönderir, öneri alır.

Bulut Servisleri

Firebase Auth: E-posta/şifre ile giriş.

Firestore: users/{uid}/dustLogs koleksiyonuna ölçümleri kaydeder.

OpenAI GPT-4o-mini: Ölçümlere göre Türkçe tavsiye döndürür.

Akış: Sensör - BleViewModel - Ekran & Firestore - AdviceViewModel - OpenAI - Tavsiye kartı


* **UI** → Jetpack Compose + Material 3  
* **Veri** → Firebase Firestore (“dustLogs”), Authentication  
* **AI** → Retrofit 2 + Moshi → `chat/completions`  

---

## Kurulum 🛠️  

### 1. Donanım (isteğe bağlı test)  
```bash
Esp32 için: dust_sensor_ble.ino
```
# Sensör 10 s aralıkla μg/m³ notify etmeye başlar

Proje Yapısı:
 ui/ # Compose ekranları
viewmodel/ # BleViewModel, LogsViewModel, AdviceViewModel
data/
 remote/ # OpenAiApi, OpenAiClient
 ble/ # BleDataRepository
 
firmware/ # ESP32 Arduino kodu

Ekran Görüntüleri:


![image](https://github.com/user-attachments/assets/b4fabc63-3bf1-40e3-a4c5-48fb4ef68278)
![image](https://github.com/user-attachments/assets/923788dd-c34b-4cf9-8e49-020ab6a2f161)
![image](https://github.com/user-attachments/assets/0596fdd5-5574-4881-9d07-55d8b9187632)



