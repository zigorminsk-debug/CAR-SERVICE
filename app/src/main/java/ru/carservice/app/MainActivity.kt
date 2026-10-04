package ru.carservice.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.ElectricBolt
import androidx.compose.material.icons.outlined.EventAvailable
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.LocalGasStation
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.OpenInNew
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import kotlinx.coroutines.delay

private const val HOME = "home"
private const val CATALOG = "catalog"
private const val WORKS = "works"
private const val VEHICLE = "vehicle"
private const val ARTICLE = "article"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = true
        setContent { CarServiceTheme { CarServiceApp() } }
    }
}

@Composable
private fun CarServiceApp() {
    var screen by rememberSaveable { mutableStateOf(HOME) }
    var selectedVehicleId by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedSectionId by rememberSaveable { mutableStateOf<String?>(null) }
    var latestRelease by remember { mutableStateOf<ReleaseInfo?>(null) }
    val context = LocalContext.current
    val selectedVehicle = CatalogData.vehicles.firstOrNull { it.id == selectedVehicleId }

    LaunchedEffect(Unit) {
        latestRelease = UpdateChecker.latestRelease()
    }

    val showNavigation = screen == HOME || screen == CATALOG || screen == WORKS
    Scaffold(
        containerColor = CarCanvas,
        bottomBar = {
            if (showNavigation) {
                BottomNavigation(screen = screen, onNavigate = { screen = it })
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(CarCanvas)
                .statusBarsPadding()
                .padding(innerPadding)
        ) {
            if (latestRelease != null && screen == HOME) {
                UpdateBanner(
                    release = latestRelease!!,
                    onOpen = { UpdateChecker.openRelease(context, latestRelease!!) },
                    onDismiss = { latestRelease = null }
                )
            }
            when (screen) {
                HOME -> DashboardScreen(
                    vehicle = selectedVehicle,
                    onCatalog = { screen = CATALOG },
                    onWorks = { screen = WORKS },
                    onOpenManual = {
                        selectedVehicleId = it.id
                        screen = VEHICLE
                    },
                    onOpenSection = {
                        selectedSectionId = it.id
                        screen = ARTICLE
                    }
                )
                CATALOG -> CatalogScreen(
                    selectedVehicleId = selectedVehicleId,
                    onVehicleSelected = {
                        selectedVehicleId = it.id
                        screen = VEHICLE
                    },
                    onBack = { screen = HOME }
                )
                WORKS -> WorksScreen(onBack = { screen = HOME })
                VEHICLE -> if (selectedVehicle != null) {
                    VehicleManualScreen(
                        vehicle = selectedVehicle,
                        onBack = { screen = CATALOG },
                        onOpenSection = {
                            selectedSectionId = it.id
                            screen = ARTICLE
                        }
                    )
                } else {
                    EmptyState(onBack = { screen = CATALOG })
                }
                ARTICLE -> {
                    val section = selectedVehicle?.let { vehicle ->
                        CatalogData.sections(vehicle).firstOrNull { it.id == selectedSectionId }
                    }
                    if (selectedVehicle != null && section != null) {
                        ManualArticleScreen(
                            vehicle = selectedVehicle,
                            section = section,
                            onBack = { screen = VEHICLE }
                        )
                    } else {
                        EmptyState(onBack = { screen = HOME })
                    }
                }
            }
        }
    }
}

@Composable
private fun BottomNavigation(screen: String, onNavigate: (String) -> Unit) {
    NavigationBar(containerColor = CardWhite, tonalElevation = 0.dp) {
        NavigationBarItem(
            selected = screen == HOME,
            onClick = { onNavigate(HOME) },
            icon = { Icon(Icons.Outlined.Home, null) },
            label = { Text("Обзор") }
        )
        NavigationBarItem(
            selected = screen == CATALOG,
            onClick = { onNavigate(CATALOG) },
            icon = { Icon(Icons.Outlined.MenuBook, null) },
            label = { Text("Каталог") }
        )
        NavigationBarItem(
            selected = screen == WORKS,
            onClick = { onNavigate(WORKS) },
            icon = { Icon(Icons.Outlined.WorkOutline, null) },
            label = { Text("Работы") }
        )
    }
}

@Composable
private fun DashboardScreen(
    vehicle: Vehicle?,
    onCatalog: () -> Unit,
    onWorks: () -> Unit,
    onOpenManual: (Vehicle) -> Unit,
    onOpenSection: (ManualSection) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 18.dp, bottom = 26.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        AppHeader(onCatalog = onCatalog)
        HeroCard(onCatalog = onCatalog)

        if (vehicle == null) {
            SelectVehicleCard(onClick = onCatalog)
        } else {
            SelectedVehicleCard(vehicle = vehicle, onClick = { onOpenManual(vehicle) })
        }

        SectionHeading(
            eyebrow = "БЫСТРЫЙ ДОСТУП",
            title = "Всё для работы в одном месте",
            action = "Все работы",
            onAction = onWorks
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            QuickActionCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.MenuBook,
                title = "Мануалы",
                caption = "По автомобилю",
                tint = SoftBlue,
                onClick = onCatalog
            )
            QuickActionCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.Build,
                title = "Работы",
                caption = "Прайс и сроки",
                tint = SoftAmber,
                onClick = onWorks
            )
        }

        if (vehicle != null) {
            SectionHeading(
                eyebrow = "МАНУАЛ ДЛЯ АВТОМОБИЛЯ",
                title = "Разделы руководства",
                action = "Открыть всё",
                onAction = { onOpenManual(vehicle) }
            )
            val sections = CatalogData.sections(vehicle).take(4)
            sections.forEach { section ->
                SectionListCard(section = section, onClick = { onOpenSection(section) })
            }
        } else {
            ServiceReadyCard()
        }
    }
}

