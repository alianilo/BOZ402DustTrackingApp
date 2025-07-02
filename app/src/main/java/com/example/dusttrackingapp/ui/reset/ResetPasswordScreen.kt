package com.example.dusttrackingapp.ui.reset

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetPasswordScreen(
    vm: ResetPasswordViewModel = viewModel(),
    onClose: () -> Unit = {}
) {
    val state = vm.state
    val ctx   = LocalContext.current

    LaunchedEffect(state.error, state.info) {
        state.error?.let { Toast.makeText(ctx, it, Toast.LENGTH_LONG).show() }
        state.info ?.let { Toast.makeText(ctx, it, Toast.LENGTH_LONG).show(); onClose() }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Geri")
                    }
                },
                title = { Text("Şifre Sıfırlama") }
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
            OutlinedTextField(
                value = state.email,
                onValueChange = { vm.onEvent(ResetPasswordEvent.EmailChanged(it)) },
                label = { Text("E-posta") },
                isError = state.error != null && state.email.isBlank(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = { vm.onEvent(ResetPasswordEvent.Submit) },
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Sıfırlama E-postası Gönder") }

            if (state.isLoading) {
                Spacer(Modifier.height(24.dp))
                CircularProgressIndicator()
            }
        }
    }
}
