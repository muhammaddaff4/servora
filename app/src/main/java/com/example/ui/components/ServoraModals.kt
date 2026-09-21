package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GlobalCurrency
import com.example.model.GlobalLanguage
import com.example.model.UserAddress
import com.example.ui.theme.*
import com.example.viewmodel.ServoraViewModel

@Composable
fun CurrencySelectionDialog(
    currentCurrency: GlobalCurrency,
    onSelect: (GlobalCurrency) -> Unit,
    onDismiss: () -> Unit
) {
    val supportedCurrencies = listOf(
        GlobalCurrency("IDR", "Rp", "Rupiah Indonesia (Rp IDR)", 16000.0),
        GlobalCurrency("USD", "$", "US Dollar ($ USD)", 1.0),
        GlobalCurrency("EUR", "€", "Euro (€ EUR)", 0.92),
        GlobalCurrency("GBP", "£", "British Pound (£ GBP)", 0.79),
        GlobalCurrency("SGD", "S$", "Singapore Dollar (S$ SGD)", 1.34),
        GlobalCurrency("JPY", "¥", "Japanese Yen (¥ JPY)", 152.0),
        GlobalCurrency("AUD", "A$", "Australian Dollar (A$ AUD)", 1.51),
        GlobalCurrency("CAD", "C$", "Canadian Dollar (C$ CAD)", 1.36),
        GlobalCurrency("AED", "AED ", "UAE Dirham", 3.67)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("Pilih Mata Uang / Currency", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("Konversi otomatis tarif teknisi & escrow", color = TextSecondary, fontSize = 12.sp)
            }
        },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                item {
                    Text("Mata Uang Utama (Indonesia & US)", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                items(supportedCurrencies) { c ->
                    val isSelected = currentCurrency.code == c.code
                    val isPrimary = c.code == "IDR" || c.code == "USD"

                    Surface(
                        onClick = { onSelect(c) },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) ElectricBlue.copy(alpha = 0.12f) else if (isPrimary) SurfaceElevated else SurfaceCard,
                        border = BorderStroke(1.dp, if (isSelected) ElectricBlue else if (isPrimary) ElectricBlue.copy(alpha = 0.3f) else SlateBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "${c.code} (${c.symbol})",
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        fontSize = 15.sp
                                    )
                                    if (c.code == "IDR") {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = CoralRed.copy(alpha = 0.15f)
                                        ) {
                                            Text(
                                                "Indonesia",
                                                color = CoralRed,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    } else if (c.code == "USD") {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = ElectricBlue.copy(alpha = 0.15f)
                                        ) {
                                            Text(
                                                "United States",
                                                color = ElectricBlue,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = c.name,
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }
                            if (isSelected) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = ElectricBlue)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Tutup", color = TextSecondary)
            }
        }
    )
}

@Composable
fun LanguageSelectionDialog(
    currentLanguage: GlobalLanguage,
    onSelect: (GlobalLanguage) -> Unit,
    onDismiss: () -> Unit
) {
    val supportedLanguages = listOf(
        GlobalLanguage("en", "English", "English"),
        GlobalLanguage("es", "Spanish", "Español"),
        GlobalLanguage("fr", "French", "Français"),
        GlobalLanguage("de", "German", "Deutsch"),
        GlobalLanguage("ja", "Japanese", "日本語"),
        GlobalLanguage("id", "Indonesian", "Bahasa Indonesia")
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Language", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(supportedLanguages) { l ->
                    Surface(
                        onClick = { onSelect(l) },
                        shape = RoundedCornerShape(12.dp),
                        color = if (currentLanguage.code == l.code) ElectricBlue.copy(alpha = 0.12f) else SurfaceCard,
                        border = BorderStroke(1.dp, if (currentLanguage.code == l.code) ElectricBlue else SlateBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = l.name, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text(text = l.localName, fontSize = 12.sp, color = TextMuted)
                            }
                            if (currentLanguage.code == l.code) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = ElectricBlue)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = TextSecondary)
            }
        }
    )
}

