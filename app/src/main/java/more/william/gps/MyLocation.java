package more.william.gps;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

class MyLocation {

    private double Latitud;
    private double Longitud;
    private Date Fecha;

    public double getLongitud() {
        return Longitud;
    }

    public void setLongitud(double longitud) {
        Longitud = longitud;
    }

    public Date getFecha() {
        return Fecha;
    }

    public void setFecha(Date fecha) {
        Fecha = fecha;
    }

    public double getLatitud() {

        return Latitud;
    }

    public void setLatitud(double latitud) {
        Latitud = latitud;
    }

    public MyLocation() {
    }

    public JSONObject toJSON () {
        JSONObject obj = new JSONObject();
        try {
            obj.put("latitud", getLatitud());
            obj.put("longitud", getLongitud());
            obj.put("date", getFecha());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

}
