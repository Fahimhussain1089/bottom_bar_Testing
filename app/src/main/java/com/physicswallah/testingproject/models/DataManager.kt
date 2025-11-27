package com.physicswallah.testingproject.models

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import com.google.gson.Gson
import com.physicswallah.testingproject.Pages
import java.nio.charset.Charset

import kotlin.text.Charsets


object DataManager { var data = emptyArray<Quots>()
    var currentQuote: Quots? =null;
    var currentPage = mutableStateOf(Pages.LISTING)
    var isDataLoaded = mutableStateOf(false)  // ✅ Now Compose can observe this

    fun loadAssestFromFile(context: Context) {
        val inputStream = context.assets.open("quotes.json")
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()

        val json = String(buffer, Charsets.UTF_8) // ✅ Correct charset usage
        val gson = Gson()

        data = gson.fromJson(json, Array<Quots>::class.java) // ✅ No ambiguity if Quots is valid
        isDataLoaded.value = true // ✅ Triggers recomposition
    }

    fun switchPages(quots: Quots?){
        if (currentPage.value == Pages.LISTING){
            currentQuote = quots
            currentPage.value = Pages.DETAIL
        }else{
            currentPage.value  =Pages.LISTING
        }
    }
}


