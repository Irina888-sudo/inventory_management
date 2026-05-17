package utils;
import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class FormBuilder {

    // Génère un JPanel avec JLabel + JTextField pour chaque champ du modèle
    // Utilise Reflection : Class.getDeclaredFields()
    public static Map<String, JTextField> buildForm(Class<?> modelClass, JPanel panel) {
        Map<String, JTextField> fields = new HashMap<>();
        panel.setLayout(new GridLayout(0, 2, 8, 8));

        for (Field f : modelClass.getDeclaredFields()) {
            // Ignore les champs techniques
            if (f.getName().equals("id")) continue;

            JLabel label = new JLabel(f.getName() + " :");
            JTextField input = new JTextField();
            panel.add(label);
            panel.add(input);
            fields.put(f.getName(), input);
        }
        return fields;
    }
}