package rahul.lohra.snorkl.presentation.ui.home

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import rahul.lohra.snorkl.NetworkListItem
import rahul.lohra.snorkl.R
import rahul.lohra.snorkl.RestApiListItem
import rahul.lohra.snorkl.WebsocketListItem
import rahul.lohra.snorkl.core.TimeUtil
import rahul.lohra.snorkl.data.WebSocketEventType
import rahul.lohra.snorkl.presentation.ui.NetworkMonitorColorDeepNaviBlue
import rahul.lohra.snorkl.presentation.ui.NetworkMonitorColorPurple


@Composable
fun RestApiListItemUi(
    restApiData: RestApiListItem,
    onItemClick: (NetworkListItem) -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val restApiMethodColorsScheme = if (isDark) {
        ListItemColorPalettes.dark
    } else {
        ListItemColorPalettes.light
    }

    val restApiMethodColor = when (restApiData.method.capitalize()) {
        "GET" -> restApiMethodColorsScheme.getText
        "PUT" -> restApiMethodColorsScheme.putText
        "POST" -> restApiMethodColorsScheme.postText
        "DELETE" -> restApiMethodColorsScheme.deleteText
        else -> restApiMethodColorsScheme.errorText
    }
    val timeTextColor = if (isDark) Color.White else Color.Black
    val statusCodeColor = when {
        restApiData.responseCode in 200..299 -> StatusCodeItemColorPalettes.success.textColor
        else -> StatusCodeItemColorPalettes.fail.textColor
    }
    Row(modifier = Modifier
        .clickable {
            onItemClick(restApiData)
        }
        .padding(horizontal = 8.dp, vertical = 8.dp)
        .fillMaxWidth()) {
        Text("${restApiData.responseCode}", color = statusCodeColor, fontWeight = FontWeight.Medium)
        Spacer(Modifier.width(12.dp))
        Column {
            Row {
                ChipsUi(
                    restApiData.method.capitalize(),
                    restApiMethodColor.bgColor,
                    restApiMethodColor.textColor
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    Uri.parse(restApiData.requestUrl).path ?: restApiData.requestUrl,
                    color = timeTextColor
                )
            }
            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = "\uD83D\uDD12 ${Uri.parse(restApiData.requestUrl).host}",
                color = timeTextColor,
                fontWeight = FontWeight.Light
            )
            Row(modifier = Modifier.padding(top = 8.dp)) {
                Text(TimeUtil.formatTimestamp(restApiData.timestamp), color = timeTextColor)
                Spacer(Modifier.width(10.dp))
                Text("⏱\uFE0F${restApiData.durationMs / 1000}s", color = timeTextColor)
            }
        }
    }
}

@Composable
fun WebsocketListItemUi(
    websocketListItem: WebsocketListItem,
    onItemClick: (NetworkListItem) -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val restApiMethodColorsScheme = if (isDark) {
        ListItemColorPalettes.dark
    } else {
        ListItemColorPalettes.light
    }
    val wsEventType = WebSocketEventType.valueOf(websocketListItem.eventType)

    val wsTextColor = getWsTextColor(isDark, wsEventType)
    val timeTextColor = if (isDark) Color.White else Color.Black

    Column(modifier = Modifier
        .clickable {
            onItemClick(websocketListItem)
        }
        .padding(horizontal = 8.dp, vertical = 8.dp)
        .fillMaxWidth()) {
        Row() {
            ChipsUi(
                "WS", wsTextColor.bgColor,
                wsTextColor.textColor
            )
            WsArrowIcon(websocketListItem)
            Spacer(Modifier.width(8.dp))
            WSDirectionText(websocketListItem, timeTextColor)
            Spacer(Modifier.width(8.dp))
            WSStatusChips(websocketListItem, wsTextColor)
        }
        Row {
            WSConnectText(websocketListItem)
            Spacer(Modifier.width(8.dp))
            Text("${Uri.parse(websocketListItem.requestUrl).host}", color = timeTextColor)
        }
        Row(modifier = Modifier.padding(top = 8.dp, start = 60.dp)) {
            Text(TimeUtil.formatTimestamp(websocketListItem.timestamp), color = timeTextColor)
            Spacer(Modifier.width(10.dp))
            Text("⏱\uFE0F${websocketListItem.durationMs / 1000}s", color = timeTextColor)
        }
    }
}

