

import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import furhatos.flow.kotlin.*
import furhatos.skills.UserManager.current
import furhatos.util.Gender
import furhatos.util.Language
import org.zeromq.SocketType
import org.zeromq.ZContext
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import javax.imageio.ImageIO
import jcifs.CIFSContext
import jcifs.config.PropertyConfiguration
import jcifs.context.BaseContext
import jcifs.smb.NtlmPasswordAuthenticator
import jcifs.smb.SmbFile
import furhatos.flow.kotlin.Furhat
import furhatos.records.User


//In der helper.kt sind alle benötigten Funktionen der Interaktion definiert.

fun furhatask (furhat:Furhat, englishText: String, turkishText: String, romanianText: String, germanText: String, sprache: Language) {
    if (sprache == Language.ENGLISH_US) {
        furhat.ask(text=englishText)
    }
    if (sprache == Language.TURKISH) {
        furhat.ask(text=turkishText)
    }
    if (sprache == Language.ROMANIAN) {
        furhat.ask(text=romanianText)
    }
    if (sprache == Language.GERMAN) {
        furhat.ask(text=germanText)
    }
}

fun furhatsay (furhat:Furhat, englishText: String, turkishText: String, romanianText: String, germanText: String, sprache: Language) {
    if (sprache == Language.ENGLISH_US) {
        furhat.say(text=englishText)
    }
    if (sprache == Language.TURKISH) {
        furhat.say(text=turkishText)
    }
    if (sprache == Language.ROMANIAN) {
        furhat.say(text=romanianText)
    }
    if (sprache == Language.GERMAN) {
        furhat.say(text=germanText)
    }
}

fun furhatsetVoice (furhat:Furhat, sprache: Language) {
    if (sprache == Language.ENGLISH_US) {
        furhat.setVoice(Language.ENGLISH_US, Gender.MALE, setInputLanguage = true)
    }
    if (sprache == Language.TURKISH) {
        furhat.setVoice(Language.TURKISH, Gender.MALE, setInputLanguage = true)
    }
    if (sprache == Language.ROMANIAN) {
        furhat.setVoice(Language.ROMANIAN, Gender.FEMALE, setInputLanguage = true)
    }
    if (sprache == Language.GERMAN) {
        furhat.setVoice(Language.GERMAN, Gender.MALE, setInputLanguage = true)
    }
}




fun GetDigitsKunde(Benutzer: User, furhat: Furhat) {
    val sprache = Benutzer.get("sprache") as Language

    val sprachNachricht = when (sprache) {
        Language.GERMAN -> "Sag mir bitte die Patientennummer Ziffer für Ziffer"
        Language.ENGLISH_US -> "Please tell me the patient number digit by digit"
        Language.TURKISH -> "Lütfen bana hasta numarasını tek tek söyleyin"
        Language.ROMANIAN-> "Vă rog să-mi spuneți numărul pacientului, cifră cu cifră"
        else -> "Wie lautet die Patientennummer Ihres Kunden"
    }

    furhat.askFor<furhatos.nlu.common.Number>(
        sprachNachricht,
        timeout = 20000,
        endSil = 5000
    ) {
        onResponse<furhatos.nlu.common.Number> {
            furhat.attend(user = current)
            val x: String = it.text.toString().replace(" ".toRegex(), "")
            val resultx: String = x.filter { it.isDigit() }
            Benutzer.put("Patientennummer", resultx)
            goto(ValidierungNummerKunde)
        }
    }
}



