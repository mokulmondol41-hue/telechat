package com.example.ui.screens.settings

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.Message
import androidx.compose.material.icons.outlined.PowerOff
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material.icons.outlined.SmartToy
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entity.KeywordReplyEntity
import com.example.ui.components.BotAvatar
import com.example.ui.theme.AppCanvasBg
import com.example.ui.theme.ChatBubbleOutgoing
import com.example.ui.theme.ChatBubbleOutgoingText
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
import com.example.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BotSettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit,
    onBotDeleted: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var welcomeText by remember { mutableStateOf("") }
    var offlineText by remember { mutableStateOf("") }
    var isOfflineReplyEnabled by remember { mutableStateOf(true) }

    var showAddKeywordDialog by remember { mutableStateOf(false) }
    var showBroadcastDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var saveFeedback by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(uiState.bot) {
        uiState.bot?.let { bot ->
            welcomeText = bot.welcomeMessage
            offlineText = bot.offlineReplyMessage
            isOfflineReplyEnabled = bot.isOfflineReplyEnabled
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = AppCanvasBg,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Bot Settings",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextNavyDark
                        )
                        Text(
                            text = uiState.bot?.botName ?: "Telegram Bot",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextGray
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("settings_back_button")
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
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Bot Info Summary Header
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = White),
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BotAvatar(name = uiState.bot?.botName ?: "Bot", size = 52.dp)
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = uiState.bot?.botName ?: "Loading...",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextNavyDark
                        )
                        Text(
                            text = "@${uiState.bot?.username ?: "bot"}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = DeepBluePrimary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            if (saveFeedback != null) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFDCFCE7),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = null,
                            tint = SuccessGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = saveFeedback ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF166534),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Section 1: Welcome Message with Preview
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = White),
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Message,
                            contentDescription = null,
                            tint = DeepBluePrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Welcome Message",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextNavyDark
                        )
                    }
                    Text(
                        text = "Automatically sent to customer when they click /start in Telegram",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGray,
                        modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                    )

                    OutlinedTextField(
                        value = welcomeText,
                        onValueChange = { welcomeText = it },
                        minLines = 3,
                        maxLines = 5,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DeepBluePrimary,
                            unfocusedBorderColor = SurfaceCardBorder,
                            focusedContainerColor = AppCanvasBg,
                            unfocusedContainerColor = AppCanvasBg
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("settings_welcome_msg_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Live Preview Bubble
                    Text(
                        text = "Live Telegram Preview:",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextGray,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = ChatBubbleOutgoing,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = welcomeText.ifBlank { "Enter a welcome message above..." },
                            color = ChatBubbleOutgoingText,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            viewModel.updateWelcomeMessage(welcomeText) {
                                saveFeedback = "Welcome message saved successfully!"
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DeepBluePrimary),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Save Welcome Message")
                    }
                }
            }

            // Section 2: Offline Auto-Reply
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = White),
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.PowerOff,
                                contentDescription = null,
                                tint = DeepBluePrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Offline Auto-Reply",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextNavyDark
                            )
                        }

                        Switch(
                            checked = isOfflineReplyEnabled,
                            onCheckedChange = { isOfflineReplyEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = White,
                                checkedTrackColor = DeepBluePrimary
                            )
                        )
                    }

                    Text(
                        text = "Triggered when customers message your bot while offline",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGray,
                        modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                    )

                    OutlinedTextField(
                        value = offlineText,
                        onValueChange = { offlineText = it },
                        enabled = isOfflineReplyEnabled,
                        minLines = 2,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DeepBluePrimary,
                            unfocusedBorderColor = SurfaceCardBorder,
                            focusedContainerColor = AppCanvasBg,
                            unfocusedContainerColor = AppCanvasBg
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("settings_offline_msg_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            viewModel.updateOfflineReply(offlineText, isOfflineReplyEnabled) {
                                saveFeedback = "Offline reply settings saved!"
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DeepBluePrimary),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Save Offline Reply")
                    }
                }
            }

            // Section 3: Keyword Auto-Reply Manager
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = White),
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Key,
                                contentDescription = null,
                                tint = DeepBluePrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Keyword Auto-Replies",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextNavyDark
                            )
                        }

                        IconButton(
                            onClick = { showAddKeywordDialog = true },
                            modifier = Modifier.testTag("add_keyword_rule_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = "Add Keyword Rule",
                                tint = DeepBluePrimary
                            )
                        }
                    }

                    Text(
                        text = "Match incoming customer messages with instant automatic replies",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGray,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    if (uiState.keywordReplies.isEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = LightBlueCard,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "No keyword rules yet. Click + to add automatic triggers like 'pricing', 'refund', or 'hours'.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextNavyMuted,
                                modifier = Modifier.padding(14.dp)
                            )
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            uiState.keywordReplies.forEach { rule ->
                                KeywordRuleItem(
                                    rule = rule,
                                    onToggle = { isEnabled -> viewModel.toggleKeywordReply(rule.id, isEnabled) },
                                    onDelete = { viewModel.deleteKeywordReply(rule.id) }
                                )
                            }
                        }
                    }
                }
            }

            // Section 4: Broadcast Message Section (Megaphone icon)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = LightBlueBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, DeepBluePrimary.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(DeepBluePrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Campaign,
                                contentDescription = "Broadcast",
                                tint = White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Broadcast Announcement",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = DeepBlueDark
                            )
                            Text(
                                text = "Send mass message to all active subscribers",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextNavyMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { showBroadcastDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("open_broadcast_dialog_button"),
                        shape = RoundedCornerShape(23.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DeepBluePrimary,
                            contentColor = White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Campaign,
                            contentDescription = null,
                            tint = White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Compose & Send Broadcast",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (uiState.broadcasts.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Recent Broadcasts (${uiState.broadcasts.size})",
                            style = MaterialTheme.typography.labelSmall,
                            color = DeepBlueDark,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        uiState.broadcasts.take(2).forEach { b ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = White,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = b.messageText,
                                        style = MaterialTheme.typography.bodySmall,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "${b.recipientsCount} sent",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = SuccessGreen,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Section 5: Danger Zone (Red, trash icon)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                border = androidx.compose.foundation.BorderStroke(1.dp, ErrorRed.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Warning,
                            contentDescription = null,
                            tint = ErrorRed,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Danger Zone",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = ErrorRed
                        )
                    }

                    Text(
                        text = "Permanently remove this bot connection and delete all local chat history.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextNavyMuted,
                        modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
                    )

                    OutlinedButton(
                        onClick = { showDeleteConfirmDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("delete_bot_button"),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ErrorRed)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.DeleteOutline,
                            contentDescription = "Delete Bot",
                            tint = ErrorRed,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Remove Bot from Workspace",
                            style = MaterialTheme.typography.labelLarge,
                            color = ErrorRed,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Add Keyword Rule Dialog
    if (showAddKeywordDialog) {
        var newKeyword by remember { mutableStateOf("") }
        var newReplyText by remember { mutableStateOf("") }
        var matchType by remember { mutableStateOf("contains") }

        AlertDialog(
            onDismissRequest = { showAddKeywordDialog = false },
            title = {
                Text(
                    text = "Add Keyword Auto-Reply",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextNavyDark
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newKeyword,
                        onValueChange = { newKeyword = it },
                        label = { Text("Trigger Keyword") },
                        placeholder = { Text("e.g. pricing, help, hours") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = newReplyText,
                        onValueChange = { newReplyText = it },
                        label = { Text("Automated Reply Message") },
                        placeholder = { Text("Message sent when keyword triggers...") },
                        minLines = 3,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = "Match Type:",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextNavyDark,
                        fontWeight = FontWeight.Bold
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = matchType == "contains",
                            onClick = { matchType = "contains" },
                            colors = RadioButtonDefaults.colors(selectedColor = DeepBluePrimary)
                        )
                        Text("Contains keyword", style = MaterialTheme.typography.bodySmall)

                        Spacer(modifier = Modifier.width(12.dp))

                        RadioButton(
                            selected = matchType == "exact",
                            onClick = { matchType = "exact" },
                            colors = RadioButtonDefaults.colors(selectedColor = DeepBluePrimary)
                        )
                        Text("Exact match", style = MaterialTheme.typography.bodySmall)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newKeyword.isNotBlank() && newReplyText.isNotBlank()) {
                            viewModel.addKeywordReply(newKeyword, newReplyText, matchType)
                            showAddKeywordDialog = false
                            saveFeedback = "Keyword rule added successfully!"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepBluePrimary)
                ) {
                    Text("Add Rule")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddKeywordDialog = false }) {
                    Text("Cancel", color = TextGray)
                }
            },
            containerColor = White,
            shape = RoundedCornerShape(20.dp)
        )
    }

    // Broadcast Message Dialog
    if (showBroadcastDialog) {
        var broadcastMessage by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showBroadcastDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Campaign,
                        contentDescription = null,
                        tint = DeepBluePrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Broadcast Announcement",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextNavyDark
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "This announcement will be delivered to all connected chat users via @${uiState.bot?.username ?: "bot"}.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGray
                    )

                    OutlinedTextField(
                        value = broadcastMessage,
                        onValueChange = { broadcastMessage = it },
                        label = { Text("Broadcast Content") },
                        placeholder = { Text("e.g. 🎉 Special announcement! Check out our new release at...") },
                        minLines = 4,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (broadcastMessage.isNotBlank()) {
                            viewModel.sendBroadcast(broadcastMessage) {
                                showBroadcastDialog = false
                                saveFeedback = "Broadcast dispatched to all subscribers!"
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepBluePrimary)
                ) {
                    Text("Send Broadcast Now")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBroadcastDialog = false }) {
                    Text("Cancel", color = TextGray)
                }
            },
            containerColor = White,
            shape = RoundedCornerShape(20.dp)
        )
    }

    // Delete Confirmation Dialog
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = {
                Text(
                    text = "Remove Telegram Bot?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = ErrorRed
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to remove '${uiState.bot?.botName}'? All live conversation records and automated rules will be permanently deleted.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextNavyDark
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmDialog = false
                        viewModel.deleteBot {
                            onBotDeleted()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                ) {
                    Text("Delete Permanently")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Cancel", color = TextNavyDark)
                }
            },
            containerColor = White,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
private fun KeywordRuleItem(
    rule: KeywordReplyEntity,
    onToggle: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = LightBlueCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = DeepBluePrimary
                    ) {
                        Text(
                            text = rule.keyword,
                            color = White,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (rule.matchType == "exact") "Exact Match" else "Contains Word",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextGray
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = rule.replyText,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextNavyDark,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Switch(
                checked = rule.isEnabled,
                onCheckedChange = onToggle,
                modifier = Modifier.size(width = 36.dp, height = 20.dp),
                colors = SwitchDefaults.colors(
                    checkedThumbColor = White,
                    checkedTrackColor = DeepBluePrimary
                )
            )

            Spacer(modifier = Modifier.width(4.dp))

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete Rule",
                    tint = ErrorRed.copy(alpha = 0.7f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
