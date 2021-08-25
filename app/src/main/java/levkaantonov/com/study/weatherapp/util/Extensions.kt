package levkaantonov.com.study.weatherapp.util

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import levkaantonov.com.study.weatherapp.models.common.Resource
import levkaantonov.com.study.weatherapp.models.ui.Location
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

typealias MutableResourceListLiveData<T> = MutableLiveData<Resource<List<T>>>
typealias ResourceListLiveData<T> = LiveData<Resource<List<T>>>

@SuppressLint("NewApi")
fun String.toDate(pattern: String = DATE_PATTERN): String =
    LocalDate.parse(this).format(DateTimeFormatter.ofPattern(pattern, Locale.getDefault()))

fun <T> MutableLiveData<List<T>>.addValue(item: T) {
    val value = this.value?.toMutableList() ?: return
    this.value = value + listOf(item)
}

fun <T> MutableLiveData<List<T>>.addPostValue(item: T) {
    val value = this.value?.toMutableList() ?: return
    this.postValue(value + listOf(item))
}

fun <T> MutableLiveData<List<T>>.updateValue(item: T, updatedItem: T) {
    val value = this.value?.toMutableList() ?: return
    val index = value.indexOf(item)
    if (index == -1)
        return
    value[index] = updatedItem
    this.value = value
}

fun <T> MutableLiveData<List<T>>.updatePostValue(item: T, updatedItem: T) {
    val value = this.value?.toMutableList() ?: return
    val index = value.indexOf(item)
    if (index == -1)
        return
    value[index] = updatedItem
    this.postValue(value)
}

fun <T> MutableLiveData<List<T>>.removeValue(item: T) {
    val value = this.value?.toMutableList() ?: return
    val index = value.indexOf(item)
    if (index == -1)
        return
    value.removeAt(index)
    this.value = value
}

fun <T> MutableLiveData<List<T>>.removePostValue(item: T) {
    val value = this.value?.toMutableList() ?: return
    val index = value.indexOf(item)
    if (index == -1)
        return
    value.removeAt(index)
    this.postValue(value)
}

fun <T> MutableResourceListLiveData<T>.updatePostItemInList(item: T, updatedItem: T) {
    val value = this.value?.data?.toMutableList() ?: return
    val index = value.indexOf(item)
    if (index == -1)
        return
    value[index] = updatedItem
    this.postValue(Resource.Success(value))
}

fun <T> MutableResourceListLiveData<T>.updateItemInList(item: T, updatedItem: T) {
    val value = this.value?.data?.toMutableList() ?: return
    val index = value.indexOf(item)
    if (index == -1)
        return
    value[index] = updatedItem
    this.value = Resource.Success(value)
}


