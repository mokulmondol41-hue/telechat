package com.example.ui.screens.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.DoneAll
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.SmartToy
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entity.ChatMessageEntity
import com.example.data.local.entity.ChatUserEntity
import com.example.ui.components.BotAvatar
import com.example.ui.components.EmptyStateView
import com.example.ui.components.StatusDot
import com.example.ui.components.TypingIndicatorBubble
import com.example.ui.components.UnreadBadge
import com.example.ui.theme.AppCanvasBg
import com.example.ui.theme.ChatBubbleIncoming
import com.example.ui.theme.ChatBubbleIncomingText
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
import com.example.ui.viewmodel.ChatViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveChatScreen(
    viewModel: ChatViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToSettings: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var inputText by remember { mutableStateOf("") }
    var showUserListSheet by remember { mutableStateOf(false) }
    var showSimulationDialog by remember { mutableStateOf(false) }
    var showAddUserDialog by remember { mutableStateOf(false) }
    var showOptionsMenu by remember { mutableStateOf(false) }
    val userListSheetState = rememberModalBottomSheetState()
    val listState = rememberLazyListState()
    val focusManager = LocalFocusManager.current

    // Auto-scroll to bottom when new messages arrive
    LaunchedEffect(uiState.messages.size, uiState.isTyping) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = AppCanvasBg,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { showUserListSheet = true }
                            .padding(vertical = 4.dp)
                    ) {
                        Box {
                            BotAvatar(
                                name = uiState.selectedUser?.displayName ?: uiState.bot?.botName ?: "Chat",
                                size = 40.dp,
                                customColor = if (uiState.selectedUser != null) {
                                    try {
                                        Color(android.graphics.Color.parseColor(uiState.selectedUser!!.avatarColorHex))
                                    } catch (e: Exception) {
                                        DeepBluePrimary
                                    }
                                } else DeepBluePrimary
                            )
                            StatusDot(
                                isOnline = uiState.selectedUser?.isOnline ?: (uiState.bot?.isOnline ?: true),
                                modifier = Modifier.align(Alignment.BottomEnd)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = uiState.selectedUser?.displayName ?: (uiState.bot?.botName ?: "Live Chat"),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextNavyDark,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Text(
                                text = if (uiState.selectedUser != null) {
                                    "TG: @${uiState.selectedUser!!.username.ifBlank { uiState.selectedUser!!.telegramUserId }} · Tap to switch"
                                } else {
                                    "@${uiState.bot?.username ?: "bot"}"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = DeepBluePrimary
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("chat_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextNavyDark
                        )
                    }
                },
                actions = {
                    // Switch/List Users icon
                    IconButton(
                        onClick = { showUserListSheet = true },
                        modifier = Modifier.testTag("chat_users_drawer_button")
                    ) {
                        Box {
                            Icon(
                                imageVector = Icons.Outlined.Forum,
                                contentDescription = "Conversations",
                                tint = DeepBluePrimary
                            )
                            val totalUnread = uiState.users.sumOf { it.unreadCount }
                            if (totalUnread > 0) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(ErrorRed)
                                        .align(Alignment.TopEnd)
                                )
                            }
                        }
                    }

                    // Test Simulator Button
                    IconButton(
                        onClick = { showSimulationDialog = true },
                        modifier = Modifier.testTag("simulate_message_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Science,
                            contentDescription = "Simulate Inbound Customer",
                            tint = SuccessGreen
                        )
                    }

                    // More Options dropdown
                    Box {
                        IconButton(onClick = { showOptionsMenu = true }) {
                            Icon(
                                imageVector = Icons.Outlined.MoreVert,
                                contentDescription = "More Options",
                                tint = TextNavyDark
                            )
                        }
                        DropdownMenu(
                            expanded = showOptionsMenu,
                            onDismissRequest = { showOptionsMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text(if (uiState.selectedUser?.isResolved == true) "Mark as Unresolved" else "Mark as Resolved")
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Outlined.CheckCircle,
                                        contentDescription = null,
                                        tint = SuccessGreen
                                    )
                                },
                                onClick = {
                                    showOptionsMenu = false
                                    viewModel.toggleResolvedStatus()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Add Test Customer") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Filled.PersonAdd,
                                        contentDescription = null,
                                        tint = DeepBluePrimary
                                    )
                                },
                                onClick = {
                                    showOptionsMenu = false
                                    showAddUserDialog = true
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Bot Settings") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Outlined.Settings,
                                        contentDescription = null,
                                        tint = TextNavyDark
                                    )
                                },
                                onClick = {
                                    showOptionsMenu = false
                                    uiState.bot?.id?.let { onNavigateToSettings(it) }
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Clear Chat History", color = ErrorRed) },
                                onClick = {
                                    showOptionsMenu = false
                                    viewModel.clearActiveChatHistory()
                                }
                            )
                        }
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
                .imePadding()
        ) {
            // Horizontal Customer Selector Pills for quick switching on mobile
            if (uiState.users.isNotEmpty()) {
                Surface(
                    color = White,
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 1.dp
                ) {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        items(uiState.users, key = { it.id }) { user ->
                            val isSelected = user.id == uiState.selectedUser?.id
                            UserSelectorChip(
                                user = user,
                                isSelected = isSelected,
                                onClick = { viewModel.selectUser(user.id) }
                            )
                        }
                    }
                }
            }

            // Main Message Feed Area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(AppCanvasBg)
            ) {
                if (uiState.selectedUser == null || uiState.messages.isEmpty()) {
                    EmptyStateView(
                        title = "No conversation selected",
                        subtitle = "Select a customer conversation or click the test tube icon at the top right to simulate incoming customer inquiries.",
                        icon = Icons.Outlined.Forum,
                        actionButtonText = "Simulate Customer Question",
                        onActionClick = { showSimulationDialog = true },
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        item {
                            // Chat start date pill
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = SurfaceCardBorder.copy(alpha = 0.6f)
                                ) {
                                    Text(
                                        text = "Today · Telegram Live Support",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextNavyMuted,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        items(uiState.messages, key = { it.id }) { message ->
                            ChatBubbleItem(message = message)
                        }

                        if (uiState.isTyping) {
                            item {
                                TypingIndicatorBubble(
                                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Quick Response Suggestions Row
            Surface(
                color = White,
                modifier = Modifier.fillMaxWidth(),
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder.copy(alpha = 0.5f))
            ) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        QuickReplyChip(
                            text = "👋 Hello! How can I help?",
                            onClick = { viewModel.sendAgentReply("Hello! How can I assist you today?") }
                        )
                    }
                    item {
                        QuickReplyChip(
                            text = "⏳ Looking into this...",
                            onClick = { viewModel.sendAgentReply("Let me check that on our system right now...") }
                        )
                    }
                    item {
                        QuickReplyChip(
                            text = "✅ Issue Resolved",
                            onClick = { viewModel.sendAgentReply("I have resolved your request! Feel free to reach out if you need anything else.") }
                        )
                    }
                }
            }

            // Bottom Message Input Bar (text field + send icon button)
            Surface(
                color = White,
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = {
                            Text(
                                text = "Reply as agent to ${uiState.selectedUser?.firstName ?: "customer"}...",
                                color = TextGray,
                                fontSize = 14.sp
                            )
                        },
                        singleLine = false,
                        maxLines = 4,
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DeepBluePrimary,
                            unfocusedBorderColor = SurfaceCardBorder,
                            focusedContainerColor = AppCanvasBg,
                            unfocusedContainerColor = AppCanvasBg
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(
                            onSend = {
                                if (inputText.isNotBlank()) {
                                    viewModel.sendAgentReply(inputText)
                                    inputText = ""
                                }
                            }
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_message_input")
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Deep Blue Send Button
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .shadow(2.dp, CircleShape)
                            .clip(CircleShape)
                            .background(
                                if (inputText.isNotBlank()) DeepBluePrimary else DeepBluePrimary.copy(alpha = 0.5f)
                            )
                            .clickable(enabled = inputText.isNotBlank()) {
                                if (inputText.isNotBlank()) {
                                    viewModel.sendAgentReply(inputText)
                                    inputText = ""
                                }
                            }
                            .testTag("send_message_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }

    // Conversations List Bottom Sheet (for selecting and managing all customer chats)
    if (showUserListSheet) {
        ModalBottomSheet(
            onDismissRequest = { showUserListSheet = false },
            sheetState = userListSheetState,
            containerColor = White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Customer Conversations",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextNavyDark
                    )
                    IconButton(onClick = { showAddUserDialog = true }) {
                        Icon(
                            imageVector = Icons.Filled.PersonAdd,
                            contentDescription = "Add Customer",
                            tint = DeepBluePrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Search user
                OutlinedTextField(
                    value = uiState.userSearchQuery,
                    onValueChange = { viewModel.setUserSearchQuery(it) },
                    placeholder = { Text("Search by name or handle...", color = TextGray, fontSize = 13.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = null,
                            tint = DeepBluePrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DeepBluePrimary,
                        unfocusedBorderColor = SurfaceCardBorder,
                        focusedContainerColor = AppCanvasBg,
                        unfocusedContainerColor = AppCanvasBg
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Filter tabs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = uiState.userFilter == "ALL",
                        onClick = { viewModel.setUserFilter("ALL") },
                        label = { Text("All (${uiState.users.size})") }
                    )
                    FilterChip(
                        selected = uiState.userFilter == "UNREAD",
                        onClick = { viewModel.setUserFilter("UNREAD") },
                        label = { Text("Unread") }
                    )
                    FilterChip(
                        selected = uiState.userFilter == "RESOLVED",
                        onClick = { viewModel.setUserFilter("RESOLVED") },
                        label = { Text("Resolved") }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(340.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.users, key = { it.id }) { user ->
                        val isSelected = user.id == uiState.selectedUser?.id
                        ConversationListItem(
                            user = user,
                            isSelected = isSelected,
                            onClick = {
                                viewModel.selectUser(user.id)
                                showUserListSheet = false
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Simulation Dialog (Test Inbound Customer Messages)
    if (showSimulationDialog) {
        AlertDialog(
            onDismissRequest = { showSimulationDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Science,
                        contentDescription = null,
                        tint = SuccessGreen,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Simulate Customer Query",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextNavyDark
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Select a prompt to simulate a customer messaging your Telegram bot. Keyword auto-replies and offline responses will trigger automatically:",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGray
                    )

                    SimulationPromptButton(
                        prompt = "How much does the Pro subscription cost?",
                        onClick = {
                            viewModel.simulateCustomerMessage("How much does the Pro subscription cost?")
                            showSimulationDialog = false
                        }
                    )
                    SimulationPromptButton(
                        prompt = "help",
                        onClick = {
                            viewModel.simulateCustomerMessage("help")
                            showSimulationDialog = false
                        }
                    )
                    SimulationPromptButton(
                        prompt = "I would like to request a refund for my order.",
                        onClick = {
                            viewModel.simulateCustomerMessage("I would like to request a refund for my order.")
                            showSimulationDialog = false
                        }
                    )
                    SimulationPromptButton(
                        prompt = "Can an agent talk to me right now?",
                        onClick = {
                            viewModel.simulateCustomerMessage("Can an agent talk to me right now?")
                            showSimulationDialog = false
                        }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showSimulationDialog = false }) {
                    Text("Cancel", color = DeepBluePrimary)
                }
            },
            containerColor = White,
            shape = RoundedCornerShape(20.dp)
        )
    }

    // Add Test Customer Dialog
    if (showAddUserDialog) {
        var newName by remember { mutableStateOf("") }
        var newHandle by remember { mutableStateOf("") }
        var initialInquiry by remember { mutableStateOf("Hi! I need help with an order.") }

        AlertDialog(
            onDismissRequest = { showAddUserDialog = false },
            title = {
                Text(
                    text = "Add Test Customer",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextNavyDark
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("Customer Name") },
                        placeholder = { Text("e.g. John Doe") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newHandle,
                        onValueChange = { newHandle = it },
                        label = { Text("Telegram Username") },
                        placeholder = { Text("e.g. jdoe_tg") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = initialInquiry,
                        onValueChange = { initialInquiry = it },
                        label = { Text("Initial Message") },
                        minLines = 2,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newName.isNotBlank()) {
                            viewModel.createTestCustomer(newName, newHandle, initialInquiry)
                            showAddUserDialog = false
                            showUserListSheet = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepBluePrimary)
                ) {
                    Text("Add Customer")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddUserDialog = false }) {
                    Text("Cancel", color = TextGray)
                }
            },
            containerColor = White,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
private fun ChatBubbleItem(message: ChatMessageEntity) {
    val isUser = message.isFromUser // true = customer, false = agent/bot
    val timeFormat = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    val formattedTime = remember(message.timestamp) { timeFormat.format(Date(message.timestamp)) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.Start else Arrangement.End
    ) {
        Column(
            modifier = Modifier.widthIn(max = 290.dp),
            horizontalAlignment = if (isUser) Alignment.Start else Alignment.End
        ) {
            Surface(
                shape = RoundedCornerShape(
                    topStart = 18.dp,
                    topEnd = 18.dp,
                    bottomStart = if (isUser) 4.dp else 18.dp,
                    bottomEnd = if (isUser) 18.dp else 4.dp
                ),
                color = if (isUser) ChatBubbleIncoming else ChatBubbleOutgoing,
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                    if (message.isAutoReply) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isUser) LightBlueCard else DeepBlueDark.copy(alpha = 0.4f),
                            modifier = Modifier.padding(bottom = 4.dp)
                        ) {
                            Text(
                                text = "AUTOMATED RULE",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isUser) DeepBluePrimary else White.copy(alpha = 0.9f),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(
                        text = message.text,
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (isUser) ChatBubbleIncomingText else ChatBubbleOutgoingText,
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(3.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                Text(
                    text = formattedTime,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    color = TextGray
                )
                if (!isUser) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Filled.DoneAll,
                        contentDescription = "Read",
                        tint = DeepBluePrimary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun UserSelectorChip(
    user: ChatUserEntity,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = if (isSelected) DeepBluePrimary else LightBlueBg,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) DeepBluePrimary else SurfaceCardBorder
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BotAvatar(
                name = user.displayName,
                size = 24.dp,
                customColor = if (isSelected) White else DeepBluePrimary
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = user.displayName.split(" ").firstOrNull() ?: user.displayName,
                style = MaterialTheme.typography.labelMedium,
                color = if (isSelected) White else TextNavyDark,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
            if (user.unreadCount > 0) {
                Spacer(modifier = Modifier.width(6.dp))
                UnreadBadge(count = user.unreadCount)
            }
        }
    }
}

@Composable
private fun ConversationListItem(
    user: ChatUserEntity,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = if (isSelected) LightBlueBg else White,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) DeepBluePrimary.copy(alpha = 0.5f) else SurfaceCardBorder
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                BotAvatar(
                    name = user.displayName,
                    size = 44.dp,
                    customColor = try {
                        Color(android.graphics.Color.parseColor(user.avatarColorHex))
                    } catch (e: Exception) {
                        DeepBluePrimary
                    }
                )
                StatusDot(
                    isOnline = user.isOnline,
                    modifier = Modifier.align(Alignment.BottomEnd)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = user.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextNavyDark,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (user.unreadCount > 0) {
                        UnreadBadge(count = user.unreadCount)
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = user.lastMessageText.ifBlank { "No messages yet" },
                    style = MaterialTheme.typography.bodySmall,
                    color = if (user.unreadCount > 0) TextNavyDark else TextGray,
                    fontWeight = if (user.unreadCount > 0) FontWeight.SemiBold else FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun QuickReplyChip(
    text: String,
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
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = DeepBlueDark,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun SimulationPromptButton(
    prompt: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = LightBlueCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, DeepBluePrimary.copy(alpha = 0.15f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Forum,
                contentDescription = null,
                tint = DeepBluePrimary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "\"$prompt\"",
                style = MaterialTheme.typography.bodyMedium,
                color = TextNavyDark,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
