package com.example.ui.screens.addbot

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AppCanvasBg
import com.example.ui.theme.DeepBlueDark
import com.example.ui.theme.DeepBluePrimary
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.LightBlueBg
import com.example.ui.theme.LightBlueCard
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SurfaceCardBorder
import com.example.ui.theme.TextGray
import com.example.ui.theme.TextNavyDark
import com.example.ui.theme.TextNavyMuted
import com.example.ui.theme.White
import com.example.ui.viewmodel.BotViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBotScreen(
    viewModel: BotViewModel,
    onNavigateBack: () -> Unit,
    onBotCreated: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var botName by remember { mutableStateOf("") }
    var botUsername by remember { mutableStateOf("") }
    var botToken by remember { mutableStateOf("") }
    var welcomeMessage by remember {
        mutableStateOf("Welcome to our support channel! How can we assist you today? Feel free to ask any question.")
    }
    var isTokenVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = AppCanvasBg,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Connect Telegram Bot",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextNavyDark
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("add_bot_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextNavyDark
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = White),
                modifier = Modifier.shadow(2.dp)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Helper guide Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = LightBlueBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, DeepBluePrimary.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(DeepBluePrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.HelpOutline,
                            contentDescription = null,
                            tint = White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "How to get your Bot Token:",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = DeepBlueDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "1. Open Telegram & search for @BotFather\n2. Send /newbot and choose a name & username\n3. Copy the HTTP API token provided by BotFather",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextNavyMuted,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // Input 1: Bot Display Name
            Column {
                Text(
                    text = "Bot Display Name",
                    style = MaterialTheme.typography.labelLarge,
                    color = TextNavyDark,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = botName,
                    onValueChange = {
                        botName = it
                        errorMessage = null
                    },
                    placeholder = { Text("e.g. Acme Support Bot", color = TextGray) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.SmartToy,
                            contentDescription = "Bot Name",
                            tint = DeepBluePrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DeepBluePrimary,
                        unfocusedBorderColor = SurfaceCardBorder,
                        focusedContainerColor = White,
                        unfocusedContainerColor = White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(1.dp, RoundedCornerShape(14.dp))
                        .testTag("add_bot_name_input")
                )
            }

            // Input 2: Bot Username
            Column {
                Text(
                    text = "Bot Username",
                    style = MaterialTheme.typography.labelLarge,
                    color = TextNavyDark,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = botUsername,
                    onValueChange = {
                        botUsername = it
                        errorMessage = null
                    },
                    placeholder = { Text("e.g. acme_support_bot", color = TextGray) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.AlternateEmail,
                            contentDescription = "Username",
                            tint = DeepBluePrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DeepBluePrimary,
                        unfocusedBorderColor = SurfaceCardBorder,
                        focusedContainerColor = White,
                        unfocusedContainerColor = White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(1.dp, RoundedCornerShape(14.dp))
                        .testTag("add_bot_username_input")
                )
            }

            // Input 3: Bot Token
            Column {
                Text(
                    text = "Bot API Token",
                    style = MaterialTheme.typography.labelLarge,
                    color = TextNavyDark,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = botToken,
                    onValueChange = {
                        botToken = it
                        errorMessage = null
                    },
                    placeholder = { Text("7283920194:AAFklw_...", color = TextGray) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Key,
                            contentDescription = "Token",
                            tint = DeepBluePrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { isTokenVisible = !isTokenVisible }) {
                            Icon(
                                imageVector = if (isTokenVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = "Toggle token visibility",
                                tint = TextGray,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    visualTransformation = if (isTokenVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DeepBluePrimary,
                        unfocusedBorderColor = SurfaceCardBorder,
                        focusedContainerColor = White,
                        unfocusedContainerColor = White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(1.dp, RoundedCornerShape(14.dp))
                        .testTag("add_bot_token_input")
                )
            }

            // Input 4: Welcome Message textarea
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Welcome Message",
                        style = MaterialTheme.typography.labelLarge,
                        color = TextNavyDark,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Sent on /start",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGray
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = welcomeMessage,
                    onValueChange = {
                        welcomeMessage = it
                        errorMessage = null
                    },
                    placeholder = { Text("Enter welcome text...", color = TextGray) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Message,
                            contentDescription = "Message",
                            tint = DeepBluePrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    minLines = 3,
                    maxLines = 5,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DeepBluePrimary,
                        unfocusedBorderColor = SurfaceCardBorder,
                        focusedContainerColor = White,
                        unfocusedContainerColor = White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(1.dp, RoundedCornerShape(14.dp))
                        .testTag("add_bot_welcome_msg_input")
                )
            }

            // Presets row
            Column {
                Text(
                    text = "Quick Presets",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextGray,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PresetChip(
                        title = "Support",
                        onClick = {
                            if (botName.isBlank()) botName = "Support Helpdesk"
                            welcomeMessage = "Hello! Welcome to our support channel. Type your question or /help to view commands."
                        }
                    )
                    PresetChip(
                        title = "E-Commerce",
                        onClick = {
                            if (botName.isBlank()) botName = "Store Assistant"
                            welcomeMessage = "Hi there! Track orders, browse discounts, or ask about returns. How can we help today?"
                        }
                    )
                    PresetChip(
                        title = "Community",
                        onClick = {
                            if (botName.isBlank()) botName = "Community Moderator"
                            welcomeMessage = "Welcome to our Telegram community! Please check our pinned rules and introduce yourself."
                        }
                    )
                }
            }

            if (errorMessage != null) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFEE2E2),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = errorMessage ?: "",
                        color = ErrorRed,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Save Bot Button (full width, blue, rounded-xl)
            Button(
                onClick = {
                    if (botName.isBlank()) {
                        errorMessage = "Please enter a Bot Name"
                    } else if (botUsername.isBlank()) {
                        errorMessage = "Please enter a Bot Username"
                    } else if (botToken.isBlank()) {
                        errorMessage = "Please enter a Telegram Bot Token"
                    } else {
                        isSubmitting = true
                        viewModel.addBot(
                            name = botName,
                            username = botUsername,
                            token = botToken,
                            welcomeMsg = welcomeMessage,
                            onSuccess = { createdId ->
                                isSubmitting = false
                                onBotCreated(createdId)
                            }
                        )
                    }
                },
                enabled = !isSubmitting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("save_bot_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DeepBluePrimary,
                    contentColor = White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = null,
                    tint = White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isSubmitting) "Connecting Bot..." else "Save Bot & Start Live Chat",
                    style = MaterialTheme.typography.labelLarge,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun PresetChip(
    title: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = LightBlueBg,
        border = androidx.compose.foundation.BorderStroke(1.dp, DeepBluePrimary.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.AutoAwesome,
                contentDescription = null,
                tint = DeepBluePrimary,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = DeepBlueDark,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