fun GetDigitsKundenochmal(Benutzer: User, furhat: Furhat) {
    val sprache = Benutzer.get("sprache") as Language

    val sprachNachricht = when (sprache) {
        Language.GERMAN -> "Könnten sie mir bitte nochmal die Patientenummer sagen am besten so 5  ${
            furhat.voice.pause("1000ms")},  6 ${furhat.voice.pause("1000ms")} und so weiter"

        Language.ENGLISH_US -> "Could you please tell me the patient number again best like this 5  ${
            furhat.voice.pause("1000ms")},  6 ${furhat.voice.pause("1000ms")} and so on"

        Language.TURKISH -> "Lütfen bana hasta numarasını tekrar en iyi şekilde tek tek söyleyebilimisiniz. Lütfen bu şekilde: 5  ${
            furhat.voice.pause("1000ms")},  6 ${furhat.voice.pause("1000ms")} bana dikte edin"

        Language.ROMANIAN-> "Ați putea să-mi spuneți din nou numărul pacientului, cel mai bine așa: 5 ${
            furhat.voice.pause("1000ms")}, 6 ${furhat.voice.pause("1000ms")} și așa mai departe"

        else -> "Könnten sie mir bitte nochmal die Patientenummer sagen am besten so 5 + " +
                "${furhat.voice.pause("1000ms")}, + 6 ${furhat.voice.pause("1000ms")} und so weiter"
    }

    furhat.askFor<furhatos.nlu.common.Number>(
        sprachNachricht,
        timeout = 20000,
        endSil = 5000
    ) {
        onResponse<furhatos.nlu.common.Number> {
            furhat.attend(user = current)
            val x: String = it.text.toString().replace(" ".toRegex(), "")
            val resultx: String = x.filter { it.isDigit() }
            Benutzer.put("Patientennummer", resultx)
            goto(ValidierungNummerKunde)
        }
    }
}

fun containsQRCode(image: BufferedImage): Boolean {
    val luminanceSource = RGBLuminanceSource(image.width, image.height, getPixels2(image))
    val binaryBitmap = BinaryBitmap(HybridBinarizer(luminanceSource))

    val reader = MultiFormatReader()

    return try {
        // Versuche, einen QR-Code im Bild zu erkennen
        reader.decode(binaryBitmap)
        // Wenn kein Fehler aufgetreten ist, gebe true zurück
        true
    } catch (e: Exception) {
        // Wenn ein Fehler aufgetreten ist (kein QR-Code gefunden), gebe false zurück
        false
    }
}

fun getPixels2(image: BufferedImage): IntArray {
    val width = image.width
    val height = image.height
    val pixels = IntArray(width * height)
    image.getRGB(0, 0, width, height, pixels, 0, width)
    return pixels
}

var qrcodecounter = 0


fun captureImageFromSocket(benutzer: User) {

    qrcodecounter++


    val context = ZContext()
    val subscriber = context.createSocket(SocketType.SUB)

    // Verbinde den Socket mit dem ZMQ.SUB-Socket
    subscriber.connect("tcp://10.198.3.150:3000")

    // Setze den Filter auf leeren String, um alle Nachrichten zu empfangen
    subscriber.subscribe("".toByteArray())

    // val imageInput: BufferedImage?

    // Empfange eine Nachricht
    val message = subscriber.recv(0)

    // Schließe den Socket und den Kontext
    subscriber.close()
    context.close()

    // Überprüfe, ob eine Nachricht empfangen wurde
    if (message != null) {
        // Verarbeite das empfangene Bild
        val imageInput = ImageIO.read(ByteArrayInputStream(message))

        if (!containsQRCode(imageInput)) {

            // Wenn kein Bild übergeben wurde oder das Bild keinen QR-Code enthält,
            // rufe die Funktion rekursiv neu auf
            Thread.sleep(1000) // Füge eine Verzögerung von einer halben Sekunde ein
            if(qrcodecounter == 10){
                return
            }
            else {
                captureImageFromSocket(benutzer)
            }
        } else {

            val luminanceSource = RGBLuminanceSource(imageInput.width, imageInput.height, getPixels2(imageInput))
            val binaryBitmap = BinaryBitmap(HybridBinarizer(luminanceSource))

            val reader = MultiFormatReader()
            val result = reader.decode(binaryBitmap)

            println("QR Code Text: ${result.text}")
            val barcodeText: String = result.text
            benutzer.put("QR Code Text", barcodeText) // Speichert den erkannten QR-Code-Text im Benutzerobjekt

        }
    } else {
        // Keine Nachricht empfangen - handle den Fall entsprechend
        // Zum Beispiel eine Fehlermeldung ausgeben oder weitere Maßnahmen ergreifen
    }
}
