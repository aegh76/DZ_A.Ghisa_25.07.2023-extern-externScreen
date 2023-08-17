import furhatos.nlu.Intent
//Erklärung: Vergleiche Klasse "nlu.Ja"

class Rumaenisch : Intent() {
    //var name: String = ""
    //var vorname: String = ""
    override fun getExamples(lang: furhatos.util.Language): List<String> {
        return listOf(
            "Rumänisch", "rumänisch", "Rumänien",
           "Română" , "în limba română vă rog", "vorbesc în limba română", "românesc, sunt românesc", "sunt român", "rumänisch", "Rumänisch",
            "Rumänien", "Ich möchte rumänisch sprechen"
        )
    }

}