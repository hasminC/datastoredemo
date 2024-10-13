package ph.edu.comteq.datastoredemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ph.edu.comteq.datastoredemo.UserPreferences.Companion.saveUsername
import ph.edu.comteq.datastoredemo.ui.theme.DataStoreDemoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var darkTheme by remember { mutableStateOf(false) }
            val navController = rememberNavController()
            var startDestination by remember { mutableStateOf("login") }
            LaunchedEffect(key1 = this@MainActivity) {
                CoroutineScope(Dispatchers.IO).launch {
                    if(UserPreferences.getUsername(this@MainActivity) != ""){
                        startDestination = "home"
                        darkTheme = UserPreferences.getDarkMode(this@MainActivity)
                    }
                }
            }
            DataStoreDemoTheme(darkTheme = darkTheme) {
                NavHost(navController = navController, startDestination=startDestination){
                    composable("login"){
                        LoginScreen(navController)
                    }
                    composable("home") {
                        HomeScreen(navController, darkTheme){ newDarkTheme ->
                            darkTheme = newDarkTheme
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LoginScreen(navController: NavHostController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    val context = LocalContext.current

    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it).padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Login", style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp).fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            if(error != ""){
                Text(text = error, style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 4.dp, horizontal = 16.dp).fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.error
                )
            }
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp).fillMaxWidth()
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation(),
                label = { Text("Password") },
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp).fillMaxWidth()
            )
            Button(onClick = {
                if (username == "admin" && password == "admin") {
                    // store username in datastore
                    CoroutineScope(Dispatchers.IO).launch {
                        saveUsername(context, username)
                    }
                    navController.navigate("home"){
                        popUpTo("login"){
                            inclusive = true
                        }
                    }
                } else {
                    error = "Invalid username or password"
                }
            },
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp).fillMaxWidth()) {
                Text("Login")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController, darkTheme: Boolean, onDarkThemeChange: (Boolean) -> Unit) {
    // var darkTheme by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var username by remember { mutableStateOf("") }

    LaunchedEffect(key1 = context) {
        CoroutineScope(Dispatchers.IO).launch {
            username = UserPreferences.getUsername(context) ?: ""
            //darkTheme = UserPreferences.getDarkMode(context)
        }
    }

    //DataStoreDemoTheme(darkTheme = darkTheme) {
        Scaffold (
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Data Store Demo") },
                    actions = {
                        Switch(
                            checked = darkTheme,
                            onCheckedChange = {
                                onDarkThemeChange(it)
                                CoroutineScope(Dispatchers.IO).launch {
                                    UserPreferences.saveDarkMode(context, it)
                                }
                            },
                            thumbContent = {
                                val icon = if (darkTheme) R.drawable.dark_mode else R.drawable.light_mode
                                Icon(
                                    painter = painterResource(id = icon),
                                    contentDescription = null,
                                    modifier = Modifier.size(SwitchDefaults.IconSize),
                                )
                            }
                        )
                    }
                )
            }
        ){
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Welcome $username!" , style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(vertical = 18.dp).fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Button(onClick = {
                    navController.navigate("login"){
                        popUpTo("home"){
                            inclusive = true
                        }
                    }
                    CoroutineScope(Dispatchers.IO).launch {
                        UserPreferences.clearUsername(context)
                    }
                },
                    modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth()
                ) {
                    Text("Logout")
                }
            }
        }
    //}
}