internal fun getWsTextColor(isDarkMode: Boolean, wsEventType: WebSocketEventType): NetworkMethodTextColorStyle {
    return when (wsEventType) {
        WebSocketEventType.CONNECTION_OPEN, WebSocketEventType.MESSAGE -> {
            if (isDarkMode) {
                NetworkMethodTextColorStyle(Color(0xff49DE80), Color(0xff2B4448))
            } else {
                ListItemColorPalettes.light.getText
            }
        }

        WebSocketEventType.CONNECTION_FAILURE -> {
            if (isDarkMode) {
                NetworkMethodTextColorStyle(Color(0xff9BA3AF), Color(0xff2C3543))
            } else {
                NetworkMethodTextColorStyle(Color(0xff9BA3AF), Color(0xffF5F6F7))
            }
        }

        else -> {
            if (isDarkMode) {
                ListItemColorPalettes.dark.errorText
            } else {
                ListItemColorPalettes.light.errorText
            }
        }
    }
}

@Composable
internal fun WSConnectText(websocketListItem: WebsocketListItem) {
    val webSocketEventType = WebSocketEventType.valueOf(websocketListItem.eventType)
    val text = when (webSocketEventType) {
        WebSocketEventType.CONNECTION_OPEN-> "connect"
        WebSocketEventType.CONNECTION_FAILURE-> "error"
        WebSocketEventType.CONNECTION_CLOSED-> "disconnected"
        WebSocketEventType.CONNECTION_CLOSING-> "disconnecting"
        WebSocketEventType.MESSAGE-> "message"
    }
    Text(text, color = NetworkMonitorColorPurple)
}

@Composable
internal fun WSStatusChips(
    websocketListItem: WebsocketListItem,
    colorStyle: NetworkMethodTextColorStyle
) {
    val text = when (websocketListItem.direction) { //TODO Rahul needs refactoring
        "incoming", "outgoing" -> "opened"
        else -> "closed"
    }
    ChipsUi(
        text, colorStyle.bgColor,
        colorStyle.textColor
    )
}

@Composable
fun WSDirectionText(websocketListItem: WebsocketListItem, textColor: Color) {
    Text(
        websocketListItem.direction, fontSize = 12.sp,
        color = textColor,
        fontWeight = FontWeight.Normal
    )
}

