package com.finalplayer.app.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.finalplayer.app.ui.settings.tabs.AdvancedTab
import com.finalplayer.app.ui.settings.tabs.AppearanceTab
import com.finalplayer.app.ui.settings.tabs.ControlsTab
import com.finalplayer.app.ui.settings.tabs.GesturesTab
import com.finalplayer.app.ui.settings.tabs.InteractionTab
import com.finalplayer.app.ui.settings.tabs.PlayerSettingsTab
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onAboutClick: () -> Unit = {},
    viewModel: SettingsViewModel = koinViewModel()
) {
    val tabs = listOf("التحكم", "الإعدادات", "الإيماءات", "الجماليات", "التفاعل", "متقدم")
    var selectedTab by remember { mutableIntStateOf(0) }

    val darkGreen = Color(0xFF2E7D32)
    val lightGreenBg = Color(0xFFF5FAF5)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "الإعدادات",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = darkGreen
                        )
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("settings_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "رجوع",
                            tint = darkGreen
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onAboutClick) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "حول التطبيق",
                            tint = darkGreen
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = lightGreenBg
                )
            )
        },
        containerColor = lightGreenBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = lightGreenBg,
                contentColor = darkGreen,
                edgePadding = 16.dp,
                indicator = { tabPositions ->
                    tabPositions.getOrNull(selectedTab)?.let { position ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(position),
                            color = darkGreen,
                            height = 3.dp
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("settings_tab_row")
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTab == index) darkGreen else Color.Gray
                                )
                            )
                        },
                        modifier = Modifier.testTag("tab_$index")
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(lightGreenBg)
            ) {
                when (selectedTab) {
                    0 -> ControlsTab(viewModel = viewModel)
                    1 -> PlayerSettingsTab(viewModel = viewModel)
                    2 -> GesturesTab(viewModel = viewModel)
                    3 -> AppearanceTab(viewModel = viewModel)
                    4 -> InteractionTab(viewModel = viewModel)
                    5 -> AdvancedTab(viewModel = viewModel)
                }
            }
        }
    }
}
