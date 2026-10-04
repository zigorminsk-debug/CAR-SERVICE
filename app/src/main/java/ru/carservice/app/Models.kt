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
    val vehicles = listOf(
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
