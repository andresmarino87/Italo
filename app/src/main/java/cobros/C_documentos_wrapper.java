package cobros;

import java.io.Serializable;
import java.util.ArrayList;

public class C_documentos_wrapper implements Serializable{
    private static final long serialVersionUID = 1L;
    private ArrayList<C_documentos> items;

    public C_documentos_wrapper(ArrayList<C_documentos> items) {
        this.items = items;
    }

    public ArrayList<C_documentos> getItems() {
        return items;
    }
}

