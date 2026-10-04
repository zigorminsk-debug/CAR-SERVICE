package ru.carservice.app

/** A small offline starter catalog. New vehicles and manuals can be added to these data files
 * or supplied by the sync API without changing the UI. */
data class Vehicle(
    val id: String,
    val brand: String,
    val model: String,
    val years: IntRange,
    val body: String,
    val engineCode: String,
    val engine: String,
    val transmission: String
) {
    val title: String get() = "$brand $model"
    val yearLabel: String get() = "${years.first}–${years.last}"
    val fitLabel: String get() = "$body · $engineCode"
}

data class ManualSection(
    val id: String,
    val title: String,
    val eyebrow: String,
    val icon: String,
    val accent: Long,
    val sourceLanguage: String,
    val sourceText: String,
    val russianText: String,
    val steps: List<String>,
    val caution: String? = null
)

data class ServiceWork(
    val id: String,
    val category: String,
    val title: String,
    val description: String,
    val duration: String,
    val price: String,
    val icon: String,
    val accent: Long
)

data class ReleaseInfo(
    val version: String,
    val url: String,
    val notes: String,
    val apkUrl: String? = null
)

object CatalogData {
    private val baseVehicles = listOf(
        Vehicle("toyota-camry-xv30", "Toyota", "Camry", 2001..2006, "седан", "1MZ-FE", "3.0 V6 бензин", "АКПП"),
        Vehicle("toyota-corolla-e120", "Toyota", "Corolla", 2000..2007, "седан", "1ZZ-FE", "1.8 бензин", "МКПП / АКПП"),
        Vehicle("volkswagen-golf-mk5", "Volkswagen", "Golf V", 2003..2009, "хэтчбек", "BKC", "1.9 TDI дизель", "МКПП"),
        Vehicle("volkswagen-passat-b6", "Volkswagen", "Passat B6", 2005..2010, "универсал", "BWA", "2.0 TFSI бензин", "DSG"),
        Vehicle("bmw-3-e90", "BMW", "3 Series E90", 2005..2012, "седан", "N46B20", "2.0 бензин", "АКПП"),
        Vehicle("bmw-5-f10", "BMW", "5 Series F10", 2010..2016, "седан", "N20B20", "2.0 turbo бензин", "АКПП"),
        Vehicle("mercedes-w204", "Mercedes-Benz", "C-Class W204", 2007..2014, "седан", "M271", "1.8 Kompressor", "АКПП"),
        Vehicle("ford-focus-2", "Ford", "Focus II", 2004..2011, "хэтчбек", "FYDB", "1.6 бензин", "МКПП"),
        Vehicle("ford-transit-vii", "Ford", "Transit VII", 2000..2013, "фургон", "2.4 TDCi", "2.4 дизель", "МКПП"),
        Vehicle("renault-logan-1", "Renault", "Logan I", 2004..2012, "седан", "K7M", "1.6 бензин", "МКПП"),
        Vehicle("skoda-octavia-a5", "Skoda", "Octavia A5", 2004..2013, "лифтбек", "BSE", "1.6 MPI бензин", "МКПП"),
        Vehicle("lada-vesta", "LADA", "Vesta", 2015..2024, "седан", "21129", "1.6 бензин", "МКПП"),
        Vehicle("lada-granta", "LADA", "Granta", 2011..2024, "седан", "11186", "1.6 бензин", "МКПП"),
        Vehicle("hyundai-solaris-1", "Hyundai", "Solaris I", 2010..2017, "седан", "G4FA", "1.4 бензин", "АКПП"),
        Vehicle("kia-rio-3", "Kia", "Rio III", 2011..2017, "седан", "G4FC", "1.6 бензин", "МКПП"),
        Vehicle("nissan-qashqai-j10", "Nissan", "Qashqai J10", 2006..2013, "кроссовер", "MR20DE", "2.0 бензин", "CVT"),
        Vehicle("honda-cr-v-3", "Honda", "CR-V III", 2007..2012, "кроссовер", "K24Z1", "2.4 бензин", "АКПП"),
        Vehicle("chevrolet-niva", "Chevrolet", "Niva", 2002..2020, "внедорожник", "2123", "1.7 бензин", "МКПП")
    )

