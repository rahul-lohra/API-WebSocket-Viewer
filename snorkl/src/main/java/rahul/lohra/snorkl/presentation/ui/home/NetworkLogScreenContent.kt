package rahul.lohra.snorkl.presentation.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import rahul.lohra.snorkl.NetworkListItem
import rahul.lohra.snorkl.RestApiListItem
import rahul.lohra.snorkl.WebsocketListItem
import rahul.lohra.snorkl.presentation.ui.LocalNetworkMonitorViewModel
import rahul.lohra.snorkl.presentation.ui.Screen

@Composable
fun NetworkLogScreenContent(
    modifier: Modifier,
    networkLogTab: NetworkLogTab,
    navController: NavController,
    lazyItems: LazyPagingItems<NetworkListItem>

) {
    when {
        lazyItems.loadState.refresh is LoadState.Loading -> {
            LoadingView()
        }

        lazyItems.loadState.refresh is LoadState.Error -> {
            val e = lazyItems.loadState.refresh as LoadState.Error
            ErrorView(message = e.error.localizedMessage ?: "Unknown error")
        }

        lazyItems.itemCount == 0 && lazyItems.loadState.refresh is LoadState.NotLoading -> {
            EmptyView(message = "No logs to display")
        }

        else -> {
            val viewModel = LocalNetworkMonitorViewModel.current
            val onItemClick = { networkData: NetworkListItem ->
                viewModel.setDetailScreenData(networkData)
                val message = networkData.id
                navController.navigate(Screen.Details(message).createRoute(message))
            }
            val colors = MaterialTheme.colorScheme
            LazyColumn {
                items(lazyItems.itemCount) { index ->
                    lazyItems[index]?.let { item ->
                        if (item is RestApiListItem) {
                            RestApiListItemUi(item, onItemClick)
                        } else {
                            WebsocketListItemUi(
                                item as WebsocketListItem,
                                onItemClick
                            )
                        }

                        if (index < lazyItems.itemCount) {
                            HorizontalDivider(
                                thickness = 0.5.dp,
                                color = Color(0x4DE5E7EB),
                            )
                        }
                    }
                }

                // Optional: Show loading at the end for pagination
                if (lazyItems.loadState.append is LoadState.Loading) {
                    item {
                        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                    }
                }

                if (lazyItems.loadState.append is LoadState.Error) {
                    item {
                        ErrorView(message = "Failed to load more logs")
                    }
                }
            }
        }
    }
}