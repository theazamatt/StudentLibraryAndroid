package com.example.studentlibrary.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.studentlibrary.ui.auth.DemoCredentials
import com.example.studentlibrary.ui.auth.UserRole

@Composable
fun LoginRoute(
    onLogin: (UserRole) -> Unit,
) {
    var selectedRoleName by rememberSaveable { mutableStateOf(UserRole.USER.name) }
    var password by rememberSaveable { mutableStateOf("") }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }
    val selectedRole = UserRole.valueOf(selectedRoleName)

    LoginScreen(
        selectedRole = selectedRole,
        password = password,
        errorMessage = errorMessage,
        onSelectRole = {
            selectedRoleName = it.name
            errorMessage = null
        },
        onPasswordChange = {
            password = it
            errorMessage = null
        },
        onLogin = {
            val validPassword =
                when (selectedRole) {
                    UserRole.USER -> DemoCredentials.UserPassword
                    UserRole.ADMIN -> DemoCredentials.AdminPassword
                }

            if (password == validPassword) {
                onLogin(selectedRole)
                password = ""
                errorMessage = null
            } else {
                errorMessage = "Неверный пароль для выбранной роли"
            }
        },
    )
}

@Composable
private fun LoginScreen(
    selectedRole: UserRole,
    password: String,
    errorMessage: String?,
    onSelectRole: (UserRole) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLogin: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Text(
                    text = "Student Library",
                    style = MaterialTheme.typography.headlineMedium,
                )
                Text(
                    text = "Войдите как пользователь или администратор",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Роль", style = MaterialTheme.typography.titleMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = selectedRole == UserRole.USER,
                            onClick = { onSelectRole(UserRole.USER) },
                            label = { Text("Пользователь") },
                        )
                        FilterChip(
                            selected = selectedRole == UserRole.ADMIN,
                            onClick = { onSelectRole(UserRole.ADMIN) },
                            label = { Text("Администратор") },
                        )
                    }
                }

                OutlinedTextField(
                    value = password,
                    onValueChange = onPasswordChange,
                    label = { Text("Пароль") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }

                Button(
                    onClick = onLogin,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Text("Войти")
                }
            }
        }
    }
}