    // Model generations and year ranges are a catalog snapshot, not classified ad data.
    // AV.BY is used as a public reference for Belarusian model/generation naming; engine
    // codes still need VIN confirmation because one generation has many modifications.
    private val opelVehicles = listOf(
        Vehicle("opel-astra-g", "Opel", "Astra G", 2000..2004, "хэтчбек / универсал", "X16XEL / Z16XE", "1.6 бензин", "МКПП / АКПП"),
        Vehicle("opel-astra-g-classic", "Opel", "Astra G Classic", 2004..2009, "седан", "Z16XEP", "1.6 бензин", "МКПП"),
        Vehicle("opel-astra-h", "Opel", "Astra H", 2004..2010, "хэтчбек / универсал", "Z16XER", "1.6 бензин", "МКПП / Easytronic"),
        Vehicle("opel-astra-h-classic", "Opel", "Astra H Classic", 2007..2014, "седан", "Z16XER", "1.6 бензин", "МКПП"),
        Vehicle("opel-astra-j", "Opel", "Astra J", 2009..2015, "хэтчбек / универсал", "A16XER / A14NET", "1.4 / 1.6 бензин", "МКПП / АКПП"),
        Vehicle("opel-astra-k", "Opel", "Astra K", 2015..2021, "хэтчбек / универсал", "B14XFL / B16DTH", "1.4 бензин / 1.6 дизель", "МКПП / АКПП"),
        Vehicle("opel-astra-l", "Opel", "Astra L", 2021..2025, "хэтчбек / универсал", "EB2ADTS", "1.2 turbo бензин", "МКПП / АКПП"),
        Vehicle("opel-corsa-c", "Opel", "Corsa C", 2000..2006, "хэтчбек", "Z12XE / Z13DT", "1.2 бензин / 1.3 дизель", "МКПП / Easytronic"),
        Vehicle("opel-corsa-d", "Opel", "Corsa D", 2006..2014, "хэтчбек", "Z12XEP / A14XER", "1.2 / 1.4 бензин", "МКПП / Easytronic"),
        Vehicle("opel-corsa-e", "Opel", "Corsa E", 2014..2019, "хэтчбек", "B12XER / B14XEL", "1.2 / 1.4 бензин", "МКПП / АКПП"),
        Vehicle("opel-corsa-f", "Opel", "Corsa F", 2019..2025, "хэтчбек", "EB2ADTS", "1.2 turbo бензин", "МКПП / АКПП"),
        Vehicle("opel-vectra-b", "Opel", "Vectra B", 2000..2002, "седан / хэтчбек / универсал", "X16XEL", "1.6 бензин", "МКПП / АКПП"),
        Vehicle("opel-vectra-c", "Opel", "Vectra C", 2002..2008, "седан / хэтчбек / универсал", "Z18XER / Z19DTH", "1.8 бензин / 1.9 дизель", "МКПП / АКПП"),
        Vehicle("opel-signum", "Opel", "Signum", 2003..2008, "лифтбек", "Z22YH", "2.2 бензин", "МКПП / АКПП"),
        Vehicle("opel-omega-b", "Opel", "Omega B рестайлинг", 2000..2003, "седан / универсал", "X20XEV", "2.0 бензин", "МКПП / АКПП"),
        Vehicle("opel-zafira-a", "Opel", "Zafira A", 2000..2005, "минивэн", "Z16XE", "1.6 бензин", "МКПП"),
        Vehicle("opel-zafira-b", "Opel", "Zafira B", 2005..2014, "минивэн", "Z16XER / Z19DTH", "1.6 бензин / 1.9 дизель", "МКПП / АКПП"),
        Vehicle("opel-zafira-c", "Opel", "Zafira C", 2011..2019, "минивэн", "A16XER / A20DTH", "1.6 бензин / 2.0 дизель", "МКПП / АКПП"),
        Vehicle("opel-insignia-a", "Opel", "Insignia I", 2008..2013, "седан / лифтбек / универсал", "A16LET / A20DTH", "1.6 turbo бензин / 2.0 дизель", "МКПП / АКПП"),
        Vehicle("opel-insignia-a-restyle", "Opel", "Insignia I рестайлинг", 2013..2017, "седан / лифтбек / универсал", "A16XHT / A20DTH", "1.6 turbo бензин / 2.0 дизель", "МКПП / АКПП"),
        Vehicle("opel-insignia-b", "Opel", "Insignia II", 2017..2022, "лифтбек / универсал", "B16DTH", "1.6 дизель", "МКПП / АКПП"),
        Vehicle("opel-meriva-a", "Opel", "Meriva A", 2003..2010, "минивэн", "Z16XEP", "1.6 бензин", "МКПП / Easytronic"),
        Vehicle("opel-meriva-b", "Opel", "Meriva B", 2010..2017, "минивэн", "A14NET", "1.4 turbo бензин", "МКПП / АКПП"),
        Vehicle("opel-mokka-a", "Opel", "Mokka I", 2012..2020, "кроссовер", "A18XER / A14NET", "1.8 / 1.4 turbo бензин", "МКПП / АКПП"),
        Vehicle("opel-mokka-b", "Opel", "Mokka II", 2020..2025, "кроссовер", "EB2ADTS", "1.2 turbo бензин", "МКПП / АКПП"),
        Vehicle("opel-antara", "Opel", "Antara", 2006..2015, "кроссовер", "A22DMH / Z24SED", "2.2 дизель / 2.4 бензин", "МКПП / АКПП"),
        Vehicle("opel-crossland-x", "Opel", "Crossland X", 2017..2021, "кроссовер", "EB2DT / B12XHT", "1.2 turbo бензин", "МКПП / АКПП"),
        Vehicle("opel-crossland", "Opel", "Crossland", 2021..2024, "кроссовер", "EB2ADTS", "1.2 turbo бензин", "МКПП / АКПП"),
        Vehicle("opel-grandland-x", "Opel", "Grandland X", 2017..2021, "кроссовер", "DV5RC / EP6FADTX", "1.5 дизель / 1.6 turbo бензин", "МКПП / АКПП"),
        Vehicle("opel-grandland", "Opel", "Grandland", 2021..2025, "кроссовер", "EP6FADTX", "1.6 turbo бензин / PHEV", "АКПП"),
        Vehicle("opel-adam", "Opel", "Adam", 2013..2019, "хэтчбек", "A12XEL", "1.2 бензин", "МКПП / Easytronic"),
        Vehicle("opel-karl", "Opel", "Karl", 2015..2019, "хэтчбек", "B12D1", "1.0 бензин", "МКПП"),
        Vehicle("opel-tigra-b", "Opel", "Tigra TwinTop", 2004..2009, "кабриолет", "Z14XEP", "1.4 бензин", "МКПП / Easytronic"),
        Vehicle("opel-frontera-b", "Opel", "Frontera B", 2000..2004, "внедорожник", "X22SE", "2.2 бензин", "МКПП"),
        Vehicle("opel-combo-c", "Opel", "Combo C", 2001..2011, "фургон / универсал", "Z13DTJ", "1.3 дизель", "МКПП"),
        Vehicle("opel-combo-d", "Opel", "Combo D", 2011..2018, "фургон / универсал", "A13FD", "1.3 дизель", "МКПП"),
        Vehicle("opel-combo-e", "Opel", "Combo E", 2018..2024, "фургон / универсал", "DV5RC", "1.5 дизель", "МКПП / АКПП"),
        Vehicle("opel-vivaro-a", "Opel", "Vivaro A", 2001..2014, "фургон", "F9Q / M9R", "1.9 / 2.0 дизель", "МКПП"),
        Vehicle("opel-vivaro-b", "Opel", "Vivaro B", 2014..2019, "фургон", "M9R", "2.0 дизель", "МКПП"),
        Vehicle("opel-vivaro-c", "Opel", "Vivaro C", 2019..2025, "фургон", "DW10", "2.0 дизель", "МКПП / АКПП"),
        Vehicle("opel-movano-a", "Opel", "Movano A", 2000..2010, "фургон", "F9Q / G9U", "2.2 / 2.5 дизель", "МКПП"),
        Vehicle("opel-movano-b", "Opel", "Movano B", 2010..2021, "фургон", "M9T", "2.3 дизель", "МКПП"),
        Vehicle("opel-movano-c", "Opel", "Movano C", 2021..2025, "фургон", "M9T", "2.3 дизель", "МКПП / АКПП")
    )