@Composable
fun LocationSelectionDialog(
    currentLocation: String,
    savedAddresses: List<UserAddress> = emptyList(),
    onSelectAddress: (UserAddress) -> Unit = {},
    onAddNewAddressClick: () -> Unit = {},
    onSelectCity: (String) -> Unit = {},
    onDismiss: () -> Unit
) {
    val indonesiaCities = listOf(
        "Kebayoran Baru, Jakarta Selatan",
        "Sudirman, Jakarta Pusat",
        "Pantai Indah Kapuk (PIK), Jakarta Utara",
        "Dago Pakar, Bandung",
        "Kertajaya, Surabaya",
        "Canggu & Seminyak, Bali",
        "Medan Kota, Sumatera Utara",
        "Semarang Barat, Jawa Tengah"
    )

    val usAndGlobalCities = listOf(
        "Manhattan, New York (NY)",
        "San Francisco Bay Area (CA)",
        "Downtown Los Angeles (CA)",
        "Chicago Loop, Illinois (IL)",
        "Miami Beach, Florida (FL)",
        "Singapore (Marina Bay)",
        "London (Mayfair), UK",
        "Tokyo (Shibuya), Japan"
    )

    var selectedTab by remember { mutableStateOf(0) } // 0: Alamat Tersimpan, 1: Kota Indonesia, 2: Kota US / Global

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Pilih / Tambah Lokasi", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }
                Text("Teknisi akan dialokasikan ke lokasi ini", color = TextSecondary, fontSize = 12.sp)
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Button Tambah Alamat Baru
                Button(
                    onClick = {
                        onDismiss()
                        onAddNewAddressClick()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
                ) {
                    Icon(Icons.Default.AddLocationAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Tambah Alamat Baru", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Tabs
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = SurfaceElevated,
                    contentColor = ElectricBlue
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Tersimpan (${savedAddresses.size})", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("🇮🇩 Indonesia", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("🇺🇸 USA / Global", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 320.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (selectedTab == 0) {
                        if (savedAddresses.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Belum ada alamat tersimpan.", color = TextMuted, fontSize = 13.sp)
                                }
                            }
                        } else {
                            items(savedAddresses) { addr ->
                                val isCurrent = currentLocation.contains(addr.streetAddress.take(10)) || addr.isDefault
                                Surface(
                                    onClick = {
                                        onSelectAddress(addr)
                                        onDismiss()
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isCurrent) ElectricBlue.copy(alpha = 0.12f) else SurfaceCard,
                                    border = BorderStroke(1.dp, if (isCurrent) ElectricBlue else SlateBorder),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = if (addr.title.contains("Kantor", ignoreCase = true)) Icons.Default.Business else Icons.Default.Home,
                                            contentDescription = null,
                                            tint = if (isCurrent) ElectricBlue else TextSecondary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(addr.title, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 13.sp)
                                                if (addr.isDefault) {
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Surface(
                                                        shape = RoundedCornerShape(4.dp),
                                                        color = EmeraldTrust.copy(alpha = 0.15f)
                                                    ) {
                                                        Text("Default", color = EmeraldTrust, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                                    }
                                                }
                                            }
                                            Text(addr.streetAddress, color = TextSecondary, fontSize = 11.sp, maxLines = 1)
                                            Text("${addr.city}, ${addr.country} • ${addr.postalCode}", color = TextMuted, fontSize = 10.sp)
                                        }
                                        if (isCurrent) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(18.dp))
                                        }
                                    }
                                }
                            }
                        }
                    } else if (selectedTab == 1) {
                        items(indonesiaCities) { city ->
                            Surface(
                                onClick = {
                                    onSelectCity(city)
                                    onDismiss()
                                },
                                shape = RoundedCornerShape(10.dp),
                                color = SurfaceCard,
                                border = BorderStroke(1.dp, SlateBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = CoralRed, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(city, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    } else {
                        items(usAndGlobalCities) { city ->
                            Surface(
                                onClick = {
                                    onSelectCity(city)
                                    onDismiss()
                                },
                                shape = RoundedCornerShape(10.dp),
                                color = SurfaceCard,
                                border = BorderStroke(1.dp, SlateBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.LocationCity, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(city, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Tutup", color = TextSecondary)
            }
        }
    )
}

@Composable
fun AddNewAddressDialog(
    onSave: (title: String, street: String, city: String, stateProvince: String, country: String, postalCode: String, notes: String) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var street by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var stateProvince by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("Indonesia") }
    var postalCode by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("Tambah Alamat Baru", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("Alamat servis untuk kunjungan tukang/teknisi", color = TextSecondary, fontSize = 12.sp)
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Country selection chips
                item {
                    Text("Pilih Negara", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Indonesia", "United States", "Global").forEach { c ->
                            FilterChip(
                                selected = country == c,
                                onClick = { country = c },
                                label = { Text(c, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = ElectricBlue,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }

                item {
                    Text("Label / Nama Lokasi", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        placeholder = { Text(if (country == "Indonesia") "Misal: Rumah Utama, Apartemen, Kantor" else "E.g. Home, Office, Condo", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                }

                item {
                    Text("Alamat Lengkap & No. Bangunan", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    OutlinedTextField(
                        value = street,
                        onValueChange = { street = it },
                        placeholder = { Text(if (country == "Indonesia") "Jl. Nama Jalan No. XX, RT/RW, Kelurahan" else "Street name, Suite, Apt number", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Kota", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            OutlinedTextField(
                                value = city,
                                onValueChange = { city = it },
                                placeholder = { Text(if (country == "Indonesia") "Jakarta Selatan" else "New York", fontSize = 12.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Provinsi / State", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            OutlinedTextField(
                                value = stateProvince,
                                onValueChange = { stateProvince = it },
                                placeholder = { Text(if (country == "Indonesia") "DKI Jakarta" else "NY", fontSize = 12.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true
                            )
                        }
                    }
                }

                item {
                    Text("Kode Pos", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    OutlinedTextField(
                        value = postalCode,
                        onValueChange = { postalCode = it },
                        placeholder = { Text("12110 / 10017", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                }

                item {
                    Text("Petunjuk / Catatan Untuk Teknisi", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        placeholder = { Text("Warna pagar, lantai unit, patokan dekat masjid/toko, dll", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                if (errorMessage != null) {
                    item {
                        Text(errorMessage!!, color = CoralRed, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (street.isBlank() || city.isBlank()) {
                        errorMessage = "Mohon lengkapi alamat dan kota."
                    } else {
                        onSave(
                            if (title.isBlank()) "Lokasi $city" else title,
                            street,
                            city,
                            stateProvince,
                            country,
                            postalCode,
                            notes
                        )
                        onDismiss()
                    }
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
            ) {
                Text("Simpan Alamat", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", color = TextSecondary)
            }
        }
    )
}

@Composable
fun ReviewSubmissionDialog(
    onSubmit: (rating: Double, comment: String, quality: Double, prof: Double, punct: Double, value: Double) -> Unit,
    onDismiss: () -> Unit
) {
    var rating by remember { mutableStateOf(5.0) }
    var comment by remember { mutableStateOf("Super fast arrival, diagnosed the problem within 15 minutes, and tested everything properly. Highly recommended!") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Rate & Review Professional", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("How would you rate the service quality?", color = TextSecondary, fontSize = 13.sp)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    (1..5).forEach { star ->
                        IconButton(onClick = { rating = star.toDouble() }) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = if (star <= rating) AmberWarning else SlateBorder,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("Write your review") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(rating, comment, 5.0, 5.0, 5.0, 5.0) },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
            ) {
                Text("Submit Review", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrustSafetyBottomSheet(
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = SurfaceCard,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Shield, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("Servora Trust & Safety Protocol", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("Built-in protection for every global booking", color = TextMuted, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            TrustProtocolItem(
                title = "1. Strict 5-Step Verification",
                desc = "Government national ID check, trade licence verification, clean criminal background screening, tool inspection, and in-person peer review."
            )

            TrustProtocolItem(
                title = "2. Servora Escrow Safe-Pay",
                desc = "Your card is never charged directly to the pro. Funds stay protected in an escrow account until you inspect and give formal app sign-off."
            )

            TrustProtocolItem(
                title = "3. Underwritten Insurance Policy",
                desc = "Every completed booking is covered by commercial property damage liability up to $1,000,000 against unexpected accidents."
            )

            TrustProtocolItem(
                title = "4. Additional Work Transparency",
                desc = "Professionals are strictly barred from demanding extra cash. Any additional work or parts must be quoted inside the app with photos for your digital approval."
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
            ) {
                Text("Got It, Close", color = Color.White)
            }
        }
    }
}

@Composable
private fun TrustProtocolItem(title: String, desc: String) {
    Column(modifier = Modifier.padding(bottom = 12.dp)) {
        Text(text = title, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = desc, color = TextSecondary, fontSize = 12.sp, lineHeight = 17.sp)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleAccessGatewayDialog(
    currentRole: com.example.model.AppRole,
    onRoleSelected: (com.example.model.AppRole, String) -> Boolean,
    onDismiss: () -> Unit
) {
    var selectedTargetRole by remember { mutableStateOf<com.example.model.AppRole?>(null) }
    var pinInput by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = SurfaceCard,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 36.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Servora Access & Role Gateway",
                            color = TextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "Pemisahan Hak Akses: Keamanan hanya diubah oleh Dev & Admin",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            when (currentRole) {
                                com.example.model.AppRole.CUSTOMER -> CyanAccent.copy(alpha = 0.15f)
                                com.example.model.AppRole.PROFESSIONAL -> EmeraldTrust.copy(alpha = 0.15f)
                                com.example.model.AppRole.ADMIN -> AmberWarning.copy(alpha = 0.15f)
                                com.example.model.AppRole.DEVELOPER -> CoralRed.copy(alpha = 0.15f)
                            }
                        )
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "AKTIF: ${currentRole.name}",
                        color = when (currentRole) {
                            com.example.model.AppRole.CUSTOMER -> CyanAccent
                            com.example.model.AppRole.PROFESSIONAL -> EmeraldTrust
                            com.example.model.AppRole.ADMIN -> AmberWarning
                            com.example.model.AppRole.DEVELOPER -> CoralRed
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (selectedTargetRole == null) {
                // Role Options List
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // 1. CUSTOMER
                    item {
                        RoleOptionCard(
                            title = "Customer / Pelanggan",
                            subtitle = "Cari jasa, booking order, escrow payment aman, live GPS tracking, chat & ulasan.",
                            badge = "Bebas Akses",
                            badgeColor = CyanAccent,
                            isCurrent = currentRole == com.example.model.AppRole.CUSTOMER,
                            onClick = {
                                onRoleSelected(com.example.model.AppRole.CUSTOMER, "")
                                onDismiss()
                            }
                        )
                    }

                    // 2. PROFESSIONAL (TUKANG / JASA)
                    item {
                        RoleOptionCard(
                            title = "Professional / Mitra Teknisi",
                            subtitle = "Penyedia jasa: Terima order, update status live kedatangan, ajukan suku cadang tambahan, dompet gaji.",
                            badge = "Mitra Jasa",
                            badgeColor = EmeraldTrust,
                            isCurrent = currentRole == com.example.model.AppRole.PROFESSIONAL,
                            onClick = {
                                onRoleSelected(com.example.model.AppRole.PROFESSIONAL, "")
                                onDismiss()
                            }
                        )
                    }

                    // 3. ADMIN HQ
                    item {
                        RoleOptionCard(
                            title = "Platform Admin HQ",
                            subtitle = "Operasional: Verifikasi dokumen lisensi teknisi, sengketa pelanggan, audit cadangan escrow.",
                            badge = "PIN: 1122",
                            badgeColor = AmberWarning,
                            isCurrent = currentRole == com.example.model.AppRole.ADMIN,
                            onClick = {
                                selectedTargetRole = com.example.model.AppRole.ADMIN
                                pinInput = ""
                                pinError = false
                            }
                        )
                    }

                    // 4. DEVELOPER
                    item {
                        RoleOptionCard(
                            title = "System Core Developer",
                            subtitle = "Satu-satunya pihak yang berhak mengubah keamanan, feature flags, audit logs, dan sandbox database.",
                            badge = "PIN: 9900",
                            badgeColor = CoralRed,
                            isCurrent = currentRole == com.example.model.AppRole.DEVELOPER,
                            onClick = {
                                selectedTargetRole = com.example.model.AppRole.DEVELOPER
                                pinInput = ""
                                pinError = false
                            }
                        )
                    }
                }
            } else {
                // PIN Authentication Prompt for Admin / Developer
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    border = BorderStroke(1.dp, if (pinError) CoralRed else SlateBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (selectedTargetRole == com.example.model.AppRole.DEVELOPER) CoralRed.copy(alpha = 0.15f)
                                        else AmberWarning.copy(alpha = 0.15f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = if (selectedTargetRole == com.example.model.AppRole.DEVELOPER) CoralRed else AmberWarning,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (selectedTargetRole == com.example.model.AppRole.DEVELOPER) "Otentikasi Developer" else "Otentikasi Admin HQ",
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (selectedTargetRole == com.example.model.AppRole.DEVELOPER) "Gunakan Master PIN: 9900" else "Gunakan Admin PIN: 1122",
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = pinInput,
                            onValueChange = {
                                pinInput = it
                                pinError = false
                            },
                            label = { Text("Masukkan Security PIN") },
                            isError = pinError,
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ElectricBlue,
                                unfocusedBorderColor = SlateBorder
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (pinError) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "PIN salah. Akses ditolak demi keamanan platform.",
                                color = CoralRed,
                                fontSize = 11.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    selectedTargetRole = null
                                    pinInput = ""
                                },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Kembali")
                            }

                            Button(
                                onClick = {
                                    val success = onRoleSelected(selectedTargetRole!!, pinInput)
                                    if (success) {
                                        onDismiss()
                                    } else {
                                        pinError = true
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selectedTargetRole == com.example.model.AppRole.DEVELOPER) CoralRed else AmberWarning
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1.5f)
                            ) {
                                Text(
                                    text = "Buka Akses",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RoleOptionCard(
    title: String,
    subtitle: String,
    badge: String,
    badgeColor: Color,
    isCurrent: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrent) ElectricBlue.copy(alpha = 0.08f) else SurfaceCard
        ),
        border = BorderStroke(
            1.dp,
            if (isCurrent) ElectricBlue else SlateBorder
        ),
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
                    Text(
                        text = title,
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(badgeColor.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = badge,
                            color = badgeColor,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = subtitle,
                    color = TextMuted,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }

            Spacer(modifier = Modifier.width(10.dp))
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextSubtle,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

