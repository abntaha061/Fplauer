package com.finalplayer.app.ui.settings.layout

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.finalplayer.app.data.preferences.PlayerLayoutPreferences
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PlayerLayoutEditSectionScreen(
    sectionId: String,
    onBack: () -> Unit
) {
    val layoutPrefs: PlayerLayoutPreferences = koinInject()
    val scope = rememberCoroutineScope()

    val (title, pref, defaultVal) = when (sectionId) {
        "edit_top_right" -> Triple("تعديل الزاوية العلوية اليمنى", layoutPrefs.topRightControls, PlayerLayoutPreferences.DEFAULT_TOP_RIGHT)
        "edit_bottom_right" -> Triple("تعديل الزاوية السفلي اليمنى", layoutPrefs.bottomRightControls, PlayerLayoutPreferences.DEFAULT_BOTTOM_RIGHT)
        "edit_bottom_left" -> Triple("تعديل الزاوية السفلي اليسرى", layoutPrefs.bottomLeftControls, PlayerLayoutPreferences.DEFAULT_BOTTOM_LEFT)
        "edit_portrait_bottom" -> Triple("تعديل عناصر التحكم السفلي في الوضع العمودي", layoutPrefs.portraitBottomControls, PlayerLayoutPreferences.DEFAULT_PORTRAIT_BOTTOM)
        "edit_controls_tab" -> Triple("تعديل الأزرار في تبويب أدوات التحكم", layoutPrefs.controlsTabButtons, PlayerLayoutPreferences.DEFAULT_CONTROLS_TAB)
        else -> Triple("تعديل التخطيط", layoutPrefs.topRightControls, PlayerLayoutPreferences.DEFAULT_TOP_RIGHT)
    }

    var activeIds by remember {
        mutableStateOf(layoutPrefs.parseControlList(pref.get()))
    }
    var showResetDialog by remember { mutableStateOf(false) }

    fun saveActiveIds(newList: List<String>) {
        activeIds = newList
        scope.launch {
            pref.set(layoutPrefs.formatControlList(newList))
        }
    }

    val availableTools = remember(activeIds) {
        ControlTools.ALL_TOOLS.filter { tool -> !activeIds.contains(tool.id) }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("إعادة ضبط التخطيط", fontWeight = FontWeight.Bold) },
            text = { Text("هل أنت متأكد أنك تريد إعادة ضبط هذه المنطقة إلى الإعدادات الافتراضية؟") },
            confirmButton = {
                TextButton(onClick = {
                    showResetDialog = false
                    val defaultList = layoutPrefs.parseControlList(defaultVal)
                    saveActiveIds(defaultList)
                }) {
                    Text("تأكيد", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "رجوع",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showResetDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "إعادة ضبط",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Notice card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "اضغط على العناصر لإضافتها أو إزالتها. اضغط علامة الناقص الحمراء للإزالة، وبطاقة الزائد الخضراء للإضافة.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // ACTIVE ITEMS SECTION
            Text(
                text = "العناصر المحددة (${activeIds.size})",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    if (activeIds.isEmpty()) {
                        Text(
                            text = "لا توجد عناصر محددة في هذه المنطقة",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(16.dp),
                            textAlign = TextAlign.Center
                        )
                    } else {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            activeIds.forEachIndexed { index, id ->
                                val tool = ControlTools.getById(id)
                                if (tool != null) {
                                    ActiveToolBadge(
                                        tool = tool,
                                        onRemove = {
                                            val updated = activeIds.toMutableList()
                                            updated.removeAt(index)
                                            saveActiveIds(updated)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // AVAILABLE ITEMS SECTION
            Text(
                text = "المتاحة (${availableTools.size})",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    if (availableTools.isEmpty()) {
                        Text(
                            text = "تمت إضافة جميع الأيقونات المتاحة",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(16.dp)
                        )
                    } else {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            availableTools.forEach { tool ->
                                AvailableToolBadge(
                                    tool = tool,
                                    onAdd = {
                                        val updated = activeIds.toMutableList()
                                        updated.add(tool.id)
                                        saveActiveIds(updated)
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // ICON GUIDE SECTION ("دليل الأيقونات")
            HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.15f))
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "دليل الأيقونات",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ControlTools.ALL_TOOLS.chunked(2).forEach { rowTools ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowTools.forEach { item ->
                            Card(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = item.title,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                        if (rowTools.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ActiveToolBadge(
    tool: ControlToolItem,
    onRemove: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(top = 4.dp, end = 4.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(14.dp)
                )
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = tool.icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = tool.title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        // Red (-) circle badge
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(22.dp)
                .clip(CircleShape)
                .background(Color(0xFFE53935))
                .clickable { onRemove() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = "إزالة",
                tint = Color.White,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
private fun AvailableToolBadge(
    tool: ControlToolItem,
    onAdd: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(top = 4.dp, end = 4.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(14.dp)
                )
                .clickable { onAdd() }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = tool.icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = tool.title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Green (+) circle badge
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(22.dp)
                .clip(CircleShape)
                .background(Color(0xFF4CAF50))
                .clickable { onAdd() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "إضافة",
                tint = Color.White,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}
