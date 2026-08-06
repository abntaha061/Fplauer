package com.finalplayer.app.ui.browser

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Http
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Router
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.finalplayer.app.domain.model.NetworkSource
import com.finalplayer.app.domain.model.NetworkSourceType
import com.finalplayer.app.domain.model.RemoteFile
import com.finalplayer.app.ui.browser.components.AddNetworkSourceSheet
import com.finalplayer.app.ui.browser.components.RemoteFileItem
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun NetworkBrowserScreen(
    viewModel: NetworkBrowserViewModel = koinViewModel(),
    onBack: () -> Unit = {},
    onPlayMedia: (streamUrl: String, title: String) -> Unit = { _, _ -> }
) {
    val sources by viewModel.sources.collectAsStateWithLifecycle()
    val connectedSource by viewModel.connectedSource.collectAsStateWithLifecycle()
    val pathSegments by viewModel.pathSegments.collectAsStateWithLifecycle()
    val remoteFiles by viewModel.remoteFiles.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val isTesting by viewModel.isTesting.collectAsStateWithLifecycle()
    val testResult by viewModel.testResult.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    var showAddSheet by remember { mutableStateOf(false) }
    var sourceToDelete by remember { mutableStateOf<NetworkSource?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = connectedSource?.displayName ?: "مصادر الشبكة / Network Sources",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (!viewModel.navigateBack()) {
                            onBack()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (connectedSource != null) {
                        TextButton(onClick = { viewModel.disconnect() }) {
                            Text("قطع الاتصال")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (connectedSource == null) {
                FloatingActionButton(
                    onClick = {
                        viewModel.clearTestResult()
                        showAddSheet = true
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Source"
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val source = connectedSource
            if (source != null) {
                // Connected browsing view
                Column(modifier = Modifier.fillMaxSize()) {
                    // Breadcrumb navigation bar
                    BreadcrumbBar(
                        segments = pathSegments,
                        onSegmentClick = { index ->
                            viewModel.navigateToBreadcrumb(index)
                        }
                    )

                    if (isLoading) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else if (errorMessage != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = errorMessage ?: "",
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(onClick = { viewModel.connectAndBrowse(source, viewModel.currentPath.value) }) {
                                    Text("إعادة المحاولة / Retry")
                                }
                            }
                        }
                    } else if (remoteFiles.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "مجلد فارغ / Empty folder",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            items(
                                items = remoteFiles,
                                key = { file -> file.path }
                            ) { file ->
                                RemoteFileItem(
                                    file = file,
                                    onClick = {
                                        if (file.isDirectory) {
                                            viewModel.browseDirectory(file.path)
                                        } else {
                                            val streamUrl = viewModel.buildStreamUrl(file)
                                            onPlayMedia(streamUrl, file.name)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            } else {
                // List of saved Network Sources
                if (sources.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "لا توجد مصادر شبكة مضافة.\nانقر على (+) لإضافة SMB / FTP / HTTP",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp)
                    ) {
                        items(
                            items = sources,
                            key = { s -> s.id }
                        ) { item ->
                            NetworkSourceCard(
                                source = item,
                                onConnect = { viewModel.connectAndBrowse(item, "/") },
                                onDelete = { sourceToDelete = item }
                            )
                        }
                    }
                }
            }
        }

        if (showAddSheet) {
            AddNetworkSourceSheet(
                sheetState = sheetState,
                isTesting = isTesting,
                testResult = testResult,
                onDismiss = { showAddSheet = false },
                onTestConnection = { s -> viewModel.testConnection(s) },
                onSaveSource = { s ->
                    viewModel.addSource(s)
                    showAddSheet = false
                }
            )
        }

        sourceToDelete?.let { target ->
            AlertDialog(
                onDismissRequest = { sourceToDelete = null },
                title = { Text("حذف مصدر الشبكة") },
                text = { Text("هل أنت تأكد من حذف المصدر '${target.displayName}'؟") },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.removeSource(target.id)
                        sourceToDelete = null
                    }) {
                        Text("حذف", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { sourceToDelete = null }) {
                        Text("إلغاء")
                    }
                }
            )
        }
    }
}

@Composable
fun NetworkSourceCard(
    source: NetworkSource,
    onConnect: () -> Unit,
    onDelete: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when (source.type) {
                    NetworkSourceType.SMB -> Icons.Default.Dns
                    NetworkSourceType.FTP -> Icons.Default.Router
                    else -> Icons.Default.Http
                },
                contentDescription = source.type.name,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = source.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${source.type.name} • ${source.host}${if (source.port > 0) ":${source.port}" else ""}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(onClick = onConnect) {
                Text("اتصال")
            }

            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Options"
                    )
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("حذف / Delete", color = MaterialTheme.colorScheme.error) },
                        onClick = {
                            menuExpanded = false
                            onDelete()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun BreadcrumbBar(
    segments: List<String>,
    onSegmentClick: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        segments.forEachIndexed { index, segment ->
            TextButton(
                onClick = { onSegmentClick(index) },
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
            ) {
                Text(
                    text = segment,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (index == segments.lastIndex) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (index < segments.lastIndex) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