@Composable
private fun AppHeader(onCatalog: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(CarNavy),
                contentAlignment = Alignment.Center
            ) {
                Text("CS", color = CarAmber, fontSize = 14.sp, fontWeight = FontWeight.Black)
            }
            Spacer(Modifier.width(10.dp))
            Column {
                Text("АВТОСЕРВИС", fontSize = 11.sp, letterSpacing = 1.5.sp, fontWeight = FontWeight.Bold, color = CarMuted)
                Text("Техническая база", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = CarInk)
            }
        }
        IconButton(onClick = onCatalog) {
            Icon(Icons.Outlined.Search, contentDescription = "Открыть поиск", tint = CarNavy)
        }
    }
}

@Composable
private fun HeroCard(onCatalog: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = CarNavy)
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(225.dp)) {
            Column(modifier = Modifier.padding(24.dp)) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color.White.copy(alpha = 0.12f)
                ) {
                    Text(
                        "СЕРВИСНЫЙ МАНУАЛ · 2000+",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        color = CarAmber,
                        fontSize = 10.sp,
                        letterSpacing = 1.2.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.height(14.dp))
                Text(
                    "Ремонт\nбез догадок.",
                    color = Color.White,
                    fontSize = 31.sp,
                    lineHeight = 34.sp,
                    fontWeight = FontWeight.Black
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "Регламенты, схемы и понятные\nинструкции для вашего автомобиля.",
                    color = Color.White.copy(alpha = 0.72f),
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 22.dp, bottom = 22.dp)
                    .size(58.dp)
                    .clip(CircleShape)
                    .background(CarAmber),
                contentAlignment = Alignment.Center
            ) {
                IconButton(onClick = onCatalog) {
                    Icon(Icons.Outlined.ArrowForward, contentDescription = "К каталогу", tint = CarNavy)
                }
            }
            Text(
                "01",
                modifier = Modifier.align(Alignment.TopEnd).padding(22.dp),
                color = Color.White.copy(alpha = 0.18f),
                fontSize = 42.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Composable
private fun SelectVehicleCard(onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, CarLine),
        colors = CardDefaults.cardColors(containerColor = CardWhite)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconBadge(Icons.Outlined.DirectionsCar, SoftBlue, CarBlue)
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Выберите автомобиль", fontWeight = FontWeight.Bold, color = CarInk)
                Text("Марка · модель · год · двигатель", color = CarMuted, fontSize = 12.sp)
            }
            Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = CarBlue)
        }
    }
}

