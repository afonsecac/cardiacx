package cu.rayrdguezo.cardiacs.modulos.listardispositivos.model;

public class BuscarDispositivoModel {

    private String name;
    private String address;


    public BuscarDispositivoModel(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public BuscarDispositivoModel(String name) {
        this.name = name;
        this.address = "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
