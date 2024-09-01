package ec.edu.espe.main.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public final class Constans {

    private Constans() {
        throw new IllegalStateException("Utility class");
    }

    public static final List<Locale> COUNTRIES = Arrays.stream(Locale.getISOCountries())
            .map(iso -> new Locale("", iso))
            .toList();
}
