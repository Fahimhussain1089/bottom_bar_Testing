package com.physicswallah.testingproject.Screen

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.physicswallah.testingproject.models.QuoteList
import com.physicswallah.testingproject.models.Quots


@Composable
fun QuoteListScreen( data: Array<Quots>,onclick: (quote:Quots) -> Unit) {//data: Array<Quots>,

    Column {
      //  println("data: $data")
        LaunchedEffect(Unit) {
            println("All Quotes:")
            data.forEach { quote ->
                println("Quote: ${quote.text} - Author: ${quote.author}")
            }
        }


        Text(
            text = "Quotes App",
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(8.dp, 24.dp)
                .fillMaxWidth(),
            style = MaterialTheme.typography.headlineSmall,
        )

        // ✅ Properly pass or call the lambda
        QuoteList(
            data = data,
            onclick)
    }
}