@Composable
private fun SelectedVehicleCard(vehicle: Vehicle, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CarNavy)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconBadge(Icons.Outlined.DirectionsCar, Color.White.copy(alpha = .12f), CarAmber)
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("ВЫБРАННЫЙ АВТОМОБИЛЬ", color = CarAmber, fontSize = 10.sp, letterSpacing = 1.sp, fontWeight = FontWeight.Bold)
                    Text(vehicle.title, color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.Bold)
                }
                Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = Color.White)
            }
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                VehicleFact(vehicle.yearLabel)
                VehicleFact(vehicle.body)
                VehicleFact(vehicle.engineCode)
            }
        }
    }
}

@Composable
private fun VehicleFact(value: String) {
    Surface(color = Color.White.copy(alpha = .1f), shape = RoundedCornerShape(8.dp)) {
        Text(value, modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp), color = Color.White.copy(alpha = .86f), fontSize = 11.sp)
    }
}

@Composable
private fun QuickActionCard(
    modifier: Modifier,
    icon: ImageVector,
    title: String,
    caption: String,
    tint: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(116.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite)
    ) {
        Column(modifier = Modifier.padding(15.dp), verticalArrangement = Arrangement.SpaceBetween) {
            IconBadge(icon, tint, CarNavy)
            Column {
                Text(title, color = CarInk, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(caption, color = CarMuted, fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun ServiceReadyCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SoftGreen)
    ) {
        Row(modifier = Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.Shield, contentDescription = null, tint = Color(0xFF00856A), modifier = Modifier.size(28.dp))
            Spacer(Modifier.width(14.dp))
            Column {
                Text("Сначала выберите авто", color = CarInk, fontWeight = FontWeight.Bold)
                Text("Так мы покажем точные нормы и код двигателя.", color = CarMuted, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun SectionHeading(eyebrow: String, title: String, action: String, onAction: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
        Column(modifier = Modifier.weight(1f)) {
            Text(eyebrow, color = CarBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.3.sp)
            Spacer(Modifier.height(3.dp))
            Text(title, color = CarInk, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        TextButton(onClick = onAction, contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)) {
            Text(action, color = CarBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun IconBadge(icon: ImageVector, background: Color, tint: Color) {
    Box(
        modifier = Modifier.size(38.dp).clip(RoundedCornerShape(12.dp)).background(background),
        contentAlignment = Alignment.Center
    ) { Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp)) }
}

@Composable
private fun SectionListCard(section: ManualSection, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite)
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            IconBadge(sectionIcon(section.icon), colorFrom(section.accent).copy(alpha = .13f), colorFrom(section.accent))
            Spacer(Modifier.width(13.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(section.eyebrow, color = colorFrom(section.accent), fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = .8.sp)
                Text(section.title, color = CarInk, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(section.steps.first(), color = CarMuted, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Icon(Icons.Outlined.ChevronRight, contentDescription = "Открыть раздел", tint = CarMuted, modifier = Modifier.size(20.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CatalogScreen(
    selectedVehicleId: String?,
    onVehicleSelected: (Vehicle) -> Unit,
    onBack: () -> Unit
) {
    var query by rememberSaveable { mutableStateOf("") }
    var brand by rememberSaveable { mutableStateOf("Все марки") }
    var year by rememberSaveable { mutableStateOf("Все годы") }
    val filtered = CatalogData.vehicles.filter { vehicle ->
        val matchesQuery = query.isBlank() || listOf(vehicle.brand, vehicle.model, vehicle.engineCode, vehicle.engine, vehicle.body)
            .any { it.contains(query, ignoreCase = true) }
        val matchesBrand = brand == "Все марки" || vehicle.brand == brand
        val matchesYear = year == "Все годы" || when (year) {
            "2000–2009" -> vehicle.years.first <= 2009
            "2010–2019" -> vehicle.years.last >= 2010
            "2020+" -> vehicle.years.last >= 2020
            else -> true
        }
        matchesQuery && matchesBrand && matchesYear
    }

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp).padding(top = 17.dp, bottom = 24.dp)) {
        BackHeader(title = "Каталог автомобилей", subtitle = "18 конфигураций · данные с 2000 года", onBack = onBack)
        Spacer(Modifier.height(19.dp))
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(15.dp),
            placeholder = { Text("Поиск марки, модели или кода двигателя", fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
            trailingIcon = if (query.isNotBlank()) {
                { IconButton(onClick = { query = "" }) { Icon(Icons.Outlined.Close, contentDescription = "Очистить") } }
            } else null
        )
        Spacer(Modifier.height(12.dp))
        Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DropdownFilter(
                label = brand,
                options = listOf("Все марки") + CatalogData.vehicles.map { it.brand }.distinct().sorted(),
                onSelected = { brand = it }
            )
            DropdownFilter(
                label = year,
                options = listOf("Все годы", "2000–2009", "2010–2019", "2020+"),
                onSelected = { year = it }
            )
            FilterChip(
                selected = true,
                onClick = { },
                label = { Text("2000+") },
                leadingIcon = { Icon(Icons.Outlined.CalendarToday, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
        }
        Spacer(Modifier.height(22.dp))
        Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                Text("БАЗА АВТОМОБИЛЕЙ", color = CarBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
                Text("Найдены автомобили", color = CarInk, fontWeight = FontWeight.Bold, fontSize = 21.sp)
            }
            Text("${filtered.size} шт.", color = CarMuted, fontSize = 12.sp)
        }
        Spacer(Modifier.height(12.dp))
        if (filtered.isEmpty()) {
            EmptySearchState()
        } else {
            filtered.forEach { vehicle ->
                VehicleListItem(vehicle = vehicle, selected = vehicle.id == selectedVehicleId, onClick = { onVehicleSelected(vehicle) })
                Spacer(Modifier.height(10.dp))
            }
        }
        Spacer(Modifier.height(8.dp))
        Text("Каталог расширяется через синхронизацию новых версий приложения.", color = CarMuted, fontSize = 11.sp, lineHeight = 16.sp)
    }
}

@Composable
private fun DropdownFilter(label: String, options: List<String>, onSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        FilterChip(
            selected = label != options.first(),
            onClick = { expanded = true },
            label = { Text(label, maxLines = 1) },
            trailingIcon = { Icon(Icons.Outlined.Tune, contentDescription = null, modifier = Modifier.size(15.dp)) }
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = { onSelected(option); expanded = false },
                    leadingIcon = if (option == label) ({ Icon(Icons.Outlined.Check, null) }) else null
                )
            }
        }
    }
}

@Composable
private fun VehicleListItem(vehicle: Vehicle, selected: Boolean, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        border = if (selected) BorderStroke(1.dp, CarBlue) else null,
        colors = CardDefaults.cardColors(containerColor = if (selected) SoftBlue else CardWhite)
    ) {
        Row(modifier = Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(48.dp).clip(RoundedCornerShape(14.dp)).background(if (selected) CarNavy else Color(0xFFEFF3F7)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.DirectionsCar, contentDescription = null, tint = if (selected) CarAmber else CarNavy)
            }
            Spacer(Modifier.width(13.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(vehicle.title, color = CarInk, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("${vehicle.yearLabel} · ${vehicle.body}", color = CarMuted, fontSize = 12.sp)
                Text("${vehicle.engineCode} · ${vehicle.engine}", color = CarMuted, fontSize = 11.sp)
            }
            Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = CarMuted, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
private fun VehicleManualScreen(vehicle: Vehicle, onBack: () -> Unit, onOpenSection: (ManualSection) -> Unit) {
    val sections = CatalogData.sections(vehicle)
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp).padding(top = 17.dp, bottom = 24.dp)) {
        BackHeader(title = "Руководство", subtitle = "Выбранная конфигурация", onBack = onBack)
        Spacer(Modifier.height(18.dp))
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(23.dp), colors = CardDefaults.cardColors(containerColor = CarNavy)) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("${vehicle.brand.uppercase()} · ${vehicle.model.uppercase()}", color = CarAmber, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.1.sp)
                Spacer(Modifier.height(6.dp))
                Text(vehicle.yearLabel, color = Color.White, fontSize = 27.sp, fontWeight = FontWeight.Black)
                Text("${vehicle.body}  /  ${vehicle.engineCode}  /  ${vehicle.engine}", color = Color.White.copy(alpha = .74f), fontSize = 12.sp)
                Spacer(Modifier.height(17.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                    VehicleFact("VIN-профиль")
                    VehicleFact(vehicle.transmission)
                }
            }
        }
        Spacer(Modifier.height(25.dp))
        SectionHeading("РУКОВОДСТВО ПО РЕМОНТУ", "Разделы и процедуры", "", {})
        Spacer(Modifier.height(10.dp))
        sections.forEachIndexed { index, section ->
            ManualSectionRow(index + 1, section, onClick = { onOpenSection(section) })
            Spacer(Modifier.height(9.dp))
        }
        Spacer(Modifier.height(12.dp))
        InfoNote("Тексты на английском и других языках переводятся на русский автоматически. Моменты затяжки и допуски сверяйте с официальной документацией производителя.")
    }
}

@Composable
private fun ManualSectionRow(number: Int, section: ManualSection, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(17.dp), colors = CardDefaults.cardColors(containerColor = CardWhite)) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(number.toString().padStart(2, '0'), color = colorFrom(section.accent), fontWeight = FontWeight.Black, fontSize = 16.sp, modifier = Modifier.width(28.dp))
            IconBadge(sectionIcon(section.icon), colorFrom(section.accent).copy(alpha = .13f), colorFrom(section.accent))
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(section.title, color = CarInk, fontWeight = FontWeight.Bold)
                Text(section.eyebrow, color = CarMuted, fontSize = 10.sp, letterSpacing = .7.sp)
            }
            Icon(Icons.Outlined.ArrowForward, contentDescription = "Открыть", tint = CarBlue, modifier = Modifier.size(19.dp))
        }
    }
}

@Composable
private fun ManualArticleScreen(vehicle: Vehicle, section: ManualSection, onBack: () -> Unit) {
    var showOriginal by rememberSaveable(section.id) { mutableStateOf(false) }
    var translatedText by remember(section.id) { mutableStateOf(section.russianText) }
    var isTranslating by remember(section.id) { mutableStateOf(section.sourceLanguage != "ru") }
    var usedMachineTranslation by remember(section.id) { mutableStateOf(false) }

    LaunchedEffect(section.id) {
        if (section.sourceLanguage != "ru") {
            TranslationEngine.toRussian(section.sourceLanguage, section.sourceText, section.russianText) { text, machine ->
                translatedText = text
                usedMachineTranslation = machine
                isTranslating = false
            }
        } else {
            isTranslating = false
        }
    }

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp).padding(top = 17.dp, bottom = 28.dp)) {
        BackHeader(title = "Раздел руководства", subtitle = vehicle.title, onBack = onBack)
        Spacer(Modifier.height(22.dp))
        Text(section.eyebrow, color = colorFrom(section.accent), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
        Spacer(Modifier.height(5.dp))
        Text(section.title, color = CarInk, fontSize = 28.sp, lineHeight = 32.sp, fontWeight = FontWeight.Black)
        Spacer(Modifier.height(15.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = RoundedCornerShape(7.dp), color = SoftGreen) {
                Row(modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Language, null, tint = Color(0xFF00856A), modifier = Modifier.size(15.dp))
                    Spacer(Modifier.width(5.dp))
                    Text(if (showOriginal) "Оригинал · ${section.sourceLanguage.uppercase()}" else "Перевод · РУССКИЙ", color = Color(0xFF007A63), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
            if (isTranslating) {
                Text("Загружаем модель перевода…", color = CarMuted, fontSize = 11.sp)
            } else if (usedMachineTranslation) {
                Text("Переведено автоматически", color = CarMuted, fontSize = 11.sp)
            }
        }
        Spacer(Modifier.height(15.dp))
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = CardWhite)) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(if (showOriginal) section.sourceText else translatedText, color = CarInk, fontSize = 16.sp, lineHeight = 25.sp)
                Spacer(Modifier.height(10.dp))
                TextButton(onClick = { showOriginal = !showOriginal }, contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)) {
                    Text(if (showOriginal) "Показать перевод" else "Показать оригинал", color = CarBlue, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
        Spacer(Modifier.height(24.dp))
        Text("ПОРЯДОК РАБОТЫ", color = CarBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
        Spacer(Modifier.height(10.dp))
        section.steps.forEachIndexed { index, step ->
            StepRow(index + 1, step)
            if (index != section.steps.lastIndex) Spacer(Modifier.height(9.dp))
        }
        section.caution?.let {
            Spacer(Modifier.height(20.dp))
            WarningCard(it)
        }
        Spacer(Modifier.height(18.dp))
        InfoNote("Источник: сервисная база CAR SERVICE. Для критичных узлов проверяйте VIN, код двигателя и официальные нормы производителя.")
    }
}

@Composable
private fun StepRow(number: Int, text: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Box(modifier = Modifier.size(28.dp).clip(CircleShape).background(CarNavy), contentAlignment = Alignment.Center) {
            Text(number.toString(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
        Spacer(Modifier.width(12.dp))
        Text(text, color = CarInk, fontSize = 14.sp, lineHeight = 20.sp, modifier = Modifier.padding(top = 3.dp))
    }
}

@Composable
private fun WarningCard(text: String) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(15.dp), colors = CardDefaults.cardColors(containerColor = SoftAmber)) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
            Icon(Icons.Outlined.Info, contentDescription = null, tint = Color(0xFF9A5B00), modifier = Modifier.size(19.dp))
            Spacer(Modifier.width(10.dp))
            Text(text, color = Color(0xFF704100), fontSize = 12.sp, lineHeight = 17.sp)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WorksScreen(onBack: () -> Unit) {
    var query by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf("Все") }
    val categories = listOf("Все") + CatalogData.works.map { it.category }.distinct()
    val filtered = CatalogData.works.filter { work ->
        (category == "Все" || work.category == category) &&
            (query.isBlank() || work.title.contains(query, true) || work.description.contains(query, true))
    }
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp).padding(top = 17.dp, bottom = 25.dp)) {
        BackHeader(title = "Работы сервиса", subtitle = "Прозрачные цены и понятные сроки", onBack = onBack)
        Spacer(Modifier.height(18.dp))
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(15.dp),
            placeholder = { Text("Найти работу или услугу", fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Outlined.Search, null) }
        )
        Spacer(Modifier.height(11.dp))
        Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            categories.forEach { item ->
                FilterChip(selected = category == item, onClick = { category = item }, label = { Text(item) })
            }
        }
        Spacer(Modifier.height(22.dp))
        Text("${filtered.size} услуг", color = CarMuted, fontSize = 12.sp)
        Spacer(Modifier.height(10.dp))
        filtered.forEach { work ->
            WorkCard(work)
            Spacer(Modifier.height(10.dp))
        }
        Spacer(Modifier.height(8.dp))
        InfoNote("Цена указана ориентировочно. Точная стоимость определяется после диагностики и согласуется до начала работ.")
    }
}

@Composable
private fun WorkCard(work: ServiceWork) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = CardWhite)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconBadge(workIcon(work.icon), colorFrom(work.accent).copy(alpha = .12f), colorFrom(work.accent))
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(work.category.uppercase(), color = colorFrom(work.accent), fontSize = 9.sp, letterSpacing = 1.sp, fontWeight = FontWeight.Bold)
                    Text(work.title, color = CarInk, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
                Text(work.price, color = CarNavy, fontWeight = FontWeight.Black, fontSize = 13.sp)
            }
            Spacer(Modifier.height(11.dp))
            Text(work.description, color = CarMuted, fontSize = 12.sp, lineHeight = 17.sp)
            Spacer(Modifier.height(12.dp))
            Divider(color = CarLine.copy(alpha = .7f))
            Spacer(Modifier.height(9.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.AccessTime, null, tint = CarMuted, modifier = Modifier.size(15.dp))
                Spacer(Modifier.width(5.dp))
                Text(work.duration, color = CarMuted, fontSize = 11.sp)
                Spacer(Modifier.weight(1f))
                Text("Записаться", color = CarBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Icon(Icons.Outlined.ArrowForward, null, tint = CarBlue, modifier = Modifier.size(16.dp).padding(start = 3.dp))
            }
        }
    }
}

