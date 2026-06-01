# inventory_management 


src/
│
├── model/
│   ├── Product.java
│   ├── StockMovement.java
│   ├── StockMethod.java        (ENUM)
│   └── MovementType.java       (ENUM IN / OUT)
│
├── dao/
│   ├── ProductDAO.java
│   ├── StockMovementDAO.java
│   └── DBConnection.java
│
├── service/
│   └── StockService.java       ← CŒUR DU PROJET
│
├── ui/
│   ├── ProductForm.java
│   ├── MovementForm.java
│   ├── StockTableView.java
│   └── MainFrame.java
│
└── utils/
    └── ReflectionFormBuilder.java   (option simple)


