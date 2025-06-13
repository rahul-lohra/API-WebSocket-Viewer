package rahul.lohra.networkmonitor

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import rahul.lohra.snorkl.Util
import rahul.lohra.snorkl.presentation.ui.NetworkMonitorActivity
import rahul.lohra.snorkl.ui.theme.AndroidNetworkMonitorTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
//        if(true){
//            startActivity(Intent(this, NetworkMonitorActivity::class.java))
//            return
//        }
        setContent {
            AndroidNetworkMonitorTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize() // Make the Column take up the entire screen
            .padding(16.dp), // Optional padding around the content
        verticalArrangement = Arrangement.Center, // Center items vertically
        horizontalAlignment = Alignment.CenterHorizontally // Center items horizontally
    ) {
        val context = LocalContext.current
        val ipAddress = Util.getLocalIpAddress(context)
        val portAddress by Util.selectedPortAddress.collectAsStateWithLifecycle()
        Text(
            text = "Phone IP: $ipAddress:${portAddress}",
            modifier = modifier
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            RestClient.makeRequest()
        }) {
            Text("Make HTTP Call")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            WebsocketClient.connectAndTest()
        }) {
            Text("Start WS")
        }
        Spacer(modifier = Modifier.width(4.dp))
        Button(onClick = {
            WebsocketClient.sendMessage("Hello From Rahul")
        }) {
            Text("Send WS events")
        }
        Spacer(modifier = Modifier.width(4.dp))
        Button(onClick = {
            WebsocketClient.close()
        }) {
            Text("Close WS events")
        }

        Button(onClick = {
            val intent = Intent(context, NetworkMonitorActivity::class.java)
            context.startActivity(intent)
        }) {
            Text("Open Network Activity")
        }


    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AndroidNetworkMonitorTheme {
        Greeting("Android")
    }
}