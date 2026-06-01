package utils;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.util.function.Consumer;

public class ButtonEditor extends AbstractCellEditor implements TableCellEditor {

    private final JButton button;
    private int currentRow;
    private final Consumer<Integer> action;

    public ButtonEditor(JCheckBox checkBox, String text,
                        Color color, Consumer<Integer> action) {
        this.action = action;
        button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 11));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

        button.addActionListener(e -> {
            fireEditingStopped();
            action.accept(currentRow); // appelle le TODO
        });
    }

    @Override
    public Component getTableCellEditorComponent(
            JTable table, Object value,
            boolean isSelected, int row, int column) {
        currentRow = row;
        return button;
    }

    @Override
    public Object getCellEditorValue() { return ""; }
}