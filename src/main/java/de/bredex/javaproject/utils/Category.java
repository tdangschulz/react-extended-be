package de.bredex.javaproject.utils;

public enum Category {

    CAR("Auto"), ACCESSORIES("Zubehoer"), NON("Keine Kategorie");

    private String gerName;

    private Category(String gerName) {
        this.gerName = gerName;
    }

    public static Category getCategoryOfString(String string) {
        for (Category category : Category.values()) {
            if (category.toString().equals(string)) {
                return category;
            }
        }

        return NON;
    }

    @Override
    public String toString() {
        return this.gerName;
    }

}
