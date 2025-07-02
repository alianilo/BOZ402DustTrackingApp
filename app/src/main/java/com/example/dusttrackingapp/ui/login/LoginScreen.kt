package com.example.dusttrackingapp.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import android.widget.Toast
import android.content.Intent
import com.example.dusttrackingapp.ui.register.RegisterActivity
import com.example.dusttrackingapp.ui.reset.ResetPasswordActivity

@Composable
fun LoginScreen(
    vm: LoginViewModel = viewModel(),
    onLoginSuccess: () -> Unit = {}
) {
    val state = vm.state
    val context = LocalContext.current
    val scope = rememberCoroutineScope()


    LaunchedEffect(state.error, state.info) {
        state.error?.let { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }
        state.info?.let  { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }
        if (state.info == "İşlem başarılı!") onLoginSuccess()

    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            Text(text = "Dust Tracking App", style = MaterialTheme.typography.headlineMedium)

            Spacer(Modifier.height(32.dp))

            OutlinedTextField(
                value = state.email,
                onValueChange = { vm.onEvent(LoginEvent.EmailChanged(it)) },
                label = { Text("E-posta") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = state.password,
                onValueChange = { vm.onEvent(LoginEvent.PasswordChanged(it)) },
                label = { Text("Şifre") },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = { vm.onEvent(LoginEvent.LoginClicked) },
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Giriş Yap") }

            Spacer(Modifier.height(8.dp))

            OutlinedButton(
                onClick = {
                    context.startActivity(
                        Intent(context, RegisterActivity::class.java)
                    )
                },
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Kayıt Ol") }

            TextButton(
                onClick = {
                    val ctx = context
                    ctx.startActivity(Intent(ctx, ResetPasswordActivity::class.java))
                },
                enabled = !state.isLoading
            ) { Text("Şifremi Unuttum") }

            if (state.isLoading) {
                Spacer(Modifier.height(24.dp))
                CircularProgressIndicator()
            }
        }
    }
}
