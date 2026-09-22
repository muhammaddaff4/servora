package com.example.data

import android.util.Log
import com.example.R
import com.example.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*

class ServoraRepository {

    // Firebase Cloud Firestore Repository Integration
    val firestoreRepository = FirestoreRepository()

    // Global Settings
    val currencies = listOf(
        GlobalCurrency("USD", "$", "US Dollar ($ USD)", 1.0),
        GlobalCurrency("IDR", "Rp", "Rupiah Indonesia (Rp IDR)", 16000.0),
        GlobalCurrency("EUR", "€", "Euro (€ EUR)", 0.92),
        GlobalCurrency("GBP", "£", "British Pound (£ GBP)", 0.79),
        GlobalCurrency("SGD", "S$", "Singapore Dollar (S$ SGD)", 1.34),
        GlobalCurrency("JPY", "¥", "Japanese Yen (¥ JPY)", 152.0),
        GlobalCurrency("AUD", "A$", "Australian Dollar (A$ AUD)", 1.51),
        GlobalCurrency("CAD", "C$", "Canadian Dollar (C$ CAD)", 1.36),
        GlobalCurrency("AED", "AED ", "UAE Dirham", 3.67)
    )

    val languages = listOf(
        GlobalLanguage("en", "English", "English (Global)"),
        GlobalLanguage("id", "Indonesian", "Bahasa Indonesia"),
        GlobalLanguage("es", "Spanish", "Español"),
        GlobalLanguage("fr", "French", "Français"),
        GlobalLanguage("de", "German", "Deutsch"),
        GlobalLanguage("ja", "Japanese", "日本語")
    )

    private val _selectedCurrency = MutableStateFlow(currencies.first())
    val selectedCurrency: StateFlow<GlobalCurrency> = _selectedCurrency.asStateFlow()

    private val _selectedLanguage = MutableStateFlow(languages.first())
    val selectedLanguage: StateFlow<GlobalLanguage> = _selectedLanguage.asStateFlow()

    private val _userLocation = MutableStateFlow("Kebayoran Baru, Jakarta Selatan, Indonesia")
    val userLocation: StateFlow<String> = _userLocation.asStateFlow()

    // Saved Addresses (Indonesia, USA & Global)
    private val _userAddresses = MutableStateFlow(
        listOf(
            UserAddress(
                id = "addr_1",
                title = "Rumah Utama (Jakarta)",
                streetAddress = "Jl. Senopati No. 42, Selong, Kebayoran Baru",
                city = "Jakarta Selatan",
                stateProvince = "DKI Jakarta",
                country = "Indonesia",
                postalCode = "12110",
                technicianNotes = "Pagar abu-abu, bel di tiang kanan, mohon lepas alas kaki sebelum masuk.",
                isDefault = true
            ),
            UserAddress(
                id = "addr_2",
                title = "Apartemen Sudirman Park",
                streetAddress = "Jl. K.H. Mas Mansyur Kav. 35, Tower B Unit 18A",
                city = "Jakarta Pusat",
                stateProvince = "DKI Jakarta",
                country = "Indonesia",
                postalCode = "10220",
                technicianNotes = "Lapor ke lobby security di lantai GF untuk akses kartu lift lantai 18.",
                isDefault = false
            ),
            UserAddress(
                id = "addr_3",
                title = "Manhattan Residence (USA)",
                streetAddress = "450 Lexington Ave, Suite 18B",
                city = "New York",
                stateProvince = "NY",
                country = "United States",
                postalCode = "10017",
                technicianNotes = "Service elevator access on 44th St side. Concierge code #4921.",
                isDefault = false
            ),
            UserAddress(
                id = "addr_4",
                title = "Villa Canggu (Bali)",
                streetAddress = "Jl. Pantai Batu Bolong No. 88, Canggu",
                city = "Badung",
                stateProvince = "Bali",
                country = "Indonesia",
                postalCode = "80361",
                technicianNotes = "Masuk gang sebelah warung kopi, parkir motor & mobil tersedia di dalam villa.",
                isDefault = false
            ),
            UserAddress(
                id = "addr_5",
                title = "San Francisco Bay Office",
                streetAddress = "201 Mission St, 12th Floor",
                city = "San Francisco",
                stateProvince = "CA",
                country = "United States",
                postalCode = "94105",
                technicianNotes = "Security badge check-in required at front desk before 5 PM.",
                isDefault = false
            )
        )
    )
    val userAddresses: StateFlow<List<UserAddress>> = _userAddresses.asStateFlow()

