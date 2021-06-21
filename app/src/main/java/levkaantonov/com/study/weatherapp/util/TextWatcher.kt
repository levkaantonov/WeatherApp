package levkaantonov.com.study.weatherapp.util

import androidx.appcompat.widget.SearchView

class TextWatcher(
    val onTextSubmit: (query: String) -> Boolean,
    val onQueryChanged: (query: String) -> Boolean,
) : SearchView.OnQueryTextListener {
    override fun onQueryTextSubmit(query: String?) = onTextSubmit(query.toString())

    override fun onQueryTextChange(newText: String?) = onQueryChanged(newText.toString())
}