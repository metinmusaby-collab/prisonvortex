# PrisonMaden

Kisisel Prison maden dunyasi Java eklentisi. Minecraft **1.21.11** icin, GUI panel,
NPC, minyon sistemi, canta/ekonomi, rankup ve kazma yukseltme dahil.

## Ozellikler

### Temel
- `/maden` - madenin yoksa olusturur (Multiverse ile), varsa isinlatir ve **Maden Paneli**'ni acar
- `/maden sil` - **sadece yetkililer** (`prisonmaden.admin`) kullanabilir; `/maden sil <oyuncu>` ile baskasininkini de silebilir
- `/maden yenile` - tukenen cevherleri yeniden dagitir (duvarlar/taban etkilenmez)
- `/madenziyaret <oyuncu>` - baska oyuncunun madenini ziyaret eder (ziyaret kapaliysa engellenir)
- `/kit` - baslangic kazmasini istedigin zaman yeniden alir
- Sunucuya ilk giriste otomatik **hazir kit** (ozel kazma + ekmek)
- Maden dunyasinda **PvP kapali**, **dogal mob spawn kapali**
- Cevre duvarlari artik kaba bedrock degil, **gorunmez barrier** (sadece taban islevsellik icin bedrock)

### Ekonomi & Canta
- Kirilan satilabilir cevherler (komur/demir/altin/redstone/lapis/elmas) otomatik olarak
  **cantana** gider (fiziksel envantere degil)
- `/canta` (veya `/çanta`) - cantani gorur, **Hepsini Sat** butonuyla tek tikla satarsin
- Kazanilan para ile `/rankup` yaparak rank atlarsin (7 kademeli rank sistemi)

### Minyon Sistemi
- Maden panelinden **2000 para** karsiliginda Minyon Tasi satin alinir, yere tiklanarak yerlestirilir
- Minyon her 10 saniyede bir cevresindeki cevherleri otomatik toplar (seviyeyle yaricap/hiz artar)
- Minyonun topladiklari **ayri bir minyon cantasina** gider - minyona sag tiklayip
  kendi menusunden **Hepsini Sat** ile satilir
- Panelden minyonu ac/kapat (sol tik) veya yukselt (sag tik, para karsiliginda, max seviye 10)

### NPC & Panel
- Her maden dunyasinin spawn noktasina otomatik bir **"Maden Paneli" NPC'si** yerlesir
- NPC'ye tiklayinca acilan panelden: isinlanma, canta, minyon yonetimi, **ucus ac/kapat**,
  rankup, kit alma, maden yenileme, ziyaret ayari ve (yetkili ise) maden silme yapilabilir
- Ucus sadece kendi madeninde calisir; dunya degistirince otomatik kapanir (guvenlik)

### Kazma Yukseltme
- Ozel kazmaya **sag tiklayinca** Servet / Verimlilik / Kirilmazlik yukseltme menusu acilir
- Demir cevheri kirinca **%5 ihtimalle Yukseltme Sisesi** duser
- 1 Yukseltme Sisesi + 3 Zumrut karsiliginda seviye artar, maksimum seviye 10

## Kurulum (derleme)

### Yontem A: Taraycidan, hicbir sey kurmadan (GitHub Actions)

1. github.com'da yeni bir repo ac, bu klasordeki TUM dosyalari (pom.xml, src, .github, README.md) yukle.
2. Commit attiktan sonra **Actions** sekmesine gir, calisan isin bitmesini bekle.
3. Altta **Artifacts** bolumunden **PrisonMaden-jar** dosyasini indir, icinden `PrisonMaden.jar` cikacak.

### Yontem B: Bilgisayarinda (Java 25 + Maven)

```
mvn clean package
```

Olusan `target/PrisonMaden.jar` dosyasini sunucunun `plugins/` klasorune at.

## Gereksinimler

- **Java 25** (derleme icin GitHub Actions'ta veya bilgisayarinda kurulu olmali)
- Paper/Spigot/Purpur **1.21.4 veya uzeri** (1.21.11 hedeflenmistir)
- **Multiverse-Core** plugini kurulu ve calisir olmali (dunya olusturma/silme/tasima icin)
- Ana dunyanin adi `world` olmali (silme sirasinda oyuncu oraya cikariliyor;
  farkliysa `MineWorldManager.java` icindeki `"world"` degerini degistir)

## Ayarlanabilir noktalar

- **Maden boyutu / cevher oranlari**: `MineWorldManager.java` -> `insaMaden()` ve `rastgeleMadenBlogu()`
- **Kit icerigi**: `KitManager.java` -> `kitVer()`
- **Yukseltme sartlari (sise + zumrut sayisi, max seviye)**: `PickaxeManager.java` basindaki sabitler
- **Sise dusme sansi (%5)**: `PickaxeManager.java` -> `SISE_DUSME_SANSI`
- **GUI slotlari/isimleri**: `GUIManager.java`

## Notlar

- **Onemli:** Eklenti Java 25 hedefiyle derlendigi icin, sunucunun kendisi de
  (Minecraft sunucu yazilimini calistiran JVM) Java 25 veya uzeriyle calismali.
  Sunucun daha eski bir Java ile calisiyorsa eklenti "UnsupportedClassVersionError"
  hatasi verip yuklenmez; boyle bir durumda `pom.xml` icindeki
  `maven.compiler.source`/`target` degerlerini sunucunun Java surumune indir.
- Dunya adlari `maden_<oyuncuadi>` seklinde olusturulur; PvP/mob kontrolleri bu on-ek
  ile baslayan tum dunyalarda otomatik calisir (Multiverse ayari + yedek Skript-benzeri
  Java kontrolu birlikte).
- Ozel kazma ve sise, isim/lore yerine **PersistentDataContainer (NBT etiket)** ile
  tanindigi icin oyuncu esyanin adini degistirse bile sistem calismaya devam eder.
