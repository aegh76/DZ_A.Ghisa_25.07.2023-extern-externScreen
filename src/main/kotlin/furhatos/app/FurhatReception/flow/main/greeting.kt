
import furhatos.app.blankskill1.flow.Parent
import furhatos.app.blankskill1.flow.main.Idle
import furhatos.flow.kotlin.*
import furhatos.flow.kotlin.voice.Voice
import furhatos.gestures.Gestures
import furhatos.skills.UserManager.current
import furhatos.util.Language
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.LocalDateTime


val Greeting : State = state(Parent) {
    onEntry {



        furhatask(
            furhat=this.furhat,
            englishText = "Hello do you have a ${furhat.voice.emphasis("QR code")} " +
        "Or do you have a patient number?",
            germanText =  "Hallo Haben Sie einen ${furhat.voice.emphasis("QR-Code")} " +
                    "oder haben Sie eine Patientennummer?",
            turkishText = "Merhaba Bir ${furhat.voice.emphasis("kuh er kod")} " +
            "ya da bir hasta numaranız var mı?" ,
            romanianText = "Bună ziua Aveți un ${furhat.voice.emphasis("QR code")} " +
            "sau aveți un număr de pacient?",
            sprache = Benutzer!!.get("sprache") as Language

        )
    }

    onResponse<Patientennummer> {
        furhat.attend(user= current)

        furhatsay(furhat=this.furhat,
            englishText = "Good, then I can help ${furhat.voice.emphasis("you")} further.",
            germanText =  "Gut, dann kann ich ${furhat.voice.emphasis("Ihnen")} weiterhelfen.",
            turkishText = "Güzel, o zaman size daha fazla ${furhat.voice.emphasis("yardımcı")} olabilirim.",
            romanianText = "Bine, atunci pot să vă ajut  în continuare.",
            sprache = Benutzer!!.get("sprache") as Language)


            //mit ${furhat.voice.emphasis("Ihnen")} kann Furhat einzelne Abschnitte betonen.

            furhat.gesture(Gestures.BigSmile, async = false)


        //Nun wird die Variable Benutzer mit dem User überschrieben, der auf dei Frage geantwortet hat.
        Benutzer = users.getUser(it.userId)

        //Der Benutzer wird von Furhat angeschaut.
        furhat.attend(user= current)

        //Mit der Funktion GetDigitsPatient wird die Frage nach der Patientennummer des Gesprächspartners gestellt.
        //User kann nicht mehr null sein deswegen Benutzer!!, da der Benutzer bereits gesetzt.
        GetDigitsKunde(Benutzer!!, this.furhat)
        goto(ValidierungNummerKunde)

    }

    onResponse<FrageWiederholen> {
        furhat.attend(user= current)
        reentry()
    }

    onResponse<QRCode>{
        Benutzer = users.getUser(it.userId)

        furhatsay(furhat=this.furhat,
            englishText = "Okay, then please show me your QR code.",
            germanText =  "Okay, dann zeig mir bitte dein QR-Code",
            turkishText = "Tamam, o zaman lütfen bana QR kodunuzu gösterin",
            romanianText = "Bine, atunci vă rog să-mi arătați codul QR",
            sprache = Benutzer!!.get("sprache") as Language)


        captureImageFromSocket(benutzer = Benutzer!!)
        Benutzer!!.put("Patientennummer", Benutzer!!.get("QR Code Text"))

        if(Benutzer!!.get("Patientennummer")==null)
        {
            reentry()
        }
        furhat.say("${Benutzer!!.get("Patientennummer")}")
        ReadExcel(Benutzer!!, this.furhat,
            networkDrivePath = "smb://na-filer-w.thm.intern/daten/Projekte/Robotik/Furhat/projekte/Belegungsplan_Dialysezentrum/Furhat.csv",
            password = "FB07Tutor2022@THM", username = "07-Tutor")

        /*  Eingaben fürs DZ
            ReadExcel(Benutzer!!, this.furhat,
            networkDrivePath = "smb://10.203.31.70/mv/furhat/Furhat.csv",
            password = "Furhat2023", username = "Furhat")*/

        val raumy: Any? = Benutzer!!.get("raum")
        val platzx: Any? = Benutzer!!.get("platz")
        val dialysebeginn = furhat.voice.sayAs(Benutzer!!.get("dialysebeginn").toString(), Voice.SayAsType.TIME)
        val dialyseende: Any = furhat.voice.sayAs(Benutzer!!.get("dialyseende").toString(), Voice.SayAsType.TIME)
      //  val datum = furhat.voice.sayAs(Benutzer!!.get("datum").toString(), Voice.SayAsType.TIME)


        //Der Nutzer wird über seine Termindaten informiert und weiß somit, wann, wo und wie lange seine Dialyse
        //stattfinden wird.


        furhatsay(furhat=this.furhat,
            englishText = "Alright, ${Benutzer!!.get("name")}. I would change ${furhat.voice.emphasis("please")} go to " +
            " the room${furhat.voice.emphasis("$raumy")}  ${furhat.voice.pause("1000ms") } to bed  ${furhat.voice.emphasis("$platzx")} " +
            "to go. Your dialysis will start at $dialysebeginn start and end at $dialyseende ",
            germanText =  "Gut, ${Benutzer!!.get("name")}. Ich würde Sie ${furhat.voice.emphasis("bittten")} in " +
                    "den Raum${furhat.voice.emphasis("$raumy")}  ${furhat.voice.pause("1000ms") } an den  Platz ${furhat.voice.emphasis("$platzx")} " +
                    "zu gehen. Ihre Dialyse fängt um $dialysebeginn an und endet um $dialyseende ",
            turkishText = "Tamamdır, ${Benutzer!!.get("isim")}. Lütfen oda numara " +
            "${furhat.voice.emphasis("$raumy")}  ${furhat.voice.pause("1000ms") } 'ya gidin. Yer numaraniz  ${furhat.voice.emphasis("$platzx")} " +
            ". Diyaliziniz saat $dialysebeginn 'da başlayacak ve $dialyseende bitecektir",
            romanianText = "Bine ${Benutzer!!.get("name")}., v-aș ruga să mergeți în camera. ${furhat.voice.emphasis("$raumy")}  " +
                    "${furhat.voice.pause("1000ms") },la locul ${furhat.voice.emphasis("$platzx")}. Dializa dumneavoastră începe la $dialysebeginn  și se termină la $dialyseende ",
            sprache = Benutzer!!.get("sprache") as Language)


        delay(1500)


       /* furhat.say (
            "Gut, ${Benutzer!!.get("name")}. Ich würde Sie ${furhat.voice.emphasis("bittten")} in " +
                    "den${furhat.voice.emphasis("$raumy")} + ${furhat.voice.pause("1000ms") }+ an den  PLatz ${furhat.voice.emphasis("$platzx")} " +
                    "zu gehen. Ihre Dialyse fängt um $dialysebeginn an und endet um $dialyseende am $datum")
*/

        val currentDateTime = LocalDateTime.now(ZoneId.of("Europe/Berlin"))
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
        val formattedDateTime = currentDateTime.format(formatter)

       /* furhatsay(furhat=this.furhat,
            englishText = "Current time and date: $formattedDateTime",
            germanText =  "Aktuelle Uhrzeit und Datum: $formattedDateTime",
            turkishText = "Geçerli saat ve tarih: $formattedDateTime",
            romanianText = "Data și ora curentă: $formattedDateTime",
            sprache = Benutzer!!.get("sprache") as Language)


        delay(1500) */

        furhatsay(furhat=this.furhat,
            englishText = "I hope I could help you have a ${furhat.voice.emphasis("beautiful")} day",
            germanText =  "Ich hoffe ich konnte Ihnen helfen, einen ${furhat.voice.emphasis("schönen")} Tag noch",
            turkishText = "umarım yardımcı olabilmişimdir. Iyi günler dilerim!",
            romanianText = "Sper că v-aș putea ajuta să aveți inca o zi ${furhat.voice.emphasis("frumoasă")} ",
            sprache = Benutzer!!.get("sprache") as Language)

        furhat.gesture(Gestures.Nod())
        furhat.gesture(Gestures.BigSmile)

        //Für 8 Sekunden hört Furhat dann seinem Gesprächspartner zu, falls noch Fragen bezüglich der Platzinformation
        //offen sind, kann furhat die Informationen nochmal wiederholen. Der State wird dann nicht nochmal von vorne
        //begonnen, sondern startet bei onReentry (Zeile 65).
        furhat.listen(timeout = 8000)

        // furhat.say("Aktuelle Uhrzeit und Datum: $formattedDateTime")


        onResponse<Nein>  {
            furhat.attend(user= current)
            goto(AngehoerigeUndTaxifahrer)
        }

        delay(3500)
        goto(Idle)
    }

    onResponse<Nein>  {
        furhat.attend(user= current)
        goto(AngehoerigeUndTaxifahrer)
    }

    onResponse<FrageWiederholen> {
        furhat.attend(user= current)
        reentry()
    }

    onResponse<WelcherPlatz> {
        furhat.attend(user= current)
        reentry()
    }

    onResponseFailed {
        reentry()
    }

}


