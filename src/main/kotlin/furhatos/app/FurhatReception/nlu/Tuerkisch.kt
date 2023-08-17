import furhatos.nlu.Intent
//Erklärung: Vergleiche Klasse "nlu.Ja"

class Tuerkisch : Intent() {
    //var name: String = ""
    //var vorname: String = ""
    override fun getExamples(lang: furhatos.util.Language): List<String> {
        return listOf(
            "turkish",
            "türkisch",
            "Türkei",
            "Auch Türkisch", "Türkçe","Türkçe konuş benimle","Benimle Türkçe konuş","Türkçe konuş Benim de", "Benimle Türkçe konuşur musun"
        )
    }
    /*fun getFullName(): String =
        "$vorname $name"
*/
}