    // Comprehensive Payment Methods (Indonesia, USA & Cash on Delivery)
    val paymentMethods = listOf(
        // 1. QRIS INDONESIA
        PaymentMethodItem(
            id = "pay_qris",
            name = "QRIS Instant (Semua Bank & E-Wallet)",
            category = PaymentCategoryType.QRIS_INDONESIA,
            countryOrigin = "ID",
            subtext = "Scan via BCA Mobile, Livin Mandiri, GoPay, OVO, DANA, ShopeePay, BRImo",
            iconType = "qris",
            badge = "Bebas Biaya Admin"
        ),

        // 2. VIRTUAL ACCOUNT INDONESIA
        PaymentMethodItem(
            id = "pay_va_bca",
            name = "BCA Virtual Account",
            category = PaymentCategoryType.VIRTUAL_ACCOUNT,
            countryOrigin = "ID",
            subtext = "Verifikasi otomatis 24/7 tanpa perlu kirim bukti transfer",
            iconType = "bank",
            badge = "Otomatis"
        ),
        PaymentMethodItem(
            id = "pay_va_mandiri",
            name = "Mandiri Virtual Account",
            category = PaymentCategoryType.VIRTUAL_ACCOUNT,
            countryOrigin = "ID",
            subtext = "Bayar via Livin' by Mandiri atau ATM Mandiri",
            iconType = "bank"
        ),
        PaymentMethodItem(
            id = "pay_va_bri",
            name = "BRI Virtual Account (BRIVA)",
            category = PaymentCategoryType.VIRTUAL_ACCOUNT,
            countryOrigin = "ID",
            subtext = "Bayar via BRImo atau ATM BRI seluruh Indonesia",
            iconType = "bank"
        ),
        PaymentMethodItem(
            id = "pay_va_bni",
            name = "BNI Virtual Account",
            category = PaymentCategoryType.VIRTUAL_ACCOUNT,
            countryOrigin = "ID",
            subtext = "Bayar via BNI Mobile Banking atau ATM BNI",
            iconType = "bank"
        ),
        PaymentMethodItem(
            id = "pay_va_permata",
            name = "Permata / CIMB Niaga VA",
            category = PaymentCategoryType.VIRTUAL_ACCOUNT,
            countryOrigin = "ID",
            subtext = "Mendukung transfer dari semua jaringan bank ATM Bersama / Prima",
            iconType = "bank"
        ),

        // 3. VIRTUAL BANK & ACH AMERIKA SERIKAT
        PaymentMethodItem(
            id = "pay_us_ach_chase",
            name = "Chase ACH Direct Debit (Plaid Verified)",
            category = PaymentCategoryType.VIRTUAL_ACCOUNT,
            countryOrigin = "US",
            subtext = "Direct bank debit linked via Plaid secure encryption",
            iconType = "bank",
            badge = "US Bank"
        ),
        PaymentMethodItem(
            id = "pay_us_wire_bofa",
            name = "Bank of America eCheck & Wire",
            category = PaymentCategoryType.VIRTUAL_ACCOUNT,
            countryOrigin = "US",
            subtext = "Routing & Account number instant escrow transfer",
            iconType = "bank",
            badge = "US Bank"
        ),
        PaymentMethodItem(
            id = "pay_us_fednow",
            name = "FedNow & Zelle Instant ACH Routing",
            category = PaymentCategoryType.VIRTUAL_ACCOUNT,
            countryOrigin = "US",
            subtext = "Instant 24/7 Federal Reserve clearance network",
            iconType = "bank",
            badge = "Real-Time"
        ),

        // 4. KARTU DEBIT / KREDIT (INDONESIA & US)
        PaymentMethodItem(
            id = "pay_card_indo",
            name = "Kartu Bank Indonesia (BCA, Mandiri, BRI, BNI)",
            category = PaymentCategoryType.BANK_CARD,
            countryOrigin = "ID",
            subtext = "Visa, Mastercard, JCB & GPN berlogo 3D Secure OTP",
            iconType = "card",
            badge = "3D Secure"
        ),
        PaymentMethodItem(
            id = "pay_card_jago",
            name = "Bank Jago / Jenius Debit Visa",
            category = PaymentCategoryType.BANK_CARD,
            countryOrigin = "ID",
            subtext = "Debit card instan dengan limit belanja fleksibel",
            iconType = "card"
        ),
        PaymentMethodItem(
            id = "pay_card_us_chase",
            name = "Chase Sapphire / Freedom (US Cards)",
            category = PaymentCategoryType.BANK_CARD,
            countryOrigin = "US",
            subtext = "US Visa / Mastercard with purchase protection",
            iconType = "card",
            badge = "US Card"
        ),
        PaymentMethodItem(
            id = "pay_card_us_bofa_citi",
            name = "Bank of America / Wells Fargo / Citi",
            category = PaymentCategoryType.BANK_CARD,
            countryOrigin = "US",
            subtext = "Standard US domestic credit & debit cards",
            iconType = "card",
            badge = "US Card"
        ),
        PaymentMethodItem(
            id = "pay_card_amex",
            name = "American Express & Discover (Global/US)",
            category = PaymentCategoryType.BANK_CARD,
            countryOrigin = "US",
            subtext = "High-limit corporate & premium charge cards",
            iconType = "card",
            badge = "Amex"
        ),

        // 5. E-WALLET INDONESIA
        PaymentMethodItem(
            id = "pay_wallet_gopay",
            name = "GoPay (Dompet Digital)",
            category = PaymentCategoryType.E_WALLET,
            countryOrigin = "ID",
            subtext = "Sambungkan akun GoPay untuk auto-debit escrow aman",
            iconType = "wallet",
            badge = "Populer"
        ),
        PaymentMethodItem(
            id = "pay_wallet_ovo",
            name = "OVO Cash",
            category = PaymentCategoryType.E_WALLET,
            countryOrigin = "ID",
            subtext = "Pembayaran instan via nomor handphone terdaftar",
            iconType = "wallet"
        ),
        PaymentMethodItem(
            id = "pay_wallet_dana",
            name = "DANA Indonesia",
            category = PaymentCategoryType.E_WALLET,
            countryOrigin = "ID",
            subtext = "Saldo DANA atau debit kartu tersimpan di DANA Protection",
            iconType = "wallet"
        ),
        PaymentMethodItem(
            id = "pay_wallet_shopee",
            name = "ShopeePay",
            category = PaymentCategoryType.E_WALLET,
            countryOrigin = "ID",
            subtext = "Cashback koin dan verifikasi PIN/Biometrik",
            iconType = "wallet"
        ),
        PaymentMethodItem(
            id = "pay_wallet_linkaja",
            name = "LinkAja (BUMN Sinergi)",
            category = PaymentCategoryType.E_WALLET,
            countryOrigin = "ID",
            subtext = "Integrasi layanan pembayaran nasional",
            iconType = "wallet"
        ),

        // 6. E-WALLET AMERIKA SERIKAT & GLOBAL
        PaymentMethodItem(
            id = "pay_wallet_apple",
            name = "Apple Pay",
            category = PaymentCategoryType.E_WALLET,
            countryOrigin = "US",
            subtext = "Biometric FaceID / TouchID instant authorization",
            iconType = "wallet",
            badge = "1-Tap"
        ),
        PaymentMethodItem(
            id = "pay_wallet_google",
            name = "Google Pay",
            category = PaymentCategoryType.E_WALLET,
            countryOrigin = "US",
            subtext = "Android Biometric tokenized secure card",
            iconType = "wallet",
            badge = "1-Tap"
        ),
        PaymentMethodItem(
            id = "pay_wallet_paypal",
            name = "PayPal Global Escrow",
            category = PaymentCategoryType.E_WALLET,
            countryOrigin = "GLOBAL",
            subtext = "International buyer protection & dispute guarantee",
            iconType = "wallet"
        ),
        PaymentMethodItem(
            id = "pay_wallet_venmo",
            name = "Venmo (US Only)",
            category = PaymentCategoryType.E_WALLET,
            countryOrigin = "US",
            subtext = "Pay using your Venmo balance or linked bank",
            iconType = "wallet"
        ),
        PaymentMethodItem(
            id = "pay_wallet_cashapp",
            name = "Cash App by Block (\$Cashtag)",
            category = PaymentCategoryType.E_WALLET,
            countryOrigin = "US",
            subtext = "Direct mobile transfer via Cashtag link",
            iconType = "wallet"
        ),

        // 7. BAYAR CASH DI TEMPAT (CASH ON DELIVERY)
        PaymentMethodItem(
            id = "pay_cash_cod",
            name = "Bayar Tunai di Tempat (Cash on Delivery)",
            category = PaymentCategoryType.CASH_ON_DELIVERY,
            countryOrigin = "GLOBAL",
            subtext = "Bayar uang tunai langsung ke teknisi setelah pekerjaan selesai, dites & disetujui. Tanda terima resmi di aplikasi.",
            iconType = "cash",
            badge = "Garansi Berlaku",
            isEscrowProtected = false
        )
    )

    fun addNewAddress(
        title: String,
        streetAddress: String,
        city: String,
        stateProvince: String,
        country: String,
        postalCode: String,
        technicianNotes: String = ""
    ): UserAddress {
        val newAddr = UserAddress(
            id = "addr_${System.currentTimeMillis()}",
            title = title.ifBlank { "Lokasi Baru" },
            streetAddress = streetAddress,
            city = city,
            stateProvince = stateProvince,
            country = country,
            postalCode = postalCode,
            technicianNotes = technicianNotes,
            isDefault = _userAddresses.value.isEmpty()
        )
        _userAddresses.value = listOf(newAddr) + _userAddresses.value
        _userLocation.value = "${newAddr.streetAddress}, ${newAddr.city}, ${newAddr.country}"
        return newAddr
    }

