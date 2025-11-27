package com.physicswallah.testingproject

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import androidx.loader.content.Loader
import com.physicswallah.testingproject.Screen.MenuBottomBar
import com.physicswallah.testingproject.ui.theme.TestingProjectTheme
import kotlinx.coroutines.delay

class TestingActivitytwo : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TestingProjectTheme {
//                Loader()
                MenuBottomBar()



            }
        }
    }
}

@Composable
fun Loader(){
    val degree = produceState(initialValue = 10) {
        while (true){
            delay(15)
            value = (value+10)%360
        }
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxWidth(1f),
        content = {
            Column (horizontalAlignment = Alignment.CenterHorizontally){
                Image(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "",
                    modifier = Modifier.size(60.dp).rotate(degree.value.toFloat())
                )
                Text(
                    text = "Loading"
                )
            }
        }

    )

}