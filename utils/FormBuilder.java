package utils;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

public class FormBuilder {

    /**
     * REFLECTION : lit les champs déclarés d'une classe
     * et génère automatiquement JLabel + JTextField pour chacun.
     *
     * @param modelClass  
     * @param panel       
     * @param exclude    
     * @return Map<nomChamp, JTextField> pour récupérer les valeurs
     */
    public static Map<String, JTextField> buildForm(
            Class<?> modelClass, JPanel panel, String... exclude) {

        // LinkedHashMap = garde l'ordre des champs
        Map<String, JTextField> fields = new LinkedHashMap<>();

        // ── REFLECTION : getDeclaredFields() ────────────────
        // Retourne tous les attributs déclarés dans la classe
        for (Field f : modelClass.getDeclaredFields()) {

            // Ignore les champs exclus (ex: "id")
            if (shouldExclude(f.getName(), exclude)) continue;

            // Crée le label avec nom lisible
            JLabel label = UITheme.formLabel(formatLabel(f.getName()));

            // Crée le champ texte stylé
            JTextField input = UITheme.styledField();

            panel.add(label);
            panel.add(input);

            // Stocke avec le nom du champ comme clé
            fields.put(f.getName(), input);
        }
        return fields;
    }

    // "stockMethod" → "Stock Method"
    private static String formatLabel(String fieldName) {
        StringBuilder sb = new StringBuilder();
        for (char c : fieldName.toCharArray()) {
            if (Character.isUpperCase(c))
                sb.append(" ").append(c);
            else
                sb.append(c);
        }
        String result = sb.toString();
        return Character.toUpperCase(result.charAt(0)) + result.substring(1);
    }

    private static boolean shouldExclude(String name, String[] exclude) {
        for (String ex : exclude)
            if (ex.equals(name)) return true;
        return false;
    }
}