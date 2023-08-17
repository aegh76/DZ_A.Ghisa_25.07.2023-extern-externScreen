

import furhatos.nlu.Intent

class QRCode : Intent() {
    override fun getExamples(lang: furhatos.util.Language): List<String> {
        return listOf(
            "QR-Code", "Ich habe einen QR-Code", "QRCode", "Kuh Arr", "QR", "Qa",
            "QR Code", "I have a QR Code", "QRCode", "Cow Arr","que Ar","quare kod var", "Bar", "var", "korkum var", "var herkod var","korkum var","var quare kod var","Evet",
            "QR kodu", "QR kodum var","Am un cod QR","Cod QR","q r code"
        )
    }
}