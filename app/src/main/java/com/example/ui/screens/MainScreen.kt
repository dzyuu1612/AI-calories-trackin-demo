package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ActivityLog
import com.example.data.model.MealLog
import com.example.data.model.UserProfile
import com.example.ui.viewmodel.NutritionViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.launch
import android.widget.Toast

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import java.io.ByteArrayOutputStream
import java.io.InputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: NutritionViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Calorie Tracker", "Diet Planner", "AI Scanner", "AI Coach", "Settings")

    Scaffold(
        topBar = {
            val profile by viewModel.userProfile.collectAsStateWithLifecycle()
            Surface(
                color = MaterialTheme.colorScheme.background,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val initial = if (profile.name.isNotBlank()) profile.name.take(1).uppercase() else "U"
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Sleek Circular Avatar Badge
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = initial,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontSize = 16.sp
                            )
                        }
                        // Sleek Header Welcome Text
                        Column {
                            Text(
                                text = "Welcome back,",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = profile.name,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 17.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }

                    // Sleek settings icon button
                    IconButton(
                        onClick = { selectedTab = 4 },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(24.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Setup Settings",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF1E1E1E), // Matte Carbon Black taskbar
                tonalElevation = 8.dp,
                modifier = Modifier.height(72.dp)
            ) {
                tabs.forEachIndexed { index, title ->
                    val icon = when (index) {
                        0 -> Icons.Default.Home
                        1 -> Icons.Default.Menu
                        2 -> Icons.Default.Star  // AI Scanner (High tech Star AI / Spark symbol)
                        3 -> Icons.Default.Person // AI Coach (Virtual Coach Avatar)
                        else -> Icons.Default.Settings
                    }
                    val isSelected = selectedTab == index
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedTab = index },
                        icon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = title,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = title,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = MaterialTheme.colorScheme.primary, // Athletic orange!
                            indicatorColor = MaterialTheme.colorScheme.primary, // Orange active block!
                            unselectedIconColor = Color.LightGray.copy(alpha = 0.6f),
                            unselectedTextColor = Color.LightGray.copy(alpha = 0.5f)
                        )
                    )
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (selectedTab) {
                0 -> TrackerTab(viewModel, onNavigateToScanner = { selectedTab = 2 })
                1 -> AiPlannerTab(viewModel)
                2 -> ScannerTab(viewModel)
                3 -> ProCoachTab(viewModel)
                4 -> SettingsTab(viewModel)
            }
        }
    }
}

// --- TAB 1: CALORIE & WELLNESS TRACKER ---

@Composable
fun TrackerTab(viewModel: NutritionViewModel, onNavigateToScanner: () -> Unit) {
    val profile by viewModel.userProfile.collectAsStateWithLifecycle()
    val meals by viewModel.todayMeals.collectAsStateWithLifecycle()
    val syncedSteps by viewModel.summarySteps.collectAsStateWithLifecycle()
    val calBurned by viewModel.summaryCaloriesBurned.collectAsStateWithLifecycle()
    val calLogged by viewModel.summaryCaloriesLogged.collectAsStateWithLifecycle()

    val protein by viewModel.proteinLogged.collectAsStateWithLifecycle()
    val carbs by viewModel.carbsLogged.collectAsStateWithLifecycle()
    val fat by viewModel.fatLogged.collectAsStateWithLifecycle()

    var showAddMealDialog by remember { mutableStateOf(false) }

    // Toggle states for collapsible content to not overwhelm the user inside the home tab
    var isTrackerDetailsExpanded by remember { mutableStateOf(false) }
    var isHistoryExpanded by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcoming text with active scanner main focus
        item {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                Text(
                    text = "AI Calorie Tracker",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Track your daily vitals, macro ratios, and water intake using carbon-powered intelligence tools.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // --- PROACTIVE LINK BANNER TO STANDALONE TASKBAR SCANNER ---
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToScanner() },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary), // Matte athletic carbon black
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Search Scanner",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "OPTICAL AI CAMERA SCANNER",
                            fontWeight = FontWeight.Black,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.primary, // Pro athletic orange
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Tap to identify food and log macros instantly",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            lineHeight = 16.sp
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Go",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // --- COLLAPSIBLE 1: DAILY METABOLIC BALANCES & TARGETS (CIRCULAR METRIC ENGINE) ---
        item {
            val baseGoal = profile.baseCalorieGoal
            val netIntake = calLogged - calBurned
            val progressPercentage = if (baseGoal > 0) netIntake.toFloat() / baseGoal.toFloat() else 0f
            val percentInt = (progressPercentage * 100).toInt().coerceIn(0, 100)
            val calLeft = (baseGoal - netIntake).coerceAtLeast(0)

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isTrackerDetailsExpanded = !isTrackerDetailsExpanded },
                colors = CardDefaults.cardColors(containerColor = Color(0xFF121212)), // Sleek Matte Black Athletic card
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // Header title area
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = "Target",
                                    tint = MaterialTheme.colorScheme.primary, // Orange
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "METABOLIC COMPASS",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary, // Orange
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = if (isTrackerDetailsExpanded) "TAP TO COLLAPSE DETAILS" else "TAP FOR DETAILED MACROS",
                                    fontSize = 8.sp,
                                    color = Color.LightGray.copy(alpha = 0.6f),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Icon(
                            imageVector = if (isTrackerDetailsExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = if (isTrackerDetailsExpanded) "Collapse" else "Expand",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Concentric Calorie Intake & Left Progress Ring
                    Box(
                        modifier = Modifier
                            .size(170.dp)
                            .align(Alignment.CenterHorizontally),
                        contentAlignment = Alignment.Center
                    ) {
                        // Background track (muted white ring)
                        CircularProgressIndicator(
                            progress = { 1f },
                            modifier = Modifier.fillMaxSize(),
                            color = Color.White.copy(alpha = 0.1f),
                            strokeWidth = 14.dp,
                            strokeCap = StrokeCap.Round
                        )
                        // Foreground dynamic level (Vibrant Athletic Orange)
                        CircularProgressIndicator(
                            progress = { progressPercentage.coerceIn(0f, 1f) },
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.primary, // Orange
                            strokeWidth = 14.dp,
                            strokeCap = StrokeCap.Round
                        )

                        // Center stats
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = String.format("%,d", calLeft),
                                fontSize = 34.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                letterSpacing = (-0.5).sp
                            )
                            Text(
                                text = "kcal remaining",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.LightGray.copy(alpha = 0.7f)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFD32F2F), RoundedCornerShape(4.dp)) // Contrast Active Red
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "GOAL: $baseGoal kcal",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Premium Math Formula Board
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "LOGGED",
                                fontSize = 8.sp,
                                color = Color.LightGray.copy(alpha = 0.6f),
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "+$calLogged",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary // Orange
                            )
                            Text(
                                text = "kcal intake",
                                fontSize = 9.sp,
                                color = Color.LightGray.copy(alpha = 0.4f)
                            )
                        }

                        // Minus Divider
                        Text("-", color = Color.LightGray.copy(alpha = 0.3f), fontWeight = FontWeight.Bold, fontSize = 16.sp)

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "ACTIVE BURN",
                                fontSize = 8.sp,
                                color = Color.LightGray.copy(alpha = 0.6f),
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "-$calBurned",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFEF4444) // Active Red!
                            )
                            Text(
                                text = "kcal burned",
                                fontSize = 9.sp,
                                color = Color.LightGray.copy(alpha = 0.4f)
                            )
                        }

                        // Equals Divider
                        Text("=", color = Color.LightGray.copy(alpha = 0.3f), fontWeight = FontWeight.Bold, fontSize = 16.sp)

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "PROGRESS",
                                fontSize = 8.sp,
                                color = Color.LightGray.copy(alpha = 0.6f),
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "$percentInt%",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                text = "completed",
                                fontSize = 9.sp,
                                color = Color.LightGray.copy(alpha = 0.4f)
                            )
                        }
                    }

                    if (isTrackerDetailsExpanded) {
                        Spacer(modifier = Modifier.height(18.dp))
                        HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "MACRONUTRIENT RATIOS SUMMARY:",
                            fontWeight = FontWeight.Black,
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.primary, // Orange
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        val macroGoalP = (profile.weightKg * 1.8f).toInt()
                        val macroGoalC = 250
                        val macroGoalF = 70
                        
                        MacroProgressRow(name = "Protein Target", value = protein.toInt(), target = macroGoalP, unit = "g", color = Color(0xFF64B5F6))
                        Spacer(modifier = Modifier.height(10.dp))
                        MacroProgressRow(name = "Carbohydrates Target", value = carbs.toInt(), target = macroGoalC, unit = "g", color = MaterialTheme.colorScheme.primary) // Orange
                        Spacer(modifier = Modifier.height(10.dp))
                        MacroProgressRow(name = "Fats Target", value = fat.toInt(), target = macroGoalF, unit = "g", color = Color(0xFFEF4444)) // Red

                        Spacer(modifier = Modifier.height(18.dp))
                        Text(
                            text = "HYDRATION & LIFESTYLE INTEGRATIONS:",
                            fontWeight = FontWeight.Black,
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.primary, // Orange
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            // Steps card
                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.04f)),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Icon(imageVector = Icons.Default.Star, contentDescription = "", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(text = String.format("%,d steps", syncedSteps), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                                    Text(text = "Synced Steps", fontSize = 10.sp, color = Color.LightGray.copy(alpha = 0.6f))
                                }
                            }

                            // Water tracker card
                            val waterLogged by viewModel.waterLoggedTodayMl.collectAsStateWithLifecycle()
                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.04f)),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.Info, contentDescription = "", tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                                        Text(
                                            text = "+250ml",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            color = MaterialTheme.colorScheme.primary, // Orange Log
                                            modifier = Modifier
                                                .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                                .clickable { viewModel.addWater(250) }
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(text = "$waterLogged ml", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                                    Text(text = "Hydration level", fontSize = 10.sp, color = Color.LightGray.copy(alpha = 0.6f))
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- COLLAPSIBLE 2: MEAL LOGS ---
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isHistoryExpanded = !isHistoryExpanded },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.secondaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Logs",
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Column {
                                Text("Dietary Intake Diary", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("${meals.size} dynamic logs recorded today (${calLogged} kcal)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Icon(
                            imageVector = if (isHistoryExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = if (isHistoryExpanded) "Collapse" else "Expand",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (isHistoryExpanded) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = { showAddMealDialog = true },
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.height(28.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Food", modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Manual Input Entry", fontSize = 10.sp)
                            }
                        }

                        if (meals.isEmpty()) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Diary empty. Use scanner chips or search above to quickly document meals to metabolic log.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.outline,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                            )
                        } else {
                            Spacer(modifier = Modifier.height(10.dp))
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                meals.forEach { meal ->
                                    MealLogItem(meal = meal, onDelete = { viewModel.deleteMeal(meal) })
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddMealDialog) {
        AddMealDialog(
            onDismiss = { showAddMealDialog = false },
            onSave = { name, cal, pro, carb, fat, type ->
                viewModel.addMeal(name, cal, pro, carb, fat, type)
                showAddMealDialog = false
            }
        )
    }
}

@Composable
fun MacroProgressRow(name: String, value: Int, target: Int, unit: String, color: Color) {
    val progress = if (target > 0) value.toFloat() / target.toFloat() else 0f
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = name, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Text(text = "$value / $target $unit", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = MaterialTheme.colorScheme.outlineVariant
        )
    }
}

