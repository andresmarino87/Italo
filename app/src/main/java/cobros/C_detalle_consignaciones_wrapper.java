package cobros;

import java.io.Serializable;
import java.util.ArrayList;

public class C_detalle_consignaciones_wrapper implements Serializable{
    private static final long serialVersionUID = 1L;
    private ArrayList<C_detalle_consignaciones> items;

    public C_detalle_consignaciones_wrapper(ArrayList<C_detalle_consignaciones> items) {
        this.items = items;
    }

    public ArrayList<C_detalle_consignaciones> getItems() {
        return items;
    }
}