    fun deleteAddress(addressId: String) {
        _userAddresses.value = _userAddresses.value.filter { it.id != addressId }
    }

    fun setDefaultAddress(address: UserAddress) {
        _userAddresses.value = _userAddresses.value.map {
            it.copy(isDefault = it.id == address.id)
        }
        _userLocation.value = "${address.streetAddress}, ${address.city}, ${address.country}"
    }

    // App Roles & Screens State
    private val _currentRole = MutableStateFlow(AppRole.CUSTOMER)
    val currentRole: StateFlow<AppRole> = _currentRole.asStateFlow()

    private val _customerScreen = MutableStateFlow(CustomerScreen.HOME)
    val customerScreen: StateFlow<CustomerScreen> = _customerScreen.asStateFlow()

    private val _proScreen = MutableStateFlow(ProScreen.DASHBOARD)
    val proScreen: StateFlow<ProScreen> = _proScreen.asStateFlow()

    private val _selectedProForDetail = MutableStateFlow<Professional?>(null)
    val selectedProForDetail: StateFlow<Professional?> = _selectedProForDetail.asStateFlow()

    private val _selectedCategory = MutableStateFlow<ServiceCategory?>(null)
    val selectedCategory: StateFlow<ServiceCategory?> = _selectedCategory.asStateFlow()

    // Categories
    val initialCategories = listOf(
        ServiceCategory("hvac", "Air Conditioning & HVAC", "ac_unit", "Installation, maintenance, gas refill, and cooling diagnostics", 45.0, isPopular = true, activeProsCount = 318),
        ServiceCategory("electrical", "Electrical / Electrician", "bolt", "Wiring, fuse boxes, lighting, short circuits, and safety audits", 40.0, isPopular = true, activeProsCount = 422),
        ServiceCategory("plumbing", "Plumbing", "water_drop", "Leak detection, pipe repairs, faucet installation, and clogs", 35.0, isPopular = true, activeProsCount = 295),
        ServiceCategory("cctv", "CCTV & Security", "videocam", "Camera setup, biometric access, smart alarms, and NVR wiring", 50.0, isPopular = true, activeProsCount = 184),
        ServiceCategory("smart_home", "Smart Home Installation", "hub", "IoT hubs, automated shading, smart thermostats, and lighting", 55.0, isPopular = true, activeProsCount = 142),
        ServiceCategory("cleaning", "Cleaning Service", "cleaning_services", "Deep cleaning, post-construction sanitization, and upholstery", 30.0, isPopular = true, activeProsCount = 512),
        ServiceCategory("carpenter", "Carpenter / Woodwork", "carpenter", "Custom cabinetry, door fitting, deck repair, and joinery", 40.0, activeProsCount = 210),
        ServiceCategory("renovation", "Construction & Renovation", "foundation", "Drywall, masonry, bathroom overhaul, and structural fixes", 75.0, activeProsCount = 160),
        ServiceCategory("painting", "Painting", "format_paint", "Interior & exterior precision coating, texture, and waterproofing", 35.0, activeProsCount = 278),
        ServiceCategory("appliance", "Appliance Repair", "kitchen", "Refrigerators, washing machines, ovens, and dishwashers", 40.0, isPopular = true, activeProsCount = 265),
        ServiceCategory("locksmith", "Locksmith", "lock", "High-security lock replacement, emergency lockout, and rekeying", 45.0, isEmergency = true, activeProsCount = 190),
        ServiceCategory("network", "Internet & Network Tech", "wifi", "Fiber optic routing, enterprise mesh Wi-Fi, and server racks", 50.0, activeProsCount = 135),
        ServiceCategory("gardening", "Gardening & Landscape", "yard", "Lawn maintenance, tree pruning, irrigation, and hardscaping", 35.0, activeProsCount = 175),
        ServiceCategory("moving", "Moving Service", "local_shipping", "Full-home packing, heavy appliance transport, and logistics", 60.0, activeProsCount = 205),
        ServiceCategory("pest", "Pest Control", "pest_control", "Termite treatments, eco-fumigation, rodent exclusion, and sweeps", 45.0, activeProsCount = 110),
        ServiceCategory("handyman", "General Handyman", "build", "TV mounting, drywall patching, shelf hanging, and minor fixes", 30.0, isPopular = true, activeProsCount = 480),
        ServiceCategory("emergency", "Emergency Services 24/7", "crisis_alert", "Immediate response for electrical fires, bursts, and lockouts", 65.0, isEmergency = true, activeProsCount = 94)
    )
    private val _categoriesFlow = MutableStateFlow(initialCategories)
    val categoriesFlow: StateFlow<List<ServiceCategory>> = _categoriesFlow.asStateFlow()
    val categories: List<ServiceCategory> get() = _categoriesFlow.value

    // Emergency Types
    val initialEmergencyTypes = listOf(
        EmergencyServiceType("em_leak", "Water Leak / Pipe Burst", "water_drop", "Urgent shutoff & pipeline repair", 14, 65.0),
        EmergencyServiceType("em_power", "Power Outage / Sparks", "bolt", "Urgent breaker trip, short circuit, or panel failure", 12, 70.0),
        EmergencyServiceType("em_lock", "Locked Out", "lock_clock", "Non-destructive emergency entry assistance", 10, 60.0),
        EmergencyServiceType("em_ac", "AC Complete Failure", "severe_cold", "Critical cooling failure in extreme heat", 20, 65.0),
        EmergencyServiceType("em_fire", "Electrical Hazard", "local_fire_department", "Smoldering socket or burning smell isolation", 10, 80.0),
        EmergencyServiceType("em_security", "Security Breach / Alarm", "security", "Broken sensor, breached perimeter lock, or CCTV reboot", 15, 75.0)
    )
    private val _emergencyTypesFlow = MutableStateFlow(initialEmergencyTypes)
    val emergencyTypesFlow: StateFlow<List<EmergencyServiceType>> = _emergencyTypesFlow.asStateFlow()
    val emergencyTypes: List<EmergencyServiceType> get() = _emergencyTypesFlow.value