@Composable
private fun BackHeader(title: String, subtitle: String, onBack: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onBack, modifier = Modifier.size(40.dp)) { Icon(Icons.Outlined.ArrowBack, contentDescription = "Назад", tint = CarNavy) }
        Spacer(Modifier.width(8.dp))
        Column {
            Text(title, color = CarInk, fontSize = 21.sp, fontWeight = FontWeight.Black)
            Text(subtitle, color = CarMuted, fontSize = 11.sp)
        }
    }
}

@Composable
private fun InfoNote(text: String) {
    Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(13.dp)).background(Color(0xFFEFF3F7)).padding(12.dp), verticalAlignment = Alignment.Top) {
        Icon(Icons.Outlined.Info, null, tint = CarMuted, modifier = Modifier.size(17.dp))
        Spacer(Modifier.width(8.dp))
        Text(text, color = CarMuted, fontSize = 11.sp, lineHeight = 16.sp)
    }
}

@Composable
private fun EmptySearchState() {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 35.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Outlined.Search, null, tint = CarMuted, modifier = Modifier.size(35.dp))
        Spacer(Modifier.height(8.dp))
        Text("Ничего не найдено", color = CarInk, fontWeight = FontWeight.Bold)
        Text("Измените запрос или фильтры", color = CarMuted, fontSize = 12.sp)
    }
}