@Composable
fun MealLogItem(meal: MealLog, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SuggestionChip(
                        onClick = { },
                        label = { Text(meal.mealType, fontSize = 10.sp) },
                        modifier = Modifier.height(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = meal.foodName, fontWeight = FontWeight.Bold, fontSize = 15.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "P: ${meal.proteinG.toInt()}g  |  C: ${meal.carbsG.toInt()}g  |  F: ${meal.fatG.toInt()}g",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${meal.calories} kcal",
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(end = 6.dp)
                )
                IconButton(onClick = onDelete) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.7f))
                }
            }
        }
    }
}

@Composable
fun AddMealDialog(onDismiss: () -> Unit, onSave: (String, Int, Float, Float, Float, String) -> Unit) {
    var foodName by remember { mutableStateOf("") }
    var caloriesStr by remember { mutableStateOf("") }
    var proteinStr by remember { mutableStateOf("") }
    var carbsStr by remember { mutableStateOf("") }
    var fatStr by remember { mutableStateOf("") }
    var mealType by remember { mutableStateOf("Breakfast") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Meal Nutrition") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = foodName,
                    onValueChange = { foodName = it },
                    label = { Text("Food Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = caloriesStr,
                        onValueChange = { caloriesStr = it },
                        label = { Text("Calories (kcal)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = proteinStr,
                        onValueChange = { proteinStr = it },
                        label = { Text("Protein (g)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1.0f)
                    )
                    OutlinedTextField(
                        value = carbsStr,
                        onValueChange = { carbsStr = it },
                        label = { Text("Carbs (g)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1.0f)
                    )
                    OutlinedTextField(
                        value = fatStr,
                        onValueChange = { fatStr = it },
                        label = { Text("Fat (g)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1.0f)
                    )
                }

                Text("Meal Type Category", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val types = listOf("Breakfast", "Lunch", "Dinner", "Snack")
                    types.forEach { type ->
                        FilterChip(
                            selected = mealType == type,
                            onClick = { mealType = type },
                            label = { Text(type, fontSize = 11.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val cal = caloriesStr.toIntOrNull() ?: 0
                    val pro = proteinStr.toFloatOrNull() ?: 0f
                    val carb = carbsStr.toFloatOrNull() ?: 0f
                    val fat = fatStr.toFloatOrNull() ?: 0f
                    if (foodName.isNotBlank() && cal > 0) {
                        onSave(foodName, cal, pro, carb, fat, mealType)
                    }
                }
            ) {
                Text("Save Entry")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// --- TAB 2: VIETNAMESE METABOLIC DIETS & PLANS ---

data class VnFoodPreset(
    val VietnameseName: String,
    val EnglishName: String,
    val portionSize: String,
    val calories: Int,
    val protein: Float,
    val carbs: Float,
    val fat: Float,
    val category: String, // "High Protein", "Low Carb", "Balanced"
    val loseAdvice: String,
    val gainAdvice: String,
    val healthAdvice: String
)

@Composable
fun AiPlannerTab(viewModel: NutritionViewModel) {
    val aiRecommendation by viewModel.aiRecommendation.collectAsStateWithLifecycle()
    val isLoading by viewModel.isAiPlanningLoading.collectAsStateWithLifecycle()
    val profile by viewModel.userProfile.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    var selectedPlanCategory by remember { mutableStateOf("All") }
    var foodSearchQuery by remember { mutableStateOf("") }
    
    // Custom Grounded Prompt State
    var dietaryPreference by remember { mutableStateOf("Balanced") }
    var customSearchText by remember { mutableStateOf("") }

    // Pre-Configured Authentic Preset Food Items
    val vnFoodPresets = remember {
        listOf(
            VnFoodPreset(
                VietnameseName = "Avocado Chicken Sourdough Toast",
                EnglishName = "Avocado, Chicken, & Egg Toast",
                portionSize = "1 large slice (180g): 1 slice grilled artisanal sourdough bread, 80g tender shredded chicken, 40g crushed Haas avocado, olive oil drizzle.",
                calories = 360,
                protein = 24f,
                carbs = 32f,
                fat = 14f,
                category = "Balanced",
                loseAdvice = "Enjoy open-faced with half the avocado spread. Omit oil spray to save 80 calories.",
                gainAdvice = "Add an extra poached egg and double the sliced chicken breast for premium protein density.",
                healthAdvice = "The healthy monounsaturated fats in avocado optimize cholesterol and improve cardiovascular function."
            ),
            VnFoodPreset(
                VietnameseName = "Quinoa Seared Salmon Bowl",
                EnglishName = "Oven-Baked Atlantic Salmon Bowl",
                portionSize = "1 premium bowl (420g): 140g baked cold-water salmon fillet, 120g organic cooked red quinoa, steamed asparagus, lemon wedges.",
                calories = 560,
                protein = 38f,
                carbs = 44f,
                fat = 22f,
                category = "High Protein",
                loseAdvice = "Reduce quinoa portion to 80g. Swap salmon dressing with low-sodium lemon-herb spray.",
                gainAdvice = "Excellent post-workout selection. double the quinoa portion and add 1/4 sliced medium avocado.",
                healthAdvice = "Provides premium Omega-3 fatty acids (EPA/DHA) that lower arterial pressure and reduce system inflammation."
            ),
            VnFoodPreset(
                VietnameseName = "Diet Greek Feta Salad",
                EnglishName = "Mediterranean Greek Olive Salad",
                portionSize = "1 large bowl (320g): 150g diced English cucumbers, 100g Roma tomatoes, 40g Greek sheep milk feta cheese, Kalamata olives, virgin olive oil.",
                calories = 290,
                protein = 9f,
                carbs = 14f,
                fat = 22f,
                category = "Low Carb",
                loseAdvice = "Remarkable voluminous diet choice! Reduce feta cheese portion by half to slash fat intake.",
                gainAdvice = "Light nutrient-rich side. Always pair with 150g grilled chicken breast to build complete muscle amino pools.",
                healthAdvice = "High dietary antioxidant carotenoids. Creates a rich dietary fiber mesh that buffers sudden glucose spikes."
            ),
            VnFoodPreset(
                VietnameseName = "Active Fit Chicken & Jasmine Rice",
                EnglishName = "Baked Chicken Breast and Jasmine Rice",
                portionSize = "1 classic container (380g): 160g oven-baked dry-rubbed chicken breast, 140g steamed white jasmine rice, 80g crispy green beans.",
                calories = 490,
                protein = 42f,
                carbs = 54f,
                fat = 8f,
                category = "High Protein",
                loseAdvice = "Swap white jasmine rice with riced cauliflower or reduce rice serving size to 70g.",
                gainAdvice = "The gold standard of lean muscle synthesis! Consume standard portion daily after weight training.",
                healthAdvice = "Superb source of pure phosphorus and niacin. Extremely low saturated fats protect arterial lining."
            ),
            VnFoodPreset(
                VietnameseName = "Traditional Tender Beef Pho Soup",
                EnglishName = "Traditional Vietnamese Beef Pho Bowl",
                portionSize = "1 classic bowl (680g): 160g flat rice noodles, 90g organic beef flank slices, 420ml beef bone broth, fresh basil, lime, bean sprouts.",
                calories = 460,
                protein = 28f,
                carbs = 58f,
                fat = 12f,
                category = "Balanced",
                loseAdvice = "Consume only 1/2 of the flat noodles, eat generous bean sprouts, and leave 60% of broth to minimize sodium.",
                gainAdvice = "Highly hydrating and nutrient-rich. Ask for double lean beef slices to bolster protein intake.",
                healthAdvice = "Bone broth contains natural glutamine and collagen which soothe stomach linings and improve gut health."
            ),
            VnFoodPreset(
                VietnameseName = "Savory Pork Baguette (Bánh Mì)",
                EnglishName = "Authentic Vietnamese Banh Mi Sandwich",
                portionSize = "1 sandwich (180g): 1 single-portion baguette bread, 60g grilled lean charcoal pork, cucumbers, fresh cilantro, pickled radish.",
                calories = 510,
                protein = 19f,
                carbs = 62f,
                fat = 20f,
                category = "Balanced",
                loseAdvice = "Omit additional pate or mayo sauce. Add extra pickled carrots and cucumbers to maximize fiber.",
                gainAdvice = "Perfect fast calorie refueling sandwich. Enjoy two servings after demanding training.",
                healthAdvice = "Always request raw cilantro and veggies to build a healthy prebiotic microbiome buffer."
            ),
            VnFoodPreset(
                VietnameseName = "Sweet Teriyaki Tofu Stir-Fry",
                EnglishName = "Seared Sesame Tofu and Wild Black Rice",
                portionSize = "1 large serving (360g): 140g firm organic seared tofu blocks, 120g wild black rice, snow peas, sliced carrots, light tamari sauce.",
                calories = 410,
                protein = 18f,
                carbs = 52f,
                fat = 14f,
                category = "Low Carb",
                loseAdvice = "Swap wild rice with steamed broccoli florets. Minimize sweet teriyaki dressing.",
                gainAdvice = "Excellent plant-based recovery meal. Add 50g boiled edamame beans to complete the essential amino acid index.",
                healthAdvice = "Tofu is rich in isoflavones which help regulate cholesterol and preserve bone mineral density."
            ),
            VnFoodPreset(
                VietnameseName = "Antioxidant Organic Acai Bowl",
                EnglishName = "Fruit Acai Protein Breakfast Bowl",
                portionSize = "1 bowl (340g): 150g pureed frozen organic acai berries, 1/2 ripe banana, fresh organic strawberries, granola, unsweetened almond milk.",
                calories = 340,
                protein = 6f,
                carbs = 58f,
                fat = 10f,
                category = "Low Carb",
                loseAdvice = "Omit granola and honey drizzle. Limit food intake to 1/2 size to control simple sugar release.",
                gainAdvice = "Perfect quick carbohydrate replenishment. Stir in 1 scoop of clean whey protein isolate post-workout.",
                healthAdvice = "Contains unmatched high polyphenols and anthocyanins that bolster immune response and counter neural fatigue."
            )
        )
    }

    val filteredFoods = remember(foodSearchQuery, selectedPlanCategory) {
        vnFoodPresets.filter { food ->
            val matchSearch = food.VietnameseName.contains(foodSearchQuery, ignoreCase = true) ||
                    food.EnglishName.contains(foodSearchQuery, ignoreCase = true)
            val matchCategory = selectedPlanCategory == "All" || food.category == selectedPlanCategory
            matchSearch && matchCategory
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Tab header section
        item {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp)),
                colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFF121212)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("AI", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "DAILY DIET & MEAL PLANNING",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                text = "Curated scientific diet strategies and serving guides to perfectly reach your nutritional and weight goals.",
                                fontSize = 11.sp,
                                color = Color.LightGray.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        }

        // --- SECTION 1: ONE-CLICK NUTRITION PLAN METABOLIC TARGET CONTEXTS ---
        item {
            Column {
                Text(
                    text = "SELECT METABOLIC DIRECTIVE STRATEGY:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Strategy 1: Low-Carb Gym Diet
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                viewModel.updateUserProfile(
                                    name = profile.name,
                                    age = profile.age,
                                    weight = profile.weightKg,
                                    height = profile.heightCm,
                                    calorieGoal = 1500,
                                    waterGoal = 2500,
                                    stepsGoal = 10000
                                )
                                Toast
                                    .makeText(
                                        context,
                                        "Activated Low-Carb Calorie Deficit Plan (1,500 kcal limit active)!",
                                        Toast.LENGTH_LONG
                                    )
                                    .show()
                            },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.5.dp, Color(0xFFEF4444))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Keto / Low-Carb", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFFD32F2F))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("1,500 kcal target. Focuses on healthy fats, lean protein, salads, and calorie deficits.", fontSize = 10.sp, lineHeight = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("ACTIVATE →", fontWeight = FontWeight.Black, fontSize = 9.sp, color = Color(0xFFD32F2F))
                        }
                    }

                    // Strategy 2: Muscle Builder Diet
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                viewModel.updateUserProfile(
                                    name = profile.name,
                                    age = profile.age,
                                    weight = profile.weightKg,
                                    height = profile.heightCm,
                                    calorieGoal = 2400,
                                    waterGoal = 3200,
                                    stepsGoal = 12000
                                )
                                Toast
                                    .makeText(
                                        context,
                                        "Activated Lean Bulking Plan (2,400 kcal budget active)!",
                                        Toast.LENGTH_LONG
                                    )
                                    .show()
                            },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Lean Bulking", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("2,400 kcal target. Focuses on complex carbohydrates, rich protein, and positive nitrogen balance.", fontSize = 10.sp, lineHeight = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("ACTIVATE →", fontWeight = FontWeight.Black, fontSize = 9.sp, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }

        // --- SECTION 2: INTERACTIVE TRADITIONAL FOOD PRESETS & MEASUREMENT DIRECTORIES ---
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "GLOBAL MEAL & CALORIE REGISTRY:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Standard portion ratios",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
 
                // Custom search input for database
                OutlinedTextField(
                    value = foodSearchQuery,
                    onValueChange = { foodSearchQuery = it },
                    placeholder = { Text("Search salmon, avocado, steak, pho...", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 1,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    ),
                    leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "", modifier = Modifier.size(16.dp)) }
                )

                // Category Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val categories = listOf("All", "Balanced", "High Protein", "Low Carb")
                    categories.forEach { cat ->
                        val isSelected = selectedPlanCategory == cat
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedPlanCategory = cat },
                            label = { Text(cat, fontSize = 10.sp) },
                            modifier = Modifier.height(28.dp)
                        )
                    }
                }
            }
        }

        // List of filtered Vietnamese Food Presets
        if (filteredFoods.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No traditional Vietnamese dishes match search criteria.", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                }
            }
        } else {
            items(filteredFoods) { food ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        // Title header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = food.VietnameseName, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                                Text(text = food.EnglishName, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Box(
                                modifier = Modifier
                                    .background(
                                        when (food.category) {
                                            "Low Carb" -> Color(0xFFEF4444).copy(alpha = 0.1f)
                                            "High Protein" -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                            else -> MaterialTheme.colorScheme.secondaryContainer
                                        },
                                        RoundedCornerShape(6.dp)
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = food.category,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when (food.category) {
                                        "Low Carb" -> Color(0xFFEF4444)
                                        "High Protein" -> MaterialTheme.colorScheme.primary
                                        else -> MaterialTheme.colorScheme.secondary
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        
                        // Portion info
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.Info, contentDescription = "", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text("Standard Serving Composition (Meal Proportion) :", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(text = food.portionSize, fontSize = 11.sp, lineHeight = 14.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Macros Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("ENERGY", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${food.calories} kcal", fontWeight = FontWeight.Black, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("PROTEIN", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${food.protein.toInt()}g", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF1E1E1E))
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("CARBS", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${food.carbs.toInt()}g", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("FATS", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${food.fat.toInt()}g", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFFEF4444))
                            }
                        }

                        // Meal proportion representation bar graph
                        Spacer(modifier = Modifier.height(8.dp))
                        val totalMacros = (food.protein + food.carbs + food.fat).coerceAtLeast(1f)
                        val weightP = food.protein / totalMacros
                        val weightC = food.carbs / totalMacros
                        val weightF = food.fat / totalMacros
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                        ) {
                            Box(modifier = Modifier.fillMaxHeight().weight(weightP.coerceAtLeast(0.01f)).background(Color(0xFF1E1E1E)))
                            Box(modifier = Modifier.fillMaxHeight().weight(weightC.coerceAtLeast(0.01f)).background(MaterialTheme.colorScheme.primary))
                            Box(modifier = Modifier.fillMaxHeight().weight(weightF.coerceAtLeast(0.01f)).background(Color(0xFFEF4444)))
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                        Spacer(modifier = Modifier.height(8.dp))

                        // Consume Guides
                        Text("METABOLIC CONSUMPTION INSTRUCTIONS:", fontSize = 9.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("• FAT LOSS:", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Color(0xFFD32F2F))
                                Text(food.loseAdvice, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("• BULKING :", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = MaterialTheme.colorScheme.primary)
                                Text(food.gainAdvice, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("• SUGGESTION:", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Color(0xFF1E1E1E))
                                Text(food.healthAdvice, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Dynamic Log standard portion button
                        Button(
                            onClick = {
                                viewModel.addMeal(
                                    name = food.VietnameseName,
                                    calories = food.calories,
                                    protein = food.protein,
                                    carbs = food.carbs,
                                    fat = food.fat,
                                    mealType = "Lunch"
                                )
                                Toast.makeText(
                                    context,
                                    "Successfully Logged standard serving of ${food.VietnameseName} to diary!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(vertical = 4.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = "", modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Log Standard serving to diary", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // --- SECTION 3: GROUNDED ADVANCED AI DIET GENERATOR ---
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Star, contentDescription = "Spark", tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "DYNAMIC GROUNDED RECIPE BUILDER", fontWeight = FontWeight.Black, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Query Gemini Live to research macro equations, ingredients, or localized modifications for custom foods.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "Choose Diet Constraint", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val diets = listOf("Balanced", "Keto", "Vegan", "Low Carb")
                        diets.forEach { diet ->
                            FilterChip(
                                selected = dietaryPreference == diet,
                                onClick = { dietaryPreference = diet },
                                label = { Text(diet, fontSize = 10.sp) },
                                modifier = Modifier.weight(1f).height(28.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = customSearchText,
                        onValueChange = { customSearchText = it },
                        placeholder = { Text("What traditional dish or modifications are you looking for? (e.g. Vegetarian pho alternatives)", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            keyboardController?.hide()
                            viewModel.generateMealPlan(dietaryPreference, customSearchText)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Querying Live Indices...", fontSize = 11.sp)
                        } else {
                            Icon(imageVector = Icons.Default.Star, contentDescription = "Spark", modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Build custom recipe plan with Gemini", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Live analytical suggestions card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = "Output", tint = Color(0xFF2E7D32))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Gemini Custom Recipe Outputs", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    if (aiRecommendation.isBlank()) {
                        Text(
                            text = "Grounded recipes and customized meal strategies will populate here. Customize parameters above and tap Generate!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            fontSize = 11.sp
                        )
                    } else {
                        SelectionContainer {
                            Text(
                                text = aiRecommendation,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- TAB 3: PRO COACH CLINICAL NUTRITION & CALCULATION ---

@Composable
fun ProCoachTab(viewModel: NutritionViewModel) {
    val aiAdvice by viewModel.aiAdvice.collectAsStateWithLifecycle()
    val isLoading by viewModel.isAiAdviceLoading.collectAsStateWithLifecycle()

    var questionText by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    val prebuiltIdeas = listOf(
        "Is my protein targets aligned with muscle synthesis?",
        "Provide scientific fat reduction recommendations according to my TDEE.",
        "How can I optimize hydration to limit lactic acid build-up?"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- BIOCHEMICAL TDEE & BMI CALCULATOR IS FIRST ---
        item {
            TdeeBmiCalculatorCard(viewModel)
        }

        item {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp)),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "EXPERT CLINICAL NUTRITION ADVISOR",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFEF6C00)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Consults standard metabolic equations and utilizes Gemini's Thinking Chain to solve complex inquiries regarding your biometric stats.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Quick Analytical Inquires:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    prebuiltIdeas.forEach { idea ->
                        OutlinedButton(
                            onClick = {
                                questionText = idea
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(text = idea, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Start)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = questionText,
                        onValueChange = { questionText = it },
                        placeholder = { Text("Ask anything about your metabolic recovery...", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFEF6C00)
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            keyboardController?.hide()
                            if (questionText.isNotBlank()) {
                                viewModel.requestExpertNutritionAdvice(questionText)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF6C00)),
                        enabled = !isLoading,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Formulating Reasoning Chain...", fontSize = 11.sp)
                        } else {
                            Icon(imageVector = Icons.Default.Check, contentDescription = "Brain", modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Submit to High Thinking AI", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Info, contentDescription = "Strategy", tint = Color(0xFFE65100))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Clinical Reasoning Outputs", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    if (aiAdvice.isBlank()) {
                        Text(
                            text = "Expert analytical advice output will show up here. Select one of the quick suggestions above or write a custom biometric query!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            fontSize = 11.sp
                        )
                    } else {
                        SelectionContainer {
                            Text(
                                  text = aiAdvice,
                                  style = MaterialTheme.typography.bodyMedium,
                                  color = MaterialTheme.colorScheme.onSurface,
                                  fontSize = 11.sp,
                                  lineHeight = 15.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- TAB 4: PROFILE SETTINGS, WEARABLES SYNC FLOW & FIREBASE AUTH ---

@Composable
fun SettingsTab(viewModel: NutritionViewModel) {
    val profile by viewModel.userProfile.collectAsStateWithLifecycle()
    val isSignedIn by viewModel.isUserSignedIn.collectAsStateWithLifecycle(initialValue = false)
    val loadingAuth by viewModel.authLoading.collectAsStateWithLifecycle()
    val authError by viewModel.authError.collectAsStateWithLifecycle()
    val isRealFirebaseConfigured = viewModel.isRealFirebaseConfigured

    var syncBrand by remember { mutableStateOf("Fitbit Tracker") }
    val isSyncingWearable by viewModel.isWearableSyncing.collectAsStateWithLifecycle()
    val wearableStatus by viewModel.wearableSyncStatus.collectAsStateWithLifecycle()

    // Form states for profile edit
    var nameEdit by remember { mutableStateOf(profile.name) }
    var ageEdit by remember { mutableStateOf(profile.age.toString()) }
    var weightEdit by remember { mutableStateOf(profile.weightKg.toString()) }
    var heightEdit by remember { mutableStateOf(profile.heightCm.toString()) }
    var calGoalEdit by remember { mutableStateOf(profile.baseCalorieGoal.toString()) }
    var waterGoalEdit by remember { mutableStateOf(profile.waterGoalMl.toString()) }
    var stepGoalEdit by remember { mutableStateOf(profile.stepGoal.toString()) }

    var isEditState by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Firebase Cloud Sign-In Authentication Control Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSignedIn) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f)
                    else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(28.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "FIREBASE CLOUD PERSISTENCE",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = if (isSignedIn) "Secured Sign-in Sync Active" else "Offline fallbacks Local Storage Active",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                        Icon(
                            imageVector = if (isSignedIn) Icons.Default.Check else Icons.Default.Warning,
                            contentDescription = "Sync",
                            tint = if (isSignedIn) Color(0xFF388E3C) else Color(0xFFF1C40F)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (!isRealFirebaseConfigured) {
                        Text(
                            text = "Note: No configuration properties found in google-services.json context. Running local sandbox mode. Sign in to test local Firebase auth triggers.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    if (authError != null) {
                        Text(
                            text = authError!!,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    if (isSignedIn) {
                        Text(text = "Logged in as: ${profile.email}", fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { viewModel.signOut() },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text("Sign Out Secure Session")
                        }
                    } else {
                        Button(
                            onClick = { viewModel.signInAnonymously() },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !loadingAuth,
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            if (loadingAuth) {
                                CircularProgressIndicator(modifier = Modifier.size(18.dp))
                            } else {
                                Text("Sign In with Google Sandbox")
                            }
                        }
                    }
                }
            }
        }

        // Wearable Integration Dashboard Sync
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(28.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "WEARABLE FITNESS SYNC PANEL",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Detect Wearable Sensor Device Brand:", fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val brands = listOf("Fitbit Tracker", "Garmin Sync", "Apple Health", "Samsung Fitness")
                        brands.forEach { brand ->
                            FilterChip(
                                selected = syncBrand == brand,
                                onClick = { syncBrand = brand },
                                label = { Text(brand.split(" ").first(), fontSize = 11.sp) },
                                modifier = Modifier.weight(1.0f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.syncWithWearable(syncBrand) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                        enabled = !isSyncingWearable,
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        if (isSyncingWearable) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onTertiary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Querying Bluetooth Bluetooth LE link...")
                        } else {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = "Sync icon")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Sync steps now from $syncBrand")
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(12.dp)
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        Text(
                            text = wearableStatus,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Edit Profile Goals Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(28.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "USER PROFILE & TARGET GOALS",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary
                        )

                        TextButton(
                            onClick = {
                                if (isEditState) {
                                    // Save changes
                                    viewModel.updateUserProfile(
                                        name = nameEdit,
                                        age = ageEdit.toIntOrNull() ?: profile.age,
                                        weight = weightEdit.toFloatOrNull() ?: profile.weightKg,
                                        height = heightEdit.toFloatOrNull() ?: profile.heightCm,
                                        calorieGoal = calGoalEdit.toIntOrNull() ?: profile.baseCalorieGoal,
                                        waterGoal = waterGoalEdit.toIntOrNull() ?: profile.waterGoalMl,
                                        stepsGoal = stepGoalEdit.toIntOrNull() ?: profile.stepGoal
                                    )
                                } else {
                                    // Set states to match
                                    nameEdit = profile.name
                                    ageEdit = profile.age.toString()
                                    weightEdit = profile.weightKg.toString()
                                    heightEdit = profile.heightCm.toString()
                                    calGoalEdit = profile.baseCalorieGoal.toString()
                                    waterGoalEdit = profile.waterGoalMl.toString()
                                    stepGoalEdit = profile.stepGoal.toString()
                                }
                                isEditState = !isEditState
                            }
                        ) {
                            Text(if (isEditState) "Save" else "Edit")
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (isEditState) {
                        OutlinedTextField(
                            value = nameEdit,
                            onValueChange = { nameEdit = it },
                            label = { Text("Name") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = ageEdit,
                                onValueChange = { ageEdit = it },
                                label = { Text("Age (years)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = weightEdit,
                                onValueChange = { weightEdit = it },
                                label = { Text("Weight (kg)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = calGoalEdit,
                            onValueChange = { calGoalEdit = it },
                            label = { Text("Daily Intake Calories Target (kcal)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = waterGoalEdit,
                                onValueChange = { waterGoalEdit = it },
                                label = { Text("Water Goal (ml)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = stepGoalEdit,
                                onValueChange = { stepGoalEdit = it },
                                label = { Text("Steps Target") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    } else {
                        // Display clean profile values
                        ProfileRow(label = "FullName / Alias", value = profile.name)
                        ProfileRow(label = "Current Associated Identity", value = profile.email)
                        ProfileRow(label = "Age Profile", value = "${profile.age} years")
                        ProfileRow(label = "Body Weight Scale", value = "${profile.weightKg} kg")
                        ProfileRow(label = "Height Stature", value = "${profile.heightCm} cm")
                        ProfileRow(label = "Target Daily Calorie Budget", value = "${profile.baseCalorieGoal} kcal")
                        ProfileRow(label = "Water Target Budget", value = "${profile.waterGoalMl} ml")
                        ProfileRow(label = "Wearable Active Step Goal", value = "${profile.stepGoal} steps")
                    }
                }
            }
        }

        item {
            val weightHistory by viewModel.weightHistory.collectAsStateWithLifecycle()
            var newWeightText by remember { mutableStateOf("") }
            var weightLoggingError by remember { mutableStateOf<String?>(null) }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(28.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "BIOMETRIC WEIGHT TRACKER TIMELINE",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newWeightText,
                            onValueChange = {
                                newWeightText = it
                                weightLoggingError = null
                            },
                            label = { Text("Log Weight (kg)") },
                            placeholder = { Text("e.g. 73.5") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        Button(
                            onClick = {
                                val w = newWeightText.toFloatOrNull()
                                if (w != null && w > 20f && w < 300f) {
                                    val sdf = SimpleDateFormat("MMM dd", java.util.Locale.US)
                                    val todayStr = sdf.format(Date())
                                    viewModel.addWeightEntry(w, todayStr)
                                    newWeightText = ""
                                    weightLoggingError = null
                                } else {
                                    weightLoggingError = "Enter valid range (20 - 300 kg)"
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Add log")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Log")
                        }
                    }

                    weightLoggingError?.let { err ->
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(err, color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "WEIGHT DIARY FEED:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    if (weightHistory.isEmpty()) {
                        Text("No logged record yet.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    } else {
                        val currentWeight = weightHistory.lastOrNull()?.weight ?: 0f
                        val firstWeight = weightHistory.firstOrNull()?.weight ?: 0f
                        val diff = currentWeight - firstWeight
                        val diffStr = if (diff > 0) "+${String.format("%.1f", diff)} kg" else "${String.format("%.1f", diff)} kg"
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Net Weight Trend:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(diffStr, fontSize = 12.sp, fontWeight = FontWeight.Black, color = if (diff <= 0f) Color(0xFF2E7D32) else Color(0xFFC62828))
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            weightHistory.reversed().forEach { entry ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 8.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.Star, contentDescription = "Log date", tint = Color(0xFFFFB300), modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("${entry.weight} kg", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(entry.date, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        IconButton(
                                            onClick = { viewModel.deleteWeightEntry(entry) },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete entry", tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            val customUserPrompt by viewModel.customUserPrompt.collectAsStateWithLifecycle()
            val customSysInstruction by viewModel.customSystemInstruction.collectAsStateWithLifecycle()

            var localPromptEdit by remember(customUserPrompt) { mutableStateOf(customUserPrompt) }
            var localSysEdit by remember(customSysInstruction) { mutableStateOf(customSysInstruction) }
            var showPromptSuccessMsg by remember { mutableStateOf(false) }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(28.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "AI SCANNER SYSTEMS CUSTOM PROMPT",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Directly modify Gemini instruction prompts to customize nutrient extraction logic of scanned meals.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = localSysEdit,
                        onValueChange = { localSysEdit = it },
                        label = { Text("System Instruction Prompts") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = localPromptEdit,
                        onValueChange = { localPromptEdit = it },
                        label = { Text("User Context Formatting Instruction") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp),
                        maxLines = 10
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (showPromptSuccessMsg) {
                        Text(
                            "AI Custom Prompts applied successfully!",
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.updateCustomPrompts(localPromptEdit, localSysEdit)
                                showPromptSuccessMsg = true
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Apply Prompt", fontSize = 11.sp)
                        }
                        
                        OutlinedButton(
                            onClick = {
                                viewModel.resetCustomPromptsToDefault()
                                showPromptSuccessMsg = true
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Reset Default", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        item {
            val clipboardManager = LocalClipboardManager.current
            val context = LocalContext.current
            var jsonInputText by remember { mutableStateOf("") }
            var validationResultMsg by remember { mutableStateOf<String?>(null) }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(28.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "OFFLINE DATA PORTABILITY ENGINE",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Export or Import your local and cloud sync profiles, weight timeline charts, and daily meals log as a simple, human-readable JSON block.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val exported = viewModel.exportBackupJson()
                                clipboardManager.setText(AnnotatedString(exported))
                                Toast.makeText(context, "Full JSON Backup copied to clipboard!", Toast.LENGTH_LONG).show()
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                        ) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = "Export icon", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Export JSON", fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "IMPORT PREVIOUS BACKUP PORT:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = jsonInputText,
                        onValueChange = {
                            jsonInputText = it
                            validationResultMsg = null
                        },
                        label = { Text("Paste JSON Backup string here") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 10.sp, fontFamily = FontFamily.Monospace),
                        maxLines = 8,
                        placeholder = { Text("{\n  \"profile\": {...},\n  \"meals\": [...]\n}") }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    validationResultMsg?.let { msg ->
                        Text(
                            text = msg,
                            color = if (msg.contains("Successfully")) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Button(
                        onClick = {
                            val isOk = viewModel.importBackupJson(jsonInputText)
                            if (isOk) {
                                validationResultMsg = "Successfully validated and imported data! Your profile goals, weight journal timeline and nutrients logs have been updated."
                                jsonInputText = ""
                                Toast.makeText(context, "Backup Restored Successfully!", Toast.LENGTH_SHORT).show()
                            } else {
                                validationResultMsg = "Failed to parse JSON backup. Make sure the schema holds valid profile/meals objects."
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Validate & Hydrate Database", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontWeight = FontWeight.Normal, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
        Text(text = value, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp)
    }
}

// Simple SelectionContainer wrapper for Copying text
@Composable
fun SelectionContainer(content: @Composable () -> Unit) {
    androidx.compose.foundation.text.selection.SelectionContainer {
        content()
    }
}

@Composable
fun FoodCameraScannerCard(viewModel: NutritionViewModel, autoLaunchActive: Boolean = false) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val isScanning by viewModel.isScanningImage.collectAsStateWithLifecycle()
    val scanResult by viewModel.scannedFoodResult.collectAsStateWithLifecycle()
    val scanError by viewModel.scanError.collectAsStateWithLifecycle()

    var customMealType by remember { mutableStateOf("Lunch") }
    var showScanDetails by remember { mutableStateOf(false) }
    var isDebugPayloadExpanded by remember { mutableStateOf(false) }
    val lastRawResponse by viewModel.lastRawResponse.collectAsStateWithLifecycle()

    var showPresets by remember { mutableStateOf(false) }

    // Minimalist 1x1 valid pixel Base64 of JPEG, used for starting a real API request to Gemini
    val mockBase64 = "/9j/4AAQSkZJRgABAQEASABIAAD/2wBDAP//////////////////////////////////////////////////////////////////////////////////////wgALCAABAAEBAREA/8QAFBABAAAAAAAAAAAAAAAAAAAAAP/aAAgBAQABPxA="

    val rawPresets = listOf(
        Triple("Avocado Toast", "Slices of crispy sourdough toast topped with creamy mashed fresh avocado, organic cherry tomatoes, and a soft-boiled egg.", "Avocado Toast"),
        Triple("Chicken & Rice", "Oven-roasted chicken breast fillet served with steamed brown jasmine rice, sesame seeds, and steamed broccoli florets.", "Chicken & Rice"),
        Triple("Quinoa Salmon Bowl", "Pan-seared Atlantic salmon fillet on organic red quinoa, shredded cabbage, avocado slices, and light soy dressing.", "Salmon Bowl"),
        Triple("Greek Salad Feta", "Crisp cucumbers, vine-ripened tomatoes, Greek Kalamata olives, red onions, double feta cheese, and extra-virgin olive oil.", "Greek Salad"),
        Triple("Beef Pho Noodles", "Traditional flat-noodle beef broth soup, thinly sliced tender flank, bean sprouts, basil, and cilantro garnish.", "Beef Pho"),
        Triple("Fruit Acai Bowl", "Organic acai berry blend topped with almond milk, wild blueberries, strawberries, chia seeds, and gluten-free granola.", "Acai Bowl"),
        Triple("Baked Tofu Stir-Fry", "Golden-brown baked tofu cubes tossed with snow peas, bell peppers, broccoli, light tamari sauce, and wild black rice.", "Tofu Stir-fry"),
        Triple("Oatmeal & Berries", "Steel-cut oatmeal simmered in unsweetened almond milk, ripe banana slices, fresh raspberries, flax seeds, and honey drizzle.", "Oatmeal Bowl")
    )

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, outputStream)
            val base64 = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
            viewModel.scanFoodImage(base64)
            showScanDetails = true
        }
    }

    var hasAutoLaunched by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (autoLaunchActive && !hasAutoLaunched) {
            kotlinx.coroutines.delay(450)
            cameraLauncher.launch(null)
            hasAutoLaunched = true
        }
    }

    // Gallery selective launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                if (bitmap != null) {
                    val outputStream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 75, outputStream)
                    val base64 = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
                    viewModel.scanFoodImage(base64)
                    showScanDetails = true
                }
            } catch (e: Exception) {
                // handle error
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header block with elegant cobalt accent
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Scan icon",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    text = "AI OPTICAL CAMERA MEAL SCANNER",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Point your camera at any meal, snap a photo, or choose a sample option from our dataset to run real-time Gemini AI nutritional analysis.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Futuristic HUD Viewfinder Area with Compact Controls
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF121212))
                    .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Circular scanner reticle with orange accents
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .border(
                            BorderStroke(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .border(
                                BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Lens Scanner",
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                // Aesthetic overlay labels
                Text(
                    text = "ACTIVE VIEWPORT DETECTION",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White.copy(alpha = 0.4f),
                    letterSpacing = 1.sp,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 10.dp)
                )

                // Quick compact bar of controls at the bottom
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Small circular icon button: SELECT PHOTO
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.1f))
                            .clickable { galleryLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share, // Gallery Selector Symbol
                            contentDescription = "Select Photo",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(20.dp))

                    // Center main shutter: CAPTURE CAM
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .clickable { cameraLauncher.launch(null) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow, // shutter capture representation
                            contentDescription = "Capture Shutter",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(20.dp))

                    // Small circular icon button: SUGGEST / SAMPLES
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(if (showPresets) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.1f))
                            .clickable { showPresets = !showPresets },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite, // Sparkles/Suggestions Symbol representation
                            contentDescription = "Suggest Preset",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            AnimatedVisibility(visible = showPresets) {
                Column {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "TAP SAMPLES FOR INSTANT AI CALORIE SCAN SIMULATOR:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    // Horizontally scrolls through realistic food datasets
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        rawPresets.forEach { preset ->
                            FilterChip(
                                selected = false,
                                onClick = {
                                    viewModel.scanFoodImage(mockBase64)
                                    showScanDetails = true
                                    // Trigger live Gemini evaluation with the specific food context prompt
                                    coroutineScope.launch {
                                        val specPrompt = "Evaluate dish: ${preset.first}. Ingredients and details: ${preset.second}."
                                        val sysInstruct = "Extract estimated nutrients and calories for this meal under standard serving portions. Return only a strict JSON text: {dishName, calories, protein, carbs, fat, analysis}."
                                        try {
                                            val responseText = com.example.data.api.GeminiClient.getMealPlanWithSearch(specPrompt, sysInstruct)
                                            val cleanJson = responseText.replace(Regex("```json|```"), "").trim()
                                            val startIndex = cleanJson.indexOf('{')
                                            val endIndex = cleanJson.lastIndexOf('}')
                                            if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
                                                val jsonStr = cleanJson.substring(startIndex, endIndex + 1)
                                                val jsonObj = org.json.JSONObject(jsonStr)
                                                val dish = jsonObj.optString("dishName", preset.third)
                                                val cal = jsonObj.optInt("calories", 390)
                                                val pro = jsonObj.optDouble("protein", 22.0).toFloat()
                                                val carb = jsonObj.optDouble("carbs", 45.0).toFloat()
                                                val fat = jsonObj.optDouble("fat", 7.5).toFloat()
                                                val analysis = jsonObj.optString("analysis", preset.second)
                                                viewModel.setScannedFood(dish, cal, pro, carb, fat, analysis, responseText)
                                            } else {
                                                viewModel.setScannedFood(preset.third, 420, 24f, 48f, 10f, preset.second, "Local fallback format: $responseText")
                                            }
                                        } catch (e: Exception) {
                                            // Use local fallback on connection drops
                                            viewModel.setScannedFood(preset.third, 420, 24f, 48f, 10f, preset.second, "Local connection fallback: ${e.localizedMessage}")
                                        }
                                    }
                                },
                                label = { Text(preset.first, fontSize = 11.sp) },
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    }
                }
            }

            // Results and interaction sheet
            if (showScanDetails) {
                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                Spacer(modifier = Modifier.height(10.dp))

                if (isScanning) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary, modifier = Modifier.size(26.dp))
                        Text(
                            text = "Gemini is performing visual nutritional analysis...",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else if (scanError != null) {
                    Text(
                        text = scanError ?: "Failed to query Gemini API.",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                } else if (scanResult != null) {
                    scanResult?.let { food ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = food.dishName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "${food.calories} kcal",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Protein: ${food.protein.toInt()}g  |  Carbs: ${food.carbs.toInt()}g  |  Fats: ${food.fat.toInt()}g",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = food.analysis,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 15.sp
                            )

                            if (lastRawResponse.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { isDebugPayloadExpanded = !isDebugPayloadExpanded }
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "AI RAW PAYLOAD (DEBUG MODE)",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                    Icon(
                                        imageVector = if (isDebugPayloadExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                        contentDescription = "Expand raw payload",
                                        tint = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                if (isDebugPayloadExpanded) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color(0xFF263238), RoundedCornerShape(8.dp))
                                            .padding(8.dp)
                                    ) {
                                        SelectionContainer {
                                            Text(
                                                text = lastRawResponse,
                                                color = Color(0xFF00E676),
                                                fontSize = 10.sp,
                                                fontFamily = FontFamily.Monospace,
                                                lineHeight = 12.sp
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Choice of meal categories
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                listOf("Breakfast", "Lunch", "Dinner", "Snack").forEach { type ->
                                    val isSelected = customMealType == type
                                    FilledTonalButton(
                                        onClick = { customMealType = type },
                                        contentPadding = PaddingValues(horizontal = 6.dp),
                                        modifier = Modifier.height(26.dp).weight(1f),
                                        colors = ButtonDefaults.filledTonalButtonColors(
                                            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                            contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                        ),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(type, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))

                            Button(
                                onClick = {
                                    viewModel.addMeal(
                                        food.dishName,
                                        food.calories,
                                        food.protein,
                                        food.carbs,
                                        food.fat,
                                        customMealType
                                    )
                                    viewModel.clearScannedFood()
                                    showScanDetails = false
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Add meal to today's metabolic record", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TdeeBmiCalculatorCard(viewModel: NutritionViewModel) {
    val profile by viewModel.userProfile.collectAsStateWithLifecycle()
    val bmi by viewModel.userBmi.collectAsStateWithLifecycle()

    var genderIsMale by remember { mutableStateOf(true) }
    var activityMultiplierIndex by remember { mutableIntStateOf(1) } // Default Lightly Active

    val activityMultipliers = listOf(
        Pair("Sedentary", 1.2f),
        Pair("Lightly Active", 1.375f),
        Pair("Moderately Active", 1.55f),
        Pair("Very Active", 1.725f),
        Pair("Extra Active", 1.9f)
    )

    val currentMultiplier = activityMultipliers[activityMultiplierIndex].second
    val computedTdee = viewModel.calculateTdee(
        weight = profile.weightKg,
        height = profile.heightCm,
        age = profile.age,
        isMale = genderIsMale,
        activityMultiplier = currentMultiplier
    )

    // BMI Classifications and details
    val (bmiClass, bmiColor) = when {
        bmi <= 0f -> Pair("Configure profile metrics", MaterialTheme.colorScheme.onSurfaceVariant)
        bmi < 18.5f -> Pair("Underweight (Consolidatory diet recommended)", Color(0xFFFFB300))
        bmi < 25f -> Pair("Normal Weight (Healthy energy balance)", MaterialTheme.colorScheme.primary)
        bmi < 30f -> Pair("Overweight (Consolidated metabolic restriction planned)", Color(0xFFD32F2F))
        else -> Pair("Obese (Strategic caloric reduction recommended)", Color(0xFFEF4444))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "TDEE icon",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    text = "BIOCHEMICAL BMI & TDEE CALCULATOR",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Track your chemical Body Mass Index (BMI) and Total Daily Energy Expenditure (TDEE) directly mapped to your physiological profile details.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // BMI & TDEE split display
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("Physio BMI", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (bmi > 0f) String.format("%.1f", bmi) else "N/A",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = bmiColor
                        )
                        Text(bmiClass, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = bmiColor, lineHeight = 10.sp)
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("Metabolic TDEE", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = String.format("%,d", computedTdee),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text("Target burn value", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Sex selections
            Text("BIOLOGICAL GENDER REFERENCE:", fontWeight = FontWeight.Bold, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(Pair("Male (XY)", true), Pair("Female (XX)", false)).forEach { (label, isMale) ->
                    val isSelected = genderIsMale == isMale
                    FilledTonalButton(
                        onClick = { genderIsMale = isMale },
                        modifier = Modifier.weight(1f).height(32.dp),
                        contentPadding = PaddingValues(0.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Scrollable activity options
            Text("DAILY EXERCISE/WORKOUT INTENSITY:", fontWeight = FontWeight.Bold, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                activityMultipliers.forEachIndexed { index, pair ->
                    val isSelected = activityMultiplierIndex == index
                    FilterChip(
                        selected = isSelected,
                        onClick = { activityMultiplierIndex = index },
                        label = { Text("${pair.first} (${pair.second}x)", fontSize = 10.sp) },
                        shape = RoundedCornerShape(8.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Applying baseline calculations to VM
            Button(
                onClick = {
                    viewModel.updateUserProfile(
                        name = profile.name,
                        age = profile.age,
                        weight = profile.weightKg,
                        height = profile.heightCm,
                        calorieGoal = computedTdee,
                        waterGoal = profile.waterGoalMl,
                        stepsGoal = profile.stepGoal
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = "Synergy", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Lock computed TDEE as calorie goal", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ScannerTab(viewModel: NutritionViewModel) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                Text(
                    text = "AI Optical Meal Auditor",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary // Orange
                )
                Text(
                    text = "Audit dishes in real-time. Use the physical camera viewfinder or import a culinary snapshot from the gallery to trigger deep neural estimation of macros powered by Gemini.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        item {
            FoodCameraScannerCard(viewModel = viewModel, autoLaunchActive = true)
        }
    }
}
