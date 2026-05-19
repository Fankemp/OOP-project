package university.view;

import university.enums.Language;

public class Lang {
    private static Language current = Language.RU;

    public static void set(Language lang) {
        current = lang;
    }

    public static Language get() {
        return current;
    }

    public static String t(String ru, String en, String kz) {
        return switch (current) {
            case ENG -> en;
            case KZ -> kz;
            default -> ru;
        };
    }
}