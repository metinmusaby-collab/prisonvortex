# OptimTowny

Paper **1.21.11** için Türkçe, Towny tarzı kasaba/ulus eklentisi.
Kasaba kurma, üyelik ve rütbe sistemi, ulus (nation) sistemi, vergi/banka sistemi,
zamanlı savaş modu, savaş bossu ve görev (quest) sistemini içerir. Sunucu başına
kurulabilecek kasaba sayısı `config.yml` üzerinden sınırlandırılabilir (varsayılan: **10**).

## GitHub üzerinden otomatik derleme

Bu repo, `.github/workflows/build.yml` içinde bir **GitHub Actions** iş akışı içerir.
`main` dalına her push yaptığınızda:

1. JDK 25 kurulur
2. `mvn clean package` çalıştırılır
3. Derlenen `OptimTowny.jar` dosyası Actions sekmesindeki **Artifacts** bölümüne yüklenir

Adımlar:
1. Bu klasörü kendi GitHub reponuza push edin.
2. GitHub'da **Actions** sekmesine gidin, iş akışının tamamlanmasını bekleyin.
3. Tamamlanan çalıştırmanın altındaki **Artifacts → OptimTowny-jar** dosyasını indirin.
4. `OptimTowny.jar` dosyasını sunucunuzun `plugins/` klasörüne koyup sunucuyu başlatın/yeniden başlatın.

> **Not:** `pom.xml` içindeki `paper-api` sürümü, deponuzdaki güncel Paper 1.21.x
> sürümüne göre ayarlanmalıdır (1.21.11 Maven deposuna henüz düşmediyse en yakın
> `1.21.x-R0.1-SNAPSHOT` sürümünü kullanın — eklenti `api-version: '1.21'` sayesinde
> tüm 1.21 alt sürümleriyle uyumlu çalışır).

## Yerel derleme (isteğe bağlı)

```bash
mvn clean package
```

Çıktı: `target/OptimTowny.jar`

## Komutlar

| Komut | Açıklama |
|---|---|
| `/kasaba kur <isim>` | Yeni kasaba kurar |
| `/kasaba katil <isim>` | Kasabaya katılır |
| `/kasaba ayril` | Kasabadan ayrılır |
| `/kasaba bilgi [isim]` | Kasaba bilgisi gösterir |
| `/kasaba liste` | Tüm kasabaları listeler |
| `/kasaba para <yatir|cek> <miktar>` | Kasaba kasasına para yatırır/çeker |
| `/kasaba rutbe <oyuncu>` | Üyeyi bir üst rütbeye yükseltir (yeterli görev tamamlamışsa) |
| `/kasaba claim` | Bulunduğun chunk'ı kasabaya katar |
| `/ulus kur <isim>` | Kasaban üzerinden ulus kurar |
| `/ulus ekle <kasaba>` | Ulusuna kasaba ekler |
| `/ulus cikar <kasaba>` | Ulustan kasaba çıkarır |
| `/ulus bilgi [isim]` | Ulus bilgisi gösterir |
| `/ulus liste` | Tüm ulusları listeler |
| `/harita` | Bulunduğun bölgenin kasaba haritasını sohbete basar |
| `/savas bilgi` | Savaş modu durumunu ve zamanlarını gösterir |
| `/savas baslat|bitir|sifirla` | (Admin) Savaş modunu elle kontrol eder |
| `/gorev liste` | Mevcut görevleri listeler |
| `/gorev al <id>` | Görevi kabul eder |
| `/gorev ilerleme` | Aktif görevlerdeki ilerlemeyi gösterir |
| `/vergi bilgi` | Vergi ayarlarını gösterir |
| `/vergi topla` | (Admin) Vergiyi manuel tetikler |

## Öne çıkan sistemler

- **Kasaba limiti:** `config.yml -> genel.maksimum-kasaba-sayisi` (varsayılan 10)
- **Vergi sistemi:** Belirli aralıklarla otomatik toplanır, ödenmezse kasaba "iflas" mekaniğine girer
- **Rütbe sistemi:** `config.yml -> rutbeler.siralama` içinde tanımlı, görev tamamlayarak yükselinir
- **Savaş modu:** `config.yml -> savas.savas-saatleri` içinde tanımlı saat aralıklarında otomatik açılır/kapanır;
  `/savas` komutuyla admin elle de kontrol edebilir
- **Boss sistemi:** Savaş modu başladığında kasabalarda belirli olasılıkla güçlü bir muhafız spawn olur,
  öldürülünce kasaba kasasına ödül yatırır
- **Harita:** `/harita`, oyuncunun bulunduğu bölgeyi merkez alan renkli ASCII harita basar
- **Chunk koruma:** Kasabaya ait bölgelerde savaş modu dışında yabancılar blok kıramaz/koyamaz
- **Ekonomi:** Eklenti kendi içinde basit bir oyuncu bakiye sistemi barındırır (Vault'a bağımlı değildir)

## Özelleştirme

Tüm fiyatlar, süreler, saatler, boss istatistikleri ve harita renkleri
`plugins/OptimTowny/config.yml` dosyasından değiştirilebilir.