    val vehicles = baseVehicles + opelVehicles

    val works = listOf(
        ServiceWork("maintenance", "ТО", "Плановое техническое обслуживание", "Масло, фильтры, диагностика и контрольные точки по регламенту.", "1–2 ч", "от 4 500 ₽", "check", 0xFF2F80ED),
        ServiceWork("oil", "ТО", "Замена масла и фильтров", "Подбор расходных материалов по VIN и коду двигателя.", "45 мин", "от 1 200 ₽", "oil", 0xFF00A896),
        ServiceWork("brakes", "Ходовая", "Тормозная система", "Колодки, диски, суппорты, тормозная жидкость и прокачка.", "1–3 ч", "от 2 000 ₽", "brakes", 0xFFE85D04),
        ServiceWork("suspension", "Ходовая", "Диагностика подвески", "Проверка люфтов, амортизаторов и состояния сайлентблоков.", "45 мин", "от 1 000 ₽", "suspension", 0xFF7C3AED),
        ServiceWork("engine", "Двигатель", "Компьютерная диагностика", "Считывание ошибок, живые параметры и сброс сервисных интервалов.", "40 мин", "от 1 500 ₽", "engine", 0xFF102A43),
        ServiceWork("timing", "Двигатель", "Замена цепи или ремня ГРМ", "Работа по заводской процедуре с установкой фаз и проверкой меток.", "4–8 ч", "от 12 000 ₽", "timing", 0xFFDB2777),
        ServiceWork("ac", "Комфорт", "Кондиционер и климат", "Диагностика утечек, вакуумирование и заправка хладагентом.", "1–2 ч", "от 2 500 ₽", "ac", 0xFF0891B2),
        ServiceWork("electrics", "Электрика", "Автоэлектрика", "Поиск обрыва, короткого замыкания и ремонт проводки.", "от 1 ч", "от 1 800 ₽", "electrics", 0xFFFFB547),
        ServiceWork("body", "Кузов", "Кузовной ремонт", "Локальная покраска, восстановление геометрии и полировка.", "по оценке", "от 5 000 ₽", "body", 0xFF64748B)
    )

