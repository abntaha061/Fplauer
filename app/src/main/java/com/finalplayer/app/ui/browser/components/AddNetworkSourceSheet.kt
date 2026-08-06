package com.finalplayer.app.ui.browser.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.finalplayer.app.domain.model.NetworkSource
import com.finalplayer.app.domain.model.NetworkSourceType
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNetworkSourceSheet(
    sheetState: SheetState,
    isTesting: Boolean,
    testResult: Result<Boolean>?,
    onDismiss: () -> Unit,
    onTestConnection: (NetworkSource) -> Unit,
    onSaveSource: (NetworkSource) -> Unit
) {
    var selectedType by remember { mutableStateOf(NetworkSourceType.SMB) }
    var displayName by remember { mutableStateOf("") }
    var host by remember { mutableStateOf("") }
    var portText by remember { mutableStateOf("") }
    var sharePath by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val buildSource = {
        val portInt = portText.toIntOrNull() ?: 0
        NetworkSource(
            id = UUID.randomUUID().toString(),
            type = selectedType,
            displayName = displayName.ifBlank { host },
            host = host,
            port = portInt,
            username = username.ifBlank { null },
            password = password.ifBlank { null },
            sharePath = sharePath.ifBlank { null }
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "إضافة مصدر شبكة جديد / Add Network Source",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Type selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedType == NetworkSourceType.SMB,
                    onClick = { selectedType = NetworkSourceType.SMB },
                    label = { Text("SMB") }
                )
                FilterChip(
                    selected = selectedType == NetworkSourceType.FTP,
                    onClick = { selectedType = NetworkSourceType.FTP },
                    label = { Text("FTP") }
                )
                FilterChip(
                    selected = selectedType == NetworkSourceType.HTTP_DIRECT,
                    onClick = { selectedType = NetworkSourceType.HTTP_DIRECT },
                    label = { Text("HTTP Direct") }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = displayName,
                onValueChange = { displayName = it },
                label = { Text("اسم العرض / Display Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = host,
                onValueChange = { host = it },
                label = { Text("العنوان / Host or IP") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = portText,
                onValueChange = { portText = it },
                label = { Text("المنفذ / Port (اختياري)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            if (selectedType == NetworkSourceType.SMB) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = sharePath,
                    onValueChange = { sharePath = it },
                    label = { Text("اسم المشاركة / Share Name (SMB)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("اسم المستخدم / Username") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("كلمة المرور / Password") },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Toggle password visibility"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Test Result feedback
            if (isTesting) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = "جاري اختبار الاتصال...", style = MaterialTheme.typography.bodyMedium)
                }
            } else if (testResult != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    if (testResult.isSuccess) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Success",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "✓ الاتصال ناجح / Connection successful",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = "Failure",
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "✗ فشل الاتصال: ${testResult.exceptionOrNull()?.localizedMessage ?: "خطأ غير معروف"}",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = { onTestConnection(buildSource()) },
                    enabled = host.isNotBlank() && !isTesting
                ) {
                    Text("اختبار الاتصال")
                }

                Spacer(modifier = Modifier.width(12.dp))

                Button(
                    onClick = {
                        val source = buildSource()
                        onSaveSource(source)
                    },
                    enabled = host.isNotBlank() && !isTesting
                ) {
                    Text("حفظ")
                }
            }
        }
    }
}
