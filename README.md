# SkriptVeri

Skript üzerinden isimlendirilmiş veri tanımlamanı ve bu veriyi PlaceholderAPI ile
`%skriptveri_<isim>%` şeklinde göstermeni sağlayan köprü eklenti.

## Gereksinimler

- Sunucu: Paper/Spigot **1.21.11**
- Java: **25**
- [Skript](https://github.com/SkriptLang/Skript) eklentisi kurulu olmalı
- (Opsiyonel) [PlaceholderAPI](https://github.com/PlaceholderAPI/PlaceholderAPI)

## GitHub Actions ile Derleme

Depoya push yaptığında `.github/workflows/build.yml` otomatik olarak
`mvn clean package` çalıştırır ve üretilen jar'ı Actions sekmesindeki
**Artifacts** bölümüne yükler. Elle tetiklemek istersen Actions
sekmesinden **Run workflow** butonunu kullanabilirsin.

Yerelde derlemek istersen:

```bash
mvn clean package
```

Çıktı: `target/skriptveri-1.0.0.jar`

## Skript Kullanımı

Veri atama (oyuncuya özel):

```
set skript veri "puan" to 10 for player
```

Veri atama (global, sunucu genelinde):

```
set skript veri "sunucu_durumu" to "acik"
```

Veri okuma:

```
command /puanim:
    trigger:
        send "Puanin: %skript veri "puan" of player%"
```

## PlaceholderAPI Kullanımı

Oyuncuya özel veri:

```
%skriptveri_puan%
```

Global veri:

```
%skriptveri_global_sunucu_durumu%
```

Bu placeholder'ları başka eklentilerde (scoreboard, tab list, chat formatı vb.)
normal PlaceholderAPI placeholder'ı gibi kullanabilirsin.

## Notlar / Genişletme Fikirleri

- Şu an veriler yalnızca bellekte tutuluyor; sunucu yeniden başlatıldığında
  sıfırlanır. Kalıcı hale getirmek istersen `DataManager` sınıfına
  YAML veya SQLite tabanlı bir kayıt/yükleme katmanı ekleyebilirsin.
- `pom.xml` içindeki Skript sürümünü (`2.9.3`) kendi kullandığın sürüme göre
  güncelle; JitPack üzerinden farklı bir commit/tag da hedefleyebilirsin.
- Paper API sürümünü (`1.21.11-R0.1-SNAPSHOT`) PaperMC deposunda henüz
  yayınlanmamışsa, en yakın uyumlu sürümle derleyip `api-version` alanını
  buna göre ayarlaman gerekebilir.