    // Professionals dataset
    val initialProfessionals = listOf(
        Professional(
            id = "pro_1",
            name = "Alex Morgan",
            title = "Certified Master HVAC & Climate Specialist",
            rating = 4.95,
            reviewCount = 384,
            completedJobs = 1284,
            experienceYears = 9,
            distanceKm = 2.4,
            startingPriceUsd = 45.0,
            hourlyRateUsd = 65.0,
            responseTimeMin = 8,
            availability = "Available today",
            isVerified = true,
            hasInsurance = true,
            hasWorkGuarantee = true,
            languages = listOf("English", "Spanish", "German"),
            categories = listOf("hvac", "electrical", "appliance"),
            bio = "Certified Master HVAC Technician with 9+ years experience across residential and commercial systems. Specializing in VRF diagnostics, inverter compressors, precision leak testing, and high-efficiency smart thermostats. EPA certified with 100% background clearance.",
            avatarRes = R.drawable.pro_alex_1789921744384,
            beforeAfterPortfolios = listOf(
                PortfolioItem("port_1", "Dual-Zone Heat Pump Compressor Overhaul", "Seized compressor drawing 42A with zero cooling output.", "Replaced contactor, flushed R410A lines, calibrated TXV, delivering 18°F delta T.", "3 days ago"),
                PortfolioItem("port_2", "Smart Inverter Board Re-soldering", "Error code E4 with intermittent shutoff on hot afternoons.", "Board refurbished with surge suppressors, cooling restored within 90 minutes.", "1 week ago")
            ),
            reviews = listOf(
                Review("rev_1", "Marcus Vance", "MV", "Yesterday", 5.0, "Alex arrived within 15 minutes of booking! Found the refrigerant leak on my central AC immediately, vacuumed the line, and charged it up. The app escrow payment made everything transparent.", 5.0, 5.0, 5.0, 5.0, "HVAC Full Diagnostics"),
                Review("rev_2", "Sophia Lindqvist", "SL", "Last week", 4.9, "Extremely professional, neat, and wore shoe covers indoors. Explained every single component before replacing. Highest recommendation!", 5.0, 4.9, 5.0, 4.8, "AC Seasonal Tune-Up")
            ),
            verifications = listOf(
                VerificationItem("Identity Verification", true, "Government ID & Biometrics Checked", "Oct 2025", "Full passport & biometric facial scan verified by global trust engine."),
                VerificationItem("Skills & License", true, "Master Technician Level IV", "Nov 2025", "State HVAC License #HV-884920 authenticated with licensing authority."),
                VerificationItem("Background Check", true, "Clean Record Clearance", "Jan 2026", "Zero incident multi-jurisdictional criminal & civil screening."),
                VerificationItem("Liability Insurance", true, "$1,000,000 Underwritten Policy", "Active", "Underwritten commercial coverage protecting property against accidental damage."),
                VerificationItem("Servora Guarantee", true, "100% Backed Protection", "Permanent", "Direct escrow release upon customer satisfaction confirmation.")
            )
        ),
        Professional(
            id = "pro_2",
            name = "Elena Rostova",
            title = "Licensed Electrical Engineer & Smart Tech",
            rating = 4.98,
            reviewCount = 512,
            completedJobs = 1640,
            experienceYears = 11,
            distanceKm = 3.8,
            startingPriceUsd = 50.0,
            hourlyRateUsd = 70.0,
            responseTimeMin = 10,
            availability = "Available today",
            isVerified = true,
            hasInsurance = true,
            hasWorkGuarantee = true,
            languages = listOf("English", "French"),
            categories = listOf("electrical", "smart_home", "cctv"),
            bio = "Master Electrician & Smart Automation Designer. Expert in 3-phase commercial panels, EV charger home installations, surge suppression, and Matter/Zigbee smart home network integration.",
            avatarRes = null,
            beforeAfterPortfolios = listOf(
                PortfolioItem("port_3", "200A Main Breaker Panel Modernization", "Dangerous 1980s fuse panel with burned busbars.", "Installed square-D QO panel with whole-home surge protector and AFCI breakers.", "5 days ago")
            ),
            reviews = listOf(
                Review("rev_3", "David K.", "DK", "2 days ago", 5.0, "Elena did a flawless job installing our Level 2 EV charging station and balancing our load center. Extremely knowledgeable.", 5.0, 5.0, 5.0, 5.0, "EV Charger & Panel Installation")
            ),
            verifications = listOf(
                VerificationItem("Identity Verification", true, "Biometrically Verified", "Dec 2025", "Full identity verified."),
                VerificationItem("Skills Certification", true, "IEEE Certified & Master Electrician", "Dec 2025", "State Electrical Board License #EL-339182."),
                VerificationItem("Background Check", true, "Cleared Tier 1", "Jan 2026", "Comprehensive civil & criminal audit."),
                VerificationItem("Insurance Policy", true, "$2,000,000 Commercial Policy", "Active", "Verified global liability coverage.")
            )
        ),
        Professional(
            id = "pro_3",
            name = "Mateo Gutierrez",
            title = "Master Master Plumber & Emergency Hydronics",
            rating = 4.91,
            reviewCount = 290,
            completedJobs = 940,
            experienceYears = 8,
            distanceKm = 1.9,
            startingPriceUsd = 40.0,
            hourlyRateUsd = 60.0,
            responseTimeMin = 12,
            availability = "Available today",
            isVerified = true,
            hasInsurance = true,
            hasWorkGuarantee = true,
            languages = listOf("English", "Spanish"),
            categories = listOf("plumbing", "emergency"),
            bio = "Rapid emergency response plumbing specialist. Certified in acoustic leak detection, thermographic wall inspections, water heater replacements, and trenchless drain clearing.",
            avatarRes = null,
            beforeAfterPortfolios = listOf(
                PortfolioItem("port_4", "Under-Slab High Pressure Water Leak", "Water pressure dropped to 15 PSI with foundation dampness.", "Isolated copper line beneath slab without breaking floor tile, rerouted PEX-A pipe.", "4 days ago")
            ),
            reviews = listOf(
                Review("rev_4", "Linda Chang", "LC", "3 days ago", 4.9, "Saved our apartment from a major water disaster at 11 PM! Arrived within 18 minutes.", 5.0, 4.9, 4.8, 5.0, "Emergency Pipe Freeze & Burst Fix")
            ),
            verifications = listOf(
                VerificationItem("Identity Verification", true, "Verified", "Jan 2026", "Identity authenticated."),
                VerificationItem("Plumbing Master License", true, "Plumbing Board #PL-9021", "Jan 2026", "Master plumber credential verified."),
                VerificationItem("Insurance Coverage", true, "$1,000,000 Bonded & Insured", "Active", "Commercial bonded policy active.")
            )
        ),
        Professional(
            id = "pro_4",
            name = "Kaito Tanaka",
            title = "High-Security Locksmith & Safe Technician",
            rating = 4.97,
            reviewCount = 420,
            completedJobs = 1350,
            experienceYears = 12,
            distanceKm = 4.1,
            startingPriceUsd = 45.0,
            hourlyRateUsd = 65.0,
            responseTimeMin = 9,
            availability = "Available today",
            isVerified = true,
            hasInsurance = true,
            hasWorkGuarantee = true,
            languages = listOf("English", "Japanese"),
            categories = listOf("locksmith", "emergency", "cctv"),
            bio = "Certified Institutional Locksmith. Non-destructive entry, digital smart lock integration (August, Yale, Schlage), master key systems, and safe repairs.",
            avatarRes = null,
            beforeAfterPortfolios = listOf(
                PortfolioItem("port_5", "Keyless Commercial Mortise Lock Retrofit", "Old tarnished physical tumbler lock.", "Upgraded to high-security biometric fingerprint & RFID keypad system.", "1 week ago")
            ),
            reviews = listOf(
                Review("rev_5", "Robert Miller", "RM", "4 days ago", 5.0, "Locked out of my apartment with stove on. Kaito arrived in 8 minutes and opened the lock in under 45 seconds without a single scratch!", 5.0, 5.0, 5.0, 5.0, "Emergency Lockout")
            ),
            verifications = listOf(
                VerificationItem("Identity Verification", true, "Verified", "Oct 2025", "ID confirmed."),
                VerificationItem("Locksmith Association", true, "ALOA Certified", "Nov 2025", "Associated Locksmiths of America credential."),
                VerificationItem("Insurance & Bond", true, "$1,000,000 Bonded", "Active", "Full bonded assurance.")
            )
        ),
        Professional(
            id = "pro_5",
            name = "Sarah Jenkins",
            title = "Architectural Finish Carpenter & Joiner",
            rating = 4.93,
            reviewCount = 215,
            completedJobs = 680,
            experienceYears = 7,
            distanceKm = 5.2,
            startingPriceUsd = 40.0,
            hourlyRateUsd = 55.0,
            responseTimeMin = 25,
            availability = "Available tomorrow",
            isVerified = true,
            hasInsurance = true,
            hasWorkGuarantee = true,
            languages = listOf("English"),
            categories = listOf("carpenter", "renovation", "handyman"),
            bio = "Specializing in custom shelving, interior pocket doors, baseboards, crown molding, and bespoke hardwood kitchen cabinetry.",
            avatarRes = null
        ),
        Professional(
            id = "pro_6",
            name = "Liam O'Connor",
            title = "Smart Home & CCTV Security Architect",
            rating = 4.96,
            reviewCount = 340,
            completedJobs = 1120,
            experienceYears = 10,
            distanceKm = 3.1,
            startingPriceUsd = 55.0,
            hourlyRateUsd = 75.0,
            responseTimeMin = 15,
            availability = "Available today",
            isVerified = true,
            hasInsurance = true,
            hasWorkGuarantee = true,
            languages = listOf("English", "Irish"),
            categories = listOf("cctv", "smart_home", "network"),
            bio = "Certified Security Architect with deep experience in 4K AI-powered IP surveillance, perimeter optical tripwires, PoE switch infrastructure, and zero-trust home network security.",
            avatarRes = null
        )
    )
    private val _professionalsFlow = MutableStateFlow(initialProfessionals)
    val professionalsFlow: StateFlow<List<Professional>> = _professionalsFlow.asStateFlow()
    val professionals: List<Professional> get() = _professionalsFlow.value

