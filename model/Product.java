package model;

public class Product {
    private int id;
    private String name;
    private StockMethod stockMethod;
    private String unit;
    private double minStock;

    public Product() {}
    public Product(int id, String name, StockMethod stockMethod, String unit) {
        this.id = id; this.name = name;
        this.stockMethod = stockMethod; this.unit = unit;
    }
    // Getters / Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public StockMethod getStockMethod() { return stockMethod; }
    public void setStockMethod(StockMethod stockMethod) { this.stockMethod = stockMethod; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public double getMinStock() { return minStock; }
    public void setMinStock(double minStock) { this.minStock = minStock; }
    

    @Override
    public String toString() { return name; } // utile pour JComboBox
}