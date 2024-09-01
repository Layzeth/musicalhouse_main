package ec.edu.espe.main.utils;

import java.util.List;

public final class Validators {
    private Validators() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static boolean allItemsListAre(List<?> list, Class<?> clazz) {
        return list.stream().allMatch(clazz::isInstance);
    }
}