    fun sections(vehicle: Vehicle): List<ManualSection> = listOf(
        ManualSection(
            "maintenance", "Регламент обслуживания", "01 · ТО", "check", 0xFF2F80ED, "en",
            "Maintenance schedule. Replace the engine oil and oil filter every 10,000 km or 12 months. Inspect the brake system, steering and suspension at each service.",
            "Регламент обслуживания. Заменяйте моторное масло и масляный фильтр каждые 10 000 км или 12 месяцев. На каждом ТО проверяйте тормозную систему, рулевое управление и подвеску.",
            listOf("Поставьте автомобиль на ровную площадку и прогрейте двигатель.", "Сверьте пробег и дату с таблицей регламента.", "Зафиксируйте выполненные работы в истории обслуживания."),
            "Интервал может меняться при тяжелых условиях эксплуатации. Используйте руководство именно для выбранного кода двигателя."
        ),
        ManualSection(
            "engine", "Двигатель", "02 · СИЛОВОЙ АГРЕГАТ", "engine", 0xFF102A43, "en",
            "Engine inspection. Before troubleshooting, check the battery voltage, connector condition and engine ground points. Read diagnostic trouble codes before replacing parts.",
            "Проверка двигателя. Перед поиском неисправности проверьте напряжение аккумулятора, состояние разъемов и точки массы двигателя. Считайте коды неисправностей до замены деталей.",
            listOf("Подключите диагностический сканер к разъему OBD-II.", "Сохраните коды и снимок параметров до их удаления.", "Проверьте проводку и питание соответствующего узла."),
            "Не отсоединяйте аккумулятор до сохранения кодов: это может удалить полезные данные диагностики."
        ),
        ManualSection(
            "transmission", "Трансмиссия", "03 · ПЕРЕДАЧА МОЩНОСТИ", "transmission", 0xFF7C3AED, "en",
            "Transmission service. Inspect the fluid level at the specified temperature. Use only the fluid specification listed for this transmission code.",
            "Обслуживание трансмиссии. Проверяйте уровень жидкости при указанной температуре. Используйте только жидкость, рекомендованную для этого кода коробки передач.",
            listOf("Уточните тип коробки и допуск жидкости по VIN.", "Проверьте отсутствие подтеков на корпусе и приводах.", "После обслуживания выполните контрольную поездку."),
            "Перелив или неподходящая жидкость могут привести к повреждению коробки передач."
        ),
        ManualSection(
            "chassis", "Ходовая и тормоза", "04 · БЕЗОПАСНОСТЬ", "chassis", 0xFFE85D04, "ru",
            "Проверка ходовой части начинается с визуального осмотра шин, дисков, пыльников и тормозных магистралей. Люфт деталей проверяется на поднятом автомобиле.",
            "Проверка ходовой части начинается с визуального осмотра шин, дисков, пыльников и тормозных магистралей. Люфт деталей проверяется на поднятом автомобиле.",
            listOf("Осмотрите шины и измерьте остаточную глубину протектора.", "Проверьте люфт шаровых опор и рулевых наконечников.", "После работ затяните крепеж с моментом из таблицы."),
            "Работы под автомобилем выполняйте только на надежных опорах, а не на одном домкрате."
        ),
        ManualSection(
            "electrics", "Электросхемы", "05 · ЭЛЕКТРИКА", "electrics", 0xFFFFB547, "en",
            "Electrical diagnosis. Disconnect the negative battery terminal before repairing wiring. Protect connectors from moisture and verify fuse ratings before testing a circuit.",
            "Диагностика электрики. Перед ремонтом проводки отсоедините минусовую клемму аккумулятора. Защищайте разъемы от влаги и проверяйте номинал предохранителя до проверки цепи.",
            listOf("Найдите цепь по обозначению и номеру предохранителя.", "Проверьте питание мультиметром относительно массы.", "После ремонта заизолируйте соединение и повторите тест."),
            "Никогда не устанавливайте предохранитель большего номинала."
        ),
        ManualSection(
            "body", "Кузов и салон", "06 · КУЗОВ", "body", 0xFF64748B, "en",
            "Body repair notes. Measure panel gaps before disassembly and protect adjacent paintwork. After repair, inspect corrosion protection and door alignment.",
            "Рекомендации по кузову. Перед разборкой измерьте зазоры панелей и защитите соседние окрашенные детали. После ремонта проверьте антикоррозионную защиту и регулировку дверей.",
            listOf("Зафиксируйте исходное состояние фото и измерениями.", "Закройте стекла и элементы салона защитным материалом.", "Восстановите защитное покрытие после ремонта."),
            "Сварочные работы рядом с топливной системой выполняются только после подготовки автомобиля."
        )
    )
}
