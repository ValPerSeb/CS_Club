
package app.controller.validator;

import java.sql.Date;

public abstract class CommonsValidator {
    public String isValidString(String element, String value) throws Exception {
        if (value.equals("")) {
            throw new Exception(element + " no puede ser un valor vacío.");
        }
        return value;
    }

    public int isValidInteger(String element, String value) throws Exception {
        isValidString(element, value);
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            throw new Exception(element + " debe ser un número entero.");
        }
    }

    public long isValidLong(String element, String value) throws Exception {
        isValidString(element, value);
        try {
            return Long.parseLong(value);
        } catch (Exception e) {
            throw new Exception(element + " debe ser un número entero.");
        }
    }

    public double isValidDouble(String element, String value) throws Exception {
        isValidString(element, value);
        try {
            return Double.parseDouble(value);
        } catch (Exception e) {
            throw new Exception(element + " debe ser un valor numérico.");
        }
    }
    
    public Date isValidDate(String element, String value) throws Exception {
        isValidString(element, value);
        try {
            return Date.valueOf(value);
        } catch (Exception e) {
            throw new Exception(element + " debe ser ingresada en el formato YYYY-MM-DD.");
        }
    }
}
