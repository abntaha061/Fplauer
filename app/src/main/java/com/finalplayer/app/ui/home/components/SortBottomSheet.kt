package com.finalplayer.app.ui.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SortBottomSheet(
    sheetState: SheetState,
    sortBy: String,
    sortAscending: Boolean,
    viewMode: String,
    layoutMode: String,
    visibleFields: Set<String>,
    onlyForFolderList: Boolean,
    onDismiss: () -> Unit,
    onSortByChanged: (String) -> Unit,
    onSortAscendingChanged: (Boolean) -> Unit,
    onViewModeChanged: (String) -> Unit,
    onLayoutModeChanged: (String) -> Unit,
    onVisibleFieldsChanged: (Set<String>) -> Unit,
    onOnlyForFolderListChanged: (Boolean) -> Unit
) {
    var isFieldsExpanded by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "خيارات الترتيب والعرض / Sort & View Options",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Sort By Section
            Text(
                text = "الترتيب حسب / Sort by",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = sortBy == "title",
                    onClick = { onSortByChanged("title") },
                    label = { Text("Title") },
                    leadingIcon = { Icon(Icons.Default.SortByAlpha, contentDescription = null) }
                )
                FilterChip(
                    selected = sortBy == "date",
                    onClick = { onSortByChanged("date") },
                    label = { Text("Date") },
                    leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) }
                )
                FilterChip(
                    selected = sortBy == "size",
                    onClick = { onSortByChanged("size") },
                    label = { Text("Size") },
                    leadingIcon = { Icon(Icons.Default.SwapVert, contentDescription = null) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Direction Section
            Text(
                text = "اتجاه الترتيب / Direction",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = sortAscending,
                    onClick = { onSortAscendingChanged(true) },
                    label = { Text("↑ A-Z / Ascending") }
                )
                FilterChip(
                    selected = !sortAscending,
                    onClick = { onSortAscendingChanged(false) },
                    label = { Text("↓ Z-A / Descending") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // View Mode Section
            Text(
                text = "نمط العرض / View Mode",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = viewMode == "folder",
                    onClick = { onViewModeChanged("folder") },
                    label = { Text("Folder") }
                )
                FilterChip(
                    selected = viewMode == "library",
                    onClick = { onViewModeChanged("library") },
                    label = { Text("Library") }
                )
                FilterChip(
                    selected = viewMode == "tree",
                    onClick = { onViewModeChanged("tree") },
                    label = { Text("Tree") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Layout Mode Section
            Text(
                text = "التخطيط / Layout",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = layoutMode == "list",
                    onClick = { onLayoutModeChanged("list") },
                    label = { Text("List") },
                    leadingIcon = { Icon(Icons.Default.List, contentDescription = null) }
                )
                FilterChip(
                    selected = layoutMode == "grid",
                    onClick = { onLayoutModeChanged("grid") },
                    label = { Text("Grid") },
                    leadingIcon = { Icon(Icons.Default.GridView, contentDescription = null) }
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Checkbox(
                    checked = onlyForFolderList,
                    onCheckedChange = { onOnlyForFolderListChanged(it) }
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Only for folder list",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Visible Fields Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "الحقول المعروضة / Visible Fields",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                IconButton(onClick = { isFieldsExpanded = !isFieldsExpanded }) {
                    Icon(
                        imageVector = if (isFieldsExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Toggle Fields"
                    )
                }
            }

            AnimatedVisibility(visible = isFieldsExpanded) {
                val allFields = listOf("Path", "Full Name", "Total Duration", "Total Media", "Date", "Folder Size")
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    allFields.forEach { field ->
                        val isSelected = visibleFields.contains(field)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                val newSet = visibleFields.toMutableSet()
                                if (isSelected) newSet.remove(field) else newSet.add(field)
                                onVisibleFieldsChanged(newSet)
                            },
                            label = { Text(field) },
                            leadingIcon = if (isSelected) {
                                { Icon(Icons.Default.Check, contentDescription = null) }
                            } else null
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("تم / Done")
            }
        }
    }
}
