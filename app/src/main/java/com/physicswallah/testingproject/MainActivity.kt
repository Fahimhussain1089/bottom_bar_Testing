package com.physicswallah.testingproject
import android.app.Notification
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.util.Log
import android.widget.ListView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import com.physicswallah.testingproject.ui.theme.TestingProjectTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardDefaults.cardElevation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.physicswallah.testingproject.Screen.QuoteDetail
import com.physicswallah.testingproject.Screen.QuoteListScreen
import com.physicswallah.testingproject.models.DataManager
import com.physicswallah.testingproject.models.QuoteList
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.concurrent.timerTask
import androidx.compose.material3.Button as Button


@Serializable
data class Post(
    val userId: Int,
    val id: Int,
    val title: String,
    val body: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CoroutineScope(Dispatchers.IO).launch {
            delay(10000)
            DataManager.loadAssestFromFile(applicationContext)

        }

        setContent {
            App()
          //  QuoteDetail()
        // NotificationScreen()
          //  Notifications()
          //  BlogCategoryListView()
         //   Recomposable()


//            TestingProjectTheme {
//                // A surface container using the 'background' color from the theme
//                Surface(
//                    modifier = Modifier.fillMaxSize(),
//                    color = MaterialTheme.colorScheme.background
//                ) {
//                    PostApp()
//                }
//            }
            //+++++++++++++++++++++++++++++++++++
//            SatCheezy()
//            ListView()


        }
    }
}
@Composable
fun App(){

    if (DataManager.isDataLoaded.value ){
        if (DataManager.currentPage.value == Pages.LISTING){
            QuoteListScreen(data = DataManager.data) {
                DataManager.switchPages(it)
            }
        }else{
            DataManager.currentQuote?.let {
                QuoteDetail(quote = it)
            }
        }
    }
   else{
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth(1f)
        ){
            Text(
                text = "Loading.....",
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}

enum class Pages{
    LISTING, DETAIL
}


@Composable
fun NotificationScreen() {
    var count by rememberSaveable { mutableStateOf(0) }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        NotificationsCounter(count) { count++ }
        Spacer(modifier = Modifier.height(16.dp))
        MessageBar(count)
    }
}

@Composable
fun MessageBar(count: Int) {
    Card(
        elevation = cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                imageVector = Icons.Outlined.Favorite,
                contentDescription = "Heart Icon",
                modifier = Modifier.padding(4.dp)
            )
            Text("Messages sent so far: $count", fontSize = 16.sp)
        }
    }
}

@Composable
fun NotificationsCounter(count: Int, increment: () -> Unit) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "You have sent $count notification(s)")
        Button(onClick = {
            increment()
            Log.d("counterStage", "Button clicked")
        }) {
            Text(text = "Send Notification")
        }
    }
}

@Composable
fun Recomposable() {
    val state = remember { mutableStateOf(0.0) }

    // Logs only during initial composition
    Log.d("TAGGED", "LOGGED DURING INITIAL COMPOSITION")

    Button(onClick = {
        // Updates state → triggers recomposition
        state.value = Math.random()
    }) {
        // Logs during composition & recomposition
        Log.d("TAGGED", "LOGGED during both composition & recomposition")
        Text(text = state.value.toString())
    }
}

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

@Preview(showBackground = true, showSystemUi = true, heightDp = 200, widthDp = 200)
@Composable
fun BlogCategoryListView() {
    val categoryList = getCategorylist()

    LazyColumn {
        items(categoryList) { item ->
            BlogCategory(img = item.img, title = item.title, subtitle = item.subtitle)
        }
    }
}
@Composable
fun BlogCategory(img: Int, title: String, subtitle: String) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = img),
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .padding(8.dp)
                    .weight(0.2f)
            )

            Column(modifier = Modifier.weight(0.8f)) {
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = subtitle, fontSize = 12.sp)
            }
        }
    }
}





@Composable
private fun ItemDescription(title: String,subtitle:String,modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = title, fontWeight = FontWeight.Bold, fontSize = 10.sp)
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Thin,
            fontSize = 12.sp
        )
    }
}
data class Category(val img: Int, val title: String,val subtitle: String)