@Composable
private fun EmptyState(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("Выберите автомобиль", color = CarInk, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(10.dp))
        Button(onClick = onBack) { Text("Вернуться в каталог") }
    }
}

@Composable
private fun UpdateBanner(release: ReleaseInfo, onOpen: () -> Unit, onDismiss: () -> Unit) {
    Surface(color = SoftBlue) {
        Row(modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 9.dp, bottom = 9.dp, end = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.Download, null, tint = CarBlue, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(9.dp))
            Text("Доступна новая версия ${release.version}", color = CarNavy, fontSize = 12.sp, modifier = Modifier.weight(1f))
            TextButton(onClick = onOpen) { Text("Обновить", color = CarBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            IconButton(onClick = onDismiss, modifier = Modifier.size(30.dp)) { Icon(Icons.Outlined.Close, "Скрыть", tint = CarMuted, modifier = Modifier.size(16.dp)) }
        }
    }
}

private fun colorFrom(value: Long): Color = Color(value)

private fun sectionIcon(name: String): ImageVector = when (name) {
    "check" -> Icons.Outlined.CheckCircle
    "engine" -> Icons.Outlined.Settings
    "transmission" -> Icons.Outlined.Build
    "chassis" -> Icons.Outlined.DirectionsCar
    "electrics" -> Icons.Outlined.ElectricBolt
    "body" -> Icons.Outlined.DirectionsCar
    else -> Icons.Outlined.MenuBook
}

private fun workIcon(name: String): ImageVector = when (name) {
    "check" -> Icons.Outlined.CheckCircle
    "oil" -> Icons.Outlined.LocalGasStation
    "brakes" -> Icons.Outlined.Shield
    "suspension" -> Icons.Outlined.DirectionsCar
    "engine" -> Icons.Outlined.Settings
    "timing" -> Icons.Outlined.Build
    "ac" -> Icons.Outlined.EventAvailable
    "electrics" -> Icons.Outlined.ElectricBolt
    "body" -> Icons.Outlined.DirectionsCar
    else -> Icons.Outlined.WorkOutline
}
