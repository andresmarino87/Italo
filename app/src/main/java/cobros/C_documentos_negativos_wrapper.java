package cobros;

import java.io.Serializable;
import java.util.ArrayList;

public class C_documentos_negativos_wrapper implements Serializable{
    private static final long serialVersionUID = 1L;
    private ArrayList<C_documentos_negativos> items;

    public C_documentos_negativos_wrapper(ArrayList<C_documentos_negativos> items) {
        this.items = items;
    }

    public ArrayList<C_documentos_negativos> getItems() {
        return items;
    }
}