fun getCategorylist():MutableList<Category>{
    val list  = mutableListOf<Category>()
    list.add(Category(R.drawable.a,"Programmer","Learn Different Language"))
    list.add(Category(R.drawable.b,"js","Learn Different js"))
    list.add(Category(R.drawable.c,"python","Learn Different python"))
    list.add(Category(R.drawable.d,"Script","Learn Different Script"))
    list.add(Category(R.drawable.a,"Programmer","Learn Different Language"))
    list.add(Category(R.drawable.b,"js","Learn Different js"))
    list.add(Category(R.drawable.c,"python","Learn Different python"))
    list.add(Category(R.drawable.d,"Script","Learn Different Script"))
    list.add(Category(R.drawable.a,"Programmer","Learn Different Language"))
    list.add(Category(R.drawable.b,"js","Learn Different js"))
    list.add(Category(R.drawable.c,"python","Learn Different python"))
    list.add(Category(R.drawable.d,"Script","Learn Different Script"))
    list.add(Category(R.drawable.a,"Programmer","Learn Different Language"))
    list.add(Category(R.drawable.b,"js","Learn Different js"))
    list.add(Category(R.drawable.c,"python","Learn Different python"))
    list.add(Category(R.drawable.d,"Script","Learn Different Script"))
    list.add(Category(R.drawable.a,"Programmer","Learn Different Language"))
    list.add(Category(R.drawable.b,"js","Learn Different js"))
    list.add(Category(R.drawable.c,"python","Learn Different python"))
    list.add(Category(R.drawable.d,"Script","Learn Different Script"))
    return list
}




//@Composable
//fun ListView() {
//   Text("hussain",
//       modifier = Modifier.clickable {  }
//           .background(Color.Blue)
//           .size(200.dp)
//           .border(4.dp, color = Color.Red)
//           .clip(CircleShape)
//           .background(Color.Yellow))
//}



//@Preview(showBackground = true, showSystemUi = true, heightDp = 200, widthDp = 200)
//@Composable
//fun SatCheezy(name: String = "hey") {
//    Box(contentAlignment =  Alignment.BottomEnd){
//        Image(painter = painterResource(id = R.drawable.heard), contentDescription = "")
//        Image(painter = painterResource(id = R.drawable.array), contentDescription = "")
//
//    }
//}








@Composable
fun PostApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "postList") {
        composable("postList") {
            PostListScreen(navController)
        }
        composable("postDetail/{postId}") { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId")?.toIntOrNull()
            PostDetailScreen(postId = postId)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostListScreen(navController: NavController) {
    var posts by remember { mutableStateOf<List<Post>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            val response = withContext(Dispatchers.IO) {
                HttpClient().get("https://jsonplaceholder.typicode.com/posts")
            }
            posts = Json.decodeFromString<List<Post>>(response.bodyAsText())
            isLoading = false
        } catch (e: Exception) {
            error = e.message
            isLoading = false
        }
    }

//    LaunchedEffect(Unit) {
//        try {
//            val url = URL("https://jsonplaceholder.typicode.com/posts")
//            val connection = url.openConnection() as HttpURLConnection
//            connection.requestMethod = "GET"
//
//            val response = connection.inputStream.bufferedReader().use { it.readText() }
//            posts = Json.decodeFromString<List<Post>>(response)
//            isLoading = false
//        } catch (e: Exception) {
//            error = e.message
//            isLoading = false
//        }
//    }

    Scaffold(
        topBar = {
//            TopAppBar(title = { Text("Posts") })
            TopAppBar(
                title = {
                    Text(
                        "Posts",
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Search */ }) {
                        Icon(Icons.Filled.Search, "Search")
                    }
                    IconButton(onClick = { /* Menu */ }) {
                        Icon(Icons.Filled.MoreVert, "Menu")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier.shadow(elevation = 4.dp),
                scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
            )
        }

    ) { innerPadding ->
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Error: $error", color = Color.Red)
                }
            }
            else -> {
                LazyColumn(modifier = Modifier.padding(innerPadding)) {
                    items(posts) { post ->
                        PostListItem(post = post) {
                            navController.navigate("postDetail/${post.id}")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostListItem(post: Post, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "User ID: ${post.userId}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Post ID: ${post.id}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = post.title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                text = "hussain this is testing :: ",
                style=MaterialTheme.typography.bodySmall
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(postId: Int?) {
    var post by remember { mutableStateOf<Post?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(postId) {
        if (postId == null) {
            error = "Invalid post ID"
            isLoading = false
            return@LaunchedEffect
        }

        try {
            val response = withContext(Dispatchers.IO) {
                HttpClient().get("https://jsonplaceholder.typicode.com/posts/$postId")
            }
            post = Json.decodeFromString<Post>(response.bodyAsText())
            isLoading = false
        } catch (e: Exception) {
            error = e.message
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Post Details") })
        }
    ) {
        innerPadding ->
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Error: $error", color = Color.Red)
                }
            }
            post == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Post not found", color = Color.Red)
                }
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "User ID: ${post!!.userId}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "Post ID: ${post!!.id}",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                            Text(
                                text = post!!.title,
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(top = 16.dp)
                            )
                            Text(
                                text = post!!.body,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}