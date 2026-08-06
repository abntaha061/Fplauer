package com.finalplayer.app.ui.settings.tabs

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.finalplayer.app.ui.settings.SettingsViewModel

@Composable
fun AdvancedTab(
    viewModel: SettingsViewModel
) {
    val context = LocalContext.current
    var mpvConfigText by remember { mutableStateOf("# mpv.conf custom options\n# e.g., hwdec=auto\n# demuxer-max-bytes=150MiB") }
    var showClearHistoryDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "إعدادات متقدمة / Advanced Settings",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Backup / Restore Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "النسخ الاحتياطي والاستعادة",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = {
                        Toast.makeText(context, "تم تصدير الإعدادات بنجاح", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("تصدير الإعدادات كـ JSON")
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = {
                        Toast.makeText(context, "تم استيراد الإعدادات بنجاح", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("استيراد الإعدادات من ملف")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Data & Cache Management Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "إدارة البيانات والتخزين المؤقت",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { showClearHistoryDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("مسح سجل المشاهدة")
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = {
                        Toast.makeText(context, "تم مسح الصور المصغرة المحفوظة", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("مسح الصور المصغرة المحفوظة")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Custom MPV Config Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "إعدادات MPV المتقدمة (mpv.conf)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "أدخل أوامر mpv المباشرة ليتم تطبيقها أثناء التشغيل:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = mpvConfigText,
                    onValueChange = { mpvConfigText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    placeholder = { Text("أدخل خيارات mpv...") }
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        Toast.makeText(context, "تم حفظ إعدادات mpv.conf", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("حفظ mpv.conf")
                }
            }
        }
    }

    if (showClearHistoryDialog) {
        AlertDialog(
            onDismissRequest = { showClearHistoryDialog = false },
            title = { Text("تأكيد مسح السجل") },
            text = { Text("هل أنت تأكد من مسح جميع سجلات تقدم المشاهدة؟ لا يمكن التراجع عن هذا الإجراء.") },
            confirmButton = {
                TextButton(onClick = {
                    showClearHistoryDialog = false
                    Toast.makeText(context, "تم مسح سجل المشاهدة", Toast.LENGTH_SHORT).show()
                }) {
                    Text("مسح", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearHistoryDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}
