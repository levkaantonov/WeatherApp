package levkaantonov.com.study.weatherapp.util

import android.annotation.SuppressLint
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@SuppressLint("NewApi")
fun String.toDate(pattern: String = DATE_PATTERN) =
    LocalDate.parse(this).format(DateTimeFormatter.ofPattern(pattern, Locale.getDefault()))