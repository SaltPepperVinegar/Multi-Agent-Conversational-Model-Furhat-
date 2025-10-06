package furhatos.app.openaichat.utils
import furhatos.flow.kotlin.furhat
import furhatos.gestures.Gesture
import furhatos.gestures.Gestures

data class GestureTag(
    val name: String,
    val offsetMs: Long = 0L,
    val durationMs: Long = 800L
)
data class Segment(
    val gesture: GestureTag,
    val sentence: String
)

private val TAG = Regex("""\[(?:G|gesture):([A-Za-z][\w-]*)\s*([^\]]*)]""")



fun toGesture(name: String): Gesture = when (name.trim().lowercase()) {
    "smile"                   -> Gestures.Smile
    "bigsmile", "grin", "beam"-> Gestures.BigSmile
    "nod", "yes"              -> Gestures.Nod
    "shake", "no"             -> Gestures.Shake
    "browraise", "raise"      -> Gestures.BrowRaise
    "browfrown", "frown"      -> Gestures.BrowFrown
    "blink"                   -> Gestures.Blink
    "closeeyes", "close"      -> Gestures.CloseEyes
    "openeyes", "wideeyes"    -> Gestures.OpenEyes
    "oh", "o"                 -> Gestures.Oh
    "roll", "eyeroll"         -> Gestures.Roll
    "surprise", "surprised"   -> Gestures.Surprise
    "thoughtful", "think"     -> Gestures.Thoughtful
    "wink"                    -> Gestures.Wink
    "anger", "angry", "expressanger"   -> Gestures.ExpressAnger
    "disgust", "expressdisgust"        -> Gestures.ExpressDisgust
    "fear", "scared", "expressfear"    -> Gestures.ExpressFear
    "sad", "sadness", "expresssad"     -> Gestures.ExpressSad
    "neutral", "gazeaway", "idle"      -> Gestures.GazeAway

    else        -> Gestures.GazeAway
}


fun splitByGestureTags(input: String): List<Segment> {
    val matches = TAG.findAll(input).toList()
    if (matches.isEmpty()) {
        val s = input.trim()
        return if (s.isEmpty()) emptyList() else
            listOf(Segment(GestureTag("Neutral"), s))
    }

    val out = mutableListOf<Segment>()
    for (i in matches.indices) {
        val m = matches[i]
        val name = m.groupValues[1]
        val args = m.groupValues.getOrNull(2).orEmpty()

        val at = extractLong(args, "at") ?: extractLong(args, "offset") ?: 0L
        val dur = extractLong(args, "dur") ?: extractLong(args, "duration") ?: 800L

        val nextStart = if (i + 1 < matches.size) matches[i + 1].range.first else input.length
        val chunk = input.substring(m.range.last + 1, nextStart).trim()

        if (chunk.isNotEmpty()) {
            out += Segment(
                gesture = GestureTag(
                    name = name,
                    offsetMs = at.coerceAtLeast(0),
                    durationMs = dur.coerceAtLeast(100)
                ),
                sentence = chunk
            )
        }
    }
    return out
}

private fun extractLong(s: String, key: String): Long? {
    val r = Regex("""\b${Regex.escape(key)}\s*=\s*(\d+)\b""")
    return r.find(s)?.groupValues?.getOrNull(1)?.toLong()
}