@Composable
fun WsArrowIcon(websocketListItem: WebsocketListItem) {
    when (websocketListItem.direction) {
        "incoming" -> {
            Icon(
                painter = painterResource(id = R.drawable.networkmonitor_arrow_down),
                contentDescription = "incoming",
                tint = NetworkMonitorColorPurple,
                modifier = Modifier.size(18.dp)
            )
        }

        "outgoing" -> {
            Icon(
                painter = painterResource(id = R.drawable.networkmonitor_arrow_up),
                contentDescription = "outgoing",
                tint = NetworkMonitorColorPurple,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Preview
@Composable
fun WebsocketListItemUiDayPreview() {
    Box(modifier = Modifier.background(Color.White)) {
        val isDark = false
        val restApiMethodColor = ListItemColorPalettes.light.getText
        val timeTextColor = if (isDark) Color.White else Color.Black
        val response = """ 
    {"type":"notification","message":"New update available"}
""".trimIndent()

        Column(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .fillMaxWidth()
        ) {
            Row() {

                ChipsUi(
                    "WS", restApiMethodColor.bgColor,
                    restApiMethodColor.textColor
                )

                Icon(
                    painter = painterResource(id = R.drawable.networkmonitor_arrow_up),
                    contentDescription = "outgoing",
                    tint = NetworkMonitorColorPurple,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "outgoing", fontSize = 12.sp,
                    fontWeight = FontWeight.Normal
                )
                Spacer(Modifier.width(8.dp))
                ChipsUi(
                    "opened", restApiMethodColor.bgColor,
                    restApiMethodColor.textColor
                )
            }
            Row {
                Text("connect", color = NetworkMonitorColorPurple)
                Spacer(Modifier.width(8.dp))
                Text("socket.example.com/")
            }

            Text(
                modifier = Modifier.padding(top = 8.dp, start = 60.dp),
                color = Color(0xff9CA4AF),
                text = response, maxLines = 1
            )
            Row(modifier = Modifier.padding(top = 8.dp, start = 60.dp)) {
                Text("09:07:55 AM", color = timeTextColor)
                Spacer(Modifier.width(10.dp))
                Text("⏱\uFE0F 2s", color = timeTextColor)
            }
        }

    }
}

@Preview
@Composable
fun WebsocketListItemUiNightPreview() {
    Box(modifier = Modifier.background(Color.White)) {
        val isDark = true
        val restApiMethodColor = ListItemColorPalettes.dark.getText
        val timeTextColor = if (isDark) Color.White else Color.Black
        val response = """ 
    {"type":"notification","message":"New update available"}
""".trimIndent()
        Box(Modifier.background(color = NetworkMonitorColorDeepNaviBlue)) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 8.dp)
                    .background(color = NetworkMonitorColorDeepNaviBlue)
                    .fillMaxWidth()
            ) {
                Row() {
                    ChipsUi(
                        "WS", restApiMethodColor.bgColor,
                        restApiMethodColor.textColor
                    )

                    Icon(
                        painter = painterResource(id = R.drawable.networkmonitor_arrow_up),
                        contentDescription = "outgoing",
                        tint = NetworkMonitorColorPurple,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "outgoing", fontSize = 12.sp,
                        color = timeTextColor,
                        fontWeight = FontWeight.Normal
                    )
                    Spacer(Modifier.width(8.dp))
                    ChipsUi(
                        "opened", restApiMethodColor.bgColor,
                        restApiMethodColor.textColor
                    )
                }
                Row {
                    Text("connect", color = NetworkMonitorColorPurple)
                    Spacer(Modifier.width(8.dp))
                    Text("socket.example.com/", color = timeTextColor)
                }
                Text(
                    modifier = Modifier.padding(top = 8.dp, start = 60.dp),
                    color = Color(0xff9CA4AF),
                    text = response, maxLines = 1
                )
                Row(modifier = Modifier.padding(top = 8.dp, start = 60.dp)) {
                    Text("09:07:55 AM", color = timeTextColor)
                    Spacer(Modifier.width(10.dp))
                    Text("⏱\uFE0F 2s", color = timeTextColor)
                }
            }
        }
    }
}

@Preview()
@Composable
fun RestApiListItemUiDayPreview() {
    Box(modifier = Modifier.background(Color.White)) {
        val isDark = false
        val restApiMethodColor = ListItemColorPalettes.light.getText
        val timeTextColor = if (isDark) Color.White else Color.Black

        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .fillMaxWidth()
        ) {
            Text("200")
            Spacer(Modifier.width(12.dp))
            Column {
                Row {
                    ChipsUi(
                        "GET", restApiMethodColor.bgColor,
                        restApiMethodColor.textColor
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("/api/api", color = timeTextColor)
                }
                Text(
                    modifier = Modifier.padding(top = 8.dp),
                    text = "\uD83D\uDD12 hispsum.co",
                    color = timeTextColor,
                    fontWeight = FontWeight.Light
                )
                Row(modifier = Modifier.padding(top = 8.dp)) {
                    Text("09:07:55 AM", color = timeTextColor)
                    Spacer(Modifier.width(10.dp))
                    Text("⏱\uFE0F 2s", color = timeTextColor)
                }
            }
        }
    }
}

@Composable
internal fun ChipsUi(text: String, bgColor: Color, textColor: Color) {
    Text(
        modifier = Modifier
            .background(
                bgColor,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(vertical = 1.dp, horizontal = 4.dp),
        color = textColor,
        fontSize = 12.sp,
        text = text, fontWeight = FontWeight.SemiBold
    )
}

@Preview
@Composable
fun RestApiListItemUiNightPreview() {
    val isDark = true
    val restApiMethodColor = ListItemColorPalettes.dark.getText
    val timeTextColor = if (isDark) Color.White else Color.Black

    Box(modifier = Modifier.background(NetworkMonitorColorDeepNaviBlue)) {
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .fillMaxWidth()
        ) {
            Text(
                "200",
                color = StatusCodeItemColorPalettes.success.textColor,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.width(12.dp))
            Column {
                Row {
                    ChipsUi(
                        "GET", restApiMethodColor.bgColor,
                        restApiMethodColor.textColor
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("/api/api/", color = timeTextColor)
                }
                Text(
                    modifier = Modifier.padding(top = 8.dp),
                    text = "\uD83D\uDD12 hispsum.co",
                    fontWeight = FontWeight.Light,
                    color = timeTextColor
                )
                Row(modifier = Modifier.padding(top = 8.dp)) {
                    Text("09:05:55 PM", color = timeTextColor)
                    Spacer(Modifier.width(10.dp))
                    Text("⏱\uFE0F1s", color = timeTextColor)
                }
            }
        }
    }
}

@Preview(group = "get")
@Composable
fun GetChipsNightPreview() {
    val restApiMethodColor = ListItemColorPalettes.dark.getText

    Row {
        Text(
            modifier = Modifier
                .background(
                    Color(0xff4ADE80),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(vertical = 1.dp, horizontal = 4.dp),
            color = restApiMethodColor.textColor,
            fontSize = 12.sp,
            text = "GET", fontWeight = FontWeight.SemiBold
        )
    }
}

@Preview(group = "get")
@Composable
fun GetChipsLightPreview() {
    val restApiMethodColor = ListItemColorPalettes.light.getText
    Row {
        Text(
            modifier = Modifier
                .background(
                    restApiMethodColor.bgColor,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(vertical = 1.dp, horizontal = 4.dp),
            color = restApiMethodColor.textColor,
            fontSize = 12.sp,
            text = "GET", fontWeight = FontWeight.SemiBold
        )
    }
}

@Preview(group = "put")
@Composable
fun PutChipsDarkPreview() {
    val restApiMethodColor = ListItemColorPalettes.dark.putText
    Row {
        Text(
            modifier = Modifier
                .background(
                    restApiMethodColor.bgColor,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(vertical = 1.dp, horizontal = 4.dp),
            color = restApiMethodColor.textColor,
            fontSize = 12.sp,
            text = "PUT", fontWeight = FontWeight.SemiBold
        )
    }
}

@Preview(group = "put")
@Composable
fun PutChipsLightPreview() {
    val restApiMethodColor = ListItemColorPalettes.light.putText

    Row {
        Text(
            modifier = Modifier
                .background(
                    restApiMethodColor.bgColor,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(vertical = 1.dp, horizontal = 4.dp),
            color = restApiMethodColor.textColor,
            fontSize = 12.sp,
            text = "PUT", fontWeight = FontWeight.SemiBold
        )
    }
}

@Preview
@Composable
fun PostChipsDarkPreview() {
    val restApiMethodColor = ListItemColorPalettes.dark.postText
    Row {
        Text(
            modifier = Modifier
                .background(
                    restApiMethodColor.bgColor,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(vertical = 1.dp, horizontal = 4.dp),
            color = restApiMethodColor.textColor,
            fontSize = 12.sp,
            text = "POST", fontWeight = FontWeight.SemiBold
        )
    }
}

@Preview
@Composable
fun PostChipsLightPreview() {
    val restApiMethodColor = ListItemColorPalettes.light.postText

    Row {
        Text(
            modifier = Modifier
                .background(
                    restApiMethodColor.bgColor,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(vertical = 1.dp, horizontal = 4.dp),
            color = restApiMethodColor.textColor,
            fontSize = 12.sp,
            text = "POST", fontWeight = FontWeight.SemiBold
        )
    }
}

@Preview
@Composable
fun DeleteChipsDarkPreview() {
    val restApiMethodColor = ListItemColorPalettes.dark.deleteText
    Row() {
        Text(
            modifier = Modifier
                .background(
                    restApiMethodColor.bgColor,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(vertical = 1.dp, horizontal = 4.dp),
            color = restApiMethodColor.textColor,
            fontSize = 12.sp,
            text = "DEL", fontWeight = FontWeight.SemiBold
        )
    }
}

@Preview
@Composable
fun DeleteChipsLightPreview() {
    val restApiMethodColor = ListItemColorPalettes.light.deleteText
    Row {
        Text(
            modifier = Modifier
                .background(
                    restApiMethodColor.bgColor,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(vertical = 1.dp, horizontal = 4.dp),
            color = restApiMethodColor.textColor,
            fontSize = 12.sp,
            text = "DEL", fontWeight = FontWeight.SemiBold
        )
    }
}

@Preview
@Composable
fun WsChipsDarkPreview() {
    val restApiMethodColor = ListItemColorPalettes.dark.wsText
    Row() {
        Text(
            modifier = Modifier
                .background(
                    restApiMethodColor.bgColor,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(vertical = 1.dp, horizontal = 4.dp),
            color = restApiMethodColor.textColor,
            fontSize = 12.sp,
            text = "WS", fontWeight = FontWeight.SemiBold
        )
    }
}

@Preview
@Composable
fun WsChipsLightPreview() {
    val restApiMethodColor = ListItemColorPalettes.light.wsText
    Row {
        Text(
            modifier = Modifier
                .background(
                    restApiMethodColor.bgColor,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(vertical = 0.dp, horizontal = 4.dp),
            color = restApiMethodColor.textColor,
            fontSize = 12.sp,
            text = "WS", fontWeight = FontWeight.SemiBold
        )
    }
}

internal data class NetworkMethodTextColorStyle(val textColor: Color, val bgColor: Color)

internal data class NetworkMethodTextColorScheme(
    val getText: NetworkMethodTextColorStyle,
    val postText: NetworkMethodTextColorStyle,
    val putText: NetworkMethodTextColorStyle,
    val deleteText: NetworkMethodTextColorStyle,
    val errorText: NetworkMethodTextColorStyle,
    val wsText: NetworkMethodTextColorStyle,
)

internal data class NetworkMethodTextColorPalettes(
    val light: NetworkMethodTextColorScheme,
    val dark: NetworkMethodTextColorScheme
)


internal val ListItemColorPalettes = NetworkMethodTextColorPalettes(
    light = NetworkMethodTextColorScheme(
        getText = NetworkMethodTextColorStyle(Color(0xff4ADE80), Color(0xffEFFCF3)),
        postText = NetworkMethodTextColorStyle(Color(0xff60a5fa), Color(0xffF0F6FE)),
        putText = NetworkMethodTextColorStyle(Color(0xfffacc15), Color(0xffFEFAEA)),
        deleteText = NetworkMethodTextColorStyle(Color(0xfff87171), Color(0xffFEF1F1)),
        errorText = NetworkMethodTextColorStyle(Color(0xfff87171), Color(0xffFEF1F1)),
        wsText = NetworkMethodTextColorStyle(Color(0xffc084fc), Color(0xffF9F3FF)),
    ),
    dark = NetworkMethodTextColorScheme(
        getText = NetworkMethodTextColorStyle(Color.White, Color(0xff4ADE80)),
        postText = NetworkMethodTextColorStyle(Color(0xff60a5fa), Color(0xff27384E)),
        putText = NetworkMethodTextColorStyle(Color(0xfffacc15), Color(0xff3A3D33)),
        deleteText = NetworkMethodTextColorStyle(Color(0xfff87171), Color(0xff3A323D)),
        errorText = NetworkMethodTextColorStyle(Color(0xfff87171), Color(0xff3A323D)),
        wsText = NetworkMethodTextColorStyle(Color(0xffc084fc), Color(0xff33344E)),
    ),
)

internal data class StatusCodeTextColorStyle(val textColor: Color)
internal data class StatusCodeTextColorScheme(
    val success: StatusCodeTextColorStyle,
    val fail: StatusCodeTextColorStyle
)

internal val StatusCodeItemColorPalettes = StatusCodeTextColorScheme(
    StatusCodeTextColorStyle(Color(0xff4ADE80)),
    StatusCodeTextColorStyle(Color(0xffF87171))
)