package fun.wxy.greendao;

import java.io.IOException;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class Generator {

    private static void generateDaoFiles(Schema schema) {
        try {
            DaoGenerator generator = new DaoGenerator();
            generator.generateAll(schema, "app/src/main/java"); // file location
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    private static void createShipTable(Schema schema) {
        Entity t = schema.addEntity("Ship");
        t.addIdProperty();
        t.addStringProperty("store_name").notNull();
        t.addStringProperty("category").notNull();
        t.addStringProperty("item_no").notNull();
        t.addStringProperty("item_name").notNull();
        t.addIntProperty("order_qty").notNull();
        t.addIntProperty("ship_qty").notNull();
        t.addStringProperty("order_type").notNull();
        t.addDoubleProperty("price").notNull();
        t.addStringProperty("ship_no");
        t.addStringProperty("status").notNull();
    }

    private static void createShipHeaderTable(Schema schema) {
        Entity t = schema.addEntity("ShipHeader");
        t.addIdProperty();
        t.addStringProperty("ship_date").notNull();
        t.addDateProperty("created_date").notNull();
    }

    private static void createTable(Schema schema) {
        // one Entity mapping to one DB table
        Entity test = schema.addEntity("Test");
        // add table column
        test.addIdProperty();
        test.addStringProperty("name");
        test.addIntProperty("age").notNull();
        test.addStringProperty("sex").notNull();


        // add table generator here.
        createShipHeaderTable(schema);
        createShipTable(schema);
    }

    public static void main(String[] args) {
        Schema schema = new Schema(2, "fun.wxy.annoy_o_tron.dao");
        createTable(schema);
        generateDaoFiles(schema);
    }

}
