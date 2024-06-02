# Işık Yoğunluğu Ölçme Uygulaması

Bu uygulama, Android cihazınızın ışık sensörünü kullanarak çevrenizdeki ışık yoğunluğunu ölçer.

## Geliştirme Platformu

Bu uygulama, Android Studio kullanılarak geliştirilmiştir. Kotlin dili kullanılmıştır.

## Kurulum

1. Bu depoyu yerel makinenize klonlayın. `git clone https://github.com/enesvarol189/Light-Intensity-Meter`
2. Android Studio'da, `File -> Open...` seçeneğini kullanarak klonlanan projeyi açın.

## Çalıştırma

### Mobil Cihazda Çalıştırma

1. Android Studio'da, `Build -> Build Bundle(s) / APK(s) -> Build APK(s)` seçeneğini kullanarak APK dosyasını oluşturun.
2. Oluşturulan APK dosyasını Android cihazınıza taşıyın ve yükleyin.
3. Uygulamayı Android cihazınızda başlatın.

### Emülatörde Çalıştırma

1. Android Studio'da, `Tools -> AVD Manager` seçeneğini kullanarak bir Android Virtual Device oluşturun veya seçin.
2. AVD'yi başlatın.
3. Android Studio'da, `Run -> Run 'app'` seçeneğini kullanarak uygulamayı başlatın. Uygulama, seçtiğiniz AVD'de başlatılacaktır.
4. Emülatörün sağ tarafındaki menüden, `...` düğmesine tıklayın ve `Virtual Sensors` sekmesini seçin.
5. `Light` bölümünde, ışık yoğunluğunu değiştirmek için kaydırıcıyı kullanın.
