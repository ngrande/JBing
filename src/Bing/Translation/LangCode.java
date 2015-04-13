package Bing.Translation;

/**
 * Language Code enums
 * Created by ngrande on 4/11/15.
 */
public enum LangCode {
    ARABIC("ar"), BOSNIAN_LATIN("bs-Latn"), BULGARIAN("bg"), CATALAN("ca"),
    CHINESE_SIMPLIFIED("zh-CHS"), CHINESE_TRADITIONAL("zh-CHT"), CROATIAN("hr"), CZECH("cs"),
    DANISH("da"), DUTCH("nl"), ENGLISH("en"), ESTONIAN("et"), FINNISH("fi"), FRENCH("fr"),
    GERMAN("de"), GREEK("el"), HAITIAN_CREOLE("ht"), HEBREW("he"), HINDI("hi"),
    HMONG_DAW("mww"), HUNGARIAN("hu"), INDONESIAN("id"), ITALIAN("it"), JAPANESE("ja"),
    KLINGON("tlh"), KLINGON_PLQAD("tlh-Qaak"), KOREAN("ko"), LATVIAN("lv"), LITHUANIAN("lt"),
    MALAY("ms"), MALTESE("mt"), NORWEGIAN("no"), PERSIAN("fa"), POLISH("pl"), PORTUGUESE("pt"),
    QUERÉTARO_OTOMI("otq"), ROMANIAN("ro"), RUSSIAN("ru"), SERBIAN_CYRILLIC("sc-Cyrl"),
    SERBIAN_LATIN("sr-Latn"), SLOVAK("sk"), SLOVENIAN("sl"), SPANISH("es"), SWEDISH("sv"),
    THAI("th"), TURKISH("tr"), UKRAINIAN("uk"), URDU("ur"), VIETNAMESE("vi"), WELSH("cy"),
    YUCATEC_MAYA("yua");

    private String code;

    LangCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
