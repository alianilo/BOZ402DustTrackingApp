package com.example.dusttrackingapp.ui.register

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    vm: RegisterViewModel = viewModel(),
    onSuccess: () -> Unit = {},
    onBack: () -> Unit = {}          // ← PARAMETRE ADI “onBack”
) {
    val state by remember { vm::state }

    val ctx = LocalContext.current
    LaunchedEffect(state.info, state.error) {
        state.error?.let { Toast.makeText(ctx, it, Toast.LENGTH_LONG).show() }
        state.info ?.let { Toast.makeText(ctx, it, Toast.LENGTH_LONG).show(); onSuccess() }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {              // ← KULLANIM
                        Icon(Icons.Filled.ArrowBack, "Geri")
                    }
                },
                title = { Text("Kayıt Ol") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 24.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            /* ———  TextField ve Button dosdoğru çalışıyor  ——— */
            OutlinedTextField(
                value = state.name,
                onValueChange = { vm.onEvent(RegisterEvent.NameChanged(it)) },
                label = { Text("İsim") },
                singleLine = true,
                isError = state.error != null && state.name.isBlank(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = state.email,
                onValueChange = { vm.onEvent(RegisterEvent.EmailChanged(it)) },
                label = { Text("E-posta") },
                singleLine = true,
                isError = state.error != null && state.email.isBlank(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = state.password,
                onValueChange = { vm.onEvent(RegisterEvent.PasswordChanged(it)) },
                label = { Text("Şifre") },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                isError = state.error != null && state.password.isBlank(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(24.dp))
            Button(
                onClick  = { vm.onEvent(RegisterEvent.Submit) },
                enabled  = !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Hesap Oluştur") }

            if (state.isLoading) {
                Spacer(Modifier.height(24.dp))
                CircularProgressIndicator()
            }
        }
    }
}
