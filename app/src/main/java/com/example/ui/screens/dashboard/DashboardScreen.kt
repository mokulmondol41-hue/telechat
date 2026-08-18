package com.example.ui.screens.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.MarkChatUnread
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.PowerSettingsNew
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.SmartToy
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.local.entity.BotEntity
import com.example.ui.components.AppBottomNavigationBar
import com.example.ui.components.BotAvatar
import com.example.ui.components.BotCardSkeleton
import com.example.ui.components.EmptyStateView
import com.example.ui.components.QuickBroadcastCard
import com.example.ui.components.StatCard
import com.example.ui.components.StatusDot
import com.example.ui.components.UnreadBadgePill
import com.example.ui.theme.AppCanvasBg
import com.example.ui.theme.DeepBlueDark
import com.example.ui.theme.DeepBluePrimary
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.LightBlueBg
import com.example.ui.theme.LightBlueCard
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateBorderSubtle
import com.example.ui.theme.SlateInactive
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SurfaceCardBorder
import com.example.ui.theme.TextGray
import com.example.ui.theme.TextNavyDark
import com.example.ui.theme.TextNavyMuted
import com.example.ui.theme.White
import com.example.ui.viewmodel.BotViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: BotViewModel,
    onNavigateToAddBot: () -> Unit,
    onNavigateToChat: (Long) -> Unit,
    onNavigateToSettings: (Long) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showProfileSheet by remember { mutableStateOf(false) }
    var showNotificationsSheet by remember { mutableStateOf(false) }
    var showBroadcastDialog by remember { mutableStateOf(false) }
    var currentNavTab by remember { mutableStateOf("HOME") }

    val profileSheetState = rememberModalBottomSheetState()
    val notifSheetState = rememberModalBottomSheetState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = AppCanvasBg,
        topBar = {
            Surface(
                color = White,
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder),
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Brand Logo + Title
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.testTag("app_header_brand")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(DeepBluePrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ChatBubbleOutline,
                                contentDescription = "TeleChat Logo",
                                tint = White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "TeleChat",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextNavyDark,
                            letterSpacing = (-0.5).sp
                        )
                    }

                    // Notification bell + Profile avatar
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .clickable { showNotificationsSheet = true }
                                .testTag("notification_bell_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (uiState.unreadCount > 0) Icons.Outlined.NotificationsActive else Icons.Outlined.Notifications,
                                contentDescription = "Notifications",
                                tint = if (uiState.unreadCount > 0) DeepBluePrimary else TextGray,
                                modifier = Modifier.size(24.dp)
                            )
                            if (uiState.unreadCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(ErrorRed)
                                        .align(Alignment.TopEnd)
                                )
                            }
                        }

                        // User Profile Pill (JD)
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(LightBlueBg)
                                .border(2.dp, White, CircleShape)
                                .shadow(2.dp, CircleShape)
                                .clickable { showProfileSheet = true }
                                .testTag("profile_avatar_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "JD",
                                color = DeepBluePrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            AppBottomNavigationBar(
                selectedTab = currentNavTab,
                onTabSelected = { tab ->
                    currentNavTab = tab
                    if (tab == "CHATS" && uiState.bots.isNotEmpty()) {
                        onNavigateToChat(uiState.bots.first().id)
                    } else if (tab == "SETTINGS" && uiState.bots.isNotEmpty()) {
                        onNavigateToSettings(uiState.bots.first().id)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddBot,
                containerColor = DeepBluePrimary,
                contentColor = White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .size(56.dp)
                    .shadow(8.dp, RoundedCornerShape(16.dp))
                    .testTag("add_bot_fab")
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add New Bot",
                    tint = White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(6.dp))

                // Stats Dashboard Cards Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Active Bots",
                        value = "${uiState.bots.count { it.isOnline }}",
                        icon = Icons.Outlined.SmartToy,
                        iconColor = DeepBluePrimary,
                        bgColor = LightBlueBg,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Live Chats",
                        value = "${uiState.activeChats}",
                        icon = Icons.Outlined.Forum,
                        iconColor = SuccessGreen,
                        bgColor = LightBlueBg,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                // Search Bar
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.onSearchQueryChanged(it) },
                    placeholder = { Text("Search bots by name or @username...", color = TextGray, fontSize = 14.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search",
                            tint = DeepBluePrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        if (uiState.searchQuery.isNotBlank()) {
                            IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                                Icon(
                                    imageVector = Icons.Filled.Clear,
                                    contentDescription = "Clear search",
                                    tint = TextGray,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DeepBluePrimary,
                        unfocusedBorderColor = SlateBorder,
                        focusedContainerColor = White,
                        unfocusedContainerColor = White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, shape = RoundedCornerShape(16.dp))
                        .testTag("search_bots_input")
                )
            }

            item {
                // Section Header: My Active Bots with Running Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "My Active Bots",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextNavyDark
                    )

                    // Badge: "3 BOTS RUNNING"
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(LightBlueBg)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${uiState.bots.count { it.isOnline }} BOTS RUNNING",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepBluePrimary,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }

            if (uiState.isLoading) {
                items(3) {
                    BotCardSkeleton()
                }
            } else if (uiState.bots.isEmpty()) {
                item {
                    EmptyStateView(
                        title = if (uiState.searchQuery.isNotBlank()) "No matching bots found" else "No Telegram bots connected",
                        subtitle = if (uiState.searchQuery.isNotBlank()) "Try another keyword or reset the search." else "Connect your first Telegram bot with an API token to start chatting live.",
                        icon = Icons.Outlined.SmartToy,
                        actionButtonText = if (uiState.searchQuery.isBlank()) "Connect Telegram Bot" else "Reset Search",
                        onActionClick = {
                            if (uiState.searchQuery.isBlank()) {
                                onNavigateToAddBot()
                            } else {
                                viewModel.onSearchQueryChanged("")
                            }
                        }
                    )
                }
            } else {
                items(uiState.bots, key = { it.id }) { bot ->
                    ProfessionalBotCardItem(
                        bot = bot,
                        onOpenChat = { onNavigateToChat(bot.id) },
                        onOpenSettings = { onNavigateToSettings(bot.id) },
                        onToggleOnline = { isOnline -> viewModel.toggleBotOnline(bot.id, isOnline) }
                    )
                }
            }

            item {
                // Quick Broadcast Card (dashed border, light blue)
                QuickBroadcastCard(
                    onBroadcastClick = {
                        if (uiState.bots.isNotEmpty()) {
                            showBroadcastDialog = true
                        } else {
                            onNavigateToAddBot()
                        }
                    },
                    subscriberCount = 2450
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Quick Broadcast Modal Dialog
    if (showBroadcastDialog) {
        var broadcastMsg by remember { mutableStateOf("") }
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
                        text = "Quick Broadcast",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextNavyDark
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Send an announcement to 2,450 subscribers across all connected bots.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGray
                    )
                    OutlinedTextField(
                        value = broadcastMsg,
                        onValueChange = { broadcastMsg = it },
                        placeholder = { Text("Enter broadcast message...") },
                        minLines = 3,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showBroadcastDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepBluePrimary),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Send to All")
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

    // Profile Bottom Sheet
    if (showProfileSheet) {
        ModalBottomSheet(
            onDismissRequest = { showProfileSheet = false },
            sheetState = profileSheetState,
            containerColor = White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(LightBlueBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "JD",
                            color = DeepBluePrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "John Doe",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextNavyDark
                        )
                        Text(
                            text = "johndoe@telechat.io",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextGray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedButton(
                    onClick = {
                        showProfileSheet = false
                        onLogout()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("logout_button"),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ErrorRed.copy(alpha = 0.5f))
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PowerSettingsNew,
                        contentDescription = "Logout",
                        tint = ErrorRed,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Sign Out",
                        style = MaterialTheme.typography.labelLarge,
                        color = ErrorRed,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Notifications Bottom Sheet
    if (showNotificationsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showNotificationsSheet = false },
            sheetState = notifSheetState,
            containerColor = White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = "Activity & Notifications",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextNavyDark
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "• Support Bot auto-replied to Sarah Connor (2m ago)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextNavyDark
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "• Broadcast completed: 142 messages delivered (1h ago)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextGray
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { showNotificationsSheet = false },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DeepBluePrimary)
                ) {
                    Text("Close")
                }
            }
        }
    }
}

@Composable
private fun ProfessionalBotCardItem(
    bot: BotEntity,
    onOpenChat: () -> Unit,
    onOpenSettings: () -> Unit,
    onToggleOnline: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onOpenChat)
            .shadow(3.dp, shape = RoundedCornerShape(20.dp), ambientColor = Color(0x0F0A1931))
            .testTag("bot_card_${bot.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Main Top Row: Avatar + Info + Unread Badge & Time
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar (56dp circle)
                BotAvatar(
                    name = bot.botName,
                    size = 56.dp,
                    isOnline = bot.isOnline,
                    customColor = if (bot.id % 2L == 0L) DeepBlueDark else DeepBluePrimary
                )

                Spacer(modifier = Modifier.width(14.dp))

                // Name, Online dot, Username
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = bot.botName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextNavyDark,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        StatusDot(isOnline = bot.isOnline)
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "@${bot.username}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGray
                    )
                }

                // Right column: Badge + status
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    UnreadBadgePill(count = if (bot.id == 1L) 12 else 0)
                    Text(
                        text = if (bot.isOnline) "Active" else "Idle",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextGray,
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons: Live Chat & Settings
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onOpenChat,
                    modifier = Modifier
                        .weight(1.3f)
                        .height(42.dp)
                        .testTag("open_live_chat_button_${bot.id}"),
                    shape = RoundedCornerShape(21.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DeepBluePrimary,
                        contentColor = White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ChatBubbleOutline,
                        contentDescription = "Chat",
                        modifier = Modifier.size(16.dp),
                        tint = White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Live Chat",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = White
                    )
                }

                OutlinedButton(
                    onClick = onOpenSettings,
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                        .testTag("open_bot_settings_button_${bot.id}"),
                    shape = RoundedCornerShape(21.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = LightBlueBg,
                        contentColor = DeepBlueDark
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DeepBluePrimary.copy(alpha = 0.25f))
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = "Settings",
                        modifier = Modifier.size(16.dp),
                        tint = DeepBlueDark
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = DeepBlueDark
                    )
                }
            }
        }
    }
}
