package cobros;

import java.io.Serializable;
import java.util.ArrayList;

public class C_pago_wrapper implements Serializable{


    private static final long serialVersionUID = 1L;
    private ArrayList<C_pagos> items;

    public C_pago_wrapper(ArrayList<C_pagos> items) {
        this.items = items;
    }

    public ArrayList<C_pagos> getItems() {
        return items;
    }
}