    // Active Bookings Flow
    private val _bookings = MutableStateFlow<List<Booking>>(
        listOf(
            Booking(
                id = "SRV-89421",
                serviceCategoryName = "Air Conditioning & HVAC",
                professional = initialProfessionals[0], // Alex Morgan
                status = BookingStatus.ON_THE_WAY,
                progressStep = JobProgressStep.JOB_STARTED,
                scheduledDate = "Today",
                scheduledTime = "10:30 AM",
                customerName = "Gabriel Sterling",
                customerAddress = "450 Lexington Ave, Suite 18B, New York",
                problemDescription = "Central AC unit is blowing ambient warm air instead of cold air. High condenser fan noise.",
                urgency = "Standard",
                estimatedMinUsd = 45.0,
                estimatedMaxUsd = 70.0,
                baseCostUsd = 45.0,
                paymentMethod = "Apple Pay •••• 4921",
                paymentStatus = PaymentStatus.HELD_IN_ESCROW,
                liveEtaMinutes = 11,
                technicianDistanceKm = 2.4,
                extraWork = null
            )
        )
    )
    val bookings: StateFlow<List<Booking>> = _bookings.asStateFlow()

    // Real-time Chat Messages
    private val _messages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                id = "m1",
                bookingId = "SRV-89421",
                senderRole = "SYSTEM",
                senderName = "Servora Escrow",
                text = "Booking SRV-89421 confirmed. $45.00 is securely held in Servora Escrow. Funds are released only after your approval.",
                timestamp = "10:15 AM"
            ),
            ChatMessage(
                id = "m2",
                bookingId = "SRV-89421",
                senderRole = "PROFESSIONAL",
                senderName = "Alex Morgan",
                text = "Hello Gabriel! I am en route in the Servora service van. I have diagnostic pressure gauges and spare capacitors ready.",
                timestamp = "10:18 AM"
            ),
            ChatMessage(
                id = "m3",
                bookingId = "SRV-89421",
                senderRole = "CUSTOMER",
                senderName = "Gabriel",
                text = "Great! The concierge has your name on the guest list. Take the south elevator to 18th floor.",
                timestamp = "10:20 AM"
            )
        )
    )
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    // User Saved Professionals
    private val _savedProIds = MutableStateFlow<Set<String>>(setOf("pro_1", "pro_2"))
    val savedProIds: StateFlow<Set<String>> = _savedProIds.asStateFlow()

    // Pro Dashboard Stats (When in Pro Mode)
    val proTodayEarnings = MutableStateFlow(285.0)
    val proMonthEarnings = MutableStateFlow(4820.0)
    val proAcceptanceRate = MutableStateFlow(97)
    val proResponseRate = MutableStateFlow(99)

    // Admin Stats (When in Admin Mode)
    val adminTotalUsers = MutableStateFlow(142850)
    val adminActivePros = MutableStateFlow(8640)
    val adminTotalBookings = MutableStateFlow(53410)
    val adminGrossRevenue = MutableStateFlow(2185400.0)
    val adminEscrowHeld = MutableStateFlow(348900.0)
    val adminPendingDisputes = MutableStateFlow(12)
    val adminVerificationQueue = MutableStateFlow(24)

    // Role Definitions & Permissions Matrix
    val roleDefinitions = listOf(
        RoleDefinition(
            role = AppRole.CUSTOMER,
            title = "Customer / Client",
            indonesianTitle = "Pelanggan / Pengguna Jasa",
            description = "Pesan layanan, lacak teknisi secara live, bayar aman via escrow, dan beri ulasan.",
            permissions = listOf(
                "Cari & Pesan 17+ Kategori Jasa",
                "Panggilan Darurat 24/7 (Emergency Dispatch)",
                "Pelacakan Lokasi Teknisi GPS Real-time",
                "Pembayaran Aman Rekening Bersama (Escrow)",
                "Chat Enkripsi Langsung dengan Teknisi",
                "Pemberian Rating & Review Pengerjaan"
            ),
            canAlterSecurity = false,
            requiresPin = false
        ),
        RoleDefinition(
            role = AppRole.PROFESSIONAL,
            title = "Verified Professional / Technician",
            indonesianTitle = "Mitra Teknisi / Tukang / Jasa",
            description = "Terima order pekerjaan, perbarui status kedatangan, ajukan suku cadang tambahan, dan tarik honor.",
            permissions = listOf(
                "Terima / Tolak Order Panggilan Masuk",
                "Perbarui Milestone Pengerjaan (Menuju Lokasi, Tiba, Selesai)",
                "Pengajuan Tambahan Biaya/Suku Cadang dengan Bukti Foto",
                "Pemberitahuan Chat Pelanggan Langsung",
                "Pantau Dompet Penghasilan & Tarik Dana (Instant Payout)",
                "Kelola Portofolio Lisensi & Sertifikasi"
            ),
            canAlterSecurity = false,
            requiresPin = false
        ),
        RoleDefinition(
            role = AppRole.ADMIN,
            title = "Platform Operations HQ Admin",
            indonesianTitle = "Admin Operasional Platform",
            description = "Verifikasi identitas teknisi, mediasi sengketa pelanggan, audit cadangan escrow, dan pantau metrik pasar.",
            permissions = listOf(
                "Verifikasi & Persetujuan Dokumen KTP/Lisensi Tukang",
                "Pusat Mediasi Sengketa (Dispute Resolution Center)",
                "Otorisasi Pengembalian Dana / Pembekuan Escrow",
                "Pemantauan Metrik Bisnis & Pendapatan Global",
                "Pengecekan Kepatuhan Polis Asuransi $1M"
            ),
            canAlterSecurity = false,
            requiresPin = true,
            defaultPin = "1122"
        ),
        RoleDefinition(
            role = AppRole.DEVELOPER,
            title = "System Core & Security Developer",
            indonesianTitle = "Pengembang Sistem & Keamanan (Developer)",
            description = "Akses tertinggi untuk mengontrol keamanan platform, feature flag sistem, log audit keamanan, dan sandbox data.",
            permissions = listOf(
                "Kontrol Penuh Fitur Keamanan & Security Flags",
                "Manajemen Enkripsi Escrow & Protokol Perlindungan",
                "Pemantauan Log Audit Sistem & Pelanggaran Akses",
                "Simulasi Database & Reset Data Sandbox Pengujian",
                "Inspeksi Status API (Gemini AI, Stripe, Maps)",
                "Pengaturan Mode Debug & Batas Kecepatan (Rate Limiting)"
            ),
            canAlterSecurity = true,
            requiresPin = true,
            defaultPin = "9900"
        )
    )

    // Security Feature Flags (Only Editable by DEVELOPER)
    private val _securityFeatureFlags = MutableStateFlow(
        listOf(
            SecurityFeatureFlag(
                id = "sec_escrow_strict",
                title = "Mandatory Escrow Vault Lock",
                description = "Wajibkan 100% dana pelanggan ditahan di rekening penampung escrow hingga pelanggan memberi persetujuan digital.",
                isEnabled = true,
                category = "ESCROW"
            ),
            SecurityFeatureFlag(
                id = "sec_pro_vetting",
                title = "Strict 5-Step Background Verification",
                description = "Kunci pesanan dari teknisi yang belum lolos verifikasi lisensi dan pemeriksaan catatan kriminal.",
                isEnabled = true,
                category = "SECURITY"
            ),
            SecurityFeatureFlag(
                id = "sec_emergency_priority",
                title = "24/7 Real-Time Emergency Routing",
                description = "Aktifkan algoritma perutean teknisi terdekat dengan respon tercepat (target <15 menit).",
                isEnabled = true,
                category = "SYSTEM"
            ),
            SecurityFeatureFlag(
                id = "sec_fraud_shield",
                title = "AI Fraud & Extra Work Anti-Gouging Shield",
                description = "Peringatkan pelanggan dan blokir secara otomatis jika teknisi meminta kenaikan tarif melebihi batas wajar.",
                isEnabled = true,
                category = "SECURITY"
            ),
            SecurityFeatureFlag(
                id = "sec_audit_immutable",
                title = "Tamper-Proof Audit Logging",
                description = "Catat setiap upaya eskalasi peran, perubahan saldo escrow, dan transaksi ke catatan audit permanen.",
                isEnabled = true,
                category = "SECURITY"
            ),
            SecurityFeatureFlag(
                id = "sec_debug_telemetry",
                title = "Developer Telemetry & Simulated GPS",
                description = "Tampilkan indikator latensi jaringan dan posisi GPS simulasi teknisi pada peta.",
                isEnabled = true,
                category = "SYSTEM"
            )
        )
    )
    val securityFeatureFlags: StateFlow<List<SecurityFeatureFlag>> = _securityFeatureFlags.asStateFlow()

    // System Audit Security Logs
    private val _systemAuditLogs = MutableStateFlow(
        listOf(
            SystemAuditLog("log_1", "10:14:02", "SYSTEM", "SEC_INIT", "Servora Escrow smart lock initialized with SHA-256 hash check.", "INFO"),
            SystemAuditLog("log_2", "10:15:20", "CUSTOMER", "ESCROW_LOCK", "Booking SRV-89421 created. $45.00 locked in secure vault.", "INFO"),
            SystemAuditLog("log_3", "10:18:45", "PROFESSIONAL", "DISPATCH_START", "Alex Morgan accepted job. GPS beacon live transmission active.", "INFO"),
            SystemAuditLog("log_4", "10:22:10", "CUSTOMER", "SECURITY_BLOCKED", "Unauthorized attempt to access Admin verification queue blocked.", "WARN"),
            SystemAuditLog("log_5", "10:25:00", "DEVELOPER", "SECURITY_VERIFY", "Developer session authenticated via Master Key 9900.", "INFO")
        )
    )
    val systemAuditLogs: StateFlow<List<SystemAuditLog>> = _systemAuditLogs.asStateFlow()

    fun toggleSecurityFeatureFlag(flagId: String, currentActorRole: AppRole): Boolean {
        // STRICT SECURITY RULE: Only DEVELOPER can modify security feature flags!
        if (currentActorRole != AppRole.DEVELOPER) {
            addAuditLog(
                actor = currentActorRole.name,
                action = "SECURITY_FLAG_DENIED",
                details = "User in role $currentActorRole attempted to modify security flag $flagId. Access denied.",
                severity = "CRITICAL"
            )
            return false
        }

        _securityFeatureFlags.value = _securityFeatureFlags.value.map { flag ->
            if (flag.id == flagId) {
                val newState = !flag.isEnabled
                addAuditLog(
                    actor = "DEVELOPER",
                    action = "SECURITY_FLAG_CHANGED",
                    details = "Security flag '${flag.title}' changed to $newState.",
                    severity = "WARN"
                )
                flag.copy(isEnabled = newState)
            } else flag
        }
        return true
    }

    fun addAuditLog(actor: String, action: String, details: String, severity: String = "INFO") {
        val timeStr = SimpleDateFormat("HH:mm:ss", Locale.US).format(Date())
        val newLog = SystemAuditLog(
            id = "log_${System.currentTimeMillis()}",
            timestamp = timeStr,
            actorRole = actor,
            action = action,
            details = details,
            severity = severity
        )
        _systemAuditLogs.value = listOf(newLog) + _systemAuditLogs.value.take(25)
    }

    fun resetAndSeedSandboxData() {
        addAuditLog(
            actor = "DEVELOPER",
            action = "SANDBOX_RESET",
            details = "Developer re-seeded sandbox bookings, simulated pros, and reset mock balances.",
            severity = "WARN"
        )
        proTodayEarnings.value = 285.0
        adminPendingDisputes.value = 12
        adminVerificationQueue.value = 24
    }

    fun clearAuditLogs() {
        _systemAuditLogs.value = listOf(
            SystemAuditLog("log_new", SimpleDateFormat("HH:mm:ss", Locale.US).format(Date()), "DEVELOPER", "LOGS_CLEARED", "System audit logs reset by developer.", "INFO")
        )
    }

    // Role Verification Helper
    fun verifyRolePin(role: AppRole, inputPin: String): Boolean {
        return when (role) {
            AppRole.ADMIN -> inputPin.trim() == "1122"
            AppRole.DEVELOPER -> inputPin.trim() == "9900"
            else -> true
        }
    }

    // Navigation & State Setters
    fun setRole(role: AppRole) {
        _currentRole.value = role
    }

    fun navigateCustomer(screen: CustomerScreen) {
        _customerScreen.value = screen
    }

    fun navigatePro(screen: ProScreen) {
        _proScreen.value = screen
    }

    fun selectProForDetail(pro: Professional) {
        _selectedProForDetail.value = pro
        _customerScreen.value = CustomerScreen.PRO_DETAIL
    }

    fun selectCategory(cat: ServiceCategory) {
        _selectedCategory.value = cat
    }

    fun setCurrency(currency: GlobalCurrency) {
        _selectedCurrency.value = currency
    }

    fun setLanguage(language: GlobalLanguage) {
        _selectedLanguage.value = language
    }

    fun toggleSavePro(proId: String) {
        val current = _savedProIds.value.toMutableSet()
        if (current.contains(proId)) current.remove(proId) else current.add(proId)
        _savedProIds.value = current
    }

    fun setLocation(loc: String) {
        _userLocation.value = loc
    }

    // Booking actions
    fun createBooking(
        category: ServiceCategory,
        pro: Professional,
        date: String,
        time: String,
        address: String,
        problem: String,
        urgency: String,
        paymentMethod: String
    ): Booking {
        val newBooking = Booking(
            id = "SRV-" + (10000..99999).random(),
            serviceCategoryName = category.name,
            professional = pro,
            status = BookingStatus.CONFIRMED,
            progressStep = JobProgressStep.JOB_STARTED,
            scheduledDate = date,
            scheduledTime = time,
            customerName = "Gabriel Sterling",
            customerAddress = address,
            problemDescription = problem,
            urgency = urgency,
            estimatedMinUsd = category.startingPriceUsd,
            estimatedMaxUsd = category.startingPriceUsd * 1.6,
            baseCostUsd = category.startingPriceUsd,
            paymentMethod = paymentMethod,
            paymentStatus = PaymentStatus.HELD_IN_ESCROW,
            liveEtaMinutes = 15,
            technicianDistanceKm = pro.distanceKm,
            extraWork = null
        )
        _bookings.value = listOf(newBooking) + _bookings.value

        // Add initial system message
        sendMessage(
            bookingId = newBooking.id,
            senderRole = "SYSTEM",
            senderName = "Servora Escrow",
            text = "Booking ${newBooking.id} confirmed with ${pro.name}. Payment is held securely in Escrow."
        )

        return newBooking
    }

    fun updateBookingStatus(bookingId: String, newStatus: BookingStatus) {
        _bookings.value = _bookings.value.map { b ->
            if (b.id == bookingId) {
                val newStep = when (newStatus) {
                    BookingStatus.CONFIRMED -> JobProgressStep.JOB_STARTED
                    BookingStatus.ACCEPTED -> JobProgressStep.JOB_STARTED
                    BookingStatus.ON_THE_WAY -> JobProgressStep.JOB_STARTED
                    BookingStatus.ARRIVED -> JobProgressStep.INSPECTION
                    BookingStatus.WORKING -> JobProgressStep.WORK_IN_PROGRESS
                    BookingStatus.COMPLETED -> JobProgressStep.COMPLETED
                    BookingStatus.CANCELLED -> b.progressStep
                }
                b.copy(status = newStatus, progressStep = newStep)
            } else b
        }
    }

    fun updateJobProgress(bookingId: String, step: JobProgressStep) {
        _bookings.value = _bookings.value.map { b ->
            if (b.id == bookingId) {
                b.copy(progressStep = step)
            } else b
        }
    }

    // Additional Work Request Simulation (Technician proposes, Customer approves)
    fun requestAdditionalWork(bookingId: String, desc: String, reason: String, costUsd: Double) {
        _bookings.value = _bookings.value.map { b ->
            if (b.id == bookingId) {
                b.copy(
                    progressStep = JobProgressStep.EXTRA_WORK_REQUESTED,
                    extraWork = ExtraWorkQuote(desc, reason, costUsd, isApproved = false)
                )
            } else b
        }
        sendMessage(
            bookingId = bookingId,
            senderRole = "PROFESSIONAL",
            senderName = "Technician",
            text = "Additional part required: $desc ($reason). Quote: +$${String.format(Locale.US, "%.2f", costUsd)}. Please approve in job tracker."
        )
    }

    fun approveAdditionalWork(bookingId: String) {
        _bookings.value = _bookings.value.map { b ->
            if (b.id == bookingId && b.extraWork != null) {
                b.copy(
                    progressStep = JobProgressStep.WORK_IN_PROGRESS,
                    baseCostUsd = b.baseCostUsd + b.extraWork.additionalCostUsd,
                    extraWork = b.extraWork.copy(isApproved = true)
                )
            } else b
        }
        sendMessage(
            bookingId = bookingId,
            senderRole = "CUSTOMER",
            senderName = "Customer",
            text = "I have reviewed and approved the additional work order."
        )
    }

    fun releaseEscrowPayment(bookingId: String) {
        _bookings.value = _bookings.value.map { b ->
            if (b.id == bookingId) {
                b.copy(
                    paymentStatus = PaymentStatus.RELEASED,
                    status = BookingStatus.COMPLETED,
                    progressStep = JobProgressStep.COMPLETED
                )
            } else b
        }
        sendMessage(
            bookingId = bookingId,
            senderRole = "SYSTEM",
            senderName = "Servora Escrow",
            text = "Escrow funds have been successfully released to the professional. Thank you for using Servora Protection!"
        )
    }

    fun addReview(proId: String, review: Review) {
        // Find pro and append review
        val pro = professionals.find { it.id == proId }
        // Can be stored or updated in local state
    }

    fun sendMessage(bookingId: String, senderRole: String, senderName: String, text: String, isAudio: Boolean = false, isLocation: Boolean = false) {
        val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
        val msg = ChatMessage(
            id = "msg_" + System.currentTimeMillis(),
            bookingId = bookingId,
            senderRole = senderRole,
            senderName = senderName,
            text = text,
            timestamp = sdf.format(Date()),
            isAudio = isAudio,
            isLocation = isLocation
        )
        _messages.value = _messages.value + msg
    }

    // Smart Service Matcher AI Simulation
    data class SmartMatchResult(
        val query: String,
        val matchedCategory: ServiceCategory,
        val estimatedMin: Double,
        val estimatedMax: Double,
        val confidenceScore: Int,
        val recommendedPros: List<Professional>,
        val aiReasoning: String
    )

    fun matchServiceQuery(query: String): SmartMatchResult {
        val q = query.lowercase().trim()
        val category = when {
            q.contains("ac") || q.contains("cool") || q.contains("hvac") || q.contains("air condition") || q.contains("freon") || q.contains("compressor") ->
                categories.first { it.id == "hvac" }
            q.contains("wire") || q.contains("electric") || q.contains("spark") || q.contains("socket") || q.contains("breaker") || q.contains("light") || q.contains("switch") ->
                categories.first { it.id == "electrical" }
            q.contains("leak") || q.contains("water") || q.contains("pipe") || q.contains("drain") || q.contains("clog") || q.contains("toilet") || q.contains("faucet") ->
                categories.first { it.id == "plumbing" }
            q.contains("lock") || q.contains("key") || q.contains("locked out") ->
                categories.first { it.id == "locksmith" }
            q.contains("camera") || q.contains("cctv") || q.contains("security") || q.contains("alarm") ->
                categories.first { it.id == "cctv" }
            q.contains("clean") || q.contains("dust") || q.contains("maid") ->
                categories.first { it.id == "cleaning" }
            q.contains("wood") || q.contains("cabinet") || q.contains("door") || q.contains("shelf") || q.contains("carpenter") ->
                categories.first { it.id == "carpenter" }
            q.contains("paint") || q.contains("wall") || q.contains("color") ->
                categories.first { it.id == "painting" }
            q.contains("fridge") || q.contains("oven") || q.contains("washing") || q.contains("dryer") || q.contains("appliance") ->
                categories.first { it.id == "appliance" }
            q.contains("wifi") || q.contains("internet") || q.contains("router") || q.contains("network") ->
                categories.first { it.id == "network" }
            q.contains("smart") || q.contains("automation") || q.contains("thermostat") || q.contains("alexa") ->
                categories.first { it.id == "smart_home" }
            q.contains("emergency") || q.contains("urgent") || q.contains("fire") || q.contains("immediate") ->
                categories.first { it.id == "emergency" }
            else ->
                categories.first { it.id == "hvac" }
        }

        val pros = professionals.filter { it.categories.contains(category.id) }.ifEmpty { professionals.take(3) }
        val reasoning = when (category.id) {
            "hvac" -> "Symptoms indicate potential refrigerant pressure drop, condenser fan failure, or clogged expansion coil. Certified HVAC specialist recommended."
            "electrical" -> "Electrical anomaly detected. Immediate diagnostic by a licensed electrician advised to prevent arcing or panel load issues."
            "plumbing" -> "Hydraulic pressure issue or line seal failure detected. Fast response technician recommended to prevent moisture intrusion."
            else -> "Matched with certified professional category based on diagnostic descriptors."
        }

        return SmartMatchResult(
            query = query,
            matchedCategory = category,
            estimatedMin = category.startingPriceUsd,
            estimatedMax = category.startingPriceUsd * 1.55,
            confidenceScore = 96,
            recommendedPros = pros,
            aiReasoning = reasoning
        )
    }

    // Format currency helper
    fun formatPrice(amountUsd: Double): String {
        val curr = _selectedCurrency.value
        val converted = amountUsd * curr.rateToUsd
        return when (curr.code) {
            "IDR" -> "Rp " + String.format(Locale.US, "%,d", converted.toLong()).replace(',', '.')
            "JPY" -> "${curr.symbol}${converted.toInt()}"
            else -> "${curr.symbol}${String.format(Locale.US, "%.2f", converted)}"
        }
    }

    // ==========================================
    // FIRESTORE SYNCHRONIZATION & STORAGE
    // ==========================================

    fun syncWithFirestore(coroutineScope: CoroutineScope) {
        if (!firestoreRepository.isAvailable()) {
            Log.i("ServoraRepository", "Firestore is in offline/standby mode")
            return
        }

        coroutineScope.launch {
            firestoreRepository.getProfessionalsFlow(fallbackList = initialProfessionals)
                .collect { pros ->
                    if (pros.isNotEmpty()) {
                        _professionalsFlow.value = pros
                    }
                }
        }

        coroutineScope.launch {
            firestoreRepository.getCategoriesFlow(fallbackList = initialCategories)
                .collect { cats ->
                    if (cats.isNotEmpty()) {
                        _categoriesFlow.value = cats
                    }
                }
        }

        coroutineScope.launch {
            firestoreRepository.getEmergencyServicesFlow(fallbackList = initialEmergencyTypes)
                .collect { ems ->
                    if (ems.isNotEmpty()) {
                        _emergencyTypesFlow.value = ems
                    }
                }
        }
    }

    suspend fun saveProfessional(professional: Professional): Result<Unit> {
        val current = _professionalsFlow.value.toMutableList()
        val index = current.indexOfFirst { it.id == professional.id }
        if (index != -1) {
            current[index] = professional
        } else {
            current.add(0, professional)
        }
        _professionalsFlow.value = current

        return if (firestoreRepository.isAvailable()) {
            firestoreRepository.saveProfessional(professional)
        } else {
            Result.success(Unit)
        }
    }

    suspend fun saveCategory(category: ServiceCategory): Result<Unit> {
        val current = _categoriesFlow.value.toMutableList()
        val index = current.indexOfFirst { it.id == category.id }
        if (index != -1) {
            current[index] = category
        } else {
            current.add(category)
        }
        _categoriesFlow.value = current

        return if (firestoreRepository.isAvailable()) {
            firestoreRepository.saveCategory(category)
        } else {
            Result.success(Unit)
        }
    }

    suspend fun saveEmergencyService(service: EmergencyServiceType): Result<Unit> {
        val current = _emergencyTypesFlow.value.toMutableList()
        val index = current.indexOfFirst { it.id == service.id }
        if (index != -1) {
            current[index] = service
        } else {
            current.add(service)
        }
        _emergencyTypesFlow.value = current

        return if (firestoreRepository.isAvailable()) {
            firestoreRepository.saveEmergencyService(service)
        } else {
            Result.success(Unit)
        }
    }

    suspend fun seedInitialDataToFirestore(): Result<String> {
        return if (firestoreRepository.isAvailable()) {
            firestoreRepository.seedInitialData(
                professionals = initialProfessionals,
                categories = initialCategories,
                emergencies = initialEmergencyTypes
            )
        } else {
            Result.failure(IllegalStateException("Firestore is not available (running in offline mode)"))
        }
    }
